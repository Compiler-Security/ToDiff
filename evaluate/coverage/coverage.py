from os import path
import os
import sys
import util
import subprocess
import re
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

extract_ospf = """
lcov --extract {src_info} \
  '*/ospfd/ospf_abr.c' \
  '*/ospfd/ospf_errors.c' \
  '*/ospfd/ospf_flood.c' \
  '*/ospfd/ospf_ia.c' \
  '*/ospfd/ospf_interface.c' \
  '*/ospfd/ospf_ism.c' \
  '*/ospfd/ospf_lsa.c' \
  '*/ospfd/ospf_lsdb.c' \
  '*/ospfd/ospf_main.c' \
  '*/ospfd/ospf_memory.c' \
  '*/ospfd/ospf_neighbor.c' \
  '*/ospfd/ospf_network.c' \
  '*/ospfd/ospf_nsm.c' \
  '*/ospfd/ospf_packet.c' \
  '*/ospfd/ospf_route.c' \
  '*/ospfd/ospf_spf.c' \
  '*/ospfd/ospfd.c' \
  --rc lcov_branch_coverage=1
"""

extract_isis = """
    lcov --extract {src_info} \
    '*/isisd/isis_circuit.c' \
    '*/isisd/isis_csm.c' \
    '*/isisd/isis_dr.c' \
    '*/isisd/isis_errors.c' \
    '*/isisd/isis_events.c' \
    '*/isisd/isis_flags.c' \
    '*/isisd/isis_lsp.c' \
    '*/isisd/isis_main.c' \
    '*/isisd/isis_misc.c' \
    '*/isisd/isis_mt.c' \
    '*/isisd/isis_nb_notifications.c' \
    '*/isisd/isis_pdu.c' \
    '*/isisd/isis_pfpacket.c' \
    '*/isisd/isis_route.c' \
    '*/isisd/isis_tx_queue.c' \
    '*/isisd/iso_checksum.c' \
    --rc lcov_branch_coverage=1
"""

def run_cmd(command):
    result = subprocess.run(command, shell=True, capture_output=True, text=True)
    return result

def calc_coverage(protocol, data_dir):
    extract_cmd = ""
    if  protocol == "OSPF":
        extract_cmd = extract_ospf
    else:
        extract_cmd = extract_isis
    lines_ave = []

    for file_name in os.listdir(data_dir):
        if ".info" in file_name:
            extract_cmd_use = extract_cmd.format(src_info=file_name)
            res = run_cmd(f"sh -c 'cd {data_dir} && {extract_cmd_use}'")
            lines_match = re.search(r'lines\.{6,}:\s+(\d+\.\d+)%', res.stderr)
            #functions_match = re.search(r'functions\.{2,}:\s+(\d+\.\d+)%', res.stderr)

            lines_coverage = float(lines_match.group(1)) if lines_match else None
            #functions_coverage = float(functions_match.group(1)) if functions_match else None
            lines_ave.append(lines_coverage)
    if (len(lines_ave) > 0):
        return sum(lines_ave) / len(lines_ave)
    else:
        return 0

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
            os.system(f"cd {self.ospf_coverage_data_dir} && sudo rm -f *.gcda")
            os.system(f"cd {self.testcase_dir} && sudo rm -f *")
            os.system(f"cp -r {self.ospf_corpus_dir}/* {self.testcase_dir}")
        else:
            os.system(f"cd {self.isis_coverage_data_dir} && sudo rm -f *.gcda")
            os.system(f"cd {self.testcase_dir} && sudo rm -f *")
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
            util.test_container_cmd("sh -c 'cd /home/frr/topo-fuzz/test/topo_test/data/coverage/ospfd && genhtml coverage.info --output-directory out'")
            os.system(f"sudo docker cp docker_topo-fuzz-test_1:/home/frr/topo-fuzz/test/topo_test/data/coverage/ospfd/coverage.info {coverage_dir}/todiff/ospf/coverage_{self.id}.info")
            os.system(f"sudo docker cp docker_topo-fuzz-test_1:/home/frr/topo-fuzz/test/topo_test/data/coverage/ospfd/out {coverage_dir}/todiff/ospf/out_{self.id}")
        else:
            util.test_container_cmd("sh -c 'cd /home/frr/topo-fuzz/test/topo_test/data/coverage/isisd && lcov --capture --directory . --output-file coverage.info'")
            util.test_container_cmd("sh -c 'cd /home/frr/topo-fuzz/test/topo_test/data/coverage/isisd && genhtml coverage.info --output-directory out'")
            os.system(f"sudo docker cp docker_topo-fuzz-test_1:/home/frr/topo-fuzz/test/topo_test/data/coverage/isisd/coverage.info {coverage_dir}/todiff/isis/coverage_{self.id}.info")
            os.system(f"sudo docker cp docker_topo-fuzz-test_1:/home/frr/topo-fuzz/test/topo_test/data/coverage/isisd/out {coverage_dir}/todiff/isis/out_{self.id}")
    
    def calc(self):
        data_dir = ""
        if self.protocol == "OSPF":
            data_dir = f"{coverage_dir}/todiff/ospf"
        else:
            data_dir = f"{coverage_dir}/todiff/isis"
        return calc_coverage(self.protocol, data_dir)

        
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
            util.test_container_cmd("sh -c 'cd /tmp/topotests/gcda/ospfd && genhtml coverage.info --output-directory out'")
            os.system(f"sudo docker cp docker_topo-fuzz-test_1:/tmp/topotests/gcda/ospfd/coverage.info {coverage_dir}/topotests/ospf/coverage_{self.id}.info")
            os.system(f"sudo docker cp docker_topo-fuzz-test_1:/tmp/topotests/gcda/ospfd/out {coverage_dir}/topotests/ospf/out_{self.id}")
        else:
            util.test_container_cmd("sh -c 'cd /tmp/topotests/gcda/isisd && lcov --capture --directory . --output-file coverage.info'")
            util.test_container_cmd("sh -c 'cd /tmp/topotests/gcda/isisd && genhtml coverage.info --output-directory out'")
            os.system(f"sudo docker cp docker_topo-fuzz-test_1:/tmp/topotests/gcda/isisd/coverage.info {coverage_dir}/topotests/isis/coverage_{self.id}.info")
            os.system(f"sudo docker cp docker_topo-fuzz-test_1:/tmp/topotests/gcda/isisd/out {coverage_dir}/topotests/isis/out_{self.id}")
    def calc(self):
        data_dir = ""
        if self.protocol == "OSPF":
            data_dir = f"{coverage_dir}/topotests/ospf"
        else:
            data_dir = f"{coverage_dir}/topotests/isis"
        return calc_coverage(self.protocol, data_dir)
class fuzzingTest():
    def __init__(self, id, protocol):
        self.id = id    
        self.protocol = protocol
    test_time = 60
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
    def calc(self):
        data_dir = ""
        if self.protocol == "OSPF":
            data_dir = f"{coverage_dir}/ossfuzz/ospf"
        else:
            data_dir = f"{coverage_dir}/ossfuzz/isis"
        return calc_coverage(self.protocol, data_dir)
#init()
t = toDiffTest(0, "ISIS")
t.prepare()
t.test()
t.collect()
# t.collect()
#t = toDiffTest(0, "ISIS")
#t = toDiffTest(0, "OSPF")
# t = fuzzingTest(0, "ISIS")
# #t.prepare()
# t.test()
# t.collect()
# print(t.calc())

# for i in range(1, 10):
#     for j in ["OSPF", "ISIS"]:
#         for k in [toDiffTest, topoTestsTest, fuzzingTest]:
#             if k is toDiffTest and j == "ISIS": continue
#             with open(f"{coverage_dir}/track.txt", "a") as fp:
#                 fp.write(f"{i} {j} {k}\n")
#             t = k(i, j)
#             t.prepare()
#             t.test()
#             t.collect()
#     t = toDiffTest(i, "OSPF")


# for j in ["OSPF", "ISIS"]:
#     for k in [toDiffTest, topoTestsTest, fuzzingTest]:
#             t = k(0, j)
#             print(j, k, t.calc())