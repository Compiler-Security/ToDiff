import os
import json
from os import path
import argparse
#topo-fuzz/test/topo_test/
up = path.dirname
import diffTestUtil.diffBABEL
import diffTestUtil.diffOSPF
import diffTestUtil.diffISIS
import diffTestUtil.diffRIP
import diffTestUtil.diffOpenFabric
import util
import json
import os
from os import path
def checkTests(protocol, diffAll):
    diff_dir = path.join(util.checkDir, "diff")
    os.makedirs(diff_dir, exist_ok=True)
    for test_name in util.get_all_test_name():
        if test_name == "test1742965787.json":
            continue
        if protocol == "ospf":
            res = diffTestUtil.diffOSPF.checkTest(test_name, diffAll)
        if protocol == "isis":
            res = diffTestUtil.diffISIS.checkTest(test_name, diffAll)
        if protocol == "rip":
            res = diffTestUtil.diffRIP.checkTest(test_name, diffAll)
        if protocol == "babel":
            res = diffTestUtil.diffBABEL.checkTest(test_name, diffAll)
        if protocol == "openfabric":
            res = diffTestUtil.diffOpenFabric.checkTest(test_name, diffAll)
        with open(path.join(diff_dir, f"{test_name}.txt"), "w") as fp:
            fp.write(res)

        
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Run differential checks for all test cases.")
    parser.add_argument('--protocol', type=str, required=True,
                        choices=["ospf", "isis", "rip", "babel", "openfabric"],
                        help='Protocol to check (e.g., ospf, isis, rip, babel, openfabric)')
    parser.add_argument('--short_circuit', action='store_true',
                        help='Stop checking as soon as the first diff is found')

    args = parser.parse_args()
    checkTests(args.protocol, not args.short_circuit)
