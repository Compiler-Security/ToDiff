from manul import *
import manul
import os
from os import path
os.system("mn -c 2> /dev/null")
h = manulTest(path.join(WORK_DIR, "44999", "conf"))
h.net.router_nodes = ["r0", "r1"]
h.run_phys("""
           node r0 add
           node r1 add
           node s0 add
           link r0-eth0 s0-eth0 add
           link r0-eth1 s0-eth1 add
           link r0-eth2 s0-eth2 add
           link r0-eth3 s0-eth3 add
           node r0 set OSPF up
           node r1 set OSPF up
           """)

h.net.net.start()

h.run_ospf("r0", "router ospf;area 32.34.40.120 range 192.168.0.0/22 advertise;no area 32.34.40.120 range 192.168.0.0/22 advertise")
#h.run_ospf("r0", "router ospf;ospf abr-type shortcut;area 32.34.40.120 shortcut enable")
#h.run_ospf("r0", "clear ip ospf process")
#time.sleep(5)
#h.run_ospf("r0", "show ip ospf database")
CLI(h.net.net)

#h.net.stop_net()
#import time
#
#h.run_ospf("r1", "show ip ospf neighbor json")