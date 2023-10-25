from src.restful_mininet.net.testnet import TestNet
from src.restful_mininet.node.router_node import FrrNode
from functools import partial, partialmethod
from os import path
from mininet.node import Node, Intf, Host
from mininet.link import Link

class BaseInst:
    EXEC_DONE = 0
    EXEC_MISS = 1
    EXEC_FAIL = 2
    INST_WRONG = 3

    def __init__(self, inst: str, net: TestNet, workdir: str):
        self.inst = inst
        self.net = net
        self.workdir = workdir

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
            #TODO
    def _run_cmd(self, f, *args, **kwargs):
        try:
            partial(f, args, kwargs)()
            return self.EXEC_DONE
        except BaseException as e:
            raise InstExecException(str(e))

    def _run_node_cmd(self, args):
        if len(args) < 3:
            raise InstErrorException("[mininet] node inst not right")
        node_name = args[0]
        op_args = args[1:]
        node: Node = self._get_node(node_name)
        if _cmds_equal_prefix(op_args, ["host", "add"]):
            if self._get_node(node_name) is not None:
                return self.EXEC_MISS
            ##FIXME partialmethod?
            return self._run_cmd(self.net.net.addHost, node_name)

        if _cmds_equal_prefix(op_args, ["host", "del"]):
            down_node = self._get_node(node_name)
            if down_node is not None:
                return self._run_cmd(self.net.net.delHost, down_node)
            return self.EXEC_MISS

        if _cmds_equal_prefix(op_args, ["switch", "add"]):
            if self._get_node(node_name) is not None:
                return self.EXEC_MISS
            return self._run_cmd(self.net.net.addSwitch, node_name)

        if _cmds_equal_prefix(op_args, ["switch", "del"]):
            down_node = self._get_node(node_name)
            if down_node is None:
                return self.EXEC_MISS
            return self._run_cmd(self.net.net.delSwitch, down_node)

        if _cmds_equal_prefix(op_args, ["router", "add"]):
            if self._get_node(node_name) is not None:
                return self.EXEC_MISS
            return self._run_cmd(self.net.net.addHost, node_name, cls=FrrNode)

        if _cmds_equal_prefix(op_args, ["router", "del"]):
            down_node = self._get_node(node_name)
            if down_node is None:
                return self.EXEC_MISS
            return self._run_cmd(self.net.net.delNode, down_node)

        if _cmds_equal_prefix(op_args, ["router", "set", "OSPF", "up"]):
            up_node: FrrNode = self._get_node(node_name)
            if up_node is None:
                return self.EXEC_MISS
            return self._run_cmd(up_node.load_frr, ["zebra", "ospfd"], path.join(self.workdir, node_name))

        if _cmds_equal_prefix(op_args, ["router", "set", "OSPF", "down"]):
            up_node: FrrNode = self._get_node(node_name)
            if up_node is None:
                return self.EXEC_MISS
            return self._run_cmd(up_node.stop_frr)

        raise InstErrorException("[mininet] node inst not right")

    def _run_intf_cmd(self, args):
        if len(args) < 3:
            raise InstErrorException("[mininet] intf inst not right")
        node_name = args[0]
        intf_name = args[1]
        op_args = args[2:]
        intf: Intf = self._get_intf(node_name, intf_name)
        _node: Node = self._get_node(node_name)
        if _cmds_equal_prefix(op_args, ["add"]):
            if intf is not None or _node is None:
                return self.EXEC_MISS
            # TODO we should use TCIntf in the future
            # FIXME check this intf down
            return self._run_cmd(_node.addIntf, Intf(intf_name, node=Node, up=False))

        if _cmds_equal_prefix(op_args, ["del"]):
            if intf is None:
                return self.EXEC_MISS
            self._run_cmd(_node.delIntf, intf)
            return self._run_cmd(intf.delete)

        if _cmds_equal_prefix(op_args, ["up"]):
            if intf is None:
                return self.EXEC_MISS
            return self._run_cmd(intf.ifconfig, "up")

        if _cmds_equal_prefix(op_args, ["down"]):
            if intf is None:
                return self.EXEC_MISS
            return self._run_cmd(intf.ifconfig, "down")

        if _cmds_equal_prefix(op_args, ["set", "ip"]):
            if len(op_args) != 3:
                raise InstErrorException("[mininet] intf inst set ip not right")
            str_ip = op_args[2]
            return self._run_cmd(intf.config, ip=str_ip)

        raise InstErrorException("[mininet] intf inst not right")

    def _run_link_cmd(self, args):
        if len(args) < 5:
            raise InstErrorException("[mininet] link inst not right")
        nodename1 = args[0]
        intfname1 = args[1]
        nodename2 = args[2]
        intfname2 = args[3]
        node1 = self._get_node(nodename1)
        node2 = self._get_node(nodename2)
        intf1 = self._get_intf(nodename1, intfname1)
        intf2 = self._get_intf(nodename2, intfname2)
        op_args = args[4:]
        self.net.net.addLink()
        if _cmds_equal_prefix(op_args, ["up"]):
            if intf1 is None or intf2 is None:
                return self.EXEC_MISS
            self.net.net.addLink()

        if _cmds_equal_prefix(op_args, ["down"]):
            pass

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


if __name__ == "__main__":
    net = TestNet()
    net.net.start()
    net.net.addHost("r1")
    net.net.addHost("r2")
    net.net.addLink("r1", "r2")
    node: Host = net.net["r1"]
    print(node.cmd("ifconfig"))
    net.net.stop()
