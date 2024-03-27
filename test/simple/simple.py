from mininet.topo import Topo
from mininet.net import Mininet
from mininet.node import  Node
from mininet.log import setLogLevel
from src.restful_mininet.node.router_node import FrrNode
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
    net.nameToNode["r1"].load_frr(daemons=["zebra", "ospfd"], conf_dir=WORK_DIR, universe = True)
    net.nameToNode["r2"].load_frr(daemons=["zebra", "ospfd"], conf_dir=WORK_DIR)
    net.nameToNode["r3"].load_frr(daemons=["zebra", "ospfd"], conf_dir=WORK_DIR)
    try:
        net.start()
        print(net.nameToNode["r1"].daemon_cmds(["show ip ospf json"]))
        sleep(10)
        print(net.nameToNode["r1"].daemon_cmds(["show ip ospf route"]))
        net.delLinkBetween(net.nameToNode["r1"],  net.nameToNode["r2"])
        sleep(10)
        print(net.nameToNode["r1"].daemon_cmds(["show ip ospf route"]))
        net.stop()
    except BaseException as e:
        log.error(f"{e}\n")
        net.stop()


if __name__ == "__main__":
    setLogLevel('info')
    simpleTest()
