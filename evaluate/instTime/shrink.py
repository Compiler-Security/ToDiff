import json
with open("/home/binshui/topo-fuzz/evaluate/instTime/numTime.json") as fp:
    t = json.load(fp)
d = {int(key): val for key, val in t.items()}
d = {key:d[key] for key in sorted(d.keys())}

with open("/home/binshui/topo-fuzz/evaluate/instTime/data1.json", "w") as fp:
    json.dump(d, fp, indent="")