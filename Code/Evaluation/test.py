import main
import openai
import runTest as rt
import subprocess as sp
from multiprocessing import cpu_count, Process, Manager
import time
import javalang
import sys
import os
from joblib import Parallel, delayed
import logging
import runTest as rt
import getData as gd
import constructPrompt as cp
import getTestCode as gtc
import invokeLLMs
import env
import re
from collections import defaultdict
import csv
import requests
from requests.adapters import HTTPAdapter
import utils
import EvaluateEveryTestCase as eetc
import numpy as np
import json
import pandas as pd
import math
from tqdm import tqdm
from tree_hugger.core import JavaParser

# jp = JavaParser(r'C:\Users\leihan\Desktop\Code\Evaluation\my-languages.so')

FIM_PREFIX = "<fim_prefix>"
FIM_SUFFIX = "<fim_suffix>"
FIM_MIDDLE = "<fim_middle>"
EOD = "<|endoftext|>"

def func(code_path, output_path):
    # loop to find all bugs
    bug_folders = gd.walkThroughCodes(code_path, "")

    dataset = []
    fail_set = []

    for bug_folder in bug_folders:
        try:
            data = {}
            focal_context = gd.getFocalContext(os.path.join(code_path, bug_folder))
            unit_test_prompt = cp.constructSimpleUnitTestPrompt(focal_context, '', 'starcoder')
            instruction = f'{FIM_PREFIX}{unit_test_prompt["prefix"]}{FIM_SUFFIX}{unit_test_prompt["suffix"]}{FIM_MIDDLE}'
            lines = []
            trigger_code_lines = gd.getTriggerCode(os.path.join(code_path, bug_folder)).readlines()
            trigger_tree = javalang.parse.parse(''.join(trigger_code_lines))
            nodes = []
            class_dec = trigger_tree.types[0]
            for class_dec_body in class_dec.body:
                if isinstance(class_dec_body, javalang.tree.MethodDeclaration) and 'abstract' not in class_dec_body.modifiers:
                    override_flag = False
                    for annotion in class_dec_body.annotations:
                        if annotion.name == 'Override':
                            override_flag = True
                            break
                    if not override_flag:
                        nodes.append(class_dec_body)
            for node in nodes:
                method_lines = utils.getJavaCodeMethod(trigger_code_lines, node.name, node.position.line-1)
                flag = False
                for i in range(len(method_lines)):
                    if method_lines[i].find('public') != -1 or method_lines[i].find('private') != -1 or method_lines[i].find('protected') != -1:
                        method_lines = method_lines[i:]
                        break
                # if flag:
                #     if utils.countSymbol(method_lines[i], '{') > 0:
                #         if len(method_lines[0][method_lines[0].find('{')+1:].lstrip()) == 0:
                #             method_lines = method_lines[1:]
                #         else:
                #             method_lines[0] = method_lines[0].replace('{', ' ')
                #         if method_lines[-1].find('}') != -1:
                #             if len(method_lines[-1][:method_lines[-1].find('}')].lstrip()) == 0:
                #                 method_lines = method_lines[:-1]
                #             else:
                #                 method_lines[-1] = method_lines[-1][:method_lines[-1].find('}')]
                #         if method_lines[-1].endswith('\n'):
                #             method_lines[-1] = method_lines[-1][:-1]
                #         method_lines[-1] = method_lines[-1]
                #         break
                lines += method_lines
            lines = utils.getJavaClassCodeWithLines(trigger_code_lines, lines, False, False)
            for i in range(len(lines)):
                if lines[i].find('class') != -1:
                    lines = lines[i+1:]
                    break
            for i in reversed(range(len(lines))):
                if utils.countSymbol(lines[i], '}') != 0:
                    lines = lines[:i]
                    break
            output = instruction + ''.join(lines) + EOD
            data['input'] = ""
            data['instruction'] = instruction
            data['output'] = output
            dataset.append(data)
        except Exception as e:
            fail_set.append(bug_folder)
            logging.info(e)
    
    write_data = json.dumps(dataset, indent=2)
    with open(output_path, 'w', newline='\n') as f:
        f.write(write_data)

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

def gen_dataset_from_csv(code_path, input_path, output_path):

    dataset = []
    fail_set = []

    meta_data = pd.read_csv(os.path.join(input_path, "meta.csv"))
    inputs_data = pd.read_csv(os.path.join(input_path, "inputs.csv"))

    all_data = pd.concat([meta_data, inputs_data], axis=1)

    for row in tqdm(all_data.itertuples()):
        if not row.exception_lbl and not isinstance(row.assertion_lbl, str):
            continue
        try:
            
            data = {}
            bug_folder = row.project + os.sep + str(row.bug_num) + os.sep + "1"
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
            dataset.append(data)
        except Exception as e:
            fail_set.append(bug_folder)
            logging.info(e)
    
    write_data = json.dumps(dataset, indent=2)
    with open(output_path, 'w', newline='\n') as f:
        f.write(write_data)

if __name__ == "__main__":
    env.RETEST_COUNT = 3
    env.PATH_SPLITER = os.sep
    env.LLM = ('gpt-3.5-turbo', 'santacoder')
    env.BUILD_FAILED = 'Build failed'
    env.TEST_FAILED = 'Test failed'
    env.TEST_SUCCESS = 'Test passed'
    env.PARSE_FAILED = 'Parse failed'
    env.INVOKE_LLM_FAILED = 'Invoke LLM failed'
    env.PREPARATION_FAILED = 'Preparation failed'
    env.UNEXPECTED_ERROR = 'Unexpected error'
    env.ASSERT_METHODS = ('assertTrue', 'assertFalse', 'fail', 'assertEquals', 'assertNotEquals', 'assertArrayEquals', 'assertNotNull', 'assertNull', 'assertSame', 'assertNotSame')
    env.ORG_TESTING_ASSERT_METHODS = ('assertThrows',)

    with open(r'C:\Users\leihan\Desktop\output_evosuite_oracle\response_methods\Chart\1\1\method_test00\1_sample_test_response.txt', 'r') as f:
        trigger_code = f.read()
    with open(r'C:\Users\leihan\Desktop\AbstractCategoryItemRenderer_ESTest.java', 'r') as rd_f:
        ls = rd_f.readlines()
        get = utils.getJavaClassCodeWithLines(ls, [trigger_code[:len(trigger_code)-len(trigger_code.lstrip())] + "@Test(timeout = 4000)\n"] + trigger_code.split('\n'))


    # with open(r"C:\Users\leihan\Desktop\Code\Evaluation\response.json") as f:
    #     tests = json.load(f)

    # prompt_path = r'C:\Users\leihan\Desktop\output_evosuite_oracle\prompt'
    # for root, dirs, files in os.walk(prompt_path):
    #     for file in files:
    #         if file == 'prompt.json':
    #             with open(os.path.join(root, file), 'r', encoding=utils.checkEncoding(os.path.join(root, file))) as f:
    #                 prompts = json.load(f)
    #             for prompt in prompts:
    #                 txt = prompt['prefix'] + "<FILL-HERE>" + prompt['suffix']
    #                 lines = txt.split('\n')
    #                 name = 'NoName'
    #                 for line in reversed(lines):
    #                     if 'public void' in line and 'test' in line:
    #                         name = line[line.find('test'):line.find('(')]
    #                         break
    #                 with open(os.path.join(root, f'prompt_{name}.txt'), 'w', encoding='utf-8') as output_file:
    #                     output_file.writelines([l + '\n' for l in lines])
                    

    # kv_pair = [1,2,3,4,5,6,7,8,9,0]
    # kv_pair_sep = []
    # for i in range(8):
    #     kv_pair_sep.append(kv_pair[math.floor(len(kv_pair)*(i/8)):math.floor(len(kv_pair)*((i+1)/8))])

    print (1)
    # test_suite_path = r'C:\Users\leihan\Desktop\Code\Evaluation\org'
    # test_methods = []
    # for root, dirs, files in os.walk(test_suite_path):
    #     for file in files:
    #         if not file.endswith("_ESTest.java"):
    #             continue
    #         full_path = os.path.join(root, file)
    #         with open(full_path) as f:
    #             class_txt = f.read()
    #         with open(full_path) as f:
    #             class_lines = f.readlines()
    #         tree = javalang.parse.parse(class_txt)
    #         class_dec = tree.types[0]
    #         for method in class_dec.constructors:
    #             method_sig, method_def, line_nums = utils.get_method_txt(class_lines, method.position.line - 1)
    #             if method_def.count("@Test") > 1:
    #                 continue
    #             test_methods.append((method, method_def, line_nums, method.documentation))
    #         for method in class_dec.methods:
    #             method_sig, method_def, line_nums = utils.get_method_txt(class_lines, method.position.line - 1)
    #             if method_def.count("@Test") > 1:
    #                 continue
    #             test_methods.append((method, method_def, line_nums, method.documentation))

    # func(r'C:\Users\leihan\Desktop\dataset_train', r'C:\Users\leihan\Desktop\Code\Evaluation\dataset_train_impl.json')
    # func(r'C:\Users\leihan\Desktop\dataset_test', r'C:\Users\leihan\Desktop\Code\Evaluation\dataset_test_impl.json')

    # gen_dataset_from_csv(r"C:\Users\leihan\Desktop\output_full", r"C:\Users\leihan\Desktop\Code\Evaluation", r"C:\Users\leihan\Desktop\Code\Evaluation\dataset_prompt.json")

    # meta_data = pd.read_csv(os.path.join(r'C:\Users\leihan\Desktop', "meta.csv"))
    # inputs_data = pd.read_csv(os.path.join(r'C:\Users\leihan\Desktop', "inputs.csv"))
    # all_data = pd.concat([meta_data, inputs_data], axis=1)

    # me_path = os.path.join(r'C:\Users\leihan\Desktop', "meta-.csv")
    # inp_path = os.path.join(r'C:\Users\leihan\Desktop', "inputs-.csv")

    # d = meta_data[:259]
    # i = inputs_data[:259]

    # d.to_csv(me_path, index=None)
    # i.to_csv(inp_path, index=None)

    print(1)