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
        return self.listin(command, ["isis psnp-interval", "isis csnp-interval"])
    
    def should_modify(self, command):
        return self.listin(command, ["isis hello-interval","isis hello-multiplier", "lsp-gen-interval","spf-interval"])


    def modify_intervals(self, command):
        commands = command.split(';')
        modified_commands = []
        
        target_commands = [
            "isis hello-interval",
            "isis hello-multiplier",
            "isis psnp-interval", 
            "isis csnp-interval",
            "lsp-gen-interval",
            "spf-interval"
        ]
        
        for cmd in commands:
            cmd = cmd.strip()
            
            for target in target_commands:
                if target in cmd:
                    import re
                    # 使用更精确的正则表达式模式处理三种情况
                    patterns = [
                        f"({target})\s+(\d+)(\s+level-[12])?",    # 模式1: isis xxx 1 level-2 或 isis xxx 1
                        f"({target})\s+(level-[12])\s+(\d+)"      # 模式2: isis xxx level-2 1
                    ]
                    
                    for pattern in patterns:
                        match = re.search(pattern, cmd)
                        if match:
                            if "level-[12])\s+(\d+" in pattern:
                                # 处理模式2: isis xxx level-2 1
                                cmd = re.sub(pattern, r"\1 \2 20", cmd)
                            else:
                                # 处理模式1: isis xxx 1 level-2 或 isis xxx 1
                                cmd = re.sub(pattern, r"\1 20\3", cmd)
                            break
                    break
                    
            modified_commands.append(cmd)
        
        return ';'.join(modified_commands)

    def delete_one_command(self):
        for rd in range(0, self.rd_num):
            for step in range(0, self.step_num[rd]):
                for rt in range(0, self.rt_num):
                    for i in range(0, len(self.commands[rd][step]["isis"][rt])):
                        if (self.should_delete(self.commands[rd][step]["isis"][rt][i])):
                            self.commands[rd][step]["isis"][rt].pop(i)
                            return True
        return False

    def modify_all_commands(self):
        for rd in range(self.rd_num):
            for step in range(self.step_num[rd]):
                for rt in range(self.rt_num):
                    for i in range(len(self.commands[rd][step]["isis"][rt])):
                            if self.should_modify(self.commands[rd][step]["isis"][rt][i]):
                                self.commands[rd][step]["isis"][rt][i] = self.modify_intervals(self.commands[rd][step]["isis"][rt][i])
    def delete(self):
        while self.delete_one_command():
            pass
    

for file_name in os.listdir("/home/zyf/topo-fuzz/test/topo_test/data/testConf"):
    file_path = os.path.join("/home/zyf/topo-fuzz/test/topo_test/data/testConf", file_name)
    r = rm(file_path)
    r.modify_all_commands()
    #r.delete()
    r.dump_file(os.path.join("/home/zyf/topo-fuzz/test/topo_test/data/testConf", file_name.split(".")[0]) + "0000.json")
