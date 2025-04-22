# ToDiff

Todiff is a prototype implementation of the technique know as ToDiff. It aims to validate Interior Gateway Protocols(a key class of routing protocols) via equivalent topology synthesis.


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
ToDiff has three testing steps:

**Step 1**: Generate valid yet radom topology and correspoding equivalent topological programs.

**Step 2**: Simulate the network, input topological programs to routing protocol implementation, and collect running output.

**Step 3**: Differentiaing the output and anlysis the root cause of discrepancies.

These three steps are implemented to three tools separetely: generator, executor and checker.

Next, we use testing OSPF protocol as an example, to show how to use these three tools to validate IGPs.

## Initialization
Before testing, one should first create directories for testing.
```bash
$ cd topo-fuzz/test/topo_test
$ sh init.sh
```
This script will create testing directories located in `test/topo_test/data` with the following stuctre.
```
data/
   |-- testConf/ # output of generator
   |-- running/ # running information of executor
   |-- result/ # output of executor
   |-- check/ # output of checker
```

Then, one should build the test environment, which are docker container.
```bash
$ cd topo-fuzz
$ sh build_test.sh
```

## Step 1: Run generator
The first step is to generate radom yet valid topology and corresponding equivalent topological programs using generator.

The generator is implemented using JAVA language and the source code is located in src/generator directory.

### How to run
```bash
$ cd topo-fuzz/test/topo_test
$ python3 gen_test.py --protocol ospf
```
One can use `--help` arg to show all the arguments that can be passed to `gen_test.py`

### Output 
The output of generator are test cases located in `testConf` directory. Each test case(`testXXX.json`) contains one topology and multiple equivalent topological programs. These test cases are then input to executor for testing.

The test cases are encoded in sepecific json format and one can get the detailed information of the format in the developer document. We provide tool to read these test cases easily, see Step 3. 


## Step 2: Run 
```bash
    sh build_test.sh
```
One should update proxy in `docker/docker-compose.yml`
## Test

### Step1: generate equivalent topological program
```bash 
    sh test/topo-test/init.sh
    python3 test/topo_test/gen_test.py
``` 

## Step2: simulate network
```bash
    python3 test/topo-test/test.py
```

## Step3: differentiating results
```bash
    python3 test/topo-test/checker/diffTest.py
```

The results are in the test/topo-test/checker/data/check/diff