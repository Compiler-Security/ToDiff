from os import path
import os
import sys
import util

#topo-fuzz/evaluate/coverage
coverage_dir = path.dirname(path.abspath(__file__))
#topo-fuzz/test/topo_test/data/
data_dir = path.join(path.dirname(path.dirname(coverage_dir)),"test", "topo_test", "data")
def init():
    os.chdir(coverage_dir)
    os.makedirs("todiff", exist_ok=True)
    os.makedirs("todiff/ospf", exist_ok=True)
    os.makedirs("todiff/isis", exist_ok=True)
    os.makedirs("topotests", exist_ok=True)
    os.makedirs("topotests/ospf", exist_ok=True)
    os.makedirs("topotests/isis", exist_ok=True)
    os.makedirs("ossfuzz", exist_ok=True)
    os.makedirs("ossfuzz/ospf", exist_ok=True)
    os.makedirs("ossfuzz/isis", exist_ok=True)

class toDiffTest():
    def __init__(self, id, protocol):
        self.id = id    
        self.protocol = protocol
    ospf_coverage_data_dir = f"{data_dir}/coverage/ospfd"
    isis_coverage_data_dir = f"{data_dir}/coverage/isisd"
    ospf_corpus_dir = f"{data_dir}/backup/ospf_coverage_case/testConf"
    isis_corpus_dir = f"{data_dir}/backup/isis_coverage_case/testConf"
    testcase_dir = f"{data_dir}/testConf"
    def prepare(self):
        if (self.protocol == "OSPF"):
            os.system(f"cd {self.ospf_coverage_data_dir} && rm -f *.gcda")
            os.system(f"cd {self.testcase_dir} && rm -f *")
            os.system(f"cp -r {self.ospf_corpus_dir}/* {self.testcase_dir}")
        else:
            os.system(f"cd {self.isis_coverage_data_dir} && rm -f *.gcda")
            os.system(f"cd {self.testcase_dir} && rm -f *")
            os.system(f"cp -r {self.isis_corpus_dir}/* {self.testcase_dir}")

    def test(self):
        if self.protocol == "OSPF":
            util.protocol = "ospf"
        else:
            util.protocol = "isis"
        util.test()
    
    def collect(self):
        if self.protocol == "OSPF":
            util.test_container_cmd("sh -c 'cd /home/frr/topo-fuzz/test/topo_test/data/coverage/ospfd && lcov --capture --directory . --output-file coverage.info'")
            os.system(f"sudo docker cp docker_topo-fuzz-test_1:/home/frr/topo-fuzz/test/topo_test/data/coverage/ospfd/coverage.info {coverage_dir}/todiff/ospf/coverage_{self.id}.info")
        else:
            util.test_container_cmd("sh -c 'cd /home/frr/topo-fuzz/test/topo_test/data/coverage/isisd && lcov --capture --directory . --output-file coverage.info'")
            os.system(f"sudo docker cp docker_topo-fuzz-test_1:/home/frr/topo-fuzz/test/topo_test/data/coverage/isisd/coverage.info {coverage_dir}/todiff/isis/coverage_{self.id}.info")

class topoTestsTest():
    def __init__(self, id, protocol):
        self.id = id    
        self.protocol = protocol
    def prepare(self):
        pass
    ospf_test_cases = ["ospf_topo1", "ospf_topo2"]
    isis_test_cases = ["isis_topo1"]
    
    def test(self):
        if self.protocol == "OSPF":
            util.test_topotests(self.ospf_test_cases) 
        else:
            util.test_topotests(self.isis_test_cases)
           
    def collect(self):
        if self.protocol == "OSPF":
            util.test_container_cmd("sh -c 'cd /tmp/topotests/gcda/ospfd && lcov --capture --directory . --output-file coverage.info'")
            os.system(f"sudo docker cp docker_topo-fuzz-test_1:/tmp/topotests/gcda/ospfd/coverage.info {coverage_dir}/topotests/ospf")
        else:
            util.test_container_cmd("sh -c 'cd /tmp/topotests/gcda/isisd && lcov --capture --directory . --output-file coverage.info'")
            os.system(f"sudo docker cp docker_topo-fuzz-test_1:/tmp/topotests/gcda/isisd/coverage.info {coverage_dir}/topotests/isis")

class fuzzingTest():
    def __init__(self, id, protocol):
        self.id = id    
        self.protocol = protocol
    test_time = 30
    def prepare(self):
        pass
    def test(self):
        util.test_fuzzing(self.protocol, self.test_time)
    
    def collect(self):
        if (self.protocol == "OSPF"):
            util.fuzz_container_cmd("sh -c 'cd /home/frr/frr/ospfd && llvm-profdata merge -sparse default.profraw -o fuzz_target.profdata && llvm-cov export ./ospfd -instr-profile=fuzz_target.profdata -format=lcov > coverage.info'")
            os.system(f"sudo docker cp docker_topo-fuzz-fuzzing_1:/home/frr/frr/ospfd/coverage.info {coverage_dir}/ossfuzz/ospf/coverage_{self.id}.info")
        else:
            util.fuzz_container_cmd("sh -c 'cd /home/frr/frr/isisd && llvm-profdata merge -sparse default.profraw -o fuzz_target.profdata && llvm-cov export ./isisd -instr-profile=fuzz_target.profdata -format=lcov > coverage.info'")
            os.system(f"sudo docker cp docker_topo-fuzz-fuzzing_1:/home/frr/frr/isisd/coverage.info {coverage_dir}/ossfuzz/isis/coverage_{self.id}.info")
#init()
# t = toDiffTest(0, "OSPF")
# t.collect()
t = fuzzingTest(0, "OSPF")
t.collect()
#print(util.test_fuzzing("OSPF", 2))
