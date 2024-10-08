import copy
##################### compare #############################
def compare_lists(l1, l2):
    """
    Compare two lists which may contain nested dictionaries or lists, considering them as unordered.
    Return a dictionary with differences.
    """
    # Make a deep copy to avoid altering original lists during the removal process
    l1_copy = copy.deepcopy(l1)
    l2_copy = copy.deepcopy(l2)

    # Lists to store unmatched items from both lists
    unmatched_l1 = []
    unmatched_l2 = []

    # Check each item in l1 for a match in l2
    for item1 in l1_copy:
        match_found = False
        for item2 in l2_copy:
            if deep_compare(item1, item2):
                l2_copy.remove(item2)
                match_found = True
                break
        if not match_found:
            unmatched_l1.append(item1)

    # Remaining items in l2_copy are unmatched
    unmatched_l2.extend(l2_copy)

    return {
        'unique_to_first': unmatched_l1,
        'unique_to_second': unmatched_l2
    }

def deep_compare(d1, d2):
    """
    Recursively compare two values which could be dictionaries, lists, or basic datatypes.
    """
    if isinstance(d1, dict) and isinstance(d2, dict):
        return dict_diff(d1, d2) == {}
    elif isinstance(d1, list) and isinstance(d2, list):
        return compare_lists(d1, d2) == {'unique_to_first': [], 'unique_to_second': []}
    else:
        return d1 == d2

import difflib
def str_diff(st1, st2):
    d = difflib.Differ()
    return [x for x in list(d.compare(st1.splitlines(), st2.splitlines())) if x.startswith("- ") or x.startswith("+ ")]

def dict_diff(d1, d2):
    """
    Recursively diff two dictionaries, including nested structures.
    Returns only the differences.
    """
    diff = {}
    keys = set(d1.keys()).union(d2.keys())
    for key in keys:
        if key in d1 and key in d2:
            if isinstance(d1[key], dict) and isinstance(d2[key], dict):
                nested_diff = dict_diff(d1[key], d2[key])
                if nested_diff:
                    diff[key] = nested_diff
            elif isinstance(d1[key], list) and isinstance(d2[key], list):
                list_diff = compare_lists(d1[key], d2[key])
                if list_diff != {'unique_to_first': [], 'unique_to_second': []}:
                    diff[key] = list_diff
            elif d1[key] != d2[key]:
                diff[key] = {'from': d1[key], 'to': d2[key]}
        elif key in d1 or key in d2:
            diff[key] = {'from': d1.get(key, 'Key not present'), 'to': d2.get(key, 'Key not present')}
    return diff if diff else {}

# # Example data
# list1 = [{'a': 1, 'b': [2, 3]}, {'c': 4}]
# list2 = [{'c': 4}, {'a': 1, 'b': [3, 2]}]

# # Using the compare function
# result = compare_lists(list1, list2)
# print(result)


################## path ###############################
import os
import json
import io
from os import path
up = path.dirname
resultDir = path.join(up(up(path.abspath(__file__))), "data", "result")
runningDir = path.join(up(up(path.abspath(__file__))), "data", "running")
confDir = path.join(up(up(path.abspath(__file__))), "data", "testConf")


def get_test_name(last_five_num):
    conf_name = None
    for file_name in os.listdir(confDir):
        if last_five_num == file_name.split(".")[0][-5:]:
            #duplicate name
            if conf_name is not None:
                return None
            else:
                conf_name = file_name
    return conf_name

def get_all_test_name():
    return sorted(os.listdir(confDir))

def get_result_dir(test_name):
    return path.join(resultDir, test_name.split(".")[0])

def get_result_name(test_name):
    return test_name.split(".")[0] + "_res.json"

def get_result_file_path(test_name):
    return path.join(get_result_dir(test_name), get_result_name(test_name))
if __name__ == "__main__":
    print(get_test_name("30842"))