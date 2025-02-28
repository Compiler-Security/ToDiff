from restful_mininet.exec.executor import executor

def test():
    e = executor("/home/frr/topo-fuzz/test/excutor_test/frr_conf/all1.conf", "/home/frr/topo-fuzz/test/excutor_test/frr_conf")
    e.test()

test()