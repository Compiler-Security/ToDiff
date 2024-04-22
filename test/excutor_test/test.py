from src.restful_mininet.api.api import executor

def test():
    e = executor("/home/frr/topo-fuzz/test/excutor_test/frr_conf/all.conf", "/home/frr/topo-fuzz/test/excutor_test/frr_conf")
    e.test()

test()