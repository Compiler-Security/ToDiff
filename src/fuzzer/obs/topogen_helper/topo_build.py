import networkx as nx
from networkx import nx_pydot
from collections import namedtuple

s_node = [

]
r2s_edge = [

]

r_node = [
    [1],
    [2],
    [3]
]

r2r_edge = [
    (1, 3),
    (1, 2, {"area":"1", "cost":100}),
    (2, 3),
]


def get_complete_r_node_attr(r_id, attr_dict:dict):
    rnode_attr = {}
    rnode_attr["data"] = attr_dict
    if "router_id" not in rnode_attr["data"]:
        rnode_attr["data"]["router_id"] = f"172.16.0.{r_id}"
    if "r_id" not in rnode_attr:
        rnode_attr["r_id"] = int(r_id)
    if "r_name" not in rnode_attr:
        rnode_attr["r_name"] = f"r{int(r_id)}"
    if "r_type" not in rnode_attr:
        rnode_attr["r_type"] = "router"
    return rnode_attr

def impl_r_nodes(r_node_old:list):
    r_node_new = []
    for node in r_node_old:
        if len(node) == 1:
            r_node_new.append((node[0], get_complete_r_node_attr(node[0], {})))
        else:
            r_node_new.append((node[0], get_complete_r_node_attr(node[0], node[1])))
    return r_node_new

def get_complete_r2r_edge_attr(e_id, attr_dict:dict):
    r2r_edge_attr = {}
    r2r_edge_attr["data"] = attr_dict
    if "e_id" not in r2r_edge_attr:
        r2r_edge_attr["e_id"] = int(e_id)
    
    if "area" not in r2r_edge_attr["data"]:
        r2r_edge_attr["data"]["area"] = "0"
    
    if "cost" not in r2r_edge_attr["data"]:
        r2r_edge_attr["data"]["cost"] = 10
    
    if "subnet" not in r2r_edge_attr["data"]:
        r2r_edge_attr["data"]["subnet"] = f"10.0.{e_id}.0/24"
    
    if "type" not in r2r_edge_attr["data"]:
        r2r_edge_attr["data"]["type"] = "point-to-point"
    return r2r_edge_attr
    

def impl_r2r_edges(r2r_edge_old:list):
    r2r_edge_new = []
    for e_id, edge in enumerate(r2r_edge_old, 1):
        if len(edge) == 2:
            r2r_edge_new.append((edge[0], edge[1], get_complete_r2r_edge_attr(e_id, {})))
        else:
            r2r_edge_new.append((edge[0], edge[1], get_complete_r2r_edge_attr(e_id, edge[2])))
    return r2r_edge_new

def get_topo(r_nodes:list, r2r_edges:list, s_nodes = None, r2s_edges = None):
    #current we don't consider s_nodes and r2s_edges (switch)
    g = nx.Graph()
    r_nodes_impl = []

    r_nodes_impl = impl_r_nodes(r_nodes)
    g.add_nodes_from(r_nodes_impl)
    
    r2r_edges_impl = impl_r2r_edges(r2r_edges)
    g.add_edges_from(r2r_edges_impl)

    return g

#g = get_topo(r_node, r2r_edge)
#print(g.edges.data(True))
#nx_pydot.write_dot(g, "a.dot")