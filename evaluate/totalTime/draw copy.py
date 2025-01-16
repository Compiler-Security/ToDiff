# import matplotlib.pyplot as plt
# import numpy as np
# import json

# with open("/home/binshui/topo-fuzz/evaluate/totalTime/data.json") as fp:
#     res = json.load(fp)

# # 示例数据
# router_count = np.array([int(key) for key in res.keys()])  # 路由器数量
# x_pos = np.arange(len(router_count)) 
# part3_time = [res[key]["graphTime"] for key in res.keys()]  # 第一部分运行时间
# part2_time = [res[key]["genTime"] for key in res.keys()] # 第二部分运行时间
# part1_time = [res[key]["runTime"] / 10 for key in res.keys()]  # 第三部分运行时间

# # 绘制堆叠柱状图
# plt.bar(router_count, part1_time, label='Part 1', color='skyblue')
# plt.bar(router_count, part2_time, bottom=part1_time, label='Part 2', color='lightcoral')
# plt.bar(router_count, part3_time, bottom=np.array(part1_time) + np.array(part2_time), label='Part 3', color='gold')

# plt.xticks(x_pos, router_count)
# # 添加标签
# plt.xlabel('Router Count')
# plt.ylabel('Execution Time')
# plt.title('Execution Time for Different Router Counts')
# plt.legend()

import matplotlib.pyplot as plt
import numpy as np

import json

with open("/home/binshui/topo-fuzz/evaluate/totalTime/data.json") as fp:
    res = json.load(fp)
# 示例数据
router_count = np.array([1, 2, 5, 7, 10, 12, 13, 16, 18, 20])  # 自定义的横轴刻度点
part3_time = np.array([res[key]["graphTime"] for key in res.keys()])  # 第一部分运行时间
part2_time = np.array([res[key]["genTime"] for key in res.keys()]) # 第二部分运行时间
part1_time = np.array([res[key]["runTime"] / 10 for key in res.keys()])  # 第三部分运行时间

# 创建一个新的x轴位置用于绘制柱状图
x_pos = np.arange(len(router_count))  # 为每个刻度点生成一个相同间距的索引

# 绘制堆叠柱状图
plt.bar(x_pos, part1_time, label='Part 1', color='skyblue')
plt.bar(x_pos, part2_time, bottom=part1_time, label='Part 2', color='#9DC3E6')
plt.bar(x_pos, part3_time, bottom=part1_time + part2_time, label='Part 3', color='#FFD966')

# 设置横轴刻度
plt.xticks(x_pos, router_count)  # 使用自定义的刻度点

# 添加标签
plt.xlabel('Router Count')
plt.ylabel('Execution Time')
plt.title('Execution Time for Different Router Counts')
plt.legend()

# 显示图形
plt.savefig("/home/binshui/topo-fuzz/evaluate/totalTime/diagram.png", format='png')