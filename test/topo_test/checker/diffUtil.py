import json

import copy
import util
import pprint
class diff:
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

    def isisIntfs(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "isis-intfs")["interfaces"]
    
    def ospfDaemon(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "ospf-daemon")
    
    def routingTable(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "routing-table")
    
    def shrink_routingTable(self, n_dict:dict):
        new_dict = copy.deepcopy(n_dict)
        for val in new_dict.values():
            for nexthop in val["nexthops"]:
                nexthop.pop("advertisedRouter", None)
        return new_dict

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
                del item["nbrState"]
                del item["converged"]
                del item["role"]
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
        for ospf_intf in new_dict1.values():
            del ospf_intf['ifIndex']
            if "timerHelloInMsecs" in ospf_intf:
                del ospf_intf["timerHelloInMsecs"]
            if "interfaceIp" in ospf_intf:
                for val in ospf_intf["interfaceIp"].values():
                    if "timerHelloInMsecs" in val:
                        del val["timerHelloInMsecs"]
            del ospf_intf["lsaRetransmissions"]

            #we don't comapre state, dr, bdr
            
            ospf_intf.pop("drId", None)
            ospf_intf.pop("drAddress", None)
            ospf_intf.pop("bdrId", None)
            ospf_intf.pop("bdrAddress", None)
            ospf_intf.pop("state", None)
                #ospf_intf.pop("networkLsaSequence", None)
                
        return new_dict1

    def shrink_isisIntfs(self, n_dict:dict):
        new_dict1 = copy.deepcopy(n_dict)
        for isis_intf in new_dict1.values():
            del isis_intf['ifIndex']
            if "timerHelloInMsecs" in isis_intf:
                del isis_intf["timerHelloInMsecs"]
            if "interfaceIp" in isis_intf:
                for val in isis_intf["interfaceIp"].values():
                    if "timerHelloInMsecs" in val:
                        del val["timerHelloInMsecs"]
            del isis_intf["lsaRetransmissions"]

            #we don't comapre state, dr, bdr
            
            isis_intf.pop("drId", None)
            isis_intf.pop("drAddress", None)
            isis_intf.pop("bdrId", None)
            isis_intf.pop("bdrAddress", None)
            isis_intf.pop("state", None)
                #ospf_intf.pop("networkLsaSequence", None)
                
        return new_dict1
    
    def check_neighbors(self, rt, rd):
        return util.dict_diff(self.shrink_neighbors(self.neighbors(0, self.step_nums[0] - 1, rt)), self.shrink_neighbors(self.neighbors(rd, self.step_nums[rd] - 1, rt)))

    def check_routingTable(self, rt, rd):
        return util.dict_diff(self.shrink_routingTable(self.routingTable(0, self.step_nums[0] - 1, rt)), self.shrink_routingTable(self.routingTable(rd, self.step_nums[rd] - 1, rt)))
    
    def check_ospfDaemon(self, rt, rd):
        return util.dict_diff(self.shrink_ospfDaemon(self.ospfDaemon(0, self.step_nums[0] - 1, rt)), self.shrink_ospfDaemon(self.ospfDaemon(rd, self.step_nums[rd] - 1, rt)))

    def check_ospfIntfs(self, rt, rd):
        return util.dict_diff(self.shrink_ospfIntfs(self.ospfIntfs(0, self.step_nums[0] - 1, rt)), self.shrink_ospfIntfs(self.ospfIntfs(rd, self.step_nums[rd] - 1, rt)))
    
    def check_isisIntfs(self, rt, rd):
        return util.dict_diff(self.shrink_isisIntfs(self.isisIntfs(0, self.step_nums[0] - 1, rt)), self.shrink_isisIntfs(self.isisIntfs(rd, self.step_nums[rd] - 1, rt)))

    def check_runningConfig(self, rt, rd):
        return util.str_diff(self.runningConfig(0, self.step_nums[0] - 1, rt), self.runningConfig(rd, self.step_nums[rd] - 1, rt))
            
    def check_convergence(self, rt, rd):
        res = {}
        if self.conf["test"]["result"][rd][self.step_nums[rd] - 1]["exec"]["convergence"] != True:
            res = {"not_convergence":rd}
        return res