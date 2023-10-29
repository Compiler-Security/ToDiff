import time

from mininet.log import setLogLevel
from mininet.net import Mininet
from mininet.link import Intf, Link
from mininet.node import Node, OVSBridge, OVSSwitch
from mininet.topo import Topo
from mininet.cli import CLI

from src.restful_mininet.net.testnet import TestNet
from src.restful_mininet.node.router_node import FrrNode
from src.restful_mininet.util.log import infoaln, error

if __name__ == "__main__":
    setLogLevel('info')
    net = Mininet()
    net.addHost("r1")
    net.addHost("r2")
    r3 = net.addHost("r3")
    net.addSwitch("s1", cls=OVSSwitch)
    r1:Node = net["r1"]
    r2:Node = net["r2"]
    s1 = net["s1"]
    l = net.addLink(r1, s1)
    net.addLink(s1, r2)
    net.addLink(s1, r3)
    net.start()
    #r1.intfs[0].setIP("10.1.0.1/24")
    #r2.intfs[0].setIP("10.1.0.2/24")
    print(r1.intfs[0].ip, r2.intfs[0].ip)
    # r1_intf1:Intf = r1.intfs[0]
    # r1_intf1.setIP("10.0.0.1/24")
    # r2_intf1:Intf = r2.intfs[0]
    # r2_intf1.ifconfig("down")
    # print(r1.cmd("ip link show"))
    # r2_intf1.setIP("10.0.0.2/24")
    # print(r1.pid, r2.pid)
    # print(r1_intf1.ip, r2_intf1.ip)
    # net.delLink(l)
    # net.addLink("r1", "r2", 0)
    # r1_intf1: Intf = r1.intfs[0]
    # print(r1_intf1.ip, r2_intf1.ip)
    # #intf1 = Intf("r1-eth0", node=r1)
    # #r1.addIntf(intf1)
    CLI(net)
    net.stop()
    # net.stop()
    # if __name__ == "__main__":
    #     net = TestNet()
    #     try:
    #         setLogLevel('info')
    #         net.CONF_DIR = "/home/frr/topo-fuzz/test/simple/frr_conf"
    #         topo = Topo()
    #         r1 = topo.addHost("r1", cls=FrrNode)
    #         r2 = topo.addHost("r2", cls=FrrNode)
    #         r3 = topo.addHost("r3", cls=FrrNode)
    #         topo.addLink(r1, r2)
    #         topo.addLink(r2, r3)
    #         net.net = Mininet(topo)
    #         net.router_nodes = ["r1", "r2", "r3"]
    #         net.start_net()
    #         r1:Node = net.net["r1"]
    #         r2:Node = net.net["r2"]
    #         #infoaln("r2 eth0 down", r2.cmd("ip link set r2-eth0 down"))
    #         infoaln("r1 ip link show", r1.cmd("ip link show"))
    #         time.sleep(5)
    #         infoaln("r1 show ip ospf interface", net.run_frr_cmds("r1", ["show ip ospf interface"], False))
    #         infoaln("r2 eth0 down", r2.cmd("ip link set dev r2-eth0 down"))
    #         infoaln("r1 ip link show", r1.cmd("ip link show"))
    #         time.sleep(5)
    #         infoaln("r1 show ip ospf interface", net.run_frr_cmds("r1", ["show ip ospf interface"], False))
    #         #net.net.addLink("r1", "r2")
    #         #time.sleep(3)
    #         #infoaln("r1 ip link show", r1.cmd("ifconfig"))
    #         net.stop_net()
    #     except BaseException as e:
    #         error(f"\033[31merror\033[0m [{e}]\n")
    #         net.stop_net()

