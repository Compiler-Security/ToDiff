import sys
from os import path
path_to_add = "/home/frr/a/topo-fuzz"
if path_to_add not in sys.path:
    sys.path.append(path_to_add)
from src.restful_mininet.net.testnet import TestNet
from src.restful_mininet.node.router_node import FrrNode
from functools import partial, partialmethod
from os import path
from mininet.node import Node, Intf, Host
from mininet.link import Link
from mininet.link import TCLink

class BaseInst:
    EXEC_DONE = 0
    EXEC_MISS = 1
    EXEC_FAIL = 2
    INST_WRONG = 3

    def __init__(self, inst: str, net: TestNet, workdir: str, ctx: dict):
        self.inst = inst
        self.net = net
        self.workdir = workdir
        self.ctx = ctx
    
    def run(self):
        pass


class InstErrorException(Exception):
    pass


class InstExecException(Exception):
    pass


def _cmds_equal_prefix(_input: list[str], expect: list[str]) -> bool:
    if len(_input) < len(expect):
        return False
    return _input[:len(expect)] == expect


class MininetInst(BaseInst):
    """
    node [node_name] [host|router|switch] [add|del|set]
        - router set
            - OSPF [up|down]
        - host set TODO
        - switch set TODO
    intf [node_name] [intf_name] [add|del|up|down|set]
        - set
            - net [ip]
    link [node1_name] [intf1_name] [node2_name] [intf2_name] [up|down|set]
        - set
            - TE TODO (see TCIntf config for TE)
    """

    def _get_node_type_from_name(self, name):
        if name[0] == "r":
            return "router"
        if name[0] == "s":
            return "switch"
        if name[0] == "h":
            return "host"

        raise InstErrorException("[mininet] node name not right")

    def _get_node_name_from_intf_name(self, name):
        l = name.split("-")
        if len(l) != 2:
            raise InstErrorException("[mininet] intf name not right")
        return l[0]

    def _get_node(self, node_name: str) -> [None | Node]:
        if node_name not in self.net.net:
            return None
        return self.net.net[node_name]

    def _get_intf(self, node_name: str, intf_name) -> [None | Intf]:
        _node = self._get_node(node_name)
        if _node is None:
            return None
        if intf_name not in _node.nameToIntf:
            return None
        return _node.intf(intf_name)

    def _get_link(self, intf1, intf2):
        for l in self.net.net.links:
            pass
            # TODO

    def _run_cmd(self, f, *args, **kwargs):
        try:
            partial(f, *args, **kwargs)()
            return self.EXEC_DONE
        except BaseException as e:
            raise InstExecException(str(e))

    def _run_node_cmd(self, args):
        if len(args) < 2:
            raise InstErrorException("[mininet] node inst not right")
        node_name = args[0]
        op_args = [self._get_node_type_from_name(node_name)] + args[1:]
        node: Node = self._get_node(node_name)
        if _cmds_equal_prefix(op_args, ["host", "add"]):
            if node is not None:
                return self.EXEC_MISS
            ##FIXME partialmethod?
            return self._run_cmd(self.net.net.addHost, node_name)

        if _cmds_equal_prefix(op_args, ["host", "del"]):
            down_node = node
            if down_node is not None:
                return self._run_cmd(self.net.net.delHost, down_node)
            return self.EXEC_MISS

        if _cmds_equal_prefix(op_args, ["switch", "add"]):
            if node is not None:
                return self.EXEC_MISS
            return self._run_cmd(self.net.net.addSwitch, node_name)

        if _cmds_equal_prefix(op_args, ["switch", "del"]):
            down_node = node
            if down_node is None:
                return self.EXEC_MISS
            return self._run_cmd(self.net.net.delSwitch, down_node)

        if _cmds_equal_prefix(op_args, ["router", "add"]):
            if node is not None:
                return self.EXEC_MISS
            return self._run_cmd(self.net.net.addHost, node_name, cls=FrrNode)

        if _cmds_equal_prefix(op_args, ["router", "del"]):
            down_node = node
            if down_node is None:
                return self.EXEC_MISS
            return self._run_cmd(self.net.net.delNode, down_node)

        if _cmds_equal_prefix(op_args, ["router", "set", "OSPF", "up"]):
            up_node: FrrNode = self._get_node(node_name)
            if up_node is None:
                return self.EXEC_MISS
            res = self._run_cmd(up_node.load_frr, ["zebra", "ospfd", "mgmtd"], conf_dir=self.workdir, universe=True)
            self._run_cmd(up_node.log_load_frr)
            return res
        
        if _cmds_equal_prefix(op_args, ["router", "set", "OSPF", "down"]):
            up_node: FrrNode = self._get_node(node_name)
            if up_node is None:
                return self.EXEC_MISS
            return self._run_cmd(up_node.stop_frr)
        
        if _cmds_equal_prefix(op_args, ["router", "set", "OSPF", "restart"]):
            up_node: FrrNode = self._get_node(node_name)
            if up_node is None:
                return self.EXEC_MISS
            return self._run_cmd(up_node.stop_frr)
        

        raise InstErrorException("[mininet] node inst not right")

    def _run_intf_cmd(self, args):
        if len(args) < 2:
            raise InstErrorException("[mininet] intf inst not right")
        intf_name = args[0]
        node_name = self._get_node_name_from_intf_name(intf_name)
        op_args = args[1:]
        intf: Intf = self._get_intf(node_name, intf_name)
        _node: Node = self._get_node(node_name)

        if _cmds_equal_prefix(op_args, ["up"]):
            if intf is None:
                return self.EXEC_MISS
            return self._run_cmd(intf.ifconfig, "up")

        if _cmds_equal_prefix(op_args, ["down"]):
            if intf is None:
                return self.EXEC_MISS
            return self._run_cmd(intf.ifconfig, "down")

        # if _cmds_equal_prefix(op_args, ["set", "ip"]):
        #     if len(op_args) != 3:
        #         raise InstErrorException("[mininet] intf inst set ip not right")
        #     str_ip = op_args[2]
        #     return self._run_cmd(intf.config, ip=str_ip)

        raise InstErrorException("[mininet] intf inst not right")

    def _save_intf_to_ctx(self, intf: Intf):
        self.ctx["intf"][intf.name]["mac"] = intf.mac
        # self.ctx["intf"][intf.name]["ip"] = intf.ip
        # if self._get_node_type_from_name(self._get_node_name_from_intf_name(intf.name)) != "switch":
        #     self.ctx["intf"][intf.name]["type"] = "L3"
        # else:
        #     self.ctx["intf"][intf.name]["type"] = "L2"

    def _load_intf_to_ctx(self, intf: Intf):
        if intf.name not in self.ctx["intf"]:
            return
        intf.setMAC(self.ctx["intf"][intf.name]["mac"])
        # if self.ctx["intf"][intf.name]["type"] == "L3":
        #     intf.setIP(self.ctx["intf"][intf.name]["ip"])

    def _get_pair_intf(self, intf: Intf, loc: str):
        p: Link = intf.link
        if loc == "L":
            return p.intf2
        else:
            return p.intf1

    def _run_link_cmd(self, args):
        if len(args) < 3:
            raise InstErrorException("[mininet] link inst not right")
        intfname1 = args[0]
        nodename1 = self._get_node_name_from_intf_name(intfname1)
        intfname2 = args[1]
        nodename2 = self._get_node_name_from_intf_name(intfname2)

        node1 = self._get_node(nodename1)
        node2 = self._get_node(nodename2)
        intf1: Intf = self._get_intf(nodename1, intfname1)
        intf2: Intf = self._get_intf(nodename2, intfname2)
        op_args = args[2:]

        if _cmds_equal_prefix(op_args, ["add"]):
            if node1 is None or node2 is None:
                return self.EXEC_MISS
            if (intf1 is not None) and (intf2 is not None):
                if self._get_pair_intf(intf1, "L") == intf2:
                    self.net.net.delLink(intf1.link)
                    l: Link = self.net.net.addLink(node1, node2, intfName1=intfname1, intfName2=intfname2, cls=TCLink)
                    self._load_intf_to_ctx(l.intf1)
                    self._load_intf_to_ctx(l.intf2)
                    return self.EXEC_DONE
            else:
                if intf1 is not None:
                    self._save_intf_to_ctx(intf1)
                    self._save_intf_to_ctx(self._get_pair_intf(intf1, "L"))
                    self.net.net.delLink(intf1.link)
                if intf2 is not None:
                    self._save_intf_to_ctx(intf2)
                    self._save_intf_to_ctx(self._get_pair_intf(intf2, "R"))
                    self.net.net.delLink(intf2.link)
                l: Link = self.net.net.addLink(node1, node2, intfName1=intfname1, intfName2=intfname2, cls=TCLink)
                self._load_intf_to_ctx(l.intf1)
                self._load_intf_to_ctx(l.intf2)
                return self.EXEC_DONE


        if _cmds_equal_prefix(op_args, ["del"]):
            if node1 is None or node2 is None:
                return self.EXEC_MISS
            if (intf1 is not None) and (intf2 is not None):
                if self._get_pair_intf(intf1, "L") == intf2:
                    self.net.net.delLink(intf1.link)
                    return self.EXEC_DONE
            return self.EXEC_MISS

        if _cmds_equal_prefix(op_args, ["up"]):
            if node1 is None or node2 is None:
                return self.EXEC_MISS
            if (intf1 is not None) and (intf2 is not None):
                if self._get_pair_intf(intf1, "L") == intf2:
                    #ATTENTION mininet has some bug, so we must set bw not to 0 in order to set loss to 0
                    intf1.config(bw = 1000, loss=0)
                    intf2.config(bw = 1000, loss=0)
                    return self.EXEC_DONE
            return self.EXEC_MISS
        
        if _cmds_equal_prefix(op_args, ["down"]):
            if node1 is None or node2 is None:
                return self.EXEC_MISS
            if (intf1 is not None) and (intf2 is not None):
                if self._get_pair_intf(intf1, "L") == intf2:
                    intf1.config(loss=100)
                    intf2.config(loss=100)
                    return self.EXEC_DONE
            return self.EXEC_MISS

        raise InstErrorException("[mininet] link inst not right")

    def run(self):
        inst_list = self.inst.split()
        try:
            if len(inst_list) <= 1:
                raise InstErrorException("[mininet] args not right")
            if inst_list[0] == "node":
                return self._run_node_cmd(inst_list[1:])
            elif inst_list[0] == "intf":
                return self._run_intf_cmd(inst_list[1:])
            elif inst_list[0] == "link":
                return self._run_link_cmd(inst_list[1:])

        except InstErrorException as e:
            return self.INST_WRONG, str(e)
        except InstExecException as e:
            return self.EXEC_FAIL, str(e)


class FRRInst(BaseInst):
    pass

from mininet.cli import CLI
if __name__ == "__main__":
    net = TestNet()
    ctx = {"intf":[]}
    MininetInst("node r1 add", net, "", ctx).run()
    MininetInst("node r2 add", net, "", ctx).run()
    print(MininetInst("link r1-eth0 r2-eth0 up", net, "", ctx).run())
    print(MininetInst("link r1-eth0 r2-eth0 down", net, "", ctx).run())
    # net.net.start()
    # net.net.addHost("r1")
    # net.net.addHost("r2")
    #net.net.addLink("r1", "r2")
    #net.net.host[2].addIntf
    # node: Host = net.net["r1"]
    # print(node.cmd("ifconfig"))
    net.net.start()
    CLI(net.net)
    print(net.net.hosts)
    net.net.stop()
