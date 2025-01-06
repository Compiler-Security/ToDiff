import os
import json
from os import path

#topo-fuzz/test/topo_test/
up = path.dirname
checkDir = path.join(up(up(path.abspath(__file__))), "data", "check")
import io

class humandReader():
    def __init__(self, file_path):
        self._loadConf(file_path)
        self.dumpDir = path.join(checkDir, self.test_name)
        os.makedirs(self.dumpDir, exist_ok=True)

    def _loadConf(self, file_path):
        self.file_path = file_path
        with open(file_path) as fp:
            self.data = json.load(fp)
        self.routers = self.data["routers"]
        self.round_num = self.data["round_num"]
        self.step_nums = self.data["step_nums"]
        self.test_name = self.data["conf_name"]
    
    def _watchOfPhy(self, rd, step):
        return self.data["commands"][rd][step]["phy"]
    

    def dump_to_file(self, name, st):
        with open(path.join(self.dumpDir, name), "w") as fp:
            fp.write(st)

    def readPhyOfRound(self, rd):
        res = io.StringIO()
        step_num = self.step_nums[rd]
        res.write(f"round {rd}, total_step{step_num}\n")
        for step in range(step_num):
            res.write(f"step {step}\n")
            for command in self._watchOfPhy(rd, step):
                res.write("\t" + command + "\n")
        
        new_name = path.basename(self.file_path)
        #test298239_phy_rd2.txt
        new_name = new_name.split(".")[0] + f"_phy_rd{rd}.txt"
        self.dump_to_file(new_name, res.getvalue())

    def readOspfOfRoundOfRouter(self, rd, rt):
        new_name = path.basename(self.file_path)
        #test298239_ospf_rd2_rt1.txt
        new_name = new_name.split(".")[0] + f"_ospf_rd{rd}_rt{rt}.txt"
        router_name = self.routers[rt]

        res = io.StringIO()
        step_nums = self.data["step_nums"][rd]
        res.write(f"round {rd}, router{rt}, total_step{step_nums}\n")
        for i in range(step_nums):
            res.write(f"step {i}\n")
            ospf_commands = self.data["commands"][rd][i]["ospf"][rt]
            if i == 0:
                commands_res = [""] * len(ospf_commands)
            else:
                commands_res = self.data["test"]["result"][rd][i]["exec"]["ospf"][router_name]
            for command, command_res in zip(ospf_commands, commands_res):
                for c in command.split(";"):
                    res.write(f"    {c}\n")
                res.write(f"        result: '{command_res}'\n\n")
        self.dump_to_file(new_name, res.getvalue())

    def readIsisOfRoundOfRouter(self, rd, rt):
        new_name = path.basename(self.file_path)
        #test298239_ospf_rd2_rt1.txt
        new_name = new_name.split(".")[0] + f"_isis_rd{rd}_rt{rt}.txt"
        router_name = self.routers[rt]

        res = io.StringIO()
        step_nums = self.data["step_nums"][rd]
        res.write(f"round {rd}, router{rt}, total_step{step_nums}\n")
        for i in range(step_nums):
            res.write(f"step {i}\n")
            isis_commands = self.data["commands"][rd][i]["isis"][rt]
            if i == 0:
                commands_res = [""] * len(isis_commands)
            else:
                commands_res = self.data["test"]["result"][rd][i]["exec"]["isis"][router_name]
            for command, command_res in zip(isis_commands, commands_res):
                for c in command.split(";"):
                    res.write(f"    {c}\n")
                res.write(f"        result: '{command_res}'\n\n")
        self.dump_to_file(new_name, res.getvalue())


    def readCore(self, router_name):
        new_name = path.basename(self.file_path)
        #test298239_ospf_core_rt1.txt
        new_name = new_name.split(".")[0] + f"_isis_core_rt{router_name[1:]}.txt"
        res = self.data["genInfo"]["core_commands"][router_name]
        self.dump_to_file(new_name, res)
    
    def readGraph(self):
        new_name = path.basename(self.file_path)
        #test32332_res_topo.dot
        new_name = new_name.split(".")[0] + f"topo.dot"
        res = self.data["genInfo"]["routerGraph"]
        self.dump_to_file(new_name, res)
    
    def readConfigGraph(self):
        new_name = path.basename(self.file_path)
        #test32332_res_topo.dot
        new_name = new_name.split(".")[0] + f"_config_graph.dot"
        res = self.data["genInfo"]["configGraph"]
        self.dump_to_file(new_name, res)


    def readAll(self):
        for rd in range(self.round_num):
            self.readPhyOfRound(rd)
        for rd in range(self.round_num):
            for rt in range(len(self.routers)):
                self.readIsisOfRoundOfRouter(rd, rt)
        for router_name in self.routers:
            self.readCore(router_name)
        self.readGraph()
        self.readConfigGraph()


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
import util
if __name__ == "__main__":
    #rd from 0
    #human_read_ospf(0, 0, "/home/frr/topo-fuzz/test/topo_test/data/result/test1726036744_r1/test1726036744_r1_res.json")
    test_name = util.get_test_name_5("49526")
    h = humandReader(util.get_result_file_path(test_name))
    # h = humandReader("/home/frr/topo-fuzz/test/topo_test/data/testConf/test1735885079.json")
    h.readAll()

    # h = humandReader("/home/frr/topo-fuzz/test/topo_test/data/testConf/test1735885079.json")
    # h.readPhyOfRound(1)