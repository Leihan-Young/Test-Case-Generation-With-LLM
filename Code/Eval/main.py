import sys
import getopt
import os
import env
import logging
import getData as gd
import constructPrompt as cp
import invokeLLMs
import getTestCode as gtc
import runTest as rt
import time
import csv
import utils
from tabulate import tabulate
from multiprocessing import cpu_count, Process, Manager
from joblib import Parallel, delayed
from datetime import datetime

def counting(length, share_value):
    while True:
        print(f'Total:{length}, Finished:{share_value.get()}, Current:{share_value.get()/length * 100}%')
        time.sleep(5)

def testProcedure(bug_folder, code_path, temp_path, share_value):
    bug_folder_analyze = bug_folder.split(env.PATH_SPLITER)
    project = bug_folder_analyze[0]
    bug_number = bug_folder_analyze[1]
    logger = logging.getLogger(f'{project}-{bug_number}')
    logger.setLevel(level=logging.DEBUG)
    handler = logging.FileHandler(f'logs{env.PATH_SPLITER}{project}-{bug_number}.log')
    logger.addHandler(handler)
    try:
        logger.debug('bug_folder=' + bug_folder)
        trigger_code = gd.getTriggerCode(os.path.join(code_path, bug_folder)).readlines()
        logger.debug(f'project={project},bug_number={bug_number}')
        logger.info(f'Checking out {project} {bug_number}b...')
        rt.checkOutProject(project, bug_number, True, os.path.join(temp_path, 'src'))
        logger.info(f'Finished checking out {project} {bug_number}b.')
        unit_res_in_bug = rt.runDeveloperTest(trigger_code, os.path.join(os.path.join(os.path.join(temp_path, 'src'), project), f'{bug_number}b'), logger)
        oracle_res_in_bug = unit_res_in_bug
        logger.info(f'Checking out {project} {bug_number}f...')
        rt.checkOutProject(project, bug_number, False, os.path.join(temp_path, 'src'))
        logger.info(f'Finished checking out {project} {bug_number}f.')
        unit_res_in_fix = rt.runDeveloperTest(trigger_code, os.path.join(os.path.join(os.path.join(temp_path, 'src'), project), f'{bug_number}f'), logger)
        oracle_res_in_fix = unit_res_in_fix
        rt.removeCheckedOutProject(project, bug_number, os.path.join(temp_path, 'src'))
        share_value.set(share_value.get() + 1)
        logger.debug('unit_res_in_bug=' + unit_res_in_bug + ",unit_res_in_fix=" + unit_res_in_fix + ",oracle_res_in_bug=" + oracle_res_in_bug + ",oracle_res_in_fix=" + oracle_res_in_fix)
        return (unit_res_in_bug, unit_res_in_fix, oracle_res_in_bug, oracle_res_in_fix)
    except Exception:
        logger.error(f'Unexpected Error in {project} {bug_number}')
        return (env.UNEXPECTED_ERROR, env.UNEXPECTED_ERROR, env.UNEXPECTED_ERROR, env.UNEXPECTED_ERROR)

def procedure(bug_folder, code_path, output_path, llm, temp_path, share_value, debug_flag):
    bug_folder_analyze = bug_folder.split(env.PATH_SPLITER)
    project = bug_folder_analyze[0]
    bug_number = bug_folder_analyze[1]
    logger = logging.getLogger(f'{project}-{bug_number}')
    logger.setLevel(level=(logging.DEBUG if debug_flag else logging.INFO))
    handler = logging.FileHandler(f'logs{env.PATH_SPLITER}{project}-{bug_number}.log')
    logger.addHandler(handler)

    try:
        unit_flag = True
        oracle_flag = True

        # skip oracle test
        oracle_flag = False
        oracle_res_in_bug = oracle_res_in_fix = env.PREPARATION_FAILED

        logger.info(f'Checking out {project} {bug_number}b...')
        rt.checkOutProject(project, bug_number, True, os.path.join(temp_path, 'src'))
        logger.info(f'Finished checking out {project} {bug_number}b.')
        logger.info(f'Checking out {project} {bug_number}f...')
        rt.checkOutProject(project, bug_number, False, os.path.join(temp_path, 'src'))
        logger.info(f'Finished checking out {project} {bug_number}f.')

        logger.info(f'Getting {project}-{bug_number} focal context...')
        try:
            focal_context = gd.getFocalContext(os.path.join(code_path, bug_folder))
            examples = gd.getExamples(os.path.join(code_path, bug_folder), os.path.join(os.path.join(os.path.join(temp_path, 'src'), project), f'{bug_number}b'), env.EXAMPLE_COUNT)
        except Exception:
            logger.error(f"Fail to get focal_context in {bug_folder}")
            focal_context = None
            unit_flag = False
            unit_res_in_bug = env.PREPARATION_FAILED
            unit_res_in_fix = env.PREPARATION_FAILED
            
        logger.debug(f'focal_context={"".join(focal_context) if focal_context != None else "None"}')

        logger.info(f'Getting {project}-{bug_number} test prefix...')
        try:
            oracle_focal_context = gd.getOracleFocalMethod(os.path.join(code_path, bug_folder))
            test_class_path, test_prefix = gd.getTestPrefix(os.path.join(code_path, bug_folder))
        except:
            logger.error(f'Fail to get test_prefix in {bug_folder}')
            test_class_path = gd.tryToGetTestClassPath(os.path.join(code_path, bug_folder))
            test_prefix = None
            oracle_flag = False
            oracle_res_in_bug = env.PREPARATION_FAILED
            oracle_res_in_fix = env.PREPARATION_FAILED
        logger.debug(f'oracle_focal_context={"".join(oracle_focal_context) if oracle_focal_context != None else "None"}')
        logger.debug(f'test_class_path={test_class_path}, test_prefix={"".join(test_prefix) if test_prefix != None else "None"}')

        if focal_context == None or test_class_path == None:
            logger.error(f"Fail to get focal_context and test_prefix in {bug_folder}")
            unit_flag = False
            unit_res_in_bug = env.PREPARATION_FAILED
            unit_res_in_fix = env.PREPARATION_FAILED
        if test_prefix == None or oracle_focal_context == None:
            logger.error(f'Fail to get test_prefix in {bug_folder}')
            oracle_flag = False
            oracle_res_in_bug = env.PREPARATION_FAILED
            oracle_res_in_fix = env.PREPARATION_FAILED

        # DEBUG
        # if not os.path.exists(os.path.join(output_path, bug_folder)):
        #     os.makedirs(os.path.join(output_path, bug_folder))
        # if unit_flag:
        #     unit_test_prompt = cp.constructSimpleUnitTestPrompt(focal_context)
        #     with open(os.path.join(os.path.join(output_path, bug_folder), 'unit_test_prompt.md'), 'w') as f:
        #         f.write(unit_test_prompt[0]['content'])
        # if oracle_flag:
        #     oracle_test_prompt = cp.constructSimpleOracleTestPrompt(focal_context, test_prefix)
        #     with open(os.path.join(os.path.join(output_path, bug_folder), 'oracle_test_prompt.md'), 'w') as f:
        #         f.write(oracle_test_prompt[0]['content'])
        # share_value.set(share_value.get() + 1)
        # if unit_flag:
        #     unit_res_in_bug = unit_res_in_fix = env.INVOKE_LLM_FAILED
        # if oracle_flag:
        #     oracle_res_in_bug = oracle_res_in_fix = env.INVOKE_LLM_FAILED
        # return (unit_res_in_bug, unit_res_in_fix, oracle_res_in_bug, oracle_res_in_fix)

        if llm == 'use-output':
            if unit_flag:
                if os.path.exists(os.path.join(os.path.join(output_path, bug_folder), 'unit_test.java')):
                    with open(os.path.join(os.path.join(output_path, bug_folder), 'unit_test.java'), 'r', encoding=utils.checkEncoding(os.path.join(os.path.join(output_path, bug_folder), 'oracle_test.java')), errors='ignore') as f:
                        unit_test = f.readlines()
                else:
                    unit_flag = False
                    unit_res_in_bug = unit_res_in_fix = env.INVOKE_LLM_FAILED
            if oracle_flag:
                if os.path.exists(os.path.join(os.path.join(output_path, bug_folder), 'oracle_test.java')):
                    with open(os.path.join(os.path.join(output_path, bug_folder), 'oracle_test.java'), 'r', encoding=utils.checkEncoding(os.path.join(os.path.join(output_path, bug_folder), 'oracle_test.java')), errors='ignore') as f:
                        oracle_test = f.readlines()
                else:
                    oracle_flag = False
                    oracle_res_in_bug = oracle_res_in_fix = env.INVOKE_LLM_FAILED
        elif llm == 'gpt-3.5-turbo':
            if unit_flag:
                logger.info(f'Constructing unit test prompt for {project}-{bug_number}')
                unit_test_prompt = cp.constructSimpleUnitTestPrompt(focal_context, examples)
                logger.debug(f'unit_test_prompt={unit_test_prompt[0]["content"]}')
                logger.info(f'Invoking LLM({llm}) for {project}-{bug_number} unit test')
                unit_test_response = invokeLLMs.invokeLLM(unit_test_prompt, llm, logger)
                if unit_test_response == None:
                    logger.error(f"Fail to invoke LLM({llm} for unit test)")
                    unit_flag = False
                    unit_res_in_bug = env.INVOKE_LLM_FAILED
                    unit_res_in_fix = env.INVOKE_LLM_FAILED
                else:
                    logger.debug(f'unit_test_response={unit_test_response.choices[0].message.content}')
                    logger.info(f'Constructing test code for {project}-{bug_number} unit test')
                    unit_test = gtc.constructFullTestCode(unit_test_response.choices[0])
                    logger.info(f'Finished constructing test code for {project}-{bug_number} unit test')
                logger.info(f'Finished invoking LLM({llm}) for {project}-{bug_number} unit test')

            if oracle_flag:
                logger.info(f'Constructing oracle test prompt for {project}-{bug_number}')
                oracle_test_prompt = cp.constructSimpleOracleTestPrompt(oracle_focal_context, test_prefix)
                logger.debug(f'oracle_test_prompt={oracle_test_prompt[0]["content"]}')
                logger.info(f'Invoking LLM({llm}) for {project}-{bug_number} oracle test')
                oracle_test_response = invokeLLMs.invokeLLM(oracle_test_prompt, llm, logger)
                if oracle_test_response == None:
                    logger.error(f"Fail to invoke LLM({llm} for oracle test)")
                    oracle_flag = False
                    oracle_res_in_bug = env.INVOKE_LLM_FAILED
                    oracle_res_in_fix = env.INVOKE_LLM_FAILED
                else:
                    logger.debug(f'oracle_test_response={oracle_test_response.choices[0].message.content}')
                    logger.info(f'Constructing test code for {project}-{bug_number} oracle test')
                    oracle_test = gtc.constructFullTestCode(oracle_test_response.choices[0])
                    logger.info(f'Finished constructing test code for {project}-{bug_number} oracle test')
                logger.info(f'Finished invoking LLM({llm}) for {project}-{bug_number} oracle test')
        
        if unit_flag or oracle_flag:
            if unit_flag:
                unit_res_in_bug, unit_res_in_fix = rt.runTestWithRetry(os.path.join(os.path.join(output_path, bug_folder), 'unit_test'), unit_test_prompt, unit_test_response, os.path.join(temp_path, 'src'), project, bug_number, test_class_path, llm, env.RETRY_COUNT, logger)

            if oracle_flag:
                oracle_res_in_bug, oracle_res_in_fix = rt.runTestWithRetry(os.path.join(os.path.join(output_path, bug_folder), 'oracle_test'), oracle_test_prompt, oracle_test_response, os.path.join(temp_path, 'src'), project, bug_number, test_class_path, llm, env.RETRY_COUNT, logger)

        rt.removeCheckedOutProject(project, bug_number, os.path.join(temp_path, 'src'))
        share_value.set(share_value.get() + 1)
        logger.debug('unit_res_in_bug=' + unit_res_in_bug + ",unit_res_in_fix=" + unit_res_in_fix + ",oracle_res_in_bug=" + oracle_res_in_bug + ",oracle_res_in_fix=" + oracle_res_in_fix)
        return (unit_res_in_bug, unit_res_in_fix, oracle_res_in_bug, oracle_res_in_fix)
    except Exception as e:
        logger.error(f'Unexpected Error in {project} {bug_number}')
        logger.exception(e)
        return (env.UNEXPECTED_ERROR, env.UNEXPECTED_ERROR, env.UNEXPECTED_ERROR, env.UNEXPECTED_ERROR)

def main(argv):
    # Basic settings
    output_path = ""
    code_path = ""
    java_rt_path = ""
    temp_path = os.path.dirname(os.path.abspath(__file__))
    llm = ""
    debug_flag = False
    test_flag = False
    retry_count = 1
    sample_count = 1
    example_count = 0
    env.RETEST_COUNT = 3
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
    opts, args = getopt.getopt(argv[1:], "hDTc:o:L:t:r:j:s:e:", ["help", "debug", "test", "code_path=", "output_path=", "LLM=", "temp_path=", "retry_count=", "java_rt_path=", "sample_count=", "example_count="])
    for opt, arg in opts:
        if opt in ("-h", "--help"):
            print('python main.py -o <output_path> -c <code_path> -L <LLM_name> -j <java_rt_path> [-D] [-t <temp_path>] [-r <retry_count>] [-s <sample_count>] [-e <example_count>]')
            print('temp_path will be the current path in default.')
            print('retry_count should be bigger than 0 (retry_count > 0)')
            print('sample_count should be bigger than 0 (sample_count > 0)')
            sys.exit()
        elif opt in ("-c", "--code_path"):
            code_path = arg
        elif opt in ("-o", "--output_path"):
            output_path = arg
        elif opt in ("-L", "--LLM"):
            llm = arg
        elif opt in ("-D", "--debug"):
            debug_flag = True
        elif opt in ("-t", "--temp_path"):
            temp_path = arg
        elif opt in ("-T", "--test"):
            test_flag = True
        elif opt in ("-r", "--retry"):
            retry_count = int(arg)
        elif opt in ("-j", "--java_rt_path"):
            java_rt_path = arg
        elif opt in ("-s", "--sample"):
            sample_count = int(arg)
        elif opt in ("-e", "--example"):
            example_count = int(arg)
    env.RETRY_COUNT = retry_count
    env.SAMPLE_COUNT = sample_count
    env.EXAMPLE_COUNT = example_count
    if retry_count < 1:
        print("retry count should be bigger than 0 (retry_count > 0)")
        sys.exit()
    if sample_count < 1:
        print("sample count should be bigger than 0 (sample_count > 0)")
        sys.exit()
    if example_count < 0 or example_count > 2:
        print("example count should be in [0, 2]")
        sys.exit()
    if code_path == "":
        print("code_path is needed")
        sys.exit()
    if output_path == "":
        print("output_path is needed")
        sys.exit()
    if java_rt_path == "" or not os.path.exists(java_rt_path):
        print("valid java_rt_path is needed")
        sys.exit()
    if not llm in env.LLM:
        print("LLM is needed")
        print("LLM:" + '/'.join(env.LLM))
    if code_path[-1] == '/':
        code_path = code_path[0:-1]
    if output_path[-1] == '/':
        output_path = output_path[0:-1]
    if os.path.exists(f'.{env.PATH_SPLITER}logs'):
        os.makedirs(f'.{env.PATH_SPLITER}logs-bak-{time.strftime("%Y-%m-%d-%H-%M-%S")}')
        os.system(f'mv .{env.PATH_SPLITER}logs{env.PATH_SPLITER}* .{env.PATH_SPLITER}logs-bak-{time.strftime("%Y-%m-%d-%H-%M-%S")}')
    if not os.path.exists(f'.{env.PATH_SPLITER}logs'):
        os.makedirs(f'.{env.PATH_SPLITER}logs')
    if debug_flag:
        log_file = f'logs{env.PATH_SPLITER}root-debug.log'
        logging.basicConfig(filename=log_file, filemode='w', level=logging.DEBUG)

    # get all java inner classes
    env.JAVA_INNER_CLASSES = utils.getJavaInnerClasses(java_rt_path)

    
    # loop to find all bugs
    bug_folders = gd.walkThroughCodes(code_path, "")
    logging.debug(f"bug_folders_length={len(bug_folders)}")
    logging.debug(f"bug_folders:\n{'|'.join(bug_folders)}")

    # run llm and evaluate TP and FPR
    if os.path.exists(os.path.join(temp_path, 'test')):
        os.system('rm -rf ' + os.path.join(temp_path, 'test'))
    # os.makedirs(os.path.join(temp_path, 'test'))
    counting_manager = Manager()
    share_value = counting_manager.Value('i', 0)
    counting_process = Process(target=counting, name='counting', args=(len(bug_folders), share_value,))
    results = []
    if test_flag:
        counting_process.start()
        results = Parallel(n_jobs=20, backend='multiprocessing')(delayed(testProcedure)(bug_folder, code_path, temp_path, share_value) for bug_folder in bug_folders)
        counting_process.terminate()
    else:
        counting_process.start()
        results = Parallel(n_jobs=3, backend='multiprocessing')(delayed(procedure)(bug_folder, code_path, output_path, llm, temp_path, share_value, debug_flag) for bug_folder in bug_folders)
        counting_process.terminate()

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
    oracle_success_count = 0
    oracle_build_failed_count = 0
    oracle_prepare_failed_count = 0
    oracle_parse_failed_count = 0
    oracle_invoke_llm_failed_count = 0
    oracle_bug_found = 0
    oracle_fp = 0
    oracle_tn = 0
    oracle_fn = 0
    oracle_unexpected_error_count = 0
    total = len(results)
    for (unit_res_in_bug, unit_res_in_fix, oracle_res_in_bug, oracle_res_in_fix) in results:
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
            
            if oracle_res_in_bug == env.BUILD_FAILED or oracle_res_in_fix == env.BUILD_FAILED:
                oracle_build_failed_count += 1
            elif oracle_res_in_bug == env.PARSE_FAILED or oracle_res_in_fix == env.PARSE_FAILED:
                oracle_parse_failed_count += 1
            elif oracle_res_in_bug == env.INVOKE_LLM_FAILED or oracle_res_in_fix == env.INVOKE_LLM_FAILED:
                oracle_invoke_llm_failed_count += 1
            elif oracle_res_in_bug == env.PREPARATION_FAILED or oracle_res_in_fix == env.PREPARATION_FAILED:
                oracle_prepare_failed_count += 1
            elif oracle_res_in_bug == env.UNEXPECTED_ERROR or oracle_res_in_fix == env.UNEXPECTED_ERROR:
                oracle_unexpected_error_count += 1
            else:
                oracle_success_count += 1
                if oracle_res_in_bug == env.TEST_FAILED:
                    if oracle_res_in_fix == env.TEST_FAILED:
                        oracle_fp += 1
                    else:
                        oracle_bug_found += 1
                else:
                    if oracle_res_in_fix == env.TEST_FAILED:
                        oracle_fn += 1
                    else:
                        oracle_tn += 1

    table_dict = {}
    table_dict['Test Type'] = ['unit_test', 'oracle_test']
    table_dict['True Positive Count'] = [unit_bug_found, oracle_bug_found]
    table_dict['False Positive Count'] = [unit_fp, oracle_fp]
    table_dict['True Negative Count'] = [unit_tn, oracle_tn]
    table_dict['False Negative Count'] = [unit_fn, oracle_fn]
    table_dict['Test Count'] = [unit_success_count, oracle_success_count]
    table_dict['Prepare Failed Count'] = [unit_prepare_failed_count, oracle_prepare_failed_count]
    table_dict['Invoke LLM Failed Count'] = [unit_invoke_llm_failed_count, oracle_invoke_llm_failed_count]
    table_dict['Parse Failed Count'] = [unit_parse_failed_count, oracle_parse_failed_count]
    table_dict['Build Failed Count'] = [unit_build_failed_count, oracle_build_failed_count]
    table_dict['Unexpected Error Count'] = [unit_unexpected_error_count, oracle_unexpected_error_count]
    table_dict['Total'] = [total, total]

    print(tabulate(table_dict, headers='keys', tablefmt='fancy_grid'))

    data = [[], [], []]
    for key, values in table_dict.items():
        data[0].append(key)
        data[1].append(values[0])
        data[2].append(values[1])

    with open(os.path.join(output_path, 'result.csv'), 'w', newline='') as output_file:
        writer = csv.writer(output_file)
        for line in data:
            writer.writerow(line)

    
if __name__ == "__main__":
    main(sys.argv)