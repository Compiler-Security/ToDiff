import os
import subprocess
import threading
import argparse
from os import path
import threading

print_lock = threading.Lock()

def safe_print(*args, **kwargs):
    with print_lock:
        print(*args, **kwargs)
# Directory helper
up = path.dirname

# Determine directories
dockerDir = up(up(up(path.abspath(__file__))))  # topo-fuzz/
dataDir = up(path.abspath(__file__))            # topo-fuzz/test/topo_test/

def getContainerName(num):
    return f"docker_topo-fuzz-test_{num}"

def _run_test_sh(cmd):
    os.chdir(dockerDir)
    return os.system(f"sh {cmd}")

def buildTestContainers():
    assert _run_test_sh("build_test.sh") == 0, "Building containers failed."

def launchTestContainers(grid):
    assert _run_test_sh("stop_test.sh") == 0, "Stopping containers failed."
    assert _run_test_sh(f"run_test.sh {grid}") == 0, "Launching containers failed."

def getAllConfs():
    confDir = path.join(dataDir, "data", "testConf")
    return os.listdir(confDir)

def choseConf(confName, threshold):
    try:
        timeidx = int(confName.split(".")[0][4:])
        return timeidx > threshold
    except Exception:
        return False  # Skip malformed file names

def launch_test(testName, idx, mxWaitTime, minWaitTime, protocol):
    command = (
        f"docker exec -it docker_topo-fuzz-test_{idx} "
        f"python3 topo-fuzz/src/restful_mininet/main.py "
        f"-t /home/frr/topo-fuzz/test/topo_test/data/testConf/{testName} "
        f"-o /home/frr/topo-fuzz/test/topo_test/data/result "
        f"-w {mxWaitTime} -m {minWaitTime} -p {protocol}"
    )
    result = subprocess.run(command, shell=True, capture_output=True, text=True)
    return result

def worker_test(testNames, idx, mxWaitTime, minWaitTime, protocol):
    for testName in testNames:
        safe_print(f"[Container {idx}] + Test {testName} started".ljust(80))
        result = launch_test(testName, idx, mxWaitTime, minWaitTime, protocol)
        with open(path.join(dataDir, "data", "running", testName.replace("json", "txt")), "w") as fp:
            fp.write(result.stdout)
            fp.write("\n")
            fp.write(result.stderr)
        safe_print(f"[Container {idx}] - Test {testName} completed".ljust(80))

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Run distributed topology tests in Docker containers.")
    parser.add_argument('--grid_num', type=int, default=10, help='Number of parallel test containers')
    parser.add_argument('--mx_wait', type=int, default=600, help='Maximum wait time for each test')
    parser.add_argument('--min_wait', type=int, default=30, help='Minimum wait time before result checking')
    parser.add_argument('--protocol', type=str, default='ospf', help='Routing protocol:[ospf, isis, rip, babel, openfabric]')
    parser.add_argument('--timestamp_threshold', type=int, default=1726732934, help='Only run tests created after this timestamp')

    args = parser.parse_args()

    # Step 1: Prepare test containers
    buildTestContainers()
    launchTestContainers(args.grid_num)

    # Step 2: Collect and filter test configs
    test_confs = [conf for conf in getAllConfs() if choseConf(conf, args.timestamp_threshold)]
    print(f"Total selected test cases: {len(test_confs)}")

    # Step 3: Divide test configs among containers
    worker_length = len(test_confs) // args.grid_num
    worker_test_confs = [[] for _ in range(args.grid_num)]
    for i, conf in enumerate(test_confs):
        worker_test_confs[i % args.grid_num].append(conf)

    # Step 4: Launch threads for each container
    threads = []
    for i in range(args.grid_num):
        thread = threading.Thread(
            target=worker_test,
            args=(worker_test_confs[i], i + 1, args.mx_wait, args.min_wait, args.protocol)
        )
        threads.append(thread)

    for thread in threads:
        thread.start()

    for thread in threads:
        thread.join()
