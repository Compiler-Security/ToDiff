import util
import json
import copy
import os
import sys
from os import path
from mininet.cli import CLI
import time
path_to_add = path.dirname(path.dirname(path.dirname(path.dirname(path.abspath(__file__)))))
if path_to_add not in sys.path:
    sys.path.append(path_to_add)
from src.restful_mininet.exec.executorDe import executorDe
# class cursor:
#     def __init__(self):
#         self.step = 0
#         self.mod = "phy"
#         self.rt = 0
#         self.idx = 0

class reducer:
    def __init__(self, file_path):
        with open(file_path) as fp:
            self.data = json.load(fp)
        self.test_name = self.data["conf_name"]
        self.step_num = self.data["step_nums"][0]
        self.commands = self.data["commands"][0]
        self.rt_num = len(self.data["routers"])
    
    def dump_file(self, file_path, new_commands):
        data_new = {}
        data_new["conf_name"] = self.test_name
        data_new["step_nums"] = [self.step_num]
        data_new["routers"] = self.data["routers"]
        data_new["commands"] = [new_commands]
        data_new["round_num"] = 1
        with open(file_path, "w") as fp:
            json.dump(data_new, fp)
    
    def delete_one_command(self, commands, skip_num):
        skip = 0
        for step in range(0, self.step_num):
            for i in range(0, len(commands[step]["phy"])):
                #current we only del ospf command for safety
                continue
                skip += 1
                if (skip > skip_num):
                    commands[step]["phy"].pop(i)
                    return True
            for rt in range(0, self.rt_num):
                for i in range(0, len(commands[step]["isis"][rt])):
                    skip += 1
                    if (skip > skip_num):
                        commands[step]["isis"][rt].pop(i)
                        return True
        
    #abr in val
    def compareFunc(self, net):
        time.sleep(10)
        val = net.nameToNode["r0"].daemon_cmds(["show isis route json"])
        val = json.loads(val)
        res = False
        # 遍历路由表
        # 如果val是列表，遍历列表
        for item in val:
                level2 = item.get('level-2', {})
                if not level2:
                    continue
                print("check!")
                for route in level2.get('ipv4', []):
                    if route.get('prefix') == "130.64.0.0/13":
                        print(f"找到匹配前缀: {route['prefix']}")
                        return False
                            
        return True
    
    def reduce(self, minWaitTime, mxWaitTime, compareFunc):
        test_dir_path = path.join(util.checkDir, "reduce", self.test_name)
        os.makedirs(test_dir_path, exist_ok=True)
        skip_num = 0
        commands = self.commands
        print(commands)
        new_commands = copy.deepcopy(commands)
        delete_num = 0
        while(self.delete_one_command(new_commands, skip_num)):
            test_path = path.join(test_dir_path, f"{self.test_name}_tmp.json")
            self.dump_file(test_path, new_commands)
            net = executorDe(test_path, util.resultDir, minWaitTime, mxWaitTime).test()
            if self.compareFunc(net):
                commands = new_commands
                delete_num += 1
            else:
                skip_num += 1
            new_commands = copy.deepcopy(commands)
            if delete_num + skip_num > 0 and (delete_num + skip_num) % 1 == 0:
                back_path = path.join(test_dir_path, f"{self.test_name}_back{skip_num}.json")
                self.dump_file(back_path, new_commands)
        
        test_final_path = path.join(test_dir_path, f"{self.test_name}_final.json")
        self.dump_file(test_final_path, new_commands)

if __name__ == "__main__":
    r = reducer("/home/frr/topo-fuzz/test/topo_test/data/testConf/test1735961414.json")
    r.reduce(0, 30, None)

    # def _step_phy(self, cur: cursor, commands):
    #     if (cur.step >= self.step_num):
    #         return False
    #     idx = cur.idx + 1
    #     if (idx >= len(commands[cur.step]["phy"])):
    #         cur.mod = "ospf"
    #         cur.rt = 0
    #         cur.idx = 0
    #         return self._step_ospf(cur, commands)
    #     else:
    #         cur.idx = idx
    #         return True

    # def _step_ospf(self, cur: cursor, commands):
    #     if (cur.rt >= self.rt_num):
    #         cur.mod = "phy"
    #         cur.idx = 0
    #         cur.step += 1
    #         return self._step_phy(cur, commands)
    #     idx = cur.idx + 1
    #     if (idx >= len(commands[cur.step]["ospf"][cur.rt])):
    #         cur.rt += 1
    #         cur.idx = 0
    #         return self._step_ospf(cur, commands)
    #     else:
    #         cur.idx = idx
    #         return True


    # def step_one_command(self, cur: cursor, commands):
    #     if cur.mod == "phy":
    #         return self._step_phy(cur, commands)
    #     if cur.mod == "ospf":
    #         return self._step_ospf(cur, commands)

    