import networkx as nx
from topo_build import get_topo, r_node, r2r_edge
from dot_gen import dump_topo_to_dot
import conf
import os
import re
from os import path
import pydot
CMD = {
    "router_id":"  ospf router-id {}".format,
    "area": "  ip ospf area {}".format,
    "network type": "  ip ospf network {}".format,
    "cost": "  ip ospf cost {}".format,
    "subnet": "  ip address {}".format,
    "iterface": "interface {}-eth{}".format,
}

def cmd_interface(cmd_list:list, data, r_name, port_num, r_id):
    cmd_list.append(CMD["iterface"](r_name, port_num))
    subnet = data["subnet"]
    assert("/" in subnet)
    x = subnet.split("/")[0].split(".")
    x[-1] = str(r_id)
    subnet = ".".join(x) + "/" + subnet.split("/")[1]
    cmd_list.append(CMD["subnet"](subnet))
    cmd_list.append(CMD["cost"](data["cost"]))
    cmd_list.append(CMD["area"](data["area"]))
    cmd_list.append(CMD["network type"](data["type"]))
    
def cmd_router_ospf(cmd_list:list, data):
    cmd_list.append("router ospf")
    cmd_list.append(CMD["router_id"](data["router_id"]))

def to_frr_conf(topo:nx.Graph, router_id):
    data = topo.nodes.get(router_id)
    r_id = data["r_id"]
    r_name = data["r_name"]
    links = list(topo.edges(router_id, data=True))
    links = [x[2] for x in links]
    links.sort(key=lambda x: x['e_id'])
    cmd_list = []
    
    for i, link_data in enumerate(links):
        cmd_interface(cmd_list, link_data["data"], r_name, i, r_id)
    
    cmd_router_ospf(cmd_list, data["data"])
    
    return "\n".join(cmd_list)
    
def dump_frr_confs(topo:nx.Graph, dump_dir:str):
    for node_id, data in topo.nodes(data=True):
        if data["r_type"] == "router":
            r_name = data["r_name"]
            frr_conf_str = to_frr_conf(topo, node_id)
            frr_dir = path.join(dump_dir, r_name)
            os.makedirs(frr_dir, exist_ok=True)
            with open(path.join(frr_dir, "frr.conf"), "w") as fp:
                fp.write(frr_conf_str)

def dump_test_script(topo:nx.Graph, dump_dir:str):
    cmd_list = []
    for _, data in topo.nodes(data=True):
        rname = data["r_name"]
        cmd_list.append(f'{rname} = tgen.add_router("{rname}")')
    for src_id, dst_id, data in topo.edges(data=True):
        r1_name = topo.nodes.get(src_id)["r_name"]
        r2_name = topo.nodes.get(dst_id)["r_name"]
        cmd_list.append(f"tgen.add_link({r1_name}, {r2_name})")

    os.makedirs(dump_dir, exist_ok=True)
    with open(path.join(dump_dir, "test.txt"), "w") as fp:
            fp.write("\n".join(cmd_list))



if __name__ == "__main__":
    r_node = [[x] for x in range(1, 4)]
    r2r_edge = [
        (1, 3),
        (1, 2),
        (2, 3, {"area":1}),
    ]
    output_dir = "/home/frr/frr/tests/topotests/14071_generate"
    topo = get_topo(r_node, r2r_edge)
    dump_frr_confs(topo, output_dir)
    dump_test_script(topo, output_dir)
    conf.verbose = True
    dump_topo_to_dot(topo, output_dir, "topo_v.dot")
    graph = pydot.graph_from_dot_file(path.join(output_dir, "topo_v.dot"))[0]
    graph.write_png(path.join(output_dir, "topo_v.png"))
    conf.verbose = False
    dump_topo_to_dot(topo, output_dir, "topo_s.dot")
    graph = pydot.graph_from_dot_file(path.join(output_dir, "topo_s.dot"))[0]
    graph.write_png(path.join(output_dir, "topo_s.png"))