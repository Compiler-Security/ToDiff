from manul import *
import manul
import os
from os import path
os.system("mn -c 2> /dev/null")
h = manulTest(path.join(WORK_DIR, "babel", "conf"))
h.net.router_nodes = ["r1", "r2", "r3"]
h.run_phys("""
           node r1 add
           node r2 add
           node r3 add
           node s7 add
           node s1 add
           link r1-eth0 s7-eth0 add
           link r2-eth0 s7-eth1 add
           link r2-eth1 s1-eth0 add
           link r3-eth0 s1-eth1 add
           node r1 set BABEL up
           node r2 set BABEL up
           node r3 set BABEL up
           """)

h.net.net.start()

CLI(h.net.net)

h.net.net.stop()
#import time
#time.sleep(40)
#h.run_ospf("r1", "show ip ospf neighbor json")