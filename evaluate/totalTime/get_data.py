import os
from os import path
import json
up = path.dirname

#topo-fuzz/test/data/
dataDir = path.join(up(up(up(path.abspath(__file__)))), "test", "topo_test", "data")
confDir = path.join(dataDir, "testConf")
resultDir = path.join(dataDir, "result")

res = {}
for test_name in os.listdir(confDir):
    with open(path.join(confDir, test_name)) as fp:
        conf = json.load(fp)
        graphTime = conf["genInfo"]["evaluate"]["genGraphTime"]
        genTime = conf["genInfo"]["evaluate"]["totalTime"] - graphTime
        totalInstruction = conf["genInfo"]["evaluate"]["totalInstruction"]
        r_num = len(conf["routers"])
        res[r_num] = {"totalInstruction":totalInstruction, "graphTime":graphTime, "genTime":genTime}
    name = test_name.split(".")[0]
    with open(path.join(resultDir, name, f"{name}_res.json" )) as fp:
        r = json.load(fp)
        res[r_num]["runTime"] = r["test"]["total_test_time"]
res = {key:res[key] for key in sorted(res.keys())}
print(res)
with open(path.join(up(path.abspath(__file__ )), "data3.json"), "w") as fp:
    json.dump(res, fp)