import sys
from os import path
path_to_add = "/home/frr/a/topo-fuzz"
if path_to_add not in sys.path:
    sys.path.append(path_to_add)
import json

from mininet.net import Mininet
from mininet.topo import Topo
from src.restful_mininet.node.router_node import FrrNode
from src.restful_mininet.util.log import *
from os import path
import os
import shutil
class TestNet:
    def __init__(self):
        self.net = Mininet(Topo())
        self.router_nodes = []
        self.host_nodes = []
        self.switch_nodes = []
        self.CONF_DIR = ""
        self.log_path = "/home/frr/log"
        if path.exists(self.log_path):
            shutil.rmtree(self.log_path)
        assert (not path.exists(self.log_path))
        os.mkdir(self.log_path)
        
        

    def start_net(self):
        #first start net
        self.net.start()
        #then start frr
        # for node in self.router_nodes:
        #     #FIXME
        #     self._get_node_by_name(node).load_frr(daemons=["zebra", "ospfd"], conf_dir=self.CONF_DIR)
        #     self._get_node_by_name(node).log_load_frr()

    def stop_net(self):
        self.net.stop()

    def build_from_topo(self):
        #FIXME
        setLogLevel('info')
        self.CONF_DIR = "/home/frr/topo-fuzz/test/simple/frr_conf"
        topo = Topo()
        r1 = topo.addHost("r1", cls=FrrNode)
        r2 = topo.addHost("r2", cls=FrrNode)
        r3 = topo.addHost("r3", cls=FrrNode)
        topo.addLink(r1, r2)
        topo.addLink(r2, r3)
        self.net = Mininet(topo)
        self.router_nodes = ["r1", "r2", "r3"]

    def get_node_by_name(self, rname):
        assert(self.net.nameToNode.__contains__(rname))
        return self.net.nameToNode[rname]

    def run_frr_cmds(self, rname:str, commands, isjson=False):
        rnode: FrrNode = self.get_node_by_name(rname)
        assert (type(rnode) is FrrNode)
        st = rnode.daemon_cmds(commands)
        st = st.strip()
        if isjson:
            try:
                return json.loads(st)
            except ValueError as error:
                warn(f"vtysh_cmd: {rname}: failed to convert json output: {st}: {error}\n")
        return st

    def run_frr_cmd(self, rname:str, command, isjson=False):
        rnode:FrrNode = self.get_node_by_name(rname)
        assert (type(rnode) is FrrNode)
        st = rnode.daemon_cmd(command)
        st = st.strip()
        if isjson:
            try:
                return json.loads(st)
            except ValueError as error:
                warn(f"vtysh_cmd: {rname}: failed to convert json output: {st}: {error}\n")
        return st

    #mutate

if __name__ == "__main__":
    net = TestNet()
    try:
        net.build_from_topo()
        net.start_net()
        st = net.run_frr_cmds("r1", ["show ip ospf route json"], True)
        infoaln("r1 ospf route", st)
        net.stop_net()
    except BaseException as e:
        error(f"\033[31merror\033[0m [{e}]\n")
        net.stop_net()

import sys