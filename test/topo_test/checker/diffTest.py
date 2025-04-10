import os
import json
from os import path

#topo-fuzz/test/topo_test/
up = path.dirname
import diffTestUtil.diffOSPF
import diffTestUtil.diffISIS
import diffTestUtil.diffRIP
import diffTestUtil.diffOpenFabric
import util
import json
import os
from os import path
def checkTests(protocol):
    diff_dir = path.join(util.checkDir, "diff")
    os.makedirs(diff_dir, exist_ok=True)
    for test_name in util.get_all_test_name():
        if test_name == "test1742965787.json":
            continue
        if protocol == "ospf":
            res = diffTestUtil.diffOSPF.checkTest(test_name, False)
        if protocol == "isis":
            res = diffTestUtil.diffISIS.checkTest(test_name, False)
        if protocol == "rip":
            res = diffTestUtil.diffRIP.checkTest(test_name, False)
        if protocol == "openfabric":
            res = diffTestUtil.diffOpenFabric.checkTest(test_name, False)
        with open(path.join(diff_dir, f"{test_name}.txt"), "w") as fp:
            fp.write(res)

        
if __name__ == "__main__":
    #checkTest(util.get_test_name_5("44999"), False)
    checkTests("isis")
