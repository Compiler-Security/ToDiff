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
class diffBABEL:
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
    
    # def routingTable(self, rd, step, router):
    #     return self.watchOfConf(rd, step, router, "routing-table")
    
    def babelRoute(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "babel-route")
    
    def babelInterface(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "babel-interface")
    
    def babelNeighbor(self, rd, step, router):
        return self.watchOfConf(rd, step, router, "babel-neighbor")
    
    def shrink_babelRoute(self, str):
        return str.split("\r\n")

    def shrink_babelInterface(self, str):
        res = {}
        key = ""
        for val in str.split("\r\n"):
            if (val[0] != ' '):
                res[val] = []
                key = val
            else:
                res[key].append(val)
        return res
    
    def shrink_babelNeighbor(self, str):
        return str.split("\r\n")



    def check_babel_route(self, rt, rd):
        res = util.compare_lists(self.shrink_ripRoute(self.babelRoute(0, self.step_nums[0] - 1, rt)), self.shrink_babelRoute(self.babelRoute(rd, self.step_nums[rd] -1, rt)))
        if len(res["unique_to_first"])== 0 and len(res["unique_to_second"]) == 0:
            return {}
        else: 
            return res
        
    def check_babel_interface(self, rt, rd):
        return util.dict_diff(self.shrink_babelInterface(self.babelInterface(0, self.step_nums[0] - 1, rt)), self.shrink_babelInterface(self.babelInterface(rd, self.step_nums[rd] - 1, rt)))
        
    def check_routingTable(self, rt, rd):
        return util.dict_diff(self.shrink_routingTable(self.routingTable(0, self.step_nums[0] - 1, rt)), self.shrink_routingTable(self.routingTable(rd, self.step_nums[rd] - 1, rt)))
    
    
    def check_babel_neighbor(self, rt, rd):
        res = util.compare_lists(self.shrink_babelNeighbor(self.babelNeighbor(0, self.step_nums[0] - 1, rt)), self.shrink_babelNeighbor(self.babelNeighbor(rd, self.step_nums[rd] -1, rt)))
        if len(res["unique_to_first"])== 0 and len(res["unique_to_second"]) == 0:
            return {}
        else: 
            return res

    def check_runningConfig(self, rt, rd):
        str_0 = self.runningConfig(0, self.step_nums[0] - 1, rt)
        str_1 = self.runningConfig(rd, self.step_nums[rd] - 1, rt)
        intfs_0 = {item.split("\r\n")[1]:item.split("\r\n")[2:-1]
            for item in re.findall("!\r\ninterface[\s\S]+?exit|!\r\nrouter babel[\s\S]+?exit", str_0)}
        intfs_1 = {item.split("\r\n")[1]:item.split("\r\n")[2:-1]
            for item in re.findall("!\r\ninterface[\s\S]+?exit|!\r\nrouter babel[\s\S]+?exit", str_1)}
        return util.dict_diff(intfs_0, intfs_1)
    
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
    diff_BABEL = diffBABEL(result_path)
 
    buf = io.StringIO()
    for rd in range(1, diff_BABEL.round_num):
        buf.write(f"====== round {rd} ======\n")
        res = checkFunc(rd, diff_BABEL, diff_BABEL.check_runningConfig, "check_runningConfig", buf)
        if (not res and not diffAll): continue

        #res = checkFunc(rd, diff_RIP, diff_RIP.check_convergence, "check_convergence", buf)
        #if (not res and not diffAll): continue
        
        res = checkFunc(rd, diff_BABEL, diff_BABEL.check_babel_route, "check_babel_route", buf)
        res = checkFunc(rd, diff_BABEL, diff_BABEL.check_babel_interface, "check_babel_interface", buf)
        res = checkFunc(rd, diff_BABEL, diff_BABEL.check_babel_neighbor, "check_babel_neighbor", buf)
        res = checkFunc(rd, diff_BABEL, diff_BABEL.check_routingTable, "check_routingTable", buf)
       
    
    return buf.getvalue()
