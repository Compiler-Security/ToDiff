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
    name = test_name.split(".")[0]
    if "0000" in name: 
        ori = True
    else:
        ori = False
    with open(path.join(resultDir, name, f"{name}_res.json" )) as fp:
        r = json.load(fp)
        r_num = len(r["routers"])
        if r_num not in res:
            res[r_num] = {}
        if ori == True:
            res[r_num]["before"] = r["test"]["total_test_time"]
        else:
            res[r_num]["after"] = r["test"]["total_test_time"]
res = {key:res[key] for key in sorted(res.keys())}
with open(path.join(up(path.abspath(__file__ )), "data4.txt"), "w") as fp:
    for r in res.keys():
        fp.write(str(res[r]["before"]) + '\t' + str(res[r]["after"]) + "\n")