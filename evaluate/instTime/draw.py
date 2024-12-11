import json
with open("/home/binshui/topo-fuzz/evaluate/instTime/data.json") as fp:
    time = json.load(fp)

import matplotlib.pyplot as plt
from scipy.optimize import curve_fit
import numpy as np
import matplotlib.pyplot as plt
# 示例数据
instruction_counts = [int(val) for val in time.keys()]  # 横轴：指令条数
execution_times = [val for val in time.values()]  # 纵轴：时间


from scipy.optimize import curve_fit

x_data = np.array(instruction_counts)  # 横轴：指令条数
y_data = np.array(execution_times)  # 纵轴：时间

def func(x, a, b, c, d):
    return a * x**3 + b * x**2 + c * x + d

params, _ = curve_fit(func, x_data, y_data)

print(f"拟合参数: a = {params[0]}, b = {params[1]:.4f}, c = {params[2]:.4f}")

x_fit = np.linspace(min(x_data), max(x_data), 100)
y_fit = func(x_fit, *params)

# 绘制折线图
plt.figure(figsize=(8, 6))
plt.plot(x_fit, y_fit, label='Fitted Curve', color='red')
plt.plot(instruction_counts, execution_times, marker='o', linestyle='-', label='Execution Time')
plt.title("Execution Time vs Instruction Count")
plt.xlabel("Number of Instructions")
plt.ylabel("Execution Time (seconds)")
plt.grid(True)
plt.legend()
plt.tight_layout()

# 显示图表
plt.savefig("/home/binshui/topo-fuzz/evaluate/instTime/diagram.png", format='png')