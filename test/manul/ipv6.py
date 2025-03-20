from manul import *
import manul
import os
from os import path
os.system("mn -c 2> /dev/null")
h = manulTest(path.join(WORK_DIR, "ipv6", "conf"))
h.net.router_nodes = ["r1"]
h.run_phys("""
           node r1 add
           node s7 add
           link r1-eth0 s7-eth0 add
           intf r1-eth0 down
           intf r1-eth0 up
           node r1 set BABEL up
           """)
h.net.net.getNodeByName("r1").cmd("ip -6 addr flush dev r1-eth0")

h.net.net.start()

CLI(h.net.net)

h.net.net.stop()