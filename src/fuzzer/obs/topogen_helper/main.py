from topo_build import get_topo
from frr_gen import dump_frr_confs, dump_test_script
from dot_gen import dump_topo_to_dot, dump_topo_to_pic
import conf
import pydot
from os import path
import os
import shutil
def generate(r_node, r2r_edge, output_dir):
    #if path.exists(output_dir):
    #    shutil.rmtree(output_dir)
    os.makedirs(output_dir, exist_ok=True)
    #get topo
    topo = get_topo(r_node, r2r_edge)

    #frr.conf
    dump_frr_confs(topo, output_dir)
    
    #test.py
    dump_test_script(topo, output_dir)
    
    #paint 
    conf.verbose = True
    dump_topo_to_dot(topo, output_dir, "topo_v.dot")
    dump_topo_to_pic(topo, output_dir, "topo_v.png")
    conf.verbose = False
    dump_topo_to_dot(topo, output_dir, "topo_s.dot")
    dump_topo_to_pic(topo, output_dir, "topo_s.png")

if __name__ == "__main__":
    r_node = [[x] for x in range(1, 3)]
    r2r_edge = [
        (1, 2)
    ]
    output_dir = "/home/topotest_bug/14056"

    generate(r_node, r2r_edge, output_dir)
