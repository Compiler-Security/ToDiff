import sys
import traceback
from os import path
path_to_add = path.dirname(path.dirname(path.dirname(path.dirname(path.abspath(__file__)))))
if path_to_add not in sys.path:
    sys.path.append(path_to_add)

from src.restful_mininet.net import testnet
from src.restful_mininet.exec.inst import MininetInst
from src.restful_mininet.util.log import *
from src.restful_mininet.exec.executor import executor
from mininet.cli import CLI
import os
import json
import time
import re

class executorPath(executor):
    def __init__(self, conf_path, output_dir_str, minWaitTime, maxWaitTime, protocol):
        super().__init__(conf_path, output_dir_str, minWaitTime, maxWaitTime, protocol)
        assert self.conf["conf_type"] == "diffPath"

    def _run_for_ospf(self, r):
        erroraln(f"\n\n======round{r}======","")
        erroraln("+ mininet init", "")
        net = testnet.TestNet()
        erroraln("- mininet init", "")
        ctx = {"intf":{}}
        commands = self.conf['commands'][r]
        res = []
        for i in range(0, self.step_nums[r]):
            erroraln(f"\n\n>>>> + step{i} <<<<", "")
            
            
            ospf_res = {}
            self.routers = commands[i]["routers"]

            if len(commands[i]["initConf"]) > 0:
                erroraln(f"+ Override OSPF conf", "")
                for router_name in commands[i]["initConf"]:
                    ospf_ops = commands[i]['ospf'][router_name]
                    self._init_ospf(router_name, ospf_ops)
                erroraln(f"- Override OSPF conf", "")
                
            erroraln(f"+ PHY commands", "")
            phy_res = self._run_phy_commands(net, ctx, commands[i]['phy'])
            erroraln(f"- PHY commands", "")

            erroraln(f"+ OSPF commands", "")
            for j in range(len(self.routers) -1, -1, -1):
                router_name = self.routers[j]
                if router_name in commands[i]["initConf"]: continue
                ospf_ops = commands[i]['ospf'][router_name]
                tmp = self._run_ospf_commands(net, router_name, ospf_ops)
                ospf_res[router_name] = tmp
                r = net.get_node_by_name(router_name)
                r._save_frr_conf()
            erroraln(f"- OSPF commands", "")
            
            if i == 0:    
                net.start_net()
            res.append({})
            res[i]['exec'] = {}
            res[i]['exec']['phy'] = phy_res
            res[i]['exec']['ospf'] = ospf_res
            
            sleep_time = commands[i]['waitTime']
            erroraln(f"wait {sleep_time} s ", "")
            
            if sleep_time == -1:
                #handle convergence
                    #min(_check_convergence() + minWaitTime, maxWaitTime)
                    #for simplicity, maxWaitTime % minWaitTime == 0
                CLI(net.net)
                erroraln("+ check convergence", "")
                begin_t = time.time()
                while True:
                    if self._check_converge_ospf(net):
                        time.sleep(self.minWaitTime)
                        res[i]['exec']['convergence'] = True
                        warnaln("   + convergence!", "")
                        time.sleep(100)
                        break
                    else:
                        if time.time() - begin_t >= self.maxWaitTime:
                            res[i]['exec']['convergence'] = False
                            warnaln("   + not convergence!", "")
                            break
                        else:
                            time.sleep(10)
            else:
                #CLI(net.net)
                time.sleep(sleep_time)
            erroraln("+ collect result", "")
            warnaln("   + collect from daemons", "")
            res[i]['watch'] = {}
            for r_name in self.routers:
                res[i]['watch'][r_name] = net.net.nameToNode[r_name].dump_info_ospf()
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

    def _run_for_rip(self, r):
        erroraln(f"\n\n======round{r}======","")
        erroraln("+ mininet init", "")
        net = testnet.TestNet()
        erroraln("- mininet init", "")
        ctx = {"intf":{}}
        commands = self.conf['commands'][r]
        res = []
        for i in range(0, self.step_nums[r]):
            erroraln(f"\n\n>>>> + step{i} <<<<", "")
            
            
            ospf_res = {}
            self.routers = commands[i]["routers"]

            if len(commands[i]["initConf"]) > 0:
                erroraln(f"+ Override RIP conf", "")
                for router_name in commands[i]["initConf"]:
                    ospf_ops = commands[i]['rip'][router_name]
                    self._init_rip(router_name, ospf_ops)
                erroraln(f"- Override RIP conf", "")
                
            erroraln(f"+ PHY commands", "")
            phy_res = self._run_phy_commands(net, ctx, commands[i]['phy'])
            erroraln(f"- PHY commands", "")

            erroraln(f"+ RIP commands", "")
            for j in range(len(self.routers) -1, -1, -1):
                router_name = self.routers[j]
                if router_name in commands[i]["initConf"]: continue
                ospf_ops = commands[i]['rip'][router_name]
                tmp = self._run_rip_commands(net, router_name, ospf_ops)
                ospf_res[router_name] = tmp
                r = net.get_node_by_name(router_name)
                r._save_frr_conf()
            erroraln(f"- RIP commands", "")
            
            if i == 0:    
                net.start_net()
            res.append({})
            res[i]['exec'] = {}
            res[i]['exec']['phy'] = phy_res
            res[i]['exec']['ospf'] = ospf_res
            
            sleep_time = commands[i]['waitTime']
            erroraln(f"wait {sleep_time} s ", "")
            
            if sleep_time == -1:
                #handle convergence
                    #min(_check_convergence() + minWaitTime, maxWaitTime)
                    #for simplicity, maxWaitTime % minWaitTime == 0
                time.sleep(30)
            else:
                #CLI(net.net)
                time.sleep(sleep_time)
            erroraln("+ collect result", "")
            warnaln("   + collect from daemons", "")
            res[i]['watch'] = {}
            for r_name in self.routers:
                res[i]['watch'][r_name] = net.net.nameToNode[r_name].dump_info_ospf()
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

    def _run_for_babel(self, r):
        erroraln(f"\n\n======round{r}======","")
        erroraln("+ mininet init", "")
        net = testnet.TestNet()
        erroraln("- mininet init", "")
        ctx = {"intf":{}}
        commands = self.conf['commands'][r]
        res = []
        for i in range(0, self.step_nums[r]):
            erroraln(f"\n\n>>>> + step{i} <<<<", "")
            
            
            ospf_res = {}
            self.routers = commands[i]["routers"]

            if len(commands[i]["initConf"]) > 0:
                erroraln(f"+ Override BABEL conf", "")
                for router_name in commands[i]["initConf"]:
                    ospf_ops = commands[i]['babel'][router_name]
                    self._init_rip(router_name, ospf_ops)
                erroraln(f"- Override BABEL conf", "")
                
            erroraln(f"+ PHY commands", "")
            phy_res = self._run_phy_commands(net, ctx, commands[i]['phy'])
            erroraln(f"- PHY commands", "")

            erroraln(f"+ BABEL commands", "")
            for j in range(len(self.routers) -1, -1, -1):
                router_name = self.routers[j]
                if router_name in commands[i]["initConf"]: continue
                ospf_ops = commands[i]['babel'][router_name]
                tmp = self._run_babel_commands(net, router_name, ospf_ops)
                ospf_res[router_name] = tmp
                r = net.get_node_by_name(router_name)
                r._save_frr_conf()
            erroraln(f"- BABEL commands", "")
            
            if i == 0:    
                net.start_net()
            res.append({})
            res[i]['exec'] = {}
            res[i]['exec']['phy'] = phy_res
            res[i]['exec']['ospf'] = ospf_res
            
            sleep_time = commands[i]['waitTime']
            erroraln(f"wait {sleep_time} s ", "")
            
            if sleep_time == -1:
                #handle convergence
                    #min(_check_convergence() + minWaitTime, maxWaitTime)
                    #for simplicity, maxWaitTime % minWaitTime == 0
                time.sleep(30)
            else:
                #CLI(net.net)
                time.sleep(sleep_time)
            erroraln("+ collect result", "")
            warnaln("   + collect from daemons", "")
            res[i]['watch'] = {}
            for r_name in self.routers:
                res[i]['watch'][r_name] = net.net.nameToNode[r_name].dump_info_ospf()
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

    def test(self):
        #FIXME we should add other process
        self.run_pocess = {
            "ospf": self._run_for_ospf,
            "isis": self._run_for_isis,
            "rip": self._run_for_rip,
            "babel": self._run_for_babel,
            "openfabric": self._run_for_openfabric
        }
        try:
            res = {}
            start = time.time()
            res['result'] = []
            for i in range(1, self.round_num):
                # here is isis or ospf
                # isis: _run_for_isis   ospf: _run
                res['result'].append(self.run_pocess[self.protocol](i))
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
if __name__ == "__main__":
    t = executorPath("/home/frr/topo-fuzz/test/topo_test/data/testConf/test1753223207.json", "/home/frr/topo-fuzz/test/topo_test/data/result", 1, 300, "ospf")
    t.test()