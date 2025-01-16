import matplotlib.pyplot as plt
import numpy as np
import os
from os import path

fontsize = 28
def draw(proto):
    old_max = [0] * 15
    old_min = [100000] * 15
    old_ave = [0] * 15
    new_max = [0] * 15
    new_min = [100000] * 15
    new_ave = [0] * 15
    s = 0
    for file_name in os.listdir("/home/binshui/topo-fuzz/evaluate/convergeTime"):
        if proto in file_name and "data" in file_name and "txt" in file_name:
            with open(path.join("/home/binshui/topo-fuzz/evaluate/convergeTime", file_name)) as fp:
                s += 1
                for i in range(0, 15):
                    st = fp.readline()
                    old = eval(st.split()[0])
                    new = eval(st.split()[1])
                    old_max[i] = max(old, old_max[i])
                    old_min[i] = min(old, old_min[i])
                    new_max[i] = max(new, new_max[i])
                    new_min[i] = min(new, new_min[i])
                    old_ave[i] += old
                    new_ave[i] += new

    old_ave = [ x / s for x in old_ave]
    
    new_ave = [ x / s for x in new_ave]

    speedup = [174/x for x in new_ave]
    print(max(speedup), min(speedup))
    line = [174] * 15

    # 示例数据
    # y_max = [57.80657339, 69.56292534, 76.92910123, 95.46807003, 92.07082725, 105.7555771, 101.4376967, 171.3718381, 107.0808733, 123.7524199, 174.1986735, 127.0516717, 167.4439015, 138.6839726, 127.9131517]  # x轴数据
    # y_min = [56.5365417, 69.39407015, 75.8545208, 91.22194004, 90.86493564, 95.39927602, 96.80461621, 165.0227177, 95.72830582, 115.7259135, 169.0809958, 119.4244812, 139.6700342, 130.3188691, 121.8991098]
    x = list(range(1, 16))

    # # 计算均值
    # y_mean = [56.92275381, 69.48777699, 76.50851202, 92.05782382, 91.5172139, 99.10957845, 97.90858356, 166.6724117, 102.337654, 120.897657, 170.0186621, 122.8375781, 150.1246681, 132.6328754, 125.4738121]

    # y1_max = [36.3701117, 40.03208137, 76.99437165, 55.97672176, 67.39711475, 60.93855, 68.826545, 84.85700727, 71.14668226, 95.06840062, 84.78187227, 118.4262335, 104.7446589, 94.05964375, 129.5928342]
    # y1_min = [36.19565558, 39.33017135, 46.63418937, 54.60919523, 64.21081114, 53.88199759, 66.46981597, 80.05897832, 66.4642241, 75.60619736, 76.64544082, 84.79382253, 103.166909, 85.29944205, 83.59878564]
    # y1_mean = [36.29007308, 39.58198063, 56.91003482, 55.27215393, 66.02057767, 56.82138379, 67.57739449, 83.02431703, 69.50848309, 84.8662804, 81.84855858, 97.83841681, 103.7008288, 88.63986564, 104.0445371]
    # 创建折线图
    
    scale = 1
    plt.figure(figsize=(12 /scale, 6.5/scale))

    plt.rcParams.update({
    'font.family': 'Times New Roman',
    'font.size': fontsize,
    'font.weight': 'bold',
    'text.color': 'black',  # 设置全局字体颜色为黑色
    'axes.labelcolor': 'black',  # 坐标轴标签颜色
    'xtick.color': 'black',  # x轴刻度颜色
    'ytick.color': 'black',  # y轴刻度颜色
    })

    
    
    plt.plot(x, old_max, label='average w/o acceleration', color='b', linestyle=':', linewidth=4)
    #plt.fill_between(x, old_min, old_max, color='b', alpha=0.2, label='min-max w/o acceleration')  # 填充最大值和最小值之间的区域

    plt.plot(x, new_ave, label='average with acceleration', color='g', linestyle='--', linewidth=4)

    plt.fill_between(x, new_min, new_max, color='g', alpha=0.2, label='min-max with acceleration')  # 填充最大值和最小值之间的区域


    plt.plot(x, line, label='convergence oracle', color='r', linewidth=3)

    #plt.legend(loc='upper left')

    # 添加标题和标签
    plt.xlabel('# Routers', fontweight='bold', labelpad=15)
    plt.ylabel('Execution Time (s)', fontweight='bold', labelpad= 10)

    # 显示图例
    plt.legend(loc='upper left', bbox_to_anchor=(-0.005, 0.95), fontsize=fontsize - 3, frameon=False)

    plt.xticks(np.arange(1, 16))
    
    plt.tight_layout()
    #长宽
   


    plt.savefig(f"/home/binshui/topo-fuzz/evaluate/convergeTime/c{proto}.png", format='png')
    plt.savefig(f"/home/binshui/topo-fuzz/evaluate/convergeTime/c{proto}.pdf", format='pdf')
draw("OSPF")