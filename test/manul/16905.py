# https://github.com/FRRouting/frr/issues/16905

from manul import *

from os import path

if __name__ == "__main__":
    # setLogLevel('info')
    # simpleTest()
    os.system("mn -c 2> /dev/null")
    h = manulTest(path.join(WORK_DIR, "16905", "conf"))

    h.run_phys("""node r0 add
              node s0 add
              link r0-eth2 s0-eth0 add
              node r0 set OSPF up
              """)

    time.sleep(1)
    h.run_ospf("r0", "interface r0-eth2;ip ospf dead-interval 10000")
    #time.sleep(10)
    #h.run_ospf("r0", "clear ip ospf process")
    #time.sleep(10)
    #h.run_ospf("r0", "clear ip ospf process")
    #print(h.net.get_node_by_name("r0").dump_ospf_intfs_info())
    CLI(h.net.net)