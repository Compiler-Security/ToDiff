import json
import os
class rm:
    def __init__(self, file_path):
        with open(file_path) as fp:
            self.data = json.load(fp)
        self.test_name = self.data["conf_name"]
        self.step_num = self.data["step_nums"]
        self.commands = self.data["commands"]
        self.rt_num = len(self.data["routers"])
        self.rd_num = self.data["round_num"]
    
    def dump_file(self, file_path):
        data_new = {}
        data_new["conf_name"] = self.test_name + "0000"
        data_new["step_nums"] = self.step_num
        data_new["routers"] = self.data["routers"]
        data_new["commands"] = self.commands
        data_new["round_num"] = self.rd_num
        with open(file_path, "w") as fp:
            json.dump(data_new, fp)

    def listin(self, command, lst):
        for st in lst:
            if st in command:
                return True
        return False
    def should_delete(self, command):
        return self.listin(command, ["isis hello-interval", "isis hello-multiplier"])
    
    def delete_one_command(self):
        for rd in range(0, self.rd_num):
            for step in range(0, self.step_num[rd]):
                for rt in range(0, self.rt_num):
                    for i in range(0, len(self.commands[rd][step]["isis"][rt])):
                        if (self.should_delete(self.commands[rd][step]["isis"][rt][i])):
                            self.commands[rd][step]["isis"][rt].pop(i)
                            return True
        return False

    def delete(self):
        while self.delete_one_command():
            pass

for file_name in os.listdir("/home/frr/topo-fuzz/test/topo_test/data/testConf"):
    file_path = os.path.join("/home/frr/topo-fuzz/test/topo_test/data/testConf", file_name)
    r = rm(file_path)
    r.delete()
    r.dump_file(os.path.join("/home/frr/topo-fuzz/test/topo_test/data/testConf", file_name.split(".")[0]) + "0000.json")
