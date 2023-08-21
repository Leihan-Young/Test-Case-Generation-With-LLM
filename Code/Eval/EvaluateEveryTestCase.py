import sys
import getopt
import os
import env
import logging
import utils
import runTest as rt
import getTestCode as gtc
import time
import javalang
import numpy as np
from tabulate import tabulate
import csv
from tqdm import tqdm

def state(res_in_bug, res_in_fix):
    if res_in_bug == env.PARSE_FAILED or res_in_fix == env.PARSE_FAILED:
        return 1
    elif res_in_bug == env.BUILD_FAILED or res_in_fix == env.BUILD_FAILED:
        return 2
    elif res_in_bug == env.TEST_SUCCESS and res_in_fix == env.TEST_FAILED:
        return 3
    elif res_in_bug == env.TEST_FAILED and res_in_fix == env.TEST_FAILED:
        return 4
    elif res_in_bug == env.TEST_SUCCESS and res_in_fix == env.TEST_SUCCESS:
        return 5
    elif res_in_bug == env.TEST_FAILED and res_in_fix == env.TEST_SUCCESS:
        return 6
    else:
        return 7
    

def eval(argv):
    output_path = ""
    java_rt_path = ""
    temp_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'temp')
    debug_flag = False
    response_path = ""
    code_path = ""
    skip_flag = False
    opts, args = getopt.getopt(argv[1:], "hDSc:r:o:t:j:", ["help", "debug", "skip", "code_path=", "response_path=", "output_path=", "temp_path=", "java_rt_path="])
    for opt, arg in opts:
        if opt in ("-h", "--help"):
            print('python main.py -c <code_path> -o <output_path> -r <response_path> -j <java_rt_path> [-D] [-t <temp_path>]')
            print('temp_path will be the current path in default.')
            sys.exit()
        elif opt in ("-o", "--output_path"):
            output_path = arg
        elif opt in ("-D", "--debug"):
            debug_flag = True
        elif opt in ("-t", "--temp_path"):
            temp_path = arg
        elif opt in ("-r", "--response_path"):
            response_path = arg
        elif opt in ("-j", "--java_rt_path"):
            java_rt_path = arg
        elif opt in ("-c", "--code_path"):
            code_path = arg
        elif opt in ("-S", "--skip"):
            skip_flag = True
    if response_path == "":
        print("response_path is needed")
        sys.exit()
    if output_path == "":
        print("output_path is needed")
        sys.exit()
    if java_rt_path == "" or not os.path.exists(java_rt_path):
        print("valid java_rt_path is needed")
        sys.exit()
    if code_path == "":
        print("code_path is needed")
        sys.exit()

    env.PATH_SPLITER = os.sep
    env.LLM = ('gpt-3.5-turbo', 'use-output')
    env.BUILD_FAILED = 'Build failed'
    env.TEST_FAILED = 'Test failed'
    env.TEST_SUCCESS = 'Test passed'
    env.PARSE_FAILED = 'Parse failed'
    env.INVOKE_LLM_FAILED = 'Invoke LLM failed'
    env.PREPARATION_FAILED = 'Preparation failed'
    env.UNEXPECTED_ERROR = 'Unexpected error'
    env.ASSERT_METHODS = ('assertTrue', 'assertFalse', 'fail', 'assertEquals', 'assertNotEquals', 'assertArrayEquals', 'assertNotNull', 'assertNull', 'assertSame', 'assertNotSame')
    env.ORG_TESTING_ASSERT_METHODS = ('assertThrows',)
    if response_path[-1] == '/':
        response_path = response_path[0:-1]
    if code_path[-1] == '/':
        code_path = code_path[0:-1]
    if output_path[-1] == '/':
        output_path = output_path[0:-1]
    if os.path.exists(f'.{env.PATH_SPLITER}reprocess-logs'):
        os.makedirs(f'.{env.PATH_SPLITER}reprocess-logs-bak-{time.strftime("%Y-%m-%d-%H-%M-%S")}')
        os.system(f'mv .{env.PATH_SPLITER}reprocess-logs{env.PATH_SPLITER}* .{env.PATH_SPLITER}reprocess-logs-bak-{time.strftime("%Y-%m-%d-%H-%M-%S")}')
    if not os.path.exists(f'.{env.PATH_SPLITER}reprocess-logs'):
        os.makedirs(f'.{env.PATH_SPLITER}reprocess-logs')
    if debug_flag:
        log_file = f'reprocess-logs{env.PATH_SPLITER}root-debug.log'
        logging.basicConfig(filename=log_file, filemode='w', level=logging.DEBUG)

    if not skip_flag:
        # get all java inner classes
        env.JAVA_INNER_CLASSES = utils.getJavaInnerClasses(java_rt_path)

        test_class_paths = {}
        for root, dirs, files in tqdm(list(os.walk(code_path))):
            for file in files:
                if file != 'triggerCode.java':
                    continue
                try:
                    with open(os.path.join(root, file), 'r', encoding=utils.checkEncoding(os.path.join(root, file)), errors='ignore') as f:
                        tree = javalang.parse.parse(f.read())
                    project = root[len(code_path) + 1:].split(os.sep)[0]
                    bug_num = root[len(code_path) + 1:].split(os.sep)[1]
                    t = tree.package.name + "." + tree.children[2][0].name
                    t = t.replace('.', os.sep) + '.java'
                    test_class_paths[project + bug_num] = t
                except Exception:
                    continue

        every_response = []
        project_single_response = {}

        for root, dirs, files in tqdm(list(os.walk(response_path))):
            for file in files:
                if file.find('test_response') == -1:
                    continue
                to_parse = root[len(response_path) + 1:]
                project = to_parse.split(os.sep)[0]
                bug_num = to_parse.split(os.sep)[1]
                if not os.path.exists(os.path.join(os.path.join(temp_path, project), bug_num + 'b')):
                    rt.checkOutProject(project, bug_num, True, temp_path)
                if not os.path.exists(os.path.join(os.path.join(temp_path, project), bug_num + 'f')):
                    rt.checkOutProject(project, bug_num, False, temp_path)
                with open(os.path.join(root, file), 'r', encoding=utils.checkEncoding(os.path.join(root, file)), errors='ignore') as f:
                    file_lines = f.readlines()
                sign = project + bug_num
                if sign not in test_class_paths:
                    continue
                try:
                    trigger_code = gtc.constructFullTestCodeFromLines(file_lines)
                    _, res_in_bug = rt.runTestCodeInWorkDir(trigger_code, os.path.join(os.path.join(temp_path, project), bug_num + 'b'), test_class_paths[project + bug_num], logging)
                    _, res_in_fix = rt.runTestCodeInWorkDir(trigger_code, os.path.join(os.path.join(temp_path, project), bug_num + 'f'), test_class_paths[project + bug_num], logging)
                except Exception as e:
                    logging.exception(e)
                    res_in_bug = res_in_fix = env.UNEXPECTED_ERROR
                logging.debug(f'res_in_bug={res_in_bug},res_in_fix={res_in_fix}')
                every_response.append((res_in_bug, res_in_fix))
                sign = project + bug_num
                if sign not in project_single_response:
                    project_single_response[sign] = (res_in_bug, res_in_fix)
                else:
                    (old_res_in_bug, old_res_in_fix) = project_single_response[sign]
                    if state(old_res_in_bug, old_res_in_fix) < state(res_in_bug, res_in_fix):
                        project_single_response[sign] = (res_in_bug, res_in_fix)
        
        np.save(os.path.join(output_path, "every_response.npy"), every_response)
        np.save(os.path.join(output_path, "single_project.npy"), project_single_response)

    else:
        every_response = np.load(os.path.join(output_path, "every_response.npy"), allow_pickle=True)
        project_single_response = np.load(os.path.join(output_path, "single_project.npy"), allow_pickle=True).item()

    unit_success_count = 0
    unit_build_failed_count = 0
    unit_prepare_failed_count = 0
    unit_parse_failed_count = 0
    unit_invoke_llm_failed_count = 0
    unit_bug_found = 0
    unit_fp = 0
    unit_tn = 0
    unit_fn = 0
    unit_unexpected_error_count = 0
    for (unit_res_in_bug, unit_res_in_fix) in every_response:
            if unit_res_in_bug == env.BUILD_FAILED or unit_res_in_fix == env.BUILD_FAILED:
                unit_build_failed_count += 1
            elif unit_res_in_bug == env.PARSE_FAILED or unit_res_in_fix == env.PARSE_FAILED:
                unit_parse_failed_count += 1
            elif unit_res_in_bug == env.INVOKE_LLM_FAILED or unit_res_in_fix == env.INVOKE_LLM_FAILED:
                unit_invoke_llm_failed_count += 1
            elif unit_res_in_bug == env.PREPARATION_FAILED or unit_res_in_fix == env.PREPARATION_FAILED:
                unit_prepare_failed_count += 1
            elif unit_res_in_bug == env.UNEXPECTED_ERROR or unit_res_in_fix == env.UNEXPECTED_ERROR:
                unit_unexpected_error_count += 1
            else:
                unit_success_count += 1
                if unit_res_in_bug == env.TEST_FAILED:
                    if unit_res_in_fix == env.TEST_FAILED:
                        unit_fp += 1
                    else:
                        unit_bug_found += 1
                else:
                    if unit_res_in_fix == env.TEST_FAILED:
                        unit_fn += 1
                    else:
                        unit_tn += 1
    table_dict = {}
    table_dict['True Positive Count'] = [unit_bug_found]
    table_dict['False Positive Count'] = [unit_fp]
    table_dict['True Negative Count'] = [unit_tn]
    table_dict['False Negative Count'] = [unit_fn]
    table_dict['Test Count'] = [unit_success_count]
    table_dict['Prepare Failed Count'] = [unit_prepare_failed_count]
    table_dict['Invoke LLM Failed Count'] = [unit_invoke_llm_failed_count]
    table_dict['Parse Failed Count'] = [unit_parse_failed_count]
    table_dict['Build Failed Count'] = [unit_build_failed_count]
    table_dict['Unexpected Error Count'] = [unit_unexpected_error_count]

    print('every response:')
    print(tabulate(table_dict, headers='keys', tablefmt='fancy_grid'))

    data = [[], [], []]
    for key, values in table_dict.items():
        data[0].append(key)
        data[1].append(values[0])

    with open(os.path.join(output_path, 'result_every_response.csv'), 'w', newline='') as output_file:
        writer = csv.writer(output_file)
        for line in data:
            writer.writerow(line)

    unit_success_count = 0
    unit_build_failed_count = 0
    unit_prepare_failed_count = 0
    unit_parse_failed_count = 0
    unit_invoke_llm_failed_count = 0
    unit_bug_found = 0
    unit_fp = 0
    unit_tn = 0
    unit_fn = 0
    unit_unexpected_error_count = 0
    for (unit_res_in_bug, unit_res_in_fix) in project_single_response.values():
            if unit_res_in_bug == env.BUILD_FAILED or unit_res_in_fix == env.BUILD_FAILED:
                unit_build_failed_count += 1
            elif unit_res_in_bug == env.PARSE_FAILED or unit_res_in_fix == env.PARSE_FAILED:
                unit_parse_failed_count += 1
            elif unit_res_in_bug == env.INVOKE_LLM_FAILED or unit_res_in_fix == env.INVOKE_LLM_FAILED:
                unit_invoke_llm_failed_count += 1
            elif unit_res_in_bug == env.PREPARATION_FAILED or unit_res_in_fix == env.PREPARATION_FAILED:
                unit_prepare_failed_count += 1
            elif unit_res_in_bug == env.UNEXPECTED_ERROR or unit_res_in_fix == env.UNEXPECTED_ERROR:
                unit_unexpected_error_count += 1
            else:
                unit_success_count += 1
                if unit_res_in_bug == env.TEST_FAILED:
                    if unit_res_in_fix == env.TEST_FAILED:
                        unit_fp += 1
                    else:
                        unit_bug_found += 1
                else:
                    if unit_res_in_fix == env.TEST_FAILED:
                        unit_fn += 1
                    else:
                        unit_tn += 1
    table_dict = {}
    table_dict['True Positive Count'] = [unit_bug_found]
    table_dict['False Positive Count'] = [unit_fp]
    table_dict['True Negative Count'] = [unit_tn]
    table_dict['False Negative Count'] = [unit_fn]
    table_dict['Test Count'] = [unit_success_count]
    table_dict['Prepare Failed Count'] = [unit_prepare_failed_count]
    table_dict['Invoke LLM Failed Count'] = [unit_invoke_llm_failed_count]
    table_dict['Parse Failed Count'] = [unit_parse_failed_count]
    table_dict['Build Failed Count'] = [unit_build_failed_count]
    table_dict['Unexpected Error Count'] = [unit_unexpected_error_count]

    print('project single:')
    print(tabulate(table_dict, headers='keys', tablefmt='fancy_grid'))

    data = [[], [], []]
    for key, values in table_dict.items():
        data[0].append(key)
        data[1].append(values[0])

    with open(os.path.join(output_path, 'result_every_response.csv'), 'w', newline='') as output_file:
        writer = csv.writer(output_file)
        for line in data:
            writer.writerow(line)

if __name__ == '__main__':
    eval(sys.argv)