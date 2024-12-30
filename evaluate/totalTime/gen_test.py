import os
from os import path
up = path.dirname

#topo-fuzz/
dockerDir = up(up(up(path.abspath(__file__))))

test_num = 20

def _run_test_sh(cmd):
    os.chdir(dockerDir)
    return os.system(f"sh {cmd}")
import time
def genTestCases(test_num):
    #rNum =[1,2,5,7,10,12,13,16,18,20]
    for i in range(0, test_num):
        time.sleep(2)
        _run_test_sh(f"run_generator_evaluate.sh {i + 1}")

if __name__ == "__main__":
    genTestCases(test_num)