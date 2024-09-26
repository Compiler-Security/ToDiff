import os
import json
from os import path

#topo-fuzz/test/topo_test/
up = path.dirname
resultDir = path.join(up(up(path.abspath(__file__))), "data", "result")
import io
import difftest
import util
def checkTest(test_name):
    result_path = path.join(util.get_result_dir(test_name), util.get_result_name(test_name))
    print(result_path)
    diff = difftest.diffTest(result_path)
    print(test_name)
    diff.check()

def checkAllTestConfig():
    for test_name in util.get_all_test_name():
        checkTest(test_name)
        
if __name__ == "__main__":
    checkAllTestConfig()
