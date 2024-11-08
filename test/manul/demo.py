from manul import *
import manul
import os
from os import path
import time
os.system("mn -c 2> /dev/null")
h = manulTest(path.join(WORK_DIR, "demo", "conf"))
h.net.router_nodes = ["r1", "r2"]
h.run_phys("""
           node r1 add
           node r2 add
           node s7 add
           link r1-eth0 s7-eth0 add
           link r2-eth0 s7-eth1 add
           node r1 set ISIS up
           node r2 set ISIS up
           """)

h.net.net.start()
time.sleep(100)
h.run_isis("r1", "interface r1-eth0;isis hello-interval 10")
h.run_isis("r1", "show isis database")
CLI(h.net.net)
