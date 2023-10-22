import networkx as nx
from typing import TypedDict
from src.excutor.cmd.cmd import Conf


class Node:
    def __init__(self, name):
        self.name = name
        self.type = "node"


class RouterNode(Node):
    def __init__(self, name):
        super().__init__(name)
        self.type = "router"


class FRRNode(RouterNode):
    def __init__(self, name):
        super().__init__(name)
        self.rtype = "frr"
        self.daemons = []
        self.confs: dict[str, Conf] = {}
        self.daemons_dict = {}

    def __str__(self):
        attributes = ", ".join(f"{key}={value}" for key, value in vars(self).items())
        return f"{self.__class__.__name__}({attributes})"


"""
TopoNode{
mininet: FRRNode/SwitchNode/...,
net: {ip:..., intf:...}
}
"""


class TopoObject:
    ID = 0
    def __init__(self):
        self.id = TopoObject.ID
        TopoObject.ID += 1


class IntIp(TopoObject):
    def __init__(self):
        super().__init__()
        self.ip = "0.0.0.0"
        self.seg = 0


class LinkCond(TopoObject):
    def __init__(self):
        super().__init__()
        self.up = False


class PhyConnect(TopoObject):
    def __init__(self):
        super().__init__()
        self.typ = "Ethernet"
        self.target = None
        self.link_cond = None

class OSPFIntf(TopoObject):
    def __init__(self):
        super().__init__()
        self.vrf = 0
        self.phyIntf = None
        self.up = False
        self.area = 0
        self.cost = 0



class PhyIntf(TopoObject):
    def __init__(self):
        super().__init__()
        self.name = ""
        self.up = False
        self.net = IntIp()
        self.OSPFIntf= None

class PhyRouter(TopoObject):
    def __init__(self):
        self.name = ""
        self.up = False
        self.OSPFRouter = None

class OSPFRouter(TopoObject):
    def __init__(self):
        super().__init__()
        self.up = False
        self.net = IntIp()
        self.up = False



class PhysTopo:
    def __init__(self):
        self.phys_intfs = {}
        self.phys_routes = {}
        # TODO self.phys_host..

    def add_intf(self, phys_route, phys_intf):
        pass


class TopoGen:
    def __init__(self):
        self.PhysRouters = {}
        PhyRouter.ID = 0
        self.PhysIntfs = {}
        PhyIntf.ID = 0
        self.OSPFRouters = {}
        OSPFRouter.ID = 0
        self.OSPFIntfs = {}
        OSPFIntf.ID = 0
        self.IntIp = {}
        IntIp.ID = 0


        self.routes = {}
        self.switchs = {}
        self.hosts = {}

    def gen_PhyRouter(self):
        r = PhyRouter()
        self.PhysRouters[r.id] = r
        return r

    def gen_OSPFRouter(self):
        r = OSPFRouter()
        self.OSPFRouters[r.id] = r
        return r

    def gen_PhyIntf(self):
        r = PhyIntf()
        self.PhysIntfs[r.id] = r
        return r

    def gen_OSPFIntf(self):
        r = OSPFIntf()
        self.OSPFIntfs[r.id] = r
        return r

    def gen_IntIp(self):
        r = IntIp()
        self.IntIp[r.id] = r
        return r


    def add_route(self, name):
        r = self.gen_PhyRouter()
        r.up = True
        self.routes[name] = r

    def del_route(self, name):
        self.routes.pop(name)

class Topo:
    def __init__(self):
        self.graph = nx.MultiGraph()

    def add_frr_route(self, route: FRRNode):
        self.graph.add_node(route.name, net=route)
        a = self.graph.nodes[route.name]["mininet"]
        print(a)


if __name__ == "__main__":
    t = Topo()
    t.add_frr_route(FRRNode("r1"))
