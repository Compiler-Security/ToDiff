def extract_functions_for_file(info_path, target_file):
    covered_funcs = set()
    in_target = False

    with open(info_path, 'r', encoding='utf-8') as f:
        for line in f:
            line = line.strip()
            if line.startswith("SF:"):
                in_target = (line[3:] == target_file)
            elif in_target and line.startswith("FNDA:"):
                count, func_name = line[5:].split(",", 1)
                if int(float(count)) > 0:
                    covered_funcs.add(func_name)

    return covered_funcs


# 设定路径
#info_file = "/home/binshui/topo-fuzz/evaluate/coverage/todiff/isis/coverage_0.info"  # LCOV 的输出文件路径
info_file = "/home/binshui/topo-fuzz/evaluate/coverage/topotests/isis/coverage.info"
target_source = "/home/frr/frr/isisd/isis_nb_config.c"

# 执行提取
covered = extract_functions_for_file(info_file, target_source)

print(f"✅ 文件 {target_source} 中被覆盖的函数有：")
for func in sorted(covered):
    print(f"  - {func}")


# def extract_single_file_coverage(full_info_path, target_source_file, output_info_path):
#     keep = False
#     output_lines = []
#     section_lines = []

#     with open(full_info_path, 'r', encoding='utf-8') as f:
#         for line in f:
#             if line.startswith("TN:") or line.startswith("end_of_record"):
#                 section_lines.append(line)
#             elif line.startswith("SF:"):
#                 # 开始新的源文件段落
#                 if section_lines:
#                     if keep:
#                         output_lines.extend(section_lines)
#                     section_lines = []
#                 keep = (line.strip()[3:] == target_source_file)
#                 section_lines = [line]
#             else:
#                 section_lines.append(line)

#         # 最后一组也要判断是否保留
#         if keep and section_lines:
#             output_lines.extend(section_lines)

#     # 写入新 .info 文件
#     with open(output_info_path, 'w', encoding='utf-8') as out:
#         out.writelines(output_lines)

#     print(f"✅ 已生成文件：{output_info_path}")


# def extract_selected_functions_coverage(
#     full_info_path, target_source_file, selected_functions, output_info_path
# ):
#     keep = False
#     section_lines = []
#     output_lines = []
#     selected_functions = set(selected_functions)

#     with open(full_info_path, 'r', encoding='utf-8') as f:
#         for line in f:
#             line = line.strip()
#             if line.startswith("TN:") or line == "end_of_record":
#                 section_lines.append(line + "\n")
#             elif line.startswith("SF:"):
#                 # 新段开始，先处理上一段
#                 if keep and section_lines:
#                     output_lines.extend(section_lines)
#                 # 重置
#                 section_lines = [line + "\n"]
#                 keep = (line[3:] == target_source_file)
#             elif keep:
#                 # 保留被选中的函数
#                 if line.startswith("FN:"):
#                     _, func_name = line.split(",", 1)
#                     if func_name in selected_functions:
#                         section_lines.append(line + "\n")
#                 elif line.startswith("FNDA:"):
#                     _, func_name = line.split(",", 1)
#                     if func_name in selected_functions:
#                         section_lines.append(line + "\n")
#                 # 其他信息（如 DA/BRDA/FNF/FNH）暂时保留
#                 elif line.startswith(("DA:", "BRDA:", "FNF:", "FNH:", "LF:", "LH:", "BRF:", "BRH:")):
#                     section_lines.append(line + "\n")

#         # 最后一组判断
#         if keep and section_lines:
#             output_lines.extend(section_lines)

#     with open(output_info_path, 'w', encoding='utf-8') as out:
#         out.writelines(output_lines)

#     print(f"✅ 已生成只包含指定函数的新 coverage 文件：{output_info_path}")

# # 设置路径
# full_info = "/home/binshui/topo-fuzz/evaluate/coverage/topotests/ospf/coverage_0.info"  # 原始 LCOV 文件
# target_source = "/home/frr/frr/isisd/isis_nb_config.c"
# output_info = "/home/binshui/topo-fuzz/evaluate/coverage/topotests/ospf/isis_nb_config.info"  # 输出的新 info 文件

# def get_functions(str):
#     return [x[4:] for x in str.split("\n")][1:-1]
# # 执行提取

# extract_selected_functions_coverage(
#     full_info_path= full_info,
#     target_source_file="/home/frr/frr/isisd/isis_nb_config.c",
#     selected_functions= get_functions("""
#   - isis_instance_advertise_passive_only_modify
#   - isis_instance_area_address_create
#   - isis_instance_create
#   - isis_instance_is_type_modify
#   - lib_interface_isis_area_tag_modify
#   - lib_interface_isis_circuit_type_modify
#   - lib_interface_isis_create
#   - lib_interface_isis_network_type_modify
#   - lib_interface_isis_passive_modify
#   - sysid_iter_cb
#                     """),
#     output_info_path=output_info
# )