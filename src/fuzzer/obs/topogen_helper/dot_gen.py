import networkx as nx
from topo_build import get_topo, r_node, r2r_edge
import conf


def str_label(l):
    x = ([f"{x[0]} {x[1]}" if x[0] != "" else f"{x[1]}" for x in l])
    return "\\n".join(x)

def dump_router_dot(data):
    rname = data["r_name"]
    l = [("", data["r_name"])]
    if conf.verbose:
        l.extend(list(data["data"].items()))
    label = str_label(l)

    return f"""{rname} [label="{label}",shape=doubleoctagon,fillcolor="#f08080",style=filled,];"""

def dump_routers_dot(g):
    router_dots = []
    for node_id, data in g.nodes(data=True):
        if data["r_type"] == "router":
            router_dots.append(dump_router_dot(data))
    return "\n".join(router_dots)

def dump_edge_dot(g:nx.Graph, src_id, dst_id, data):
    r1_name = g.nodes.get(src_id)["r_name"]
    r2_name = g.nodes.get(dst_id)["r_name"]
    area = data["data"]["area"]
    e_id = data["e_id"]
    cost = data["data"]["cost"]
    l = [("", f"{e_id}:{area}:{cost}")]
    if conf.verbose:
        l.extend(list(data["data"].items()))
    label = str_label(l)
    return f'{r1_name} -- {r2_name} [label = "{label}"];'

def dump_area_dot(g:nx.Graph, area_id, num):
    edges = []
    for src_id, dst_id, data in g.edges.data(True):
        if data['area'] == area_id:
            edges.append(dump_edge_dot(g, src_id, dst_id, data))
    edges_str = "\n".join(edges)
    return f"""subgraph cluster{num} {{ \n label="area {area_id}"\n {edges_str} \n }}"""

def dump_areas_dot(g):
    pass

def dump_links_dot(g:nx.Graph):
    edges = []
    for src_id, dst_id, data in g.edges.data(True):
        edges.append(dump_edge_dot(g, src_id, dst_id, data))
    edges_str = "\n".join(edges)
    return edges_str



from os import path
import pydot
def dump_topo_to_dot(g:nx.Graph, dir_path:str, file_name:str):
    routers_str = dump_routers_dot(g)
    links_str = dump_links_dot(g)
    st =  f"graph g{{ \n {routers_str} \n {links_str}  \n }}"
    with open(path.join(dir_path, file_name), "w") as fp:
        fp.write(st)

def dump_topo_to_pic(g:nx.Graph, dir_path:str, file_name:str):
    routers_str = dump_routers_dot(g)
    links_str = dump_links_dot(g)
    st =  f"graph g{{ \n {routers_str} \n {links_str}  \n }}"
    graph = pydot.graph_from_dot_data(st)[0]
    graph.write_png(path.join(dir_path, file_name))

if __name__ == "__main__":
    topo = get_topo(r_node, r2r_edge)
    print(dump_topo_to_dot(topo, "/home/topotests_for_bug/topogen_helper/output"))