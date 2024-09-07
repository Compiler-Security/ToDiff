#sudo docker-compose -f docker/docker-compose.yml up --scale topo-fuzz-dev=6 -d topo-fuzz-dev 

import os
from os import path
from path import abspath
os.chdir(path.abspath(__file__))

path.abspath