import sys
from os import path
path_to_add = path.dirname(path.dirname(path.dirname(path.abspath(__file__))))
if path_to_add not in sys.path:
    sys.path.append(path_to_add)
import argparse
from src.restful_mininet.exec.executor import executor

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("-t", "--testFile", type=str, required = True, help = "path of the test file")
    parser.add_argument("-o", "--outputDir", type=str, required = True, help = "dir of test output")
    args = parser.parse_args()
    e = executor(args.testFile, args.outputDir)
    exit(e.test())