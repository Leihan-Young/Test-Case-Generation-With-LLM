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
import re
import utils
import javalang
import subprocess as sp
from tree_hugger.core import JavaParser
from tabulate import tabulate
from multiprocessing import cpu_count, Process, Manager, set_start_method
from joblib import Parallel, delayed
from datetime import datetime
import json

basic_seed = 1
BUDGET = 180

def counting(length, share_value):
    while True:
        print(f'Total:{length}, Finished:{share_value.get()}, Current:{share_value.get()/length * 100}%')
        time.sleep(5)

def assertProcedure(bug_folder, code_path, output_path, llm, temp_path, share_value, debug_flag):
    bug_folder_analyze = bug_folder.split(env.PATH_SPLITER)
    project = bug_folder_analyze[0]
    bug_number = bug_folder_analyze[1]
    logger = logging.getLogger(f'{project}-{bug_number}')
    logger.setLevel(level=(logging.DEBUG if debug_flag else logging.INFO))
    handler = logging.FileHandler(f'logs{env.PATH_SPLITER}{project}-{bug_number}.log')
    logger.addHandler(handler)

    try:
        test_id = int(bug_number) * 100
        file_name = f"{project}-{bug_number}b-evosuite.{test_id}.tar.bz2"
        test_suite_path = os.path.join(temp_path, 'test_out_dir', project, 'evosuite', str(test_id))
        if not os.path.exists(os.path.join(test_suite_path, file_name)):
            random_seed = basic_seed * 1000 + int(bug_number)
            cmd = f"gen_tests.pl -g evosuite -p {project} -v {bug_number}b -n {test_id} -o {os.path.join(temp_path, 'test_out_dir')} -b {BUDGET} " \
                f"-s {random_seed}"
            logger.info(f"run cmd: {cmd}")

            result = sp.run(cmd.split(), env=os.environ.copy(), stdout=sp.PIPE, stderr=sp.PIPE, text=True)
            if result.returncode != 0:
                logger.info(f"retry {cmd}")
                result = sp.run(cmd.split(), env=os.environ.copy(), stdout=sp.PIPE, stderr=sp.PIPE, text=True)
                if result.returncode != 0:
                    logger.error(f"Run {cmd}:\nstdout: {result.stdout}\nstderr: {result.stderr}")
                    raise Exception("Fail to generate test suite.")
        sp.run(f"cd {test_suite_path};tar xf {file_name}", shell=True, check=True)

        logger.info(f'Checking out {project} {bug_number}b...')
        rt.checkOutProject(project, bug_number, True, os.path.join(temp_path, 'src'))
        logger.info(f'Finished checking out {project} {bug_number}b.')
        logger.info(f'Checking out {project} {bug_number}f...')
        rt.checkOutProject(project, bug_number, False, os.path.join(temp_path, 'src'))
        logger.info(f'Finished checking out {project} {bug_number}f.')

        try:
            focal_context = gd.getFocalContext(os.path.join(code_path, bug_folder))
        except Exception:
            logger.error(f"Fail to get focal_context in {bug_folder}")
        # logger.debug(f'focal_context={"".join(focal_context) if focal_context != None else "None"}')
        test_class_path = gd.tryToGetTestClassPath(os.path.join(code_path, bug_folder))
        logger.debug(f'test_class_path={test_class_path if test_class_path != None else "None"}')
        if focal_context == None or test_class_path == None:
            return [(env.PREPARATION_FAILED, env.PREPARATION_FAILED)]
        
        if env.STEP == 1:
            logger.info("Choose Step 1...")
            test_methods = []
            for root, dirs, files in os.walk(test_suite_path):
                for file in files:
                    if not file.endswith("_ESTest.java"):
                        continue
                    full_path = os.path.join(root, file)
                    with open(full_path) as f:
                        class_txt = f.read()
                    with open(full_path) as f:
                        class_lines = f.readlines()
                    tree = javalang.parse.parse(class_txt)
                    class_dec = tree.types[0]
                    for method in class_dec.constructors:
                        method_sig, method_def, line_nums = utils.get_method_txt(class_lines, method.position.line - 1)
                        if method_def.count("@Test") > 1:
                            continue
                        test_methods.append((method, method_def, line_nums, method.documentation))
                    for method in class_dec.methods:
                        method_sig, method_def, line_nums = utils.get_method_txt(class_lines, method.position.line - 1)
                        if method_def.count("@Test") > 1:
                            continue
                        test_methods.append((method, method_def, line_nums, method.documentation))
            
            test_name_re = re.compile("public void (test[0-9]*)\(\)")
            test_prompts = []
            for obj, test_method, line_nums, doc in test_methods:
                if not test_name_re.search(test_method):
                    continue
                test_prefix = utils.getTestPrefixFromTestCode(test_method)
                if test_prefix == None:
                    continue
                logger.info(f'Constructing oracle test prompt for {project}-{bug_number}')
                logger.debug(f'test_prefix={test_prefix}')
                test_prompt = cp.constructSimpleUnitTestPrompt(focal_context, None, llm, test_prefix)
                test_prompts.append(test_prompt)
            logger.debug(f'test_prompts_len={len(test_prompts)}')
            write_data = json.dumps(test_prompts, indent=2)
            if not os.path.exists(os.path.join(output_path, "prompt", project, bug_number)):
                os.makedirs(os.path.join(output_path, "prompt", project, bug_number))
            with open(os.path.join(output_path, "prompt", project, bug_number, "prompt.json"), 'w', newline='\n') as f:
                f.write(write_data)

        # logger.info(f'Invoking LLM({llm}) for {project}-{bug_number} oracle test')
        # tests = invokeLLMs.invokeLLM(test_prompts, llm, logger)
        # logger.info(f'Finished invoking LLM({llm}) for {project}-{bug_number} oracle test')

        if env.STEP == 2:
            logger.info("Choose Step 2...")
            results = []
            logger.info("Getting LLM response...")
            if not os.path.exists(os.path.join(output_path, "response", project, bug_number, "response.json")):
                share_value.set(share_value.get() + 1)
                logger.error("Invoke LLM failed...")
                return [(env.INVOKE_LLM_FAILED, env.INVOKE_LLM_FAILED)]
            with open(os.path.join(output_path, "response", project, bug_number, "response.json")) as f:
                tests = json.load(f)

            logger.info("Evaluating...")
            for test in tests:
                name = "name"
                test = test[0]
                test = utils.getTestCodeMethod(test.split('\n'))
                if test == None:
                    continue
                ind = test[0].find('test')
                if ind == -1:
                    continue
                name = test[0][ind:test[0].rfind('(')]
                test[0] = test[0][:ind] + "newTestMethod_" + test[0][ind:]
                test = ['\n'.join(test)]
                logger.info(f"test={test[0]}")
                res_in_bug, res_in_fix = rt.runTestLLMCoderEvosuite(os.path.join(output_path, "response_methods", bug_folder, f'method_{name}'), None, test, os.path.join(temp_path, 'src'), project, bug_number, os.path.join(temp_path, 'test_out_dir', project, 'evosuite', str(int(bug_number) * 100)), llm, logger)
                logger.info(f"res_in_bug={res_in_bug},res_in_fix={res_in_fix}")
                results.append((res_in_bug, res_in_fix))
                logger.debug(f'{name}:res_in_bug={res_in_bug},res_in_fix={res_in_fix}')

            share_value.set(share_value.get() + 1)
            return results

        share_value.set(share_value.get() + 1)
        return [(env.PREPARATION_FAILED, env.PREPARATION_FAILED)]

    except Exception as e:
        logger.error(f'Unexpected Error in {project} {bug_number}')
        logger.exception(e)
        return [(env.UNEXPECTED_ERROR, env.UNEXPECTED_ERROR)]

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
        if test_class_path == None:
            test_class_path = gd.tryToGetTestClassPath(os.path.join(code_path, bug_folder))
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

        if llm == 'santacoder' or llm == 'starcoder':
            if unit_flag:
                logger.info(f'Constructing unit test prompt for {project}-{bug_number}')
                unit_test_prompt = cp.constructSimpleUnitTestPrompt(focal_context, examples, llm)
                logger.debug(f'unit_test_prefix={unit_test_prompt["prefix"]}, unit_test_suffix={unit_test_prompt["suffix"]}')
                logger.info(f'Invoking LLM({llm}) for {project}-{bug_number} unit test')
                unit_test = invokeLLMs.invokeLLM(unit_test_prompt, llm, logger)
                logger.info(f'Finished invoking LLM({llm}) for {project}-{bug_number} unit test')
                if unit_test == None:
                    logger.error(f"Fail to invoke LLM({llm} for unit test)")
                    unit_flag = False
                    unit_res_in_bug = env.INVOKE_LLM_FAILED
                    unit_res_in_fix = env.INVOKE_LLM_FAILED
                else:
                    unit_res_in_bug, unit_res_in_fix = rt.runTestLLMCoder(os.path.join(os.path.join(output_path, bug_folder), 'unit_test'), unit_test_prompt, unit_test, os.path.join(temp_path, 'src'), project, bug_number, test_class_path, llm, logger)
            if oracle_flag:
                logger.info(f'Constructing oracle test prompt for {project}-{bug_number}')
                oracle_test_prompt = cp.constructSimpleOracleTestPrompt(focal_context, examples, llm)
                logger.debug(f'oracle_test_prefix={oracle_test_prompt["prefix"]}, oracle_test_suffix={oracle_test_prompt["suffix"]}')
                logger.info(f'Invoking LLM({llm}) for {project}-{bug_number} oracle test')
                oracle_test = invokeLLMs.invokeLLM(oracle_test_prompt, llm, logger)
                logger.info(f'Finished invoking LLM({llm}) for {project}-{bug_number} oracle test')
                if oracle_test == None:
                    logger.error(f"Fail to invoke LLM({llm} for oracle test)")
                    oracle_flag = False
                    oracle_res_in_bug = env.INVOKE_LLM_FAILED
                    oracle_res_in_fix = env.INVOKE_LLM_FAILED
                else:
                    oracle_res_in_bug, oracle_res_in_fix = rt.runTestLLMCoder(os.path.join(os.path.join(output_path, bug_folder), 'oracle_test'), oracle_test_prompt, oracle_test, os.path.join(temp_path, 'src'), project, bug_number, test_class_path, llm, logger)
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
    debug_flag = True
    test_flag = False
    retry_count = 1
    sample_count = 1
    example_count = 0
    step = 0
    env.RETEST_COUNT = 3
    env.PATH_SPLITER = os.sep
    env.LLM = ('gpt-3.5-turbo', 'santacoder', 'starcoder', 'starcoder-evosuite')
    env.BUILD_FAILED = 'Build failed'
    env.TEST_FAILED = 'Test failed'
    env.TEST_SUCCESS = 'Test passed'
    env.PARSE_FAILED = 'Parse failed'
    env.INVOKE_LLM_FAILED = 'Invoke LLM failed'
    env.PREPARATION_FAILED = 'Preparation failed'
    env.UNEXPECTED_ERROR = 'Unexpected error'
    env.ASSERT_METHODS = ('assertTrue', 'assertFalse', 'fail', 'assertEquals', 'assertNotEquals', 'assertArrayEquals', 'assertNotNull', 'assertNull', 'assertSame', 'assertNotSame')
    env.ORG_TESTING_ASSERT_METHODS = ('assertThrows',)
    opts, args = getopt.getopt(argv[1:], "hDTc:o:L:t:r:j:s:e:S:", ["help", "debug", "test", "code_path=", "output_path=", "LLM=", "temp_path=", "retry_count=", "java_rt_path=", "sample_count=", "example_count=", "step="])
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
        elif opt in ("-S", "--step"):
            step = int(arg)
    env.RETRY_COUNT = retry_count
    env.SAMPLE_COUNT = sample_count
    env.EXAMPLE_COUNT = example_count
    env.STEP = step
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
        log_file = f'.{env.PATH_SPLITER}logs{env.PATH_SPLITER}root-debug.log'
        logging.basicConfig(filename=log_file, filemode='w', level=logging.DEBUG, force=True)

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
    counting_process.start()
    if llm == 'starcoder-evosuite':
        results = Parallel(n_jobs=20, backend='multiprocessing')(delayed(assertProcedure)(bug_folder, code_path, output_path, llm, temp_path, share_value, debug_flag) for bug_folder in bug_folders)
    else:
        results = Parallel(n_jobs=2, backend='multiprocessing')(delayed(procedure)(bug_folder, code_path, output_path, llm, temp_path, share_value, debug_flag) for bug_folder in bug_folders)
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
    bug_found = 0
    if llm == 'starcoder-evosuite':
        total = 0
        for result in results:
            total += len(result)
            flag = True
            for (unit_res_in_bug, unit_res_in_fix) in result:
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
                            if flag:
                                bug_found += 1
                                flag = False
                            unit_bug_found += 1
                    else:
                        if unit_res_in_fix == env.TEST_FAILED:
                            unit_fn += 1
                        else:
                            unit_tn += 1
    else:
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
    if llm == "starcoder-evosuite":
        table_dict["Bug Found"] = [bug_found, bug_found]

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