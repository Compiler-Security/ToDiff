import os
import json
from os import path

#topo-fuzz/test/topo_test/
up = path.dirname
checkDir = path.join(up(up(path.abspath(__file__))), "data", "check")
import io
def human_read_ospf(rd, rt, file_path):
    with open(file_path) as fp:
        data = json.load(fp)
    new_name = path.basename(file_path)
    #test298239_ospf_rd2_rt1.txt
    new_name = new_name.split(".")[0] + f"_ospf_rd{rd}_rt{rt}.txt"
    router_name = f"r{rt}"

    res = io.StringIO()
    step_nums = data["step_nums"][rd]
    res.write(f"round {rd}, router{rt}, total_step{step_nums}\n")
    for i in range(step_nums):
        res.write(f"step {i}\n")
        ospf_commands = data["commands"][rd][i]["ospf"][rt]
        if i == 0:
            commands_res = [""] * len(ospf_commands)
        else:
            commands_res = data["test"]["result"][rd][i]["exec"]["ospf"][router_name]
        for command, command_res in zip(ospf_commands, commands_res):
            for c in command.split(";"):
                res.write(f"    {c}\n")
            res.write(f"        result: '{command_res}'\n\n")
    with open(path.join(checkDir, new_name), "w") as fp:
        fp.write(res.getvalue())


# def human_read_phy(rd, rt, file_path):
#     with open(file_path) as fp:
#         data = json.load(fp)
#     new_name = path.basename(file_path)
#     #test298239_ospf_rd2_rt1.txt
#     new_name = new_name.split(".")[0] + f"_ospf_rd{rd}_rt{rt}.txt"
#     router_name = f"r{rt}"

#     res = io.StringIO()
#     step_nums = data["step_nums"][rd]
#     res.write(f"round {rd}, router{rt}, total_step{step_nums}\n")
#     for i in range(step_nums):
#         res.write(f"step {i}\n")
#         phy_commands = data["commands"][rd][i]["phy"][rt]
#         if i == 0:
#             commands_res = [""] * len(phy_commands)
#         else:
#             commands_res = data["test"]["result"][rd][i]["exec"]["ospf"][router_name]
#         for command, command_res in zip(phy_commands, commands_res):
#             for c in command.split(";"):
#                 res.write(f"    {c}\n")
#             res.write(f"        result: '{command_res}'\n\n")
#     with open(path.join(checkDir, new_name), "w") as fp:
#         fp.write(res.getvalue())

if __name__ == "__main__":
    #rd from 0
    human_read_ospf(0, 0, "/home/frr/topo-fuzz/test/topo_test/data/result/test1726036744_r1/test1726036744_r1_res.json")