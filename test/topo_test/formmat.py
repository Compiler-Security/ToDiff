import json
with open("/home/frr/topo-fuzz/test/topo_test/data/testConf/test1726036744.json") as fp:
    j = json.load(fp)

#print(j['genInfo']['configGraph'])
print(j['genInfo']['routerGraph'])