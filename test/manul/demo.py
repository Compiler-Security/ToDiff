from manul import *
import manul
import os
from os import path
import time
os.system("mn -c 2> /dev/null")
h = manulTest(path.join(WORK_DIR, "demo", "conf"))
h.net.router_nodes = ["r1", "r2", "r3"]
h.run_phys("""
           node r1 add
           node r2 add
           node r3 add
           node s7 add
           node s8 add
           node s9 add
           link r1-eth0 s7-eth0 add
           link r2-eth0 s7-eth1 add
           link r1-eth1 s9-eth0 add
           link r3-eth1 s9-eth1 add
           link r2-eth1 s8-eth0 add
           link r3-eth0 s8-eth1 add
           node r1 set ISIS up
           node r2 set ISIS up
           node r3 set ISIS up
           """)

h.net.net.start()
time.sleep(60)
#h.run_isis("r1", "interface r1-eth0;isis hello-interval 5")
h.run_isis("r1", "show isis interface detail")
CLI(h.net.net)

