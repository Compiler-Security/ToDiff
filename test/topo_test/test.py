import os
from os import path
up = path.dirname

#topo-fuzz/
dockerDir = up(up(up(path.abspath(__file__))))
#topo-fuzz/test/topo_test/
dataDir = up(path.abspath(__file__))

gridNum = 5
mxWaitTime = 60
minWaitTime = 20

def getContainerName(num):
    return f"docker_topo-fuzz-test_{num}"


def _run_test_sh(cmd):
    os.chdir(dockerDir)
    return os.system(f"sh {cmd}")

def buildTestContainers():
    assert  _run_test_sh(f"build_test.sh")== 0, "buildContainers fail"

def launchTestContainers(grid):
    assert _run_test_sh(f"run_test.sh {grid}")== 0, "runContainers fail"

def getAllConfs():
    confDir = path.join(dataDir, "data", "testConf")
    return os.listdir(confDir)

def choseConf(confName):
    return True

import subprocess
def launch_test(testName, idx):
    #FIXME the PATH should be relative
    command = f"docker exec -it docker_topo-fuzz-test_{idx} python3 topo-fuzz/src/restful_mininet/main.py -t /home/frr/topo-fuzz/test/topo_test/data/testConf/{testName} -o /home/frr/topo-fuzz/test/topo_test/data/result -w {mxWaitTime} -m {minWaitTime}"
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
if __name__ == "__main__":
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