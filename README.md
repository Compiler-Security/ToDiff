# ToDiff

Todiff is a prototype implementation of the technique known as ToDiff. It aims to validate IGP protocols via equivalent topology synthesis.

## Install
```bash
cd ~
#java
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install gradle

#docker
apt install docker
apt install docker-compose
sudo chmod 666 /var/run/docker.sock
```

## Build
```bash
    sh build_test.sh
```
One should update the proxy in `docker/docker-compose.yml`
## Test

## Step 1: Generating equivalent topological programs
```bash 
    sh test/topo-test/init.sh
    python3 test/topo_test/gen_test.py
``` 

## Step 2: Simulating the network
```bash
    python3 test/topo-test/test.py
```

## Step 3: Differentiating the results
```bash
    python3 test/topo-test/checker/diffTest.py
```

The results are in the test/topo-test/checker/data/check/diff
