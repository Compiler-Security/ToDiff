import matplotlib.pyplot as plt
import numpy as np
import json

from matplotlib.ticker import ScalarFormatter, LogLocator, FixedLocator, FuncFormatter

fontsize = 24
lp = 20
def write(protocol):
# 读取 JSON 数据
    with open(f"/home/binshui/topo-fuzz/evaluate/totalTime/data{protocol}.json") as fp:
        res = json.load(fp)

    plt.rcParams.update({
    'font.family': 'Times New Roman',
    'font.size': fontsize,
    'font.weight': 'bold',
    'text.color': 'black',  # 设置全局字体颜色为黑色
    'axes.labelcolor': 'black',  # 坐标轴标签颜色
    'xtick.color': 'black',  # x轴刻度颜色
    'ytick.color': 'black',  # y轴刻度颜色
    })
    # 获取路由器数量和时间数据
    router_count = np.array([int(key) for key in res.keys()])  # 路由器数量
    #part4_time = np.random.uniform(0.1, 0.2, 15)
    part3_time = np.array([res[key]["graphTime"] for key in res.keys()])  # 第一部分运行时间
    part2_time = np.array([res[key]["genTime"] for key in res.keys()])  # 第二部分运行时间
    part1_time = np.array([res[key]["runTime"] for key in res.keys()])  # 第三部分运行时间
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

    scale = 1.21
    # 创建图形
    fig, ax1 = plt.subplots(figsize=(12 * scale, 6.5 * scale))

    
    # 绘制堆叠柱状图
    #ax1.bar(x_pos, part4_time, label='Differentiating Results Time', color='orange')
    ax1.bar(x_pos, part3_time, label='1: Network Generation', color='#FFD966')
    ax1.bar(x_pos, part2_time, bottom=part3_time, label='2: Topology Synthesis', color='lightcoral')
    ax1.bar(x_pos, part1_time, bottom=part2_time + part3_time, label='3: Network Execution', color='#9DC3E6')



    # 设置左轴标签
    ax1.set_xlabel('# Routers', labelpad=15, fontweight='bold')
    ax1.set_ylabel('Execution Time (s)', color='black', fontweight='bold')
    #ax1.set_title('Four Stages Time and Command Count for Different Router Counts')

    # 设置横轴刻度
    ax1.set_xticks(x_pos)
    ax1.set_xticklabels(router_count)

    # 添加右侧的y轴
    # ax2 = ax1.twinx()  # 创建一个共享x轴的右侧y轴
    # ax2.plot(x_pos, command_count, label='# Commands', color='green', marker='o', linestyle='-', linewidth=2)
    # ax2.set_ylabel('# Commands', color='black', labelpad=lp)

    #ax1.set_ylim(0, 14)  # 左侧 y 轴范围（根据需要调整，例如 0 到 20 秒）
    #ax2.set_ylim(0, 5000) 

    # 设置图例
    #ax1.legend(loc='upper left', bbox_to_anchor=(0, 1.0), fontsize=fontsize - 2)
    ax1.legend(loc='upper center', bbox_to_anchor=(0.47, 1.23), ncol=3, fontsize=fontsize - 3, frameon=False)
    #ax2.legend(loc='lower left', bbox_to_anchor=(0, 0.9), fontsize=fontsize - 6)

    fig.subplots_adjust(right=0.85) 
    # 自动调整布局
    fig.tight_layout()

    def custom_formatter(x, pos):
        if x == 0.1:
            return "0.1"
        elif x in [1, 10, 100]:
            return f"{int(x)}"
        return ""

    locator = FixedLocator([0.1, 1, 10, 100])  # 设定固定刻度位置
    formatter = FuncFormatter(custom_formatter)  # 自定义格式化器
    #ax1.set_ylim(1, 200)  # 设置对数刻度范围
    # ax1.set_yticks([0.1, 0.5, 1, 2, 5, 10, 20])  # 不平均刻度
    # ax1.set_yticklabels(['', '0.5', '1', '2', '5', '10', '20'])]
    ax1.set_yscale('log')
    ax1.yaxis.set_major_locator(locator)
    ax1.yaxis.set_major_formatter(formatter)
    #ax1.set_yticks([1, 2, 5, 10, 20, 50, 110])
    # 显示图形
    plt.savefig(f"/home/binshui/topo-fuzz/evaluate/totalTime/diagram{protocol}.pdf", format='pdf')
    plt.savefig(f"/home/binshui/topo-fuzz/evaluate/totalTime/diagram{protocol}.png", format='png')
    # 显示图形
    plt.show()

write("OSPF")
write("ISIS")
