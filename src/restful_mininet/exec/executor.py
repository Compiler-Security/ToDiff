import sys
import traceback
from os import path
path_to_add = path.dirname(path.dirname(path.dirname(path.dirname(path.abspath(__file__)))))
if path_to_add not in sys.path:
    sys.path.append(path_to_add)

from src.restful_mininet.net import testnet
from src.restful_mininet.exec.inst import MininetInst
from src.restful_mininet.util.log import *
from mininet.cli import CLI
import os
import json
import time

class executor:

    def __init__(self, conf_path, output_dir_str, minWaitTime, maxWaitTime) -> None:
        setLogLevel('info')
        self.conf_path = conf_path
        self.maxWaitTime = maxWaitTime
        self.minWaitTime = minWaitTime
        with open(self.conf_path) as fp:
             self.conf = json.load(fp)
        self.step_nums = self.conf['step_nums']
        self.conf_name = self.conf['conf_name']
        self.round_num = self.conf['round_num']
        self.output_dir = path.join(output_dir_str, self.conf_name)
        self.routers = self.conf['routers']
        os.makedirs(self.output_dir, exist_ok=True)
        ###attention conf_dir is used to Temporarily store the configuration before loading /etc/frr.conf. When ospf is closed, memory will be written and the result of the write will be copied here.
        self.conf_file_dir = path.join(self.output_dir, 'conf')
        os.makedirs(self.conf_file_dir, exist_ok=True)
        os.system("mn -c 2> /dev/null")
    
    def _run_phy(self, net, ctx, phy_commands):
        res = []
        for op in phy_commands:
            ress = MininetInst(op, net, self.conf_file_dir, ctx).run()
            res.append(ress)
            if (ress != 0):
                erroraln(f"phy exec <{op}> wrong! exit the test, reason is:\n", ress)
                assert False
        warnaln("   PHY commands result:", res)
        return res
    
    def _run_ospf(self, net:testnet.TestNet, router_name, ospf_commands):
        res = []
        for op in ospf_commands:
            if op in ["clear ip ospf process", "write terminal"]:
                res.append(net.run_frr_cmds(router_name, [op]))
            else:
                resStr = ""
                sub_ops = op.split(";")
                ctx_op = sub_ops[0]
                #single ctx_op eg. router ospf
                if (len(sub_ops) == 1):
                    res.append(net.run_frr_cmds( router_name, ['configure terminal', ctx_op]))
                else:
                    sub_ops = sub_ops[1:]
                    for sub_op in sub_ops:
                        r = net.run_frr_cmds(router_name, ['configure terminal', ctx_op, sub_op])
                        if r != "":
                            resStr += sub_op + "<-" + r + ";"
                    res.append(resStr)
        warnaln("   OSPF commands result:", res)
        return res
    
    def _init_ospf(self, router_name, ospf_commands):
        conf_name = f"{router_name}.conf"
        with open(path.join(self.conf_file_dir, conf_name), 'w') as fp:
            for opa in ospf_commands:
                for op in opa.split(";"):
                    fp.write(op)
                    fp.write('\n')
          
    def test(self):
        try:
            res = {}
            start = time.time()
            res['result'] = []
            for i in range(0, self.round_num):
                res['result'].append(self._run(i))
            stop = time.time()
            res['total_test_time'] = stop - start
            self.conf['test'] = res
            result_path = path.join(self.output_dir, f"{self.conf_name}_res.json")
            with open(result_path, "w") as fp:
                json.dump(self.conf, fp)
            return 0
        except Exception as e:
            traceback.print_exception(e)
            os.system("mn -c")
            return -1

    def _check_converge(self, net:testnet.TestNet):
        for r_name in self.routers:
            warnln(f"    +check router {r_name}")
            res = net.net.nameToNode[r_name].dump_neighbor_info()
            if res == None:
                warnln(f"    -check router {r_name}")
                return False
            for val in res['neighbors'].values():
                for val1 in val:
                    if (val1['converged'] != 'Full' or val1['linkStateRetransmissionListCounter'] > 0):
                        warnln(f"    -check router {r_name}")
                        return False
            warnln(f"    -check router {r_name}")
        return True
    
    def _run(self, r):
        erroraln(f"\n\n======round{r}======","")
        erroraln("+ mininet init", "")
        net = testnet.TestNet()
        erroraln("- mininet init", "")
        ctx = {"intf":{}}
        commands = self.conf['commands'][r]
        res = []
        for i in range(0, self.step_nums[r]):
            erroraln(f"\n\n>>>> + step{i} <<<<", "")
            
            erroraln(f"+ OSPF commands", "")
            ospf_res = {}
            if i == 0:
                for j in range(0, len(self.routers)):
                    router_name = self.routers[j]
                    ospf_ops = commands[i]['ospf'][j]
                    self._init_ospf(router_name, ospf_ops)
            else:
                for j in range(len(self.routers) -1, -1, -1):
                    router_name = self.routers[j]
                    ospf_ops = commands[i]['ospf'][j]
                    tmp = self._run_ospf(net, router_name, ospf_ops)
                    if j == 0:
                        print(tmp)
                    ospf_res[router_name] = tmp
            erroraln(f"- OSPF commands", "")
            
            erroraln(f"+ PHY commands", "")
            phy_res = self._run_phy(net, ctx, commands[i]['phy'])
            erroraln(f"- PHY commands", "")

            if i == 0:    
                net.start_net()
            res.append({})
            res[i]['exec'] = {}
            res[i]['exec']['phy'] = phy_res
            res[i]['exec']['ospf'] = ospf_res
            
            sleep_time = commands[i]['waitTime']
            erroraln(f"wait {sleep_time} s ", "")
            #CLI(net.net)
            if sleep_time == -1:
                #handle convergence
                    #min(_check_convergence() + minWaitTime, maxWaitTime)
                    #for simplicity, maxWaitTime % minWaitTime == 0
                erroraln("+ check convergence", "")
                begin_t = time.time()
                while True:
                    if self._check_converge(net):
                        time.sleep(self.minWaitTime)
                        res[i]['exec']['convergence'] = True
                        warnaln("   + convergence!", "")
                        break
                    else:
                        if time.time() - begin_t >= self.maxWaitTime:
                            res[i]['exec']['convergence'] = False
                            warnaln("   + not convergence!", "")
                            break
                        else:
                            time.sleep(self.minWaitTime)
            else:
                time.sleep(sleep_time)
            
            erroraln("+ collect result", "")
            warnaln("   + collect from daemons", "")
            res[i]['watch'] = {}
            for r_name in self.routers:
                #some routers may be deleted
                if r_name not in net.net.nameToNode:
                    continue
                res[i]['watch'][r_name] = net.net.nameToNode[r_name].dump_info()
            warnaln("   - collect from daemons", "")
            warnaln("   + collect from asan", "")
            for r_name in self.routers:
                if r_name not in net.net.nameToNode:
                    continue
                net.net.nameToNode[r_name].check_asan()
            warnaln("   - collect from asan", "")
            erroraln("- collect result", "")
        net.stop_net()
        return res
    
if __name__ == "__main__":
    t = executor("/home/frr/topo-fuzz/test/topo_test/data/testConf/test1726036744.json", "/home/frr/topo-fuzz/test/topo_test/data/result", 20, 60)
    t.test()