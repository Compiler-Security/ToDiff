from manul import *
import manul
import os
from os import path
os.system("mn -c 2> /dev/null")
h = manulTest(path.join(WORK_DIR, "30842", "conf"))
h.net.router_nodes = ["r1", "r2", "r4"]
h.run_phys("""
           node r1 add
           node r2 add
           node s7 add
           link r1-eth0 s7-eth0 add
           link r2-eth2 s7-eth1 add
           node r1 set RIP up
           node r2 set RIP up
           """)

h.net.net.start()

CLI(h.net.net)
#import time
#time.sleep(40)
#h.run_ospf("r1", "show ip ospf neighbor json")