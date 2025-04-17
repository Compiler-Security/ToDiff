import matplotlib.pyplot as plt
import numpy as np
import json

# 读取 JSON 数据
with open("/home/binshui/topo-fuzz/evaluate/totalTime/dataISIS.json") as fp:
    res = json.load(fp)

# 获取路由器数量和时间数据
router_count = np.array([int(key) for key in res.keys()])  # 路由器数量
#part4_time = np.random.uniform(0.1, 0.2, 15)
part3_time = np.array([res[key]["graphTime"] for key in res.keys()])  # 第一部分运行时间
part2_time = np.array([res[key]["genTime"] for key in res.keys()])  # 第二部分运行时间
part1_time = np.array([res[key]["runTime"] / 15 for key in res.keys()])  # 第三部分运行时间
t1 = part1_time.sum()
t2 = part2_time.sum()
t3 = part3_time.sum()
#t4 = part4_time.sum()
s = t1 + t2 + t3
print(t1 / s, t2 / s, t3 / s)
#part1_time = np.array([0 for key in res.keys()]) 
# 假设生成的指令数量在数据中也有对应字段（如果有的话）
# 如果没有，替换为你实际的数据
command_count = np.array([res[key]["totalInstruction"] for key in res.keys()])  # 生成的指令数量
print(command_count.sum() / 8 / 15)
# 创建一个新的x轴位置用于绘制柱状图
x_pos = np.arange(len(router_count))  # 为每个刻度点生成一个相同间距的索引

# 创建图形
fig, ax1 = plt.subplots(figsize=(12, 6))

# 绘制堆叠柱状图
#ax1.bar(x_pos, part4_time, label='Differentiating Results Time', color='orange')
ax1.bar(x_pos, part3_time, bottom=part1_time + part2_time, label='Step1: Valid Network Generation', color='gold')
ax1.bar(x_pos, part2_time, bottom=part1_time, label='Stage2: Topology Synthesis', color='lightcoral')
ax1.bar(x_pos, part1_time, label='Stage3: Network Execution \n And Result Comparison', color='skyblue')



# 设置左轴标签
ax1.set_xlabel('# Routers')
ax1.set_ylabel('Execution Time (s)', color='black')
#ax1.set_title('Four Stages Time and Command Count for Different Router Counts')

# 设置横轴刻度
ax1.set_xticks(x_pos)
ax1.set_xticklabels(router_count)

# 添加右侧的y轴
ax2 = ax1.twinx()  # 创建一个共享x轴的右侧y轴
ax2.plot(x_pos, command_count, label='# Commands', color='green', marker='o', linestyle='-', linewidth=2)
ax2.set_ylabel('Command Count', color='black')

# 设置图例
ax1.legend(loc='upper left', bbox_to_anchor=(0, 1.0), fontsize=10)
ax2.legend(loc='lower left', bbox_to_anchor=(0, 0.65), fontsize=10)

fig.subplots_adjust(right=0.85) 
# 自动调整布局
fig.tight_layout()

# 显示图形
plt.savefig("/home/binshui/topo-fuzz/evaluate/totalTime/diagramOSPF.png", format='png')

# 显示图形
plt.show()
