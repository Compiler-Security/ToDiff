import os
from os import path
up = path.dirname

#topo-fuzz/
dockerDir = up(up(up(path.abspath(__file__))))
#topo-fuzz/test/topo_test/
dataDir = up(path.abspath(__file__))

test_num = 10

def _run_test_sh(cmd):
    os.chdir(dockerDir)
    return os.system(f"sh {cmd}")

def genTestCases(test_num):
    for i in range(0, test_num):
        _run_test_sh("run_generator.sh")

if __name__ == "__main__":
    genTestCases(test_num)