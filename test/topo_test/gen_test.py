import os
from os import path
import argparse

# Define parent directory helper
up = path.dirname

# Define paths
dockerDir = path.join(up(up(up(path.abspath(__file__)))), "script")  # Path to topo-fuzz/
dataDir = up(path.abspath(__file__))            # Path to topo-fuzz/test/topo_test/

def _run_test_sh(cmd):
    os.chdir(dockerDir)
    return os.system(f"sh {cmd}")

def genTestCases(test_num, protocol, router_count, mx_degree, program_num):
    for i in range(test_num):
        _run_test_sh(f"run_generator.sh {protocol} {router_count} {mx_degree} {program_num}")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Generate topology test cases")
    parser.add_argument('--test_num', type=int, default=10, help='Number of test cases to generate')
    parser.add_argument('--protocol', type=str, default='ospf', help='Routing protocols: [ospf,isis,rip,babel,openfabric]')
    parser.add_argument('--router_count', type=int, default=7, help='Number of routers')
    parser.add_argument('--mx_degree', type=int, default=5, help='Maximum number of router interfaces')
    parser.add_argument('--program_num', type=int, default=3, help='Number of equivalent topological programs per test case')

    args = parser.parse_args()

    genTestCases(args.test_num, args.protocol, args.router_count, args.mx_degree, args.program_num)