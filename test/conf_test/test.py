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
import json
def simpleTest():
    topo = Topo()
    frr = FrrNode
    r1 = topo.addHost("r1", cls=frr, ip = None)
    s1 = topo.addHost("s1", ip = None)
    topo.addLink(r1, s1)
    topo.addLink(r1, s1)
    topo.addLink(r1, s1)
    #build network
    net = Mininet(topo)
    net.nameToNode["r1"].load_frr(daemons=["zebra", "ospfd"], conf_dir=WORK_DIR, universe = True)
    try:
        net.start()
        with open('./test/conf_test/data/out.json', 'w') as f:
            data = net.nameToNode["r1"].dump_info_to_json()
            f.write(data)
        net.stop()
    except BaseException as e:
        log.error(f"{e}\n")
        net.stop()


if __name__ == "__main__":
    setLogLevel('info')
    simpleTest()
