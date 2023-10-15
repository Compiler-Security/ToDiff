SCRIPT_PATH="$(readlink -f $0)"
DIR_PATH="$(dirname $SCRIPT_PATH)"

cd "$dirname($(dirname $DIR_PATH))"
docker compose -f docker/docker-compose.yml build > ${DIR_PATH}/build_log.txt 
if [ $? -ne 0 ]; then
  echo "\e[31mBuild Fail\e[0m"
  exit 1  # 你可以设置任何非零的退出状态码
fi

echo "\e[32mBuild Pass\e[0m"

docker compose -f docker/docker-compose.yml run --rm topo-fuzz sh -c "cd test/simple && python3 test_simple.py" > ${DIR_PATH}/test_log.txt 
if [ $? -ne 0 ]; then
  echo "\e[31mTest Fail\e[0m"
  exit 1  # 你可以设置任何非零的退出状态码
fi

echo "T\e[32mest Pass\e[0m"