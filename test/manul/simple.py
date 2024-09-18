from mininet.topo import Topo
from mininet.net import Mininet
from mininet.node import  Node
from mininet.log import setLogLevel
from src.restful_mininet.node.router_node import FrrNode
from src.restful_mininet.net.testnet import TestNet
from src.restful_mininet.exec.inst import MininetInst
from src.restful_mininet.util.log import *
from time import sleep
import os
from mininet import log
from os import path
import signal
import shutil
from mininet.cli import CLI

BIN_DIR="/usr/lib/frr"
WORK_DIR = path.join(path.dirname(path.abspath(__file__)), "frr_conf")

def exit():
    os.kill(os.getpid(), signal.CTRL_C_EVENT)

def kill_pid(pid:int):
    try:
        os.kill(pid, 15)
    except ProcessLookupError:
        log.warn(f"pid {pid} not alive")
    except Exception as e:
        log.error(f"pid {pid} can't be killed")

#export PYTHONPATH="$PYTHONPATH:$PWD"
import functools
def simpleTest():
    topo = Topo()
    frr = FrrNode
    r1 = topo.addHost("r1", cls=frr)
    r2 = topo.addHost("r2", cls=frr)
    r3 = topo.addHost("r3", cls=frr)
    topo.addLink(r1, r2)
    topo.addLink(r2, r3)
    #build network
    net = Mininet(topo)
    net.nameToNode["r1"].load_frr(daemons=["zebra", "ospfd", "mgmtd"], conf_dir=WORK_DIR, universe = True)
    net.nameToNode["r2"].load_frr(daemons=["zebra", "ospfd", "mgmtd"], conf_dir=WORK_DIR, universe = True)
    net.nameToNode["r3"].load_frr(daemons=["zebra", "ospfd", "mgmtd"], conf_dir=WORK_DIR, universe = True)

    try:
        net.start()
        net.delLinkBetween(r1, r2)
        net.stop()
    except BaseException as e:
        log.error(f"{e}\n")
        net.stop()

class humanTest():
    def __init__(self):
        self.net = TestNet()
        self.ctx = {"intf":{}}
        self.conf_file_dir = WORK_DIR
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
    
    def run_phy(self, phy_command):
        self._run_phy(self.net, self.ctx, [phy_command])

    def stop(self):
        self.net.stop_net()
if __name__ == "__main__":
    # setLogLevel('info')
    # simpleTest()
    h = humanTest()
    h.run_phy("node r1 add")
    h.run_phy("node r1 set OSPF up")
