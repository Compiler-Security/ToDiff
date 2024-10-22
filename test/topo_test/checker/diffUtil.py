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
    
    def ospfDaemon(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "ospf-daemon")
    
    def routingTable(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "routing-table")
    
    def ospfDatabase(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "database")
    
    def shrink_ospfDatabase(self, n_dict:dict):
        new_dict = copy.deepcopy(n_dict)
        """
        routerId:
        routerLinkStates:{
            areas:{
                "0.0.0.1":[
                    {
                        lsaAge:
                        checksum:
                        ...
                    },
                    {

                    }
                    ...
                ]
            }
        },
        networkLinkStates:{}
        ...
        """
        routerId =  new_dict["routerId"]
        del new_dict["routerId"]
        del new_dict["networkLinkStates"]
        for lsa_type in new_dict:
            if isinstance(new_dict[lsa_type], dict):
                areas = new_dict[lsa_type]["areas"]
                for area, lsa_heads in areas.items():
                        for lsa_head in lsa_heads:
                            del lsa_head["lsaAge"]
                            del lsa_head["lsaSeqNumber"]
                            del lsa_head["checksum"]
                            del lsa_head["lsaFlags"]
                            if lsa_type == "routerLinkStates":
                                router_links =[]
                                for link_name in lsa_head["routerLinks"]:
                                    link = lsa_head["routerLinks"][link_name]
                                    if "designatedRouterAddress" in link:
                                        del link["designatedRouterAddress"]
                                    router_links.append(link)
                                lsa_head["routerLinks"] = router_links
                        
            else:
                """                
                FIXME 
                Currently we don't check asExternalLinkStates, asExternalOpaqueLsa
                """
                pass
                            #should checksum be del?
        new_dict["routerId"] = routerId
        return new_dict

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
            if "networkLsaSequence" in ospf_intf:
                del ospf_intf["networkLsaSequence"]
            del ospf_intf["lsaRetransmissions"]

            #we don't comapre state, dr, bdr
            
            ospf_intf.pop("drId", None)
            ospf_intf.pop("drAddress", None)
            ospf_intf.pop("bdrId", None)
            ospf_intf.pop("bdrAddress", None)
            ospf_intf.pop("state", None)
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

    def check_ospfIntfs(self, rt, rd):
        return util.dict_diff(self.shrink_ospfIntfs(self.ospfIntfs(0, self.step_nums[0] - 1, rt)), self.shrink_ospfIntfs(self.ospfIntfs(rd, self.step_nums[rd] - 1, rt)))
    
    def check_ospfDatabase(self, rt, rd):
        return util.dict_diff(self.shrink_ospfDatabase(self.ospfDatabase(0, self.step_nums[0] - 1, rt)), self.shrink_ospfDatabase(self.ospfDatabase(rd, self.step_nums[rd] - 1, rt)))
    
    def check_runningConfig(self, rt, rd):
        return util.str_diff(self.runningConfig(0, self.step_nums[0] - 1, rt), self.runningConfig(rd, self.step_nums[rd] - 1, rt))
            
    def check_convergence(self, rt, rd):
        res = {}
        if self.conf["test"]["result"][rd][self.step_nums[rd] - 1]["exec"]["convergence"] != True:
            res = {"not_convergence":rd}
        return res