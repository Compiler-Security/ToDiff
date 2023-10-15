from mininet.topo import Topo
from mininet.net import Mininet
from mininet.log import setLogLevel
class SingleSwitchTopo(Topo):
    def build(self, n=2):
        switch = self.addSwitch('s1')
        for h in range(n):
            host = self.addHost(f"r{h + 1}")
            self.addLink(host, switch)

def simpleTest():
    topo = SingleSwitchTopo(n=4)
    net = Mininet(topo)
    net.start()

    net.stop()

if __name__ == "__main__":
    setLogLevel('info')
    simpleTest()

# from mininet.topolib import LinearTopo
# tree4 = TreeTopo(depth=2,fanout=2)
# net = Mininet(topo=tree4)
# net.start()
# h1, h4  = net.hosts[0], net.hosts[3]
# h1.cmd('sudo /home/frr/frr/ospfd/ospfd -f /home/mininet/frr.conf -i /tmp/frr_h1.pid > /tmp/h1.out 2>&1')
# h4.cmd('sudo /home/frr/frr/ospfd/ospfd -f /home/mininet/frr.conf -i /tmp/frr_h2.pid > /tmp/h2.out 2>&1')
# net.stop()