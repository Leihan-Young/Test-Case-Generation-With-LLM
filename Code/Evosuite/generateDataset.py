# encoding=utf-8
import logging
import os
import shutil
from common.d4j import parse_projects
import fire
import pandas as pd
import subprocess as sp
from joblib import delayed, Parallel
import extract_tests
from tqdm import tqdm
import re
import env
import utils
import json
import getData as gd
import constructPrompt as cp
import runTest as rt

"""
1. Generate tests from the buggy version
2. Construct inputs.csv and meta.csv using toga's script
"""

FIM_PREFIX = "<fim_prefix>"
FIM_SUFFIX = "<fim_suffix>"
FIM_MIDDLE = "<fim_middle>"
EOD = "<|endoftext|>"
TMP_PATH = "./tmp/extract_tests"

def setUpLog():
    # joblib will not copy the global state of a module
    logging.basicConfig(level=logging.INFO)


setUpLog()

# 3 minutes
BUDGET = 180
SKIP_BUGS = {
}


def gen_tests_for_bug(proj: str,
                      bug_num: int,
                      out_dir: str,
                      suffix: str,
                      seed: int):
    setUpLog()
    system = "evosuite"
    test_id = str(bug_num * 100)
    # for each bug, there can only have one tar.bz2
    # therefore, we just remove it and regenerate
    test_suite = os.path.join(out_dir, proj, system, test_id, f"{proj}-{bug_num}{suffix}-{system}.{test_id}.tar.bz2")
    if os.path.exists(test_suite):
        logging.warning(f"Skip gen_tests for {proj}-{bug_num}")
        return proj, bug_num, "skip"
    # try to generate bug-revealing test_case 5 times
    extract_tests.checkout_project(proj, str(bug_num))
    fail_flag = False
    for i in range(3):
        random_seed = i * 10000 + seed * 1000 + bug_num
        cmd = f"gen_tests.pl -g {system} -p {proj} -v {bug_num}{suffix} -n {test_id} -o {out_dir} -b {BUDGET} " \
            f"-s {random_seed}"
        logging.info(f"run cmd: {cmd}")

        result = sp.run(cmd.split(), env=os.environ.copy(), stdout=sp.PIPE, stderr=sp.PIPE, text=True)
        if result.returncode != 0:
            logging.info(f"retry {cmd}")
            result = sp.run(cmd.split(), env=os.environ.copy(), stdout=sp.PIPE, stderr=sp.PIPE, text=True)
            if result.returncode != 0:
                logging.error(f"Run {cmd}:\nstdout: {result.stdout}\nstderr: {result.stderr}")
                fail_flag = True
                break
            
        # bug-revealing test
        cmd = f"defects4j test -w {os.path.join(extract_tests.get_tmp_dir(), '{}_{}_buggy/'.format(proj, bug_num))} -s {test_suite}"
        logging.info(f"run cmd: {cmd}")
        result = sp.run(cmd.split(), env=os.environ.copy(), stdout=sp.PIPE, stderr=sp.PIPE, text=True)
        if result.returncode != 0:
            logging.info(f"retry {cmd}")
            result = sp.run(cmd.split(), env=os.environ.copy(), stdout=sp.PIPE, stderr=sp.PIPE, text=True)
            if result.returncode != 0:
                logging.error(f"Run {cmd}:\nstdout: {result.stdout}\nstderr: {result.stderr}")
                fail_flag = True
                break
        if result.stdout.find("Failing tests: 0") == -1 and result.stdout.find("Failing tests:") != -1:
            fail_flag = False
            break
    if fail_flag:
        cmd = f"rm {test_suite}"
        logging.info(f"remove fail test suite: {cmd}")
        result = sp.run(cmd.split(), env=os.environ.copy(), stdout=sp.PIPE, stderr=sp.PIPE, text=True)
        return proj, bug_num, "fail"
    else:
        logging.info(f"{proj}-{bug_num} passed")
        return proj, bug_num, "pass"


def skip_bug(proj: str, bug_num: int):
    # useless
    if bug_num in SKIP_BUGS.get(proj, set()):
        logging.info(f"Force skipping gen_tests for {proj}-{bug_num}")
        return True
    return False


def gen_tests_for_proj(proj: str,
                       meta_file: str,
                       out_dir: str,
                       suffix: str,
                       seed: int):
    meta = pd.read_csv(meta_file, header=0)
    for bug_num in meta["bug.id"]:
        bug_num = int(bug_num)
        if skip_bug(proj, bug_num):
            continue
        gen_tests_for_bug(proj, bug_num, out_dir, suffix, seed)


def gen_tests(seed: int,
              proj: str = None,
              meta_dir: str = "data/metadata",
              out_dir: str = "data/evosuite_fix_version/generated",
              suffix: str = "f",
              n_jobs: int = None):
    projs = parse_projects(proj)

    tasks = []
    for proj in projs:
        meta_file = os.path.join(meta_dir, f"{proj}.csv")
        meta = pd.read_csv(meta_file, header=0)
        # for each active bug
        for bug_num in meta["bug.id"]:
            bug_num = int(bug_num)
            if skip_bug(proj, bug_num):
                continue
            # run evosuite to generate tests
            tasks.append(delayed(gen_tests_for_bug)(proj, bug_num, out_dir, suffix, seed))
    results = Parallel(n_jobs=n_jobs, prefer='processes')(tqdm(tasks))
    df = pd.DataFrame(results, columns=["project", "bug_id", "success"])
    result_file = os.path.join(out_dir, "gen_tests_result.csv")
    df.to_csv(result_file, index=False)


def prepare_tests(test_dir: str = "data/evosuite_fix_version/generated"):
    system = "evosuite"
    projs = parse_projects(None)

    for proj in projs:
        base_dir = os.path.join(test_dir, proj, system)
        for dir_name_str in os.listdir(base_dir):
            dir_name = int(dir_name_str)
            if dir_name >= 100:
                new_name = str(dir_name // 100)
                shutil.move(os.path.join(base_dir, dir_name_str), os.path.join(base_dir, new_name))

        for dirpath, dirnames, filenames in os.walk(base_dir):
            for file in filenames:
                if file.endswith(".tar.bz2"):
                    sp.run(f"cd {dirpath};tar xf {file}", shell=True, check=True)


def ex_tests(test_corpus_dir: str = "data/evosuite_fix_version",
             output_dir: str = "data/evosuite_fix_version/tests",
             sample_5projects: bool = False,
             d4j_path: str = "/data/zhiquanyang/StarCoder/defects4j",
             njobs: int = None):
    d4j_path = os.path.expanduser(d4j_path)
    extract_tests.main(test_corpus_dir, sample_5projects, d4j_path, output_dir, njobs)

def preprocessExceptionTestPrefix(test_prefix):
    result_array = []
    lines = test_prefix.split('\n')
    # 去除signature
    for i in range(len(lines)):
        if utils.countSymbol(lines[i], '{') > 0:
            if lines[i].rstrip()[-1] != '{':
                lines[i] = lines[i][lines[i].rfind('{') + 1:]
                lines = lines[i:]
            else:
                lines = lines[i+1:]
            break
    blank = lines[0][:len(lines[0])-len(lines[0].lstrip())]
    # 保留try前prefix
    for i in range(len(lines)):
        if re.match(".*[^a-zA-Z]try[^a-zA-Z].*", lines[i]) is not None:
            result_array += lines[:i]
            lines = lines[i:]
            break
    # 保留try内prefix
    for i in range(len(lines)):
        if lines[i].find('{') != -1:
            lines[i] = lines[i][lines[i].find('{')+1:]
            if len(lines[i].lstrip()) == 0:
                lines = lines[i+1:]
            else:
                lines = lines[i:]
            break
    for i in range(len(lines)):
        if re.match(".*[^a-zA-Z]catch[^a-zA-Z].*", lines[i]) is not None:
            lines = lines[i:]
            break
        result_array.append(blank + lines[i].lstrip())
    tmp_line = lines[0][lines[0].find('catch') + len('catch'):]
    tmp_line = tmp_line[tmp_line.find('(')+1:tmp_line.rfind(')')].lstrip().rstrip()
    exception_class = tmp_line.split(' ')[0]
    oracle = f'assertException({exception_class}.class)'
    result_array.append(blank + oracle)
    result = []
    for i in range(len(result_array)):
        if result_array[i].lstrip().startswith(r'//') or re.match(".*[^a-zA-Z]fail[^a-zA-Z].*", result_array[i]) is not None:
            continue
        result.append(result_array[i])
    return '\n'.join(result), oracle

def processTestPrefix(test_prefix, oracle):
    lines = test_prefix.split('\n')
    remove_size = 0
    for i in range(len(lines)):
        if utils.countSymbol(lines[i], '{') > 0:
            remove_size = len(lines[i])-len(lines[i].lstrip())
            if lines[i].rstrip()[-1] != '{':
                lines[i] = lines[i+1][:remove_size] + lines[i][lines[i].rfind('{') + 1:]
                lines = lines[i:]
            else:
                lines = lines[i+1:]
            break
    for i in range(len(lines)):
        if len(lines[i]) - len(lines[i].lstrip()) > remove_size:
            lines[i] = lines[i][remove_size:]
    processed = '\n'.join(lines)
    return processed[:processed.find(oracle)]

def renameTestMethod(test_method):
    lines = test_method.split('\n')
    for i in range(len(lines)):
        if lines[i].find('test') != -1:
            tmp = lines[i].split(' ')
            for j in range(len(tmp)):
                if tmp[j].find('test') != -1:
                    tmp[j] = "newTestMethod()"
                    break
            lines[i] = ' '.join(tmp)
            break
    return '\n'.join(lines)


def multi_processes(row, code_path):
    try:
        rt.checkOutProject(row.project, int(row.bug_num), True, os.path.join(TMP_PATH, "{}_{}_buggy/".format(row.project, int(row.bug_num))))
        if not row.exception_lbl and not isinstance(row.assertion_lbl, str):
            raise Exception('assert type but no assert oracle')
        bug_folder = row.project + os.sep + str(int(row.bug_num)) + os.sep + "1"
        # test_class_path, _ = gd.getTestPrefix(os.path.join(code_path, bug_folder))
        # if test_class_path == None:
        #     test_class_path = gd.tryToGetTestClassPath(os.path.join(code_path, bug_folder))
        # if test_class_path == None:
        #     raise Exception('test_class_path==None')
        # _, res = rt.runTestCodeInWorkDir([renameTestMethod(row.test_prefix)], os.path.join(TMP_PATH, "{}_{}_buggy/".format(row.project, int(row.bug_num))),
        #                         test_class_path, logging)
        # if res != env.TEST_FAILED:
        #     raise Exception('test passed')
        data = {}
        focal_context = gd.getFocalContext(os.path.join(code_path, bug_folder))
        test_prefix = row.test_prefix
        if row.exception_lbl:
            test_prefix, oracle = preprocessExceptionTestPrefix(test_prefix)
        else:
            oracle = row.assertion_lbl
        test_prefix = processTestPrefix(test_prefix, oracle)
        oracle += ';'
        oracle_test_prompt = cp.constructSimpleOracleTestPrompt(focal_context, test_prefix, 'starcoder')
        instruction = f'{FIM_PREFIX}{oracle_test_prompt["prefix"]}{FIM_SUFFIX}{oracle_test_prompt["suffix"]}{FIM_MIDDLE}'
        output = instruction + oracle + EOD

        data['input'] = ""
        data['instruction'] = instruction
        data['output'] = output
        return (True, data)
    except Exception as e:
        logging.warning(e)
        return (False, None)

def gen_dataset_from_csv(code_path, input_path, output_path, n_jobs=20):

    dataset = []
    fail_set = set()

    meta_data = pd.read_csv(os.path.join(input_path, "meta.csv"))
    inputs_data = pd.read_csv(os.path.join(input_path, "inputs.csv"))
    all_data = pd.concat([meta_data, inputs_data], axis=1)

    tasks = []
    for row in all_data.itertuples():
        tasks.append(delayed(multi_processes)(row, code_path))
    results = Parallel(n_jobs=n_jobs, backend='threading')(tqdm(tasks))
    for (res, data) in results:
        if res:
            dataset.append(data)
    write_data = json.dumps(dataset, indent=2)
    with open(output_path, 'w', newline='\n') as f:
        f.write(write_data)


if __name__ == "__main__":

    env.RETEST_COUNT = 3
    env.PATH_SPLITER = os.sep
    env.LLM = ('gpt-3.5-turbo', 'santacoder', 'starcoder')
    env.BUILD_FAILED = 'Build failed'
    env.TEST_FAILED = 'Test failed'
    env.TEST_SUCCESS = 'Test passed'
    env.PARSE_FAILED = 'Parse failed'
    env.INVOKE_LLM_FAILED = 'Invoke LLM failed'
    env.PREPARATION_FAILED = 'Preparation failed'
    env.UNEXPECTED_ERROR = 'Unexpected error'
    env.ASSERT_METHODS = ('assertTrue', 'assertFalse', 'fail', 'assertEquals', 'assertNotEquals', 'assertArrayEquals', 'assertNotNull', 'assertNull', 'assertSame', 'assertNotSame')
    env.ORG_TESTING_ASSERT_METHODS = ('assertThrows',)

    java_rt_path = "/data/zhiquanyang/StarCoder/eval/java_classes"
    env.JAVA_INNER_CLASSES = utils.getJavaInnerClasses(java_rt_path)

    # gen_tests(1, n_jobs=20, out_dir="data/evosuite_bug_revealing/generated")
    # prepare_tests(test_dir="data/evosuite_bug_revealing/generated")
    # ex_tests(njobs=20, test_corpus_dir="data/evosuite_bug_revealing", output_dir="data/evosuite_bug_revealing/tests")
    gen_dataset_from_csv(r"/data/zhiquanyang/StarCoder/eval/trigger_code/output", r"/data/zhiquanyang/StarCoder/evosuite_dataset/data/evosuite_bug_revealing/tests", r"/data/zhiquanyang/StarCoder/evosuite_dataset/dataset_train_evosuite.json")
