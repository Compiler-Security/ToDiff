# ToDiff

Todiff is a prototype implementation of the technique know as ToDiff. It aims to validate IGP protocols via equivalent topology synthesis.

## Build
```bash
    sh build_test.sh
```
One should update proxy in `docker/docker-compose.yml`
## Test

### Step1: generate equivalent topological program
```bash 
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
