from mininet.node import Node
from mininet.log import setLogLevel
from mininet.topo import Topo
from mininet.net import Mininet
from mininet.cli import CLI

class FrrNode(Node):
    def __init__(self, name, **params):
        params["privateDirs"] = ["/run/frr"]
        super().__init__(name, **params)


if __name__ == "__main__":
    setLogLevel('info')
    topo = Topo()
    r1 = topo.addHost("r1", cls=FrrNode)
    r2 = topo.addHost("r2", cls=FrrNode)
    r3 = topo.addHost("r3", cls=FrrNode)
    topo.addLink(r1, r2)
    topo.addLink(r2, r3)
    net = Mininet(topo)
    CLI(net)
