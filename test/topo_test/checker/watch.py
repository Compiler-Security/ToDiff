import json

class watchTest:
    def __init__(self, file_path):
        self._loadConf(file_path)
        
    def _loadConf(self, file_path):
        self.file_path = file_path
        with open(file_path) as fp:
            self.conf = json.load(fp)
        self.routers = self.conf["routers"]
        self.round_num = self.conf["round_num"]
        self.step_nums = self.conf["step_nums"]
    
    def _watchOfPhy(self, rd, step):
        return self.conf["commands"][rd][step]["phy"]


    def watchPhyOfRouter(self, rd):
        step_num = self.step_nums[rd]
        for step in range(step_num):
            print(f"step {step}")
            print(self._watchOfPhy(rd, step))

if __name__ == "__main__":
    w = watchTest("/home/frr/topo-fuzz/test/topo_test/data/testConf/test1726036736.json")
    w.watchPhyOfRouter(2)