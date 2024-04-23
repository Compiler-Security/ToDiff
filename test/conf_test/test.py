import sys
from os import path
path_to_add = "/home/frr/a/topo-fuzz"
if path_to_add not in sys.path:
    sys.path.append(path_to_add)
from mininet.topo import Topo
from mininet.net import Mininet
from mininet.node import  Node
from mininet.log import setLogLevel
from src.restful_mininet.net import testnet
from src.restful_mininet.node.router_node import FrrNode
from src.restful_mininet.exec.inst import MininetInst
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

def inst_test():
    net = testnet.TestNet()
    ctx = {"intf":{}}
    MininetInst("node r1 add", net, "", ctx).run()
    MininetInst("node s1 add", net, "", ctx).run()
    MininetInst("node r2 add", net, "", ctx).run()
    print(MininetInst("link r1-eth0 s1-eth0 add", net, "", ctx).run())
    print(MininetInst("link r2-eth0 s1-eth1 add", net, "", ctx).run())
    print(MininetInst("node r1 set OSPF up", net, WORK_DIR, ctx).run())
    print(MininetInst("node r2 set OSPF up", net, WORK_DIR, ctx).run())
    try:
        net.start_net()
        # print(net.net.hosts)
        # sleep(5)
        # print(net.run_frr_cmd("r1", "show ip ospf"))
        # print(net.run_frr_cmd("r2", "show ip ospf"))
        # CLI(net.net)
        # print(MininetInst("link r1-eth0 r2-eth0 down", net, "", ctx).run())
        # CLI(net.net)
        # print(MininetInst("link r1-eth0 r2-eth0 up", net, "", ctx).run())
        # CLI(net.net)
        #print(MininetInst("intf r1-eth0 down", net, "", ctx).run())
        #CLI(net.net)
        #print(MininetInst("intf r1-eth0 up", net, "", ctx).run())
        CLI(net.net)
        net.stop_net()
    except BaseException as e:
        log.error(f"{e}\n")
        net.stop_net()

def inst_test2():
    phySt = """
	node r0 add
	node r2 add
	node r1 add
	node s0 add
	node s4 add
	node s1 add
	node s6 add
	node s5 add
	node s2 add
	node s7 add
	node s3 add
	link r1-eth1 s4-eth0 add
	link r2-eth1 s4-eth1 add
	link r1-eth2 s6-eth0 add
	link r1-eth0 s0-eth0 add
	link r0-eth1 s2-eth0 add
	link r0-eth3 s7-eth0 add
	link r2-eth2 s7-eth1 add
	link r2-eth0 s3-eth0 add
	link r0-eth2 s5-eth0 add
	link r0-eth0 s1-eth0 add
	node r0 set OSPF up
	node r2 set OSPF up
	node r1 set OSPF up
    """
    net = testnet.TestNet()
    ctx = {"intf":{}}
    for st in phySt.split('\n'):
        cmd = st.strip()
        print(MininetInst(cmd, net, WORK_DIR, ctx).run())
    net.start_net()
    CLI(net.net)
    net.stop_net()

if __name__ == "__main__":
    setLogLevel('info')
    #simpleTest()
    inst_test2()
