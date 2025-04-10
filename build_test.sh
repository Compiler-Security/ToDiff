#!/bin/bash

if [ "$1" = "rebuild" ]; then
    # if first arg is rebuild
    sudo docker-compose  -f docker/docker-compose.yml build --no-cache --parallel  topo-fuzz-test 
else
    sudo docker-compose  -f docker/docker-compose.yml build  topo-fuzz-test 
fi