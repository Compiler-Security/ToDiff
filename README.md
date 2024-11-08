# topo-fuzz

## build image for testing
```bash
    sh build.sh
```

## create container and start fuzzing
```bash 
    sh run.sh topo-fuzz
``` 

## stop fuzzing
```bash
    sh stop.sh topo-fuzz
```

## clean
```bash
    sh clean.sh
```

## dev
1.  change args in Dockerfile.dev (proxy)

2. sh clean.sh

3. sh run_dev.sh

4. attach to container, modify file, ** add git in the host!!! **