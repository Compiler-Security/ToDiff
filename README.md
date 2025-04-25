# ToDiff

[Todiff](https://todiff.github.io/index.html) is a prototype implementation of the technique know as ToDiff. It aims to validate Interior Gateway Protocols(a key class of routing protocols) via equivalent topology synthesis.



## Build
### Environment:

Linux Systems: Ubuntu 24.04 is recommended.

### Dependency:

*  gradle 8.13+

You can use the following command to install gradle:
```bash
$ curl -s "https://get.sdkman.io" | bash
$ source "$HOME/.sdkman/bin/sdkman-init.sh"
$ sdk install gradle
```

*  docker 28.1+
*  docker-compose 1.29.2+
*  python 3.10+
*  lcov 2.0+

You can use the following command to install lcov on Ubuntu: 
```bash
$ apt install lcov
```

## ToDiff project
FIXME: we download ToDiff to the `topo-fuzz` directory for historical reason.
```bash
$ git clone https://github.com/Compiler-Security/ToDiff.git topo-fuzz
$ cd topo-fuzz
```



## Run

### Overview

ToDiff performs differential testing in three steps:

**Step 1**: Generate valid yet random topologies along with their corresponding equivalent topological programs.

**Step 2**: Simulate the network, inject the topological programs into routing protocol implementations, and collect the execution output.

**Step 3**: Compare the outputs and analyze the root causes of any discrepancies.

These steps are handled by three separate components: `generator`, `executor`, and `checker`.

We demonstrate the full workflow using the OSPF protocol as an example.

---

## Initialization

Before running tests, create the necessary testing directories:

```bash
$ cd topo-fuzz/test/topo_test
$ sh init.sh
```

This script creates a directory structure under `test/topo_test/data` as follows:

```
data/
├── testConf/   # output from the generator
├── running/    # intermediate data from the executor
├── result/     # execution output from the executor
└── check/      # discrepancy reports from the checker
```

Next, build the Docker-based testing environment:

```bash
$ cd topo-fuzz
$ sh build_test.sh
```

---

## Step 1: Run Generator

This step uses the `generator` (written in Java) to generate random yet valid topologies and corresponding equivalent topological programs. The source code is located in `src/generator`.

### How to Run

```bash
$ cd topo-fuzz/test/topo_test
$ python3 gen_test.py --protocol ospf
```

Use `--help` to view all available options for `gen_test.py`.

### Output

The generator outputs test cases in the `testConf/` directory. Each file (e.g., `testXXX.json`) contains a single topology and multiple equivalent programs. These will be passed to the executor in the next step.

Each test case follows a specific JSON format. Detailed documentation is available in the developer guide. For easier inspection, you can use `humanread.py` (see Step 3).

---

## Step 2: Run Executor

This step simulates the network defined by each test case, runs the topological programs on the routing protocol implementation, and records the output after convergence.

The executor is based on the Mininet framework and is implemented in Python (`src/restful_minient`). It runs in parallel using Docker containers.

### How to Run

```bash
$ cd topo-fuzz/test/topo_test
$ python3 test.py --protocol=ospf --grid_num=10
```

- `--grid_num` specifies the number of test cases to run in parallel.
- Use `--help` to list all supported arguments.

### Output

Execution results are saved in the `data/result/` directory. Each result file is a JSON document representing the protocol's output for a specific test case.

---

## Step 3: Run Checker

This step compares the outputs of equivalent programs and identifies discrepancies. The checker is implemented in Python at `test/topo_test/checker/diffTest.py`.

### How to Run

```bash
$ cd topo-fuzz/test/topo_test/checker
$ python3 diffTest.py --protocol=ospf
```

Results are saved in `test/topo_test/data/check/diff/`, with each result file named like `testXXX.json.txt`.

Each file compares the outputs of all equivalent programs against the first one, router by router. Example output:

```
====== round 1 ======
>>>>> +check check_ospfDaemon <<<<<
----- router r0 -----
{
    "areas": {
        "0.0.0.2": {
            "lsaSummaryNumber": {
                "from": 23,
                "to": 26
            }
        }
    }
}
```

In this example, the `lsaSummaryNumber` field under `ospfDaemon` differs between the first and second topological programs.

For root cause analysis, you may use a program slicer to trace the relevant variable and execution path. Debug the two executions step-by-step to locate where internal states start to diverge.

---

### Auxiliary Scripts

Several helper scripts are available for analysis:

- `humanread.py`: Converts test cases from JSON to human-readable `.txt` format.  
  Path: `test/topo_test/checker/humanread.py`

- `shrinktest.py`: Extracts specific topological programs from a test case for focused analysis.  
  Path: `test/topo_test/checker/shrinktest.py`
