from manul import *
import manul
import os
from os import path
os.system("mn -c 2> /dev/null")
h = manulTest(path.join(WORK_DIR, "motiExample", "conf"))
h.net.router_nodes = ["r0", "r1"]
h.run_phys("""
           node r0 add
           node r1 add
           node s0 add
           node s1 add
           node s2 add
           link r0-eth1 s0-eth0 add
           link r0-eth0 s1-eth0 add
           link r1-eth0 s0-eth1 add
           link r1-eth1 s2-eth0 add
           node r0 set OSPF up
           node r1 set OSPF up
           """)

h.net.net.start()

h.run_ospf("r0", "router ospf;no area 0.0.0.1 nssa")

#show ip ospf database router, Flags field
#we should compile two different version of FRR
CLI(h.net.net)
