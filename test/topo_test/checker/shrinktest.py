import os
import json
from os import path

#topo-fuzz/test/topo_test/
up = path.dirname
checkDir = path.join(up(up(path.abspath(__file__))), "data", "check")

def shrink_test(rd, file_path):
    with open(file_path) as fp:
        data = json.load(fp)
    new_name = path.basename(file_path)
    #test298239_r2.json
    new_name = new_name.split(".")[0] + f"_r{rd}.json"
    data["conf_name"] = new_name[:new_name.find(".")]
    data["step_nums"] = [data["step_nums"][rd]]
    data["round_num"] = 1
    data["commands"] = [data["commands"][rd]]
    with open(path.join(checkDir, new_name), "w") as fp:
        json.dump(data, fp)
    
if __name__ == "__main__":
    #rd from 0
    shrink_test(0, "/home/frr/topo-fuzz/test/topo_test/data/testConf/test1742282936.json")
