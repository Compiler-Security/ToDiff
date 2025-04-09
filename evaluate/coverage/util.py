import os
from os import path
up = path.dirname

#topo-fuzz/
dockerDir = up(up(up(path.abspath(__file__))))
#topo-fuzz/test/topo_test/
dataDir = path.join(dockerDir,"test", "topo_test")


gridNum = 10
mxWaitTime = 600
minWaitTime = 30
protocol = "isis"

def getContainerName(num):
    return f"docker_topo-fuzz-test_{num}"


def _run_test_sh(cmd):
    os.chdir(dockerDir)
    return os.system(f"sh {cmd}")

def buildTestContainers():
    assert  _run_test_sh(f"build_test.sh")== 0, "buildContainers fail"

def buildFuzzingContainers():
    assert  _run_test_sh(f"build_fuzzing.sh")== 0, "buildContainers fail"

def launchTestContainers(grid):
    assert _run_test_sh(f"stop_test.sh")== 0, "runContainers fail"
    assert _run_test_sh(f"run_test.sh {grid}")== 0, "runContainers fail"

def lauchFuzzingContainers(grid):
    assert _run_test_sh(f"stop_fuzzing.sh")== 0, "runContainers fail"
    assert _run_test_sh(f"run_fuzzing.sh {grid}")== 0, "runContainers fail"

def getAllConfs():
    confDir = path.join(dataDir, "data", "testConf")
    return os.listdir(confDir)

def choseConf(confName):
    timeidx = int(confName.split(".")[0][4:])
    return timeidx > 1726732934

import subprocess
def launch_test(testName, idx):
    #FIXME the PATH should be relative
    command = f"docker exec -it docker_topo-fuzz-test_{idx} python3 topo-fuzz/src/restful_mininet/main.py -t /home/frr/topo-fuzz/test/topo_test/data/testConf/{testName} -o /home/frr/topo-fuzz/test/topo_test/data/result -w {mxWaitTime} -m {minWaitTime} -p {protocol}"
    result = subprocess.run(command, shell=True, capture_output=True, text=True)
    return result

def worker_test(testNames, idx):
    for testName in testNames:
        print(f"+test {testName} start")
        result = launch_test(testName, idx)
        with open(path.join(dataDir, "data", "running", testName.replace("json", "txt")), "w") as fp:
            fp.write(result.stdout)
            fp.write("\n")
            fp.write(result.stderr)
        print(f"-test {testName} done")
        #TODO handle result

import threading

def test_container_cmd(cmd):
    command = f"docker exec -it docker_topo-fuzz-test_1 {cmd}"
    result = subprocess.run(command, shell=True, capture_output=True, text=True)
    return result 

def launch_one_topo_test(test_name):
    return test_container_cmd(f"sudo -E python3 -m pytest /home/frr/frr/tests/topotests/{test_name} --cov-topotest")

def test_topotests(testcases):
    #buildTestContainers()
    launchTestContainers(1)
    for testcase in testcases:
        print(launch_one_topo_test(testcase))

def launch_fuzzing(protocol, time):
    daemon = ""
    if protocol == "OSPF":
        daemon = "ospfd"
    else:
        daemon = "isisd"
    return fuzz_container_cmd(f"sh -c 'cd /home/frr/frr/{daemon} && ASAN_OPTIONS=detect_leaks=0 ./{daemon} -max_total_time={time}'")

def fuzz_container_cmd(cmd):
    command = f"docker exec -it docker_topo-fuzz-fuzzing_1 {cmd}"
    result = subprocess.run(command, shell=True, capture_output=True, text=True)
    return result

def test_fuzzing(protocol, time):
    buildFuzzingContainers()
    lauchFuzzingContainers(1)
    while True:
        res = launch_fuzzing(protocol, time)
        if "Segmentation fault" not in res.stdout:
            return res

def test():
    #prepare for test
    #   1.run test containers
    buildTestContainers()
    launchTestContainers(gridNum)
    #   2.get all test confs
    test_confs = [conf for conf in getAllConfs() if choseConf(conf) == True]
    print(test_confs)
    #   3.split all confs by grid
    worker_length = len(test_confs) // gridNum
    worker_test_confs = []
    for i in range(0, gridNum):
        if i == gridNum - 1:
            worker_test_confs.append(test_confs[i * worker_length:])
        else:
            worker_test_confs.append(test_confs[i * worker_length:(i + 1) * worker_length])
    
    #test
    #   1.prepare threads
    threads = []
    for i in range(0, gridNum):
        thread = threading.Thread(target=worker_test, args=[worker_test_confs[i], i + 1])
        threads.append(thread)
    #   2.launch threads
    for thread in threads:
        thread.start()
    #   3. join threads
    for thread in threads:
        thread.join()

 #docker exec -it docker_topo-fuzz-test_1 python3 topo-fuzz/src/restful_mininet/main.py -t /home/frr/topo-fuzz/test/excutor_test/frr_conf/all8.conf -o /home/frr/topo-fuzz/test/excutor_test/frr_conf/tmp -w 3