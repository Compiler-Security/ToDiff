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

    def shrink_ospfDaemon(self, n_dict:dict):
        key_set = ["routerId", "tosRoutesOnly", "rfc2328Conform", "holdtimeMinMsecs", "holdtimeMaxMsecs", "spfScheduleDelayMsecs", "maximumPaths", "writeMultiplier", "abrType", "attachedAreaCounter"]
        new_dict = {x:n_dict[x] for x in key_set if x in n_dict}
        new_dict["areas"] = {}
        for (area, val) in n_dict["areas"].items():
            if "backbone" in val.keys():
                key_set = ["backbone"]
            else:
                key_set = ["shortcuttingMode", "sBitConcensus"]
            key_set += ["areaIfTotalCounter", "areaIfActiveCounter", "nbrFullAdjacentCounter", "lsaNumber", "lsaRouterNumber", "lsaNetworkNumber", "lsaSummaryNumber", "lsaAsbrNumber", "lsaNssaNumber"]
            new_dict["areas"][area] = {x:val[x] for x in key_set if x in val}
        return new_dict
    
    def shrink_ospfIntfs(self, n_dict:dict):
        new_dict1 = copy.deepcopy(n_dict)
        for new_dict in new_dict1.values():
            if "timerHelloInMsecs" in new_dict:
                del new_dict["timerHelloInMsecs"]
            for val in new_dict["interfaceIp"].values():
                if "timerHelloInMsecs" in new_dict:
                    del new_dict["timerHelloInMsecs"]
        return new_dict1

    def check_neighbors(self, rt):
        for i in range(1, self.round_num):
            diff = util.dict_diff(self.shrink_neighbors(self.neighbors(i - 1, self.step_nums[i - 1] - 1, rt)), self.shrink_neighbors(self.neighbors(i, self.step_nums[i] - 1, rt)))
            if (diff != {}):
                print(f"round {i-1} {i} neighbors")
                print(json.dumps(diff, indent=4))

    def check_routingTable(self, rt):
        for i in range(1, self.round_num):
            diff = util.dict_diff(self.routingTable(i - 1, self.step_nums[i - 1] - 1, rt),self.routingTable(i, self.step_nums[i] - 1, rt))
            if (diff != {}):
                print(f"round {i-1} {i} routing table")
                print(json.dumps(diff, indent=4))
    
    def check_ospfDaemon(self, rt):
        for i in range(1, self.round_num):
            diff = util.dict_diff(self.shrink_ospfDaemon(self.ospfDaemon(i - 1, self.step_nums[i - 1] - 1, rt)), self.shrink_ospfDaemon(self.ospfDaemon(i, self.step_nums[i] - 1, rt)))
            if (diff != {}):
                print(f"round {i-1} {i} ospf-daemon")
                print(json.dumps(diff, indent=4))

    def check_ospfIntfs(self, rt):
        for i in range(1, self.round_num):
            diff = util.dict_diff(self.shrink_ospfIntfs(self.ospfIntfs(i - 1, self.step_nums[i - 1] - 1, rt)), self.shrink_ospfIntfs(self.ospfIntfs(i, self.step_nums[i] - 1, rt)))
            if (diff != {}):
                print(f"round {i-1} {i} ospf-intfs")
                print(json.dumps(diff, indent=4))

    def check_runningConfig(self, rt):
        for i in range(1, self.round_num):
            diff = util.str_diff(self.runningConfig(i-1, self.step_nums[i-1] - 1, rt), self.runningConfig(i, self.step_nums[i] - 1, rt))
            if (len(diff) > 0):
                print(f"round {i-1} {i} running-configs")
                print(json.dumps(diff, indent=4))
                
    def check(self):
            i = 0
            for rt in self.routers:
                print(f"check router {rt}")
                # for rd in range(0, self.round_num):
                #     print(self.routingTable(rd, self.step_nums[rd] - 1, rt).keys())
                self.check_ospfIntfs(rt)
                self.check_neighbors(rt)
                self.check_routingTable(rt)
                self.check_ospfDaemon(rt)
                self.check_runningConfig(rt)
            # for rd in range(0, self.round_num):
            #     print(json.dumps(self.ospfIntfs(rd, self.step_nums[rd] - 1, "r1"), indent=4))

    def print_diff_running_config(self):
        i = 0
        for rt in self.routers:
            print(f"check router {rt}")
            self.check_runningConfig(rt)

    def check_item(self, rt, item):
        i = 0
        print(f"check router {rt}")
        if item == "oi":
            self.check_ospfIntfs(rt)
        if item == "nb":
            self.check_neighbors(rt)
        if item == "rt":
            self.check_neighbors(rt)
        if item == "od":
            self.check_ospfDaemon(rt)
        if item == "rc":
            self.check_runningConfig(rt)


if __name__ == "__main__":
    d = diffTest("/home/frr/topo-fuzz/test/topo_test/data/result/test1726036744/test1726036744_res.json")
    d.print_diff_running_config()