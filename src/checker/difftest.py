import json

import copy
import util
class diffTest:
    def __init__(self, file_path):
        self.file_path = file_path
        with open(file_path) as fp:
            self.conf = json.load(fp)
        self.routers = self.conf["routers"]
        self.round_num = self.conf["round_num"]
        self.step_nums = self.conf["step_nums"]
        self.router_pair = self.get_router_pair()
    def get_router_pair(self):
        old_router = self.routers[0]
        res = []
        for i in range(1, len(self.routers)):
            res.append((old_router, self.routers[i]))
            old_router = self.routers[i]
        return res
    
    def watchOfConf(self, rd, step, router, field):
        return self.conf["test"]["result"][rd][step]["watch"][router][field]

    def neighbors(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "neighbors")["neighbors"]

    

    def runningConfig(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "running-config")
    
    def ospfIntfs(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "ospf-intfs")["interfaces"]
    
    def ospfDaemon(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "ospf-daemon")
    
    def routingTable(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "routing-table")
    
    def shrink_neighbors(self, n_dict:dict):
        new_dict = copy.deepcopy(n_dict)
        for val in new_dict.values():
            for item in val:
                del item["upTimeInMsec"]
                del item["routerDeadIntervalTimerDueMsec"]
                del item["upTime"]
                del item["deadTime"]
                del item["linkStateRetransmissionListCounter"]
                del item["linkStateRequestListCounter"]
                del item["databaseSummaryListCounter"]
        return new_dict


    def check_neighbors(self, rt):
        for i in range(1, self.round_num):
            diff = util.dict_diff(self.shrink_neighbors(self.neighbors(i - 1, self.step_nums[i - 1] - 1, rt)), self.shrink_neighbors(self.neighbors(i, self.step_nums[i] - 1, rt)))
            if (diff != {}):
                print(f"round {i-1} {i}")
                print(json.dumps(diff, indent=4))

    def check(self):
            i = 0
            for rt in self.routers:
                print(f"router {rt}")
                self.check_neighbors(rt)
                #print(self.runningConfig(rd, self.step_nums[rd] - 1, "r1"))

if __name__ == "__main__":
    d = diffTest("/home/frr/a/topo-fuzz/test/excutor_test/frr_conf/test1_res.json")
    d.check()