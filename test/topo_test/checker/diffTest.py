import os
import json
from os import path

#topo-fuzz/test/topo_test/
up = path.dirname
resultDir = path.join(up(up(path.abspath(__file__))), "data", "result")
import io
import diffUtil
import util
import pprint
import functools
import json

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
    diff = diffUtil.diff(result_path)
 
    buf = io.StringIO()
    for rd in range(1, diff.round_num):
        buf.write(f"====== round {rd} ======\n")
        res = checkFunc(rd, diff, diff.check_runningConfig, "check_runningConfig", buf)
        if (not res and not diffAll): continue

        res = checkFunc(rd, diff, diff.check_convergence, "check_convergence", buf)
        if (not res and not diffAll): continue
    
        # res = checkFunc(rd, diff, diff.check_ospfIntfs, "check_ospfIntfs", buf)
        res = checkFunc(rd, diff, diff.check_isisIntfs, "check_isisIntfs", buf)

        res = checkFunc(rd, diff, diff.check_neighbors_isis, "check_neighbors", buf)
        # res = checkFunc(rd, diff, diff.check_ospfDaemon, "check_ospfDaemon", buf)
        res = checkFunc(rd, diff, diff.check_isisDaemon, "check_isisDaemon", buf)
        res = checkFunc(rd, diff, diff.check_routingTable_isis, "check_routingTable", buf)
    
    return buf.getvalue()
import os
def checkTests():
    diff_dir = path.join(util.checkDir, "diff")
    os.makedirs(diff_dir, exist_ok=True)
    for test_name in util.get_all_test_name():
        res = checkTest(test_name, False)
        with open(path.join(diff_dir, f"{test_name}.txt"), "w") as fp:
            fp.write(res)

        
if __name__ == "__main__":
    #checkTests()

    # test one example
    diff_dir = path.join(util.checkDir, "diff")
    os.makedirs(diff_dir, exist_ok=True)
    
    test_name = util.get_test_name_5("42093")
    res = checkTest(test_name, False)
    with open(path.join(diff_dir, f"{test_name}.txt"), "w") as fp:
            fp.write(res)
