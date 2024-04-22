
from src.restful_mininet.net import testnet
from src.restful_mininet.api.inst import MininetInst
from os import path
from mininet.cli import CLI
import os
import json
import time
class executor:

    def __init__(self, conf_path, output_dir_str) -> None:
        self.conf_path = conf_path
        self.output_dir = output_dir_str
        with open(self.conf_path) as fp:
             self.conf = json.load(fp)
        self.step_nums = self.conf['step_nums']
        self.conf_name = self.conf['conf_name']
        self.round_num = self.conf['round_num']
        self.routers = self.conf['routers']
        os.makedirs(self.output_dir, exist_ok=True)
        self.tmp_file_dir = path.join(self.output_dir, 'tmp')
        os.makedirs(self.tmp_file_dir, exist_ok=True)
    
    def run_phy(self, net, ctx, phy_commands):
        res = []
        for op in phy_commands:
            res.append(MininetInst(op, net, self.tmp_file_dir, ctx).run())
        return res
    
    def run_ospf(self, net:testnet.TestNet, router_name, ospf_commands):
        res = []
        for op in ospf_commands:
            res.append(net.run_frr_cmds(router_name, ['configure terminal'] + op.split(";")))
        return res
    
    def init_ospf(self, router_name, ospf_commands):
        conf_name = f"{router_name}.conf"
        with open(path.join(self.tmp_file_dir, conf_name), 'w') as fp:
            for opa in ospf_commands:
                for op in opa.split(";"):
                    fp.write(op)
                    fp.write('\n')
          
    def test(self):
        res = {}
        start = time.time()
        res['result'] = []
        for i in range(0, self.round_num):
            res['result'].append(self.run(i))
        stop = time.time()
        res['total_test_time'] = stop - start
        self.conf['test'] = res
        result_path = path.join(self.output_dir, f"{self.conf_name}_res.json")
        with open(result_path, "w") as fp:
            json.dump(self.conf, fp)

    def run(self, r):
        net = testnet.TestNet()
        ctx = {"intf":{}}
        commands = self.conf['commands'][r]
        print(commands)
        res = []
        for i in range(0, self.step_nums[r]):
            print(i)
            ospf_res = {}
            if i == 0:
                for j in range(0, len(self.routers)):
                    router_name = self.routers[j]
                    ospf_ops = commands[i]['ospf'][j]
                    self.init_ospf(router_name, ospf_ops)
            else:
                for j in range(0, len(self.routers)):
                    router_name = self.routers[j]
                    ospf_ops = commands[i]['ospf'][j]
                    tmp = self.run_ospf(net, router_name, ospf_ops)
                    ospf_res[router_name] = tmp
    
            phy_res = self.run_phy(net, ctx, commands[i]['phy'])
            if i == 0:    
                net.start_net()
            res.append({})
            res[i]['exec'] = {}
            res[i]['exec']['phy'] = phy_res
            res[i]['exec']['ospf'] = ospf_res

            sleep_time = commands[i]['waitTime']
            if sleep_time == -1:
                #FIXME this should check shrink
                time.sleep(10)
            else:
                time.sleep(sleep_time)
            CLI(net.net)
            res[i]['watch'] = {}
            for r_name in self.routers:
                res[i]['watch'][r_name] = net.net.nameToNode[r_name].dump_info()
        net.stop_net()
        return res
    
if __name__ == "__main__":
    t = executor("/home/frr/a/topo-fuzz/test/excutor_test/frr_conf/all.conf", "/home/frr/a/topo-fuzz/test/excutor_test/frr_conf")
    t.test()