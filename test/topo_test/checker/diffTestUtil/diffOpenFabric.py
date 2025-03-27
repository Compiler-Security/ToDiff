import json
import sys
from os import path
path_to_add = path.dirname(path.dirname(path.abspath(__file__)))
if path_to_add not in sys.path:
    sys.path.append(path_to_add)

import copy
import util

class diffOpenFabric:
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

    def neighbors_openfabric(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "neighbors")

    def runningConfig(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "running-config")
    
    def ospfIntfs(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "ospf-intfs")["interfaces"]

    def openfabricIntfs(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "openfabric-intfs")
    
    def ospfDaemon(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "ospf-daemon")

    def openfabricDaemon(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "openfabric-daemon")
      
    def routingTable(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "routing-table")
    
    def shrink_routingTable(self, n_dict:dict):
        print(n_dict)
        new_dict = copy.deepcopy(n_dict)
        for val in new_dict.values():
            for nexthop in val["nexthops"]:
                nexthop.pop("advertisedRouter", None)
        return new_dict

    def shrink_routingTable_openfabric(self, routing_table):
        if routing_table and isinstance(routing_table, list):
            return routing_table[0]
        return None

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
    def shrink_neighbors_openfabric(self, data: dict) -> dict:
        new_data = copy.deepcopy(data)
        for area in new_data.get("areas", []):
            for circuit in area.get("circuits", []):
                # 删除 expires-in 字段
                circuit.pop("expires-in", None)
                
                interface = circuit.get("interface", {})
                if isinstance(interface, dict):

                    #interface.pop("adj-flaps", None)
                    interface.pop("last-ago", None)
                    #interface.pop("snpa", None)
                    #interface.pop("lan-id", None)
                    #interface.pop("lan-prio", None)
                    #interface.pop("dis-flaps", None)
        return new_data
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
    
    def shrink_openfabricDaemon(self, data: dict) -> dict:
        new_data = copy.deepcopy(data)
        
        for vrf in new_data.get("vrfs", []):
            vrf.pop("process-id", None)
            vrf.pop("up-time", None)
            
            for area in vrf.get("areas", []):
                area.pop("tx-pdu-type", None)
                area.pop("rx-pdu-type", None)
                
                for level in area.get("levels", []):
                    #level.pop("lsp-purged", None)
                    level.pop("lsp0-regenerated", None)
                    level.pop("last-run-elapsed", None)
                    level.pop("last-run-duration-usec", None)
                    level.pop("last-run-count", None)
        
        return new_data
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

    def shrink_openfabricIntfs(self, data:dict):
        new_data = copy.deepcopy(data)

        for area in new_data.get("areas", []):

            for circuit in area.get("circuits", []):
                interface = circuit.get("interface", {})
                interface.pop("snpa", None)
                interface.pop("ipv6-link-locals", None)
                for level in interface.get("levels", []):
                    #level.pop("metric", None)
                    lan = level.get("lan", {})
                    #lan.pop("is-dis", None)
                    
        return new_data
    
    def check_neighbors(self, rt, rd):
        return util.dict_diff(self.shrink_neighbors(self.neighbors(0, self.step_nums[0] - 1, rt)), self.shrink_neighbors(self.neighbors(rd, self.step_nums[rd] - 1, rt)))

    def check_neighbors_openfabric(self, rt, rd):
        return util.dict_diff(self.shrink_neighbors_openfabric(self.neighbors_openfabric(0, self.step_nums[0] - 1, rt)), self.shrink_neighbors_openfabric(self.neighbors_openfabric(rd, self.step_nums[rd] - 1, rt)))

    def check_routingTable(self, rt, rd):
        return util.dict_diff(self.shrink_routingTable(self.routingTable(0, self.step_nums[0] - 1, rt)), self.shrink_routingTable(self.routingTable(rd, self.step_nums[rd] - 1, rt)))
    
    def check_routingTable_openfabric(self, rt, rd):
        rt0 = self.shrink_routingTable_openfabric(self.routingTable(0, self.step_nums[0] - 1, rt))
        rtd = self.shrink_routingTable_openfabric(self.routingTable(rd, self.step_nums[rd] - 1, rt))
        if rt0 is None or rtd is None:
            return {}
        return util.dict_diff(rt0, rtd)
        # return util.dict_diff(self.shrink_routingTable_openfabric(self.routingTable(0, self.step_nums[0] - 1, rt)), self.shrink_routingTable_openfabric(self.routingTable(rd, self.step_nums[rd] - 1, rt)))

    def check_ospfDaemon(self, rt, rd):
        return util.dict_diff(self.shrink_ospfDaemon(self.ospfDaemon(0, self.step_nums[0] - 1, rt)), self.shrink_ospfDaemon(self.ospfDaemon(rd, self.step_nums[rd] - 1, rt)))

    def check_openfabricDaemon(self, rt, rd):
        return util.dict_diff(self.shrink_openfabricDaemon(self.openfabricDaemon(0, self.step_nums[0] - 1, rt)), self.shrink_openfabricDaemon(self.openfabricDaemon(rd, self.step_nums[rd] - 1, rt)))
    
    def check_ospfIntfs(self, rt, rd):
        return util.dict_diff(self.shrink_ospfIntfs(self.ospfIntfs(0, self.step_nums[0] - 1, rt)), self.shrink_ospfIntfs(self.ospfIntfs(rd, self.step_nums[rd] - 1, rt)))
    
    def check_openfabricIntfs(self, rt, rd):
        return util.dict_diff(self.shrink_openfabricIntfs(self.openfabricIntfs(0, self.step_nums[0] - 1, rt)), self.shrink_openfabricIntfs(self.openfabricIntfs(rd, self.step_nums[rd] - 1, rt)))

    def check_runningConfig(self, rt, rd):
        return util.str_diff(self.runningConfig(0, self.step_nums[0] - 1, rt), self.runningConfig(rd, self.step_nums[rd] - 1, rt))
            
    def check_convergence(self, rt, rd):
        res = {}
        if self.conf["test"]["result"][rd][self.step_nums[rd] - 1]["exec"]["convergence"] != True:
            res = {"not_convergence":rd}
        return res

import functools
import io

def checkFunc(rd, diff, func, name, buf):
    same = True
    buf.write(f">>>>> +check {name} <<<<<\n")
    for rt in diff.routers:
        res = functools.partial(func)(rt, rd)
        if (res != {} and res != []):
            buf.write(f"----- router {rt} -----\n")
            buf.write(json.dumps(res, indent=4))
            buf.write("\n")
            same = False
    return same
    
def checkTest(test_name, diffAll):
    result_path = path.join(util.get_result_dir(test_name), util.get_result_name(test_name))
    diff = diffOpenFabric(result_path)
 
    buf = io.StringIO()
    for rd in range(1, diff.round_num):
        buf.write(f"====== round {rd} ======\n")
        res = checkFunc(rd, diff, diff.check_runningConfig, "check_runningConfig", buf)
        if (not res and not diffAll): continue

        res = checkFunc(rd, diff, diff.check_convergence, "check_convergence", buf)
        if (not res and not diffAll): continue
    
        # res = checkFunc(rd, diff, diff.check_ospfIntfs, "check_ospfIntfs", buf)
        res = checkFunc(rd, diff, diff.check_openfabricIntfs, "check_openfabricIntfs", buf)

        res = checkFunc(rd, diff, diff.check_neighbors_openfabric, "check_neighbors", buf)
        # res = checkFunc(rd, diff, diff.check_ospfDaemon, "check_ospfDaemon", buf)
        res = checkFunc(rd, diff, diff.check_openfabricDaemon, "check_openfabricDaemon", buf)
        res = checkFunc(rd, diff, diff.check_routingTable_openfabric, "check_routingTable", buf)
    
    return buf.getvalue()