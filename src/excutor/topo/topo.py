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


class PhysTopo:
    def __init__(self):
        self.phys_intfs = {}
        self.phys_routes = {}
        # TODO self.phys_host..

    def add_intf(self, phys_route, phys_intf):
        pass


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


class Link(TopoObject):
    def __init__(self):
        super().__init__()
        self.typ = "Ethernet"
        self.target = None
        self.link_cond = None


class Intf(TopoObject):
    def __init__(self, name):
        super().__init__()
        self.name = name
        self.up = False
        self.net = None
        self.OSPFIntf = None
        self.attri = {
            "net": None
        }

    def halt(self):
        self.up = False

class OSPFIntf(TopoObject):
    def __init__(self, name):
        super().__init__()
        self.up = False
        self.name = name
        self.phyIntf = None
        self.attri = {
            "vrf": 0,
            "area": 0,
            "cost": 0
        }

    def halt(self):
        self.up = False


class Router(TopoObject):
    def __init__(self, name):
        super().__init__()
        self.name = name
        self.intfs = {}
        self.OSPFRouter = None
        self.attri = {}

    def halt(self):
        if self.OSPFRouter is not None:
            self.OSPFRouter.halt()
        for intf in self.intfs:
            intf.halt()

def get_intf_name(rname, intf_idx)->str:
    return f"{rname}-eth{intf_idx}"

class OSPF(TopoObject):
    def __init__(self, name, ir):
        super().__init__()
        self.ir:TopoIR = ir
        self._name = ""
        self._ospfIntfs = {}
        self._status = "down" #up #restart
        self._net = None

    def set_name(self):
        pass
    def set_status(self, status):
        if self._status == status:
            return
        if status == "down":
            pass
            #self.ir.emitDelRouter()
        self._status = status

    def halt(self):
        for ospf_intf in self.ospfIntfs.values():
            ospf_intf.halt()
        self.up = False




# class topoGen:
#     def __init__(self):
#         self.PhysRouters = {}
#         PhyRouter.ID = 0
#         self.PhysIntfs = {}
#         Intf.ID = 0
#         self.OSPFRouters = {}
#         OSPFRouter.ID = 0
#         self.OSPFIntfs = {}
#         OSPFIntf.ID = 0
#         self.IntIp = {}
#         IntIp.ID = 0
#
#         self.routes = {}
#         self.switchs = {}
#         self.hosts = {}
#
#     def gen_PhyRouter(self):
#         r = PhyRouter()
#         self.PhysRouters[r.id] = r
#         return r
#
#     def gen_OSPFRouter(self):
#         r = OSPFRouter()
#         self.OSPFRouters[r.id] = r
#         return r
#
#     def gen_PhyIntf(self):
#         r = PhyIntf()
#         self.PhysIntfs[r.id] = r
#         return r
#
#     def gen_OSPFIntf(self):
#         r = OSPFIntf()
#         self.OSPFIntfs[r.id] = r
#         return r
#
#     def gen_IntIp(self):
#         r = IntIp()
#         self.IntIp[r.id] = r
#         return r
#
#     def add_route(self, name):
#         r = self.gen_PhyRouter()
#         r.up = True
#         self.routes[name] = r
#
#     def del_route(self, name):
#         self.routes.pop(name)


class TopoIR:
    def emitAddRouter(self, rname):
        pass
    def emitDelRouter(self, rname):
        pass


class TopoGen:
    def __init__(self):
        self.routers = {}
        self.switches = {}
        self.hosts = {}
        self.ir = TopoIR()

    def auto_router_name(self):
        return f"r{len(self.routers) + 1}"

    def add_router(self, rname=None)->str:
        if rname is None:
            rname = self.auto_router_name()
        r = Router()
        r.name = rname
        self.routers[rname] = r
        self.ir.emitAddRouter(rname)
        return rname

    def del_router(self, rname=None)->str:
        if rname in self.routers:
            self.routers[rname].halt()
            self.routers.pop(rname)
            return rname
        self.ir.emitDelRouter(rname)
        return None

    def add_interface(self, rname)->str:
        pass

    def del_interface(self, rname)->str:
        pass

    def start_ospf(self, rname):
        assert(rname in self.routers)

    def stop_ospf(self, rname):
        assert(rname in self.routers)

    def set_ospf(self, rname):
        assert(rname in self.routers)

    def restart_ospf(self, rname):
        assert(rname in self.routers)

    def start_

    



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
