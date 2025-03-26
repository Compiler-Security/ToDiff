from manul import *
import manul
import os
import time
from os import path
os.system("mn -c 2> /dev/null")
h = manulTest(path.join(WORK_DIR, "babel", "conf"))
h.net.router_nodes = ["r1", "r2"]
h.run_phys("""
           node r1 add
           node r2 add
           node s1 add
           node s7 add
           link r1-eth0 s7-eth0 add
           link r2-eth0 s7-eth1 add
           link r2-eth1 s1-eth0 add
           node r1 set BABEL up
           node r2 set BABEL up
           """)

h.net.net.start()
time.sleep(3)
print("start")

h.run_ospf("r1","router babel;no network r1-eth0")
#print("ok")
h.run_ospf("r1", "router babel;network r1-eth0")

time.sleep(20)
#while True:
print(h.net.net.getNodeByName("r1").daemon_cmds(["show babel route"]))
#    time.sleep(10)
h.net.net.stop()
#
#CLI(h.net.net)
#import time
#time.sleep(40)
#h.run_ospf("r1", "show ip ospf neighbor json")