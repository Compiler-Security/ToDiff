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

from  diffTestUtil.diffBABEL import diffBABEL
class diffBABELPath(diffBABEL):
    def __init__(self, file_path):
        super().__init__(file_path)
        assert self.conf["conf_type"] == "diffPath"

    def check_routingTable(self, rt, rd):
        compareNet = self.conf["commands"][rd][self.conf["step_nums"][rd] -1]["compareNet"]
        return util.dict_diff(self.shrink_routingTable(self.routingTable(0, self.step_nums[0] - 1, rt), compareNet), self.shrink_routingTable(self.routingTable(rd, self.step_nums[rd] - 1, rt), compareNet))
    
    def shrink_routingTable(self, n_dict:dict, compareNet):
        new_dict = copy.deepcopy(n_dict)
        for val in new_dict.values():
            val[0].pop("nexthopGroupId", None)
            val[0].pop("uptime", None)
            val[0].pop("installedNexthopGroupId", None)
            val[0].pop("internalNextHopNum", None)
            val[0].pop("internalNextHopActiveNum", None)
            for nexthop in val[0]["nexthops"]:
                nexthop.pop("interfaceName", None)
                nexthop.pop("advertisedRouter", None)
                nexthop.pop("interfaceIndex", None)
        l = [k for k in new_dict.keys()]
        for val in l:
            if val not in compareNet:
                new_dict.pop(val)
        return new_dict
    
    
import functools
import io
def checkFunc(rd, diff, func, name, buf):
    same = True
    buf.write(f">>>>> +check {name} <<<<<\n")
    routers0 = diff.conf["commands"][0][diff.conf["step_nums"][0] -1]["routers"]
    routers1 = diff.conf["commands"][rd][diff.conf["step_nums"][rd] -1]["routers"]
    routers = [r for r in routers0 if r in routers1]
    for rt in routers:
        res = functools.partial(func)(rt, rd)
        if (res != {} and res != []):
            buf.write(f"----- router {rt} -----\n")
            buf.write(json.dumps(res, indent=4))
            buf.write("\n")
            same = False
    return same
    
def checkTest(test_name, diffAll):
    result_path = path.join(util.get_result_dir(test_name), util.get_result_name(test_name))
    diff_BABEL = diffBABELPath(result_path)
 
    buf = io.StringIO()
    for rd in range(1, diff_BABEL.round_num):
        buf.write(f"====== round {rd} ======\n")
        res = checkFunc(rd, diff_BABEL, diff_BABEL.check_runningConfig, "check_runningConfig", buf)
        if (not res and not diffAll): continue

        #res = checkFunc(rd, diff_RIP, diff_RIP.check_convergence, "check_convergence", buf)
        #if (not res and not diffAll): continue
        res = checkFunc(rd, diff_BABEL, diff_BABEL.check_babel_interface, "check_babel_interface", buf)
        if (not res and not diffAll): continue
        res = checkFunc(rd, diff_BABEL, diff_BABEL.check_babel_neighbor, "check_babel_neighbor", buf)
        if (not res and not diffAll): continue
        res = checkFunc(rd, diff_BABEL, diff_BABEL.check_babel_route, "check_babel_route", buf)
        if (not res and not diffAll): continue
        res = checkFunc(rd, diff_BABEL, diff_BABEL.check_routingTable, "check_routingTable", buf)
       
    
    return buf.getvalue()


import json
if __name__ == "__main__":
    test_name = "test1753801180.json"
    result_path = path.join(util.get_result_dir(test_name), util.get_result_name(test_name))
    diff_OSPFPath = diffBABELPath(result_path)
    print(checkTest("test1753801180.json", True))