from mininet.topo import Topo
from mininet.net import Mininet
from mininet.node import  Node
from mininet.log import setLogLevel
from src.restful_mininet.node.router_node import FrrNode
from src.restful_mininet.net.testnet import TestNet
from src.restful_mininet.exec.inst import MininetInst
from src.restful_mininet.util.log import *
import time
import time
from time import sleep
import os
from mininet import log
from os import path
import signal
import shutil
from mininet.cli import CLI
#export PYTHONPATH="$PYTHONPATH:$PWD"
BIN_DIR="/usr/lib/frr"
WORK_DIR = path.join(path.dirname(path.abspath(__file__)), "frr_conf")
class manulTest():
    def __init__(self, conf_file_dir):
        self.net = TestNet()
        self.ctx = {"intf":{}}
        self.conf_file_dir = conf_file_dir
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
    
    def _run_ospf(self, net:TestNet, router_name, ospf_commands):
        res = []
        for op in ospf_commands:
            if op in ["clear ip ospf process", "write terminal", "show ip ospf neighbor json", "show ip ospf", "show ip ospf interface json", "show ip ospf database"]:
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
    def _run_openfabric(self, net:TestNet, router_name, openfabric_commands):
        res = []
        for op in openfabric_commands:
            if op in ["write terminal", "show running-config","show openfabric neighbor detail json", "show openfabric interface detail json", "show openfabric route json", "show openfabric summary json"]:
                res.append(net.run_frr_cmds(router_name, [op]))
            else:
                resStr = ""
                sub_ops = op.split(";")
                ctx_op = sub_ops[0]
                #single ctx_op eg. router openfabric
                if (len(sub_ops) == 1):
                    res.append(net.run_frr_cmds( router_name, ['configure terminal', ctx_op]))
                else:
                    sub_ops = sub_ops[1:]
                    for sub_op in sub_ops:
                        r = net.run_frr_cmds(router_name, ['configure terminal', ctx_op, sub_op])
                        if r != "":
                            resStr += sub_op + "<-" + r + ";"
                    res.append(resStr)
        warnaln("   openfabric commands result:", res)
        print(res[0])
        return res

    def _run_isis(self, net:TestNet, router_name, isis_commands):
        res = []
        for op in isis_commands:
            if op in ["write terminal", "show running-config","show isis neighbor detail", "show isis interface detail", "show isis route json", "show isis summary"]:
                res.append(net.run_frr_cmds(router_name, [op]))
            else:
                resStr = ""
                sub_ops = op.split(";")
                ctx_op = sub_ops[0]
                #single ctx_op eg. router isis
                if (len(sub_ops) == 1):
                    res.append(net.run_frr_cmds( router_name, ['configure terminal', ctx_op]))
                else:
                    sub_ops = sub_ops[1:]
                    for sub_op in sub_ops:
                        r = net.run_frr_cmds(router_name, ['configure terminal', ctx_op, sub_op])
                        if r != "":
                            resStr += sub_op + "<-" + r + ";"
                    res.append(resStr)
        warnaln("   isis commands result:", res)
        print(res[0])
        return res
    def run_phy(self, phy_command):
        self._run_phy(self.net, self.ctx, [phy_command])

    def run_phys(self, phy_command):
        cs = [res.strip()  for res in phy_command.split("\n") if res.strip() != '']
        self._run_phy(self.net, self.ctx, cs)
    
    def run_ospf(self, router_name, ospf_command):
        self._run_ospf(self.net, router_name, [ospf_command])

    def run_ospfs(self, router_name, ospf_commands):
        self._run_ospf(self.net, router_name, ospf_commands)
    def run_isis(self, router_name, isis_command):
        self._run_isis(self.net, router_name, [isis_command])
    def run_openfabric(self, router_name, openfabric_command):
        self._run_openfabric(self.net, router_name, [openfabric_command])
    def stop(self):
        self.net.stop_net()

    # def save_run_info(self):
    #     for rname in self.net.router_nodes:
    #         r = self.net.get_node_by_name(rname)
    #         save_path = path.join(self.conf_file_dir, rname)
    #         os.makedirs(save_path, exist_ok=True)
    #         r.cmds_error(["cp", "-r", "/run/frr", save_path])

if __name__ == "__main__":
    # setLogLevel('info')
    # simpleTest()
    os.system("mn -c 2> /dev/null")
    h = manulTest()

    h.run_phys("""node r0 add
              node s0 add
              link r0-eth2 s0-eth0 add
              node r0 set OSPF up
              """)

    time.sleep(1)
    h.run_ospf("r0", "interface r0-eth2;ip ospf dead-interval 54315")
    #time.sleep(2)
    h.run_ospf("r0", "clear ip ospf process")
    #h.run_ospf("r0", "interface r0-eth2;ip ospf dead-interval minimal hello-multiplier 10")
    # h.run_ospf("r0", "interface r0-eth2;ip ospf dead-interval 1")
    #time.sleep(5)
    # h.run_phy("link r1-eth0 s1-eth0 remove")
    # time.sleep(1)
    # h.run_phy("link r1-eth0 s1-eth0 add")
