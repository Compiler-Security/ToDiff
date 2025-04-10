from manul import *
import manul
import os
from os import path
import time
os.system("mn -c 2> /dev/null")
h = manulTest(path.join(WORK_DIR, "36667", "conf"))
h.net.router_nodes = ["r0",
    "r1",
    "r2",
    "r3",
    "r4",
    "r5"]
h.run_phys("""
           node r3 add
          node r1 add
          node r5 add
          node r2 add
          node r4 add
          node r0 add
          node s4 add
          node s0 add
          node s2 add
          node s8 add
          node s1 add
          node s10 add
          node s6 add
          node s7 add
          node s12 add
          node s3 add
          node s5 add
          node s9 add
          node s11 add
          link r2-eth1 s3-eth0 add
          intf r2-eth1 up
          link r0-eth0 s5-eth0 add
          intf r0-eth0 up
          link r0-eth2 s9-eth0 add
          intf r0-eth2 up
          link r3-eth1 s4-eth1 add
          intf r3-eth1 up
          link r2-eth0 s2-eth0 add
          intf r2-eth0 up
          link r4-eth0 s11-eth0 add
          intf r4-eth0 up
          link r3-eth2 s7-eth0 add
          intf r3-eth2 up
          link r3-eth0 s6-eth0 add
          intf r3-eth0 up
          link r5-eth2 s8-eth1 add
          intf r5-eth2 up
          link r5-eth1 s1-eth0 add
          intf r5-eth1 up
          link r4-eth1 s12-eth0 add
          intf r4-eth1 up
          link r1-eth0 s10-eth0 add
          intf r1-eth0 up
          link r2-eth3 s9-eth1 add
          intf r2-eth3 up
          link r5-eth0 s0-eth0 add
          intf r5-eth0 up
          link r0-eth1 s4-eth0 add
          intf r0-eth1 up
          link r2-eth2 s8-eth0 add
          intf r2-eth2 up
          node r3 set ISIS up
          node r1 set ISIS up
          node r5 set ISIS up
          node r2 set ISIS up
          node r4 set ISIS up
          node r0 set ISIS up
           """)

h.net.net.start()
time.sleep(100)
# h.run_isis("r0", "interface r0-eth1;no isis network point-to-point;")
# h.run_isis("r0", "interface r0-eth1;isis three-way-handshake;isis network point-to-point")
# h.run_isis("r0", "interface r0-eth0;isis network point-to-point;no isis network point-to-point")
# h.run_isis("r0", "interface r0-eth1;no isis three-way-handshake")
# time.sleep(60)
h.run_isis("r0", "show isis interface detail")
CLI(h.net.net)

