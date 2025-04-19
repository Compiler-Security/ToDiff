import util
from os import path
import os
import sys
import util
import subprocess
import re
#topo-fuzz/evaluate/coverage
coverage_dir = path.dirname(path.abspath(__file__))

def coverage_calc_single(coverage_file, source_file, func_list_path):
    func_lines, hit_line_count = util.get_mixed_line_counts(coverage_file, source_file)
    all_funcs = []
    with open(func_list_path, "r") as fp:
        for name in fp.readlines():
            all_funcs.append(name.strip())
    total_line = 0
    for name in all_funcs:
        if name in func_lines:
            total_line += func_lines[name]
    coverage = hit_line_count / total_line
    #print(f"{hit_line_count}:{total_line}, {coverage}")
    return coverage

def coverage_calc(coverage_dir, source_file, func_list_path, item):
    ave = 0
    s = 0
    for coverage_file in os.listdir(coverage_dir):
        if ".info" in coverage_file:
            ave += coverage_calc_single(f"{coverage_dir}/{coverage_file}", source_file, func_list_path)
            s += 1
    print(item, ave / s)

######OSPF TODiff##############
# coverage_file = f"{coverage_dir}/todiff/ospf/"
# source_file = "/home/frr/frr/ospfd/ospf_vty.c"
# func_list_path = f"{coverage_dir}/topo_related_function_ospf.txt"
# coverage_calc(coverage_file, source_file, func_list_path, "ToDiff OSPF")


#ISIS TODiff
coverage_file = f"{coverage_dir}/todiff/isis/"
source_file = "/home/frr/frr/isisd/isis_vty.c"
func_list_path = f"{coverage_dir}/topo_related_function_isis.txt"
coverage_calc(coverage_file, source_file, func_list_path, "ToDiff ISIS")

#######OSPF TopoTests#########
# coverage_file = f"{coverage_dir}/topotests/ospf/"
# source_file = "/home/frr/frr/ospfd/ospf_vty.c"
# func_list_path = f"{coverage_dir}/topo_related_function_ospf.txt"
# coverage_calc(coverage_file, source_file, func_list_path, "TopoTests OSPF")
# #######OSPF Fuzzing###########
# coverage_file = f"{coverage_dir}/ossfuzz/ospf/"
# source_file = "/home/frr/frr/ospfd/ospf_vty.c"
# func_list_path = f"{coverage_dir}/topo_related_function_ospf.txt"
# coverage_calc(coverage_file, source_file, func_list_path, "Fuzzing OSPF")