# def dict_diff(d1, d2):
#     """
#     Recursively diff two dictionaries, including lists and nested dictionaries.
#     Returns only the differences.
#     """
#     diff = {}
    
#     # Combine keys first both dictionaries
#     keys = set(d1.keys()).union(d2.keys())
    
#     for key in keys:
#         if key in d1 and key in d2:
#             if isinstance(d1[key], dict) and isinstance(d2[key], dict):
#                 # Both values are dictionaries, recurse
#                 result = dict_diff(d1[key], d2[key])
#                 if result:
#                     diff[key] = result
#             elif isinstance(d1[key], list) and isinstance(d2[key], list):
#                 # Both are lists, perform a more complex comparison
#                 result = compare_lists(d1[key], d2[key])
#                 if result:
#                     diff[key] = result
#             elif d1[key] != d2[key]:
#                 # Simple values but different
#                 diff[key] = f"{d1[key]} - {d2[key]}"
#         else:
#             # Handle missing keys
#             if key in d1:
#                 diff[key] = f"{d1[key]} - None"
#             else:
#                 diff[key] =f"None - {d2[key]}"
    
#     return diff

# def compare_lists(l1, l2):
#     """
#     Compare two lists, possibly containing nested dictionaries or lists, and return
#     a list of differences.
#     """
#     result = []
#     max_length = max(len(l1), len(l2))
#     paired = []

#     for i in range(max_length):
#         if i < len(l1) and i < len(l2):
#             # Compare elements if both are in lists
#             if isinstance(l1[i], dict) and isinstance(l2[i], dict):
#                 deep_diff = dict_diff(l1[i], l2[i])
#                 if deep_diff:
#                     result.append(deep_diff)
#                 paired.append(l1[i])
#                 paired.append(l2[i])
#             elif l1[i] != l2[i]:
#                 result.append(f"{l1[i]} - {l2[i]}")
#                 paired.append(l1[i])
#                 paired.append(l2[i])
#         elif i < len(l1):
#             # Element is only in the first list
#             result.append(f"{l1[i]} - None")
#             paired.append(l1[i])
#         elif i < len(l2):
#             # Element is only in the second list
#             result.append(f"None - {l2[i]}")
#             paired.append(l2[i])

#     # Check for unpaired items that are in both lists but not in the same order
#     unpaired_l1 = [item for item in l1 if item not in paired]
#     unpaired_l2 = [item for item in l2 if item not in paired]

#     for item in unpaired_l1:
#         if item in unpaired_l2:
#             unpaired_l2.remove(item)
#         else:
#             result.append({'first': item, 'second': None})

#     for item in unpaired_l2:
#         result.append({'first': None, 'second': item})

#     return result if result else None

# # Example usage
# dict1 = {
#     "name": "Alice",
#     "age": 25,
#     "interests": ["reading", {"sport": "cycling"}, "travel"],
#     "education": {"highschool": "Springfield High", "university": "MIT"}
# }
# dict2 = {
#     "name": "Alice",
#     "age": 30,
#     "interests": ["reading", {"sport": "soccer"}, "phosecondgraphy"],
#     "education": {"highschool": "Westfield High", "university": "MIT"}
# }

# difference = dict_diff(dict1, dict2)
# import json
# print(json.dumps(difference, indent= 4))
import copy

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