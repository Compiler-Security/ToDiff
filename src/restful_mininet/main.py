import sys
from os import path
path_to_add = path.dirname(path.dirname(path.dirname(path.abspath(__file__))))
if path_to_add not in sys.path:
    sys.path.append(path_to_add)
import argparse
from src.restful_mininet.exec.executor import executor

#python3 main.py -t /home/frr/topo-fuzz/test/topo_test/data/testConf/test1736318306.json -o /home/frr/topo-fuzz/test/topo_test/data/result -p isis
if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("-t", "--testFile", type=str, required = True, help = "path of the test file")
    parser.add_argument("-o", "--outputDir", type=str, required = True, help = "dir of test output")
    parser.add_argument("-w", "--maxWaitTime", type=int, required= False, default= 120, help = "max wait time default 60")
    parser.add_argument("-m", "--minWaitTime", type=int, required= False, default= 20, help = "min wait time default 20")
    parser.add_argument("-p", "--protocol", type=str, required=True, help= "test protocol [ospf, isis]")
    args = parser.parse_args()
    e = executor(args.testFile, args.outputDir, args.minWaitTime,  args.maxWaitTime, args.protocol)
    exit(e.test())