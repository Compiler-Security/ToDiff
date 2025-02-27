import json
import sys
from os import path
path_to_add = path.dirname(path.dirname(path.abspath(__file__)))
if path_to_add not in sys.path:
    sys.path.append(path_to_add)

import copy
import util
import pprint
import re
from datetime import datetime
class diffRIP:
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



    def runningConfig(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "running-config")
    
    def routingTable(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "routing-table")
    
    def ripRoute(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "rip-route")
    
    def ripStatus(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "status")
    
    def shrink_ripRoute(self, str):
        routes = str[str.find("Tag Time\r\n"):].split("\r\n")[1:]
        new_list =[]
        for i, route in enumerate(routes):
            r = route.split()
            if (len(r) == 0): continue
            if (int(r[3]) == 16): continue
            new_list.append({
                "type": r[0],
                "Network":r[1],
                "NextHop":r[2],
                "Metric":r[3],
                "From": r[4],
                "Tag":r[5]
            }
            )
        return new_list
    
    def get_match(self, r_str, text):
        match = re.search(r_str, text)
        return match.group(1)

    def get_match_two(self, r_str, text):
        match = re.search(r_str, text)
        return match.group(1), match.group(2)
    def get_time(self, time_obj):
        return time_obj.hour * 3600 + time_obj.minute * 60 + time_obj.second
    def shrink_ripStatus(self, str):
        new_dict = {}
        new_dict["update"] = self.get_match("every ([0-9]+) seconds", str)
        new_dict["timeout"] = self.get_match("Timeout after ([0-9]+) seconds", str)
        new_dict["garbage"] = self.get_match("garbage collect after ([0-9]+) seconds", str)
        new_dict["default_send_version"], new_dict["default_recv_version"] = self.get_match_two("Default version control: send version ([0-9a-zA-Z ]+), receive ([0-9a-zA-Z ]+)", str)
        new_dict["intf_version"] = [
        {
            "interface": item.split()[0],
            "send_v": item.split()[1],
            "recv_v": item.split()[2]
        }
             for item in self.get_match("Key-chain\r\n([\S\s]+) Routing for", str).split("\r\n")[:-1]
        ]
        new_dict["routing_for_networks"] = [
             item.strip() for item in self.get_match(" Routing for Networks:\r\n([\S\s]+) Routing Information Sources:", str).split("\r\n")[:-1]
        ]
        new_dict["routing_information_sources"] = [
        {
            "gateway": item.split()[0],
            # "badPackets": item.split()[1],
            # "badRoutes": item.split()[2],
            "distance": item.split()[3],
        }
             for item in self.get_match("Last Update\r\n([\S\s]+) Distance:", str).split("\r\n")[:-1] if self.get_time(datetime.strptime(item.split()[4], "%H:%M:%S").time()) < 10
        ]
        new_dict["distance"] = self.get_match("default is ([0-9]+)", str)
        return new_dict


    def shrink_routingTable(self, n_dict:dict):
        new_dict = copy.deepcopy(n_dict)
        for val in new_dict.values():
            val[0].pop("uptime")
            val[0].pop("nexthopGroupId")
            val[0].pop("installedNexthopGroupId")
            for nextHop in val[0]["nexthops"]:
                nextHop.pop("interfaceIndex")
        return new_dict
   

    
    def check_rip_route(self, rt, rd):
        res = util.compare_lists(self.shrink_ripRoute(self.ripRoute(0, self.step_nums[0] - 1, rt)), self.shrink_ripRoute(self.ripRoute(rd, self.step_nums[rd] -1, rt)))
        if len(res["unique_to_first"])== 0 and len(res["unique_to_second"]) == 0:
            return {}
        else: 
            return res
        
    def check_rip_status(self, rt, rd):
        return util.dict_diff(self.shrink_ripStatus(self.ripStatus(0, self.step_nums[0] - 1, rt)), self.shrink_ripStatus(self.ripStatus(rd, self.step_nums[rd] - 1, rt)))
        
    def check_routingTable(self, rt, rd):
        return util.dict_diff(self.shrink_routingTable(self.routingTable(0, self.step_nums[0] - 1, rt)), self.shrink_routingTable(self.routingTable(rd, self.step_nums[rd] - 1, rt)))
    
    
    def check_runningConfig(self, rt, rd):
        str_0 = self.runningConfig(0, self.step_nums[0] - 1, rt)
        str_1 = self.runningConfig(rd, self.step_nums[rd] - 1, rt)
        intfs_0 = {item.split("\r\n")[1]:item.split("\r\n")[2:-1]
            for item in re.findall("!\r\ninterface[\s\S]+?exit|!\r\nrouter rip[\s\S]+?exit", str_0)}
        intfs_1 = {item.split("\r\n")[1]:item.split("\r\n")[2:-1]
            for item in re.findall("!\r\ninterface[\s\S]+?exit|!\r\nrouter rip[\s\S]+?exit", str_1)}
        return util.dict_diff(intfs_0, intfs_1)

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
    diff_RIP = diffRIP(result_path)
 
    buf = io.StringIO()
    for rd in range(1, diff_RIP.round_num):
        buf.write(f"====== round {rd} ======\n")
        res = checkFunc(rd, diff_RIP, diff_RIP.check_runningConfig, "check_runningConfig", buf)
        if (not res and not diffAll): continue

        #res = checkFunc(rd, diff_RIP, diff_RIP.check_convergence, "check_convergence", buf)
        #if (not res and not diffAll): continue
        
        res = checkFunc(rd, diff_RIP, diff_RIP.check_rip_route, "check_rip_route", buf)
        res = checkFunc(rd, diff_RIP, diff_RIP.check_rip_status, "check_rip_status", buf)
        res = checkFunc(rd, diff_RIP, diff_RIP.check_routingTable, "check_routingTable", buf)
       
    
    return buf.getvalue()
