#!/bin/bash

# 使用方式: ./link-gcno.sh <gcda目录> <gcno目录>

set -e

if [ $# -ne 2 ]; then
    echo "用法: $0 <gcda目录> <gcno目录>"
    exit 1
fi

gcdadir="$1"
bdir="$2"

cd "$gcdadir"

echo "当前目录: $(pwd)"
echo "构建目录: $bdir"
echo "开始链接 .gcno 文件..."

find . -name '*.gcda' | while read -r f; do
    # 去掉前缀 ./ （如果有）
    f=${f#./}
    # 替换后缀为 .gcno
    gcno_file=${f%.gcda}.gcno
    # 创建符号链接
    ln -fs "$bdir/$gcno_file" "$gcno_file"
    # 同步时间戳
    touch -h -r "$bdir/$gcno_file" "$gcno_file"
    echo "链接: $gcno_file"
done

echo "全部完成。"
