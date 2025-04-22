import os
from os import path
up = path.dirname

#topo-fuzz/
dockerDir = up(up(up(path.abspath(__file__))))
#topo-fuzz/test/topo_test/
dataDir = path.join(dockerDir,"test", "topo_test")


gridNum = 10
mxWaitTime = 60
minWaitTime = 10
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
        #launchTestContainers(idx)
        print(f"+test {testName} start {idx}")
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
    buildTestContainers()
    launchTestContainers(1)
    for testcase in testcases:
        print(launch_one_topo_test(testcase))

def launch_fuzzing(protocol, time):
    daemon = ""
    if protocol == "OSPF":
        daemon = "ospfd"
        return fuzz_container_cmd(f"sh -c 'cd /home/frr/frr/{daemon} && ASAN_OPTIONS=detect_leaks=0 ./{daemon} -max_total_time={time}'")
    else:
        daemon = "isisd"
        return fuzz_container_cmd(f"sh -c 'cd /home/frr/frr/{daemon} && ASAN_OPTIONS=detect_leaks=0 ./{daemon} -max_total_time={time}'")
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
    worker_test_confs = [[] for i in range(0, gridNum)]
    print(worker_test_confs)
    for i in range(0, len(test_confs)):
        worker_test_confs[i % gridNum].append(test_confs[i])
    print(worker_test_confs)
    # for i in range(0, gridNum):
    #     if i == gridNum - 1:
    #         worker_test_confs.append(test_confs[i * worker_length:])
    #     else:
    #         worker_test_confs.append(test_confs[i * worker_length:(i + 1) * worker_length])
    
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

def extract_live_functions_for_file(info_path, target_file):
    covered_funcs = set()
    in_target = False

    with open(info_path, 'r', encoding='utf-8') as f:
        for line in f:
            line = line.strip()
            if line.startswith("SF:"):
                in_target = (line[3:] == target_file)
            elif in_target and line.startswith("FNDA:"):
                count, func_name = line[5:].split(",", 1)
                if int(float(count)) > 0:
                    covered_funcs.add(func_name)

    return covered_funcs

def get_mixed_line_counts(info_path, target_source):
    with open(info_path, 'r') as f:
        content = f.read()

    file_blocks = content.split("end_of_record")
    result = {}
    total_hit_lines = 0

    for block in file_blocks:
        if f"SF:{target_source}" not in block:
            continue

        lines = block.strip().splitlines()
        functions = []
        line_hits = {}

        for line in lines:
            if line.startswith("FN:"):
                parts = line[3:].split(",")
                functions.append({"line": int(parts[0]), "name": parts[1]})
            elif line.startswith("DA:"):
                lnum, hits = map(int, line[3:].split(","))
                line_hits[lnum] = hits

        total_hit_lines += sum(1 for h in line_hits.values() if h > 0)

        # 按起始行排序函数
        functions.sort(key=lambda f: f["line"])
        line_numbers = sorted(line_hits.keys())

        for i, func in enumerate(functions):
            name = func["name"]
            start = func["line"]
            end = functions[i + 1]["line"] if i + 1 < len(functions) else max(line_numbers, default=start)

            total_lines = [ln for ln in line_numbers if start <= ln < end]
            hit_lines = [ln for ln in total_lines if line_hits[ln] > 0]

            if hit_lines:
                result[name] = len(hit_lines)  # 命中函数：只算 hit 行
            else:
                result[name] = len(total_lines)  # 未命中函数：算全部行

    return result, total_hit_lines