import main
import openai
# import runTest as rt
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

def counting(length, q):
    while True:
        print(f'Total:{length}, Finished:{q.get()}, Current:{q.get()/length * 100}%')
        time.sleep(1)

def test(x, q):
    time.sleep(1)
    q.set(q.get() + 1)
    return x

if __name__ == "__main__":
    # argv = ['main.py', '-o', './output', '-c', '/output_full', '-L', 'gpt-3.5-turbo', '-D', '-T']
    # argv = ['main.py', '-o', './output', '-c', '/output_test', '-L', 'use-output', '-D',]
    argv = ['main.py', '-o', '.\\Eval\\output', '-c', 'C:\\Users\\leihan\\Desktop\\output_test_single', '-L', 'gpt-3.5-turbo', '-D', '-j', '.\\Eval\\java_classes', '-r', '3', '-s', '3']
    # argv = ['main.py', '-o', './output', '-c', 'C:\\Users\\leihan\\Desktop\\output_test', '-L', 'use-output', '-D']
    # main.main(argv)

    # f = gd.getFocalContext(r"C:\Users\leihan\Desktop\output_test\Chart\1\1")

    every_response = np.load("C:\\Users\\leihan\\Desktop\\every_response.npy", allow_pickle=True)
    single_project = np.load("C:\\Users\\leihan\\Desktop\\single_project.npy", allow_pickle=True)

    # fc = gd.getFocalContext(r"C:\Users\leihan\Desktop\output_test\Cli\16\1")

    # env.PATH_SPLITER = os.sep
    # res = gd.getExamples(r'C:\Users\leihan\Desktop\output_test\Cli\15\1', r'C:\Users\leihan\Desktop\Code\Eval\work\Cli-15\bug', 2)

    # with open('test.txt', 'r') as f:
    #     deps = utils.getNeededDep(f.readlines())

    with open(r'C:\Users\leihan\Desktop\Code\test.java', 'r') as f:
        tree = javalang.parse.parse(f.read())

    # java_rt_path = 'C:\\Users\\leihan\\Desktop\\Code\\Eval\\java_classes'
    # env.PATH_SPLITER = os.sep
    # env.JAVA_INNER_CLASSES = utils.getJavaInnerClasses(java_rt_path)

    # with open('C:\\Users\\leihan\\Desktop\\Code\\test.java') as f:
    #     trigger_code_lines = f.readlines()
    # check_out_path = 'C:\\Users\\leihan\\Desktop\\Code\\Eval\\work'
    # with open('C:\\Users\\leihan\\Desktop\\Code\\Eval\\work\\tests\\org\\jfree\\chart\\axis\\junit\\LogAxisTests.java') as f:
    #     test_code = f.readlines()

    # needed_deps = utils.getNeededDep(trigger_code_lines)
    # project_classes = utils.getProjectClasses(utils.getJavaSrcCodePath(check_out_path))
    
    # index, deps = utils.getDepsAndStartIndex(test_code)
    
    # dep_packages = ['org.junit.Assert']
    # for needed_dep in needed_deps:
    #     if needed_dep in env.JAVA_INNER_CLASSES :
    #         dep_packages.append(env.JAVA_INNER_CLASSES[needed_dep])
    #     elif needed_dep in project_classes:
    #         dep_packages.append(project_classes[needed_dep])

    # dep_packages_filtered = []
    # for dep_package in dep_packages:
    #     if dep_package not in deps:
    #         dep_packages_filtered.append(dep_package)

    # test_code = test_code[:index] + utils.getImportLines(dep_packages_filtered) + test_code[index:]


    # with open('C:\\Users\\leihan\\Desktop\\Code\\test.log', 'r') as f:
    #     additional_messages = f.readlines()
    # m = cp.extractingErrorMessages(additional_messages)

    # with open('C:\\Users\\leihan\\Desktop\\Code\\test.log', 'r') as f:
    #     cp.extractingErrorMessages(f.readlines())

#     env.PATH_SPLITER = os.sep
#     rt.runTestCodeInWorkDir('public void test(){ fail(); }', 'C:\\Users\\leihan\\Desktop\\Code\\work\\bug', 'org\\jfree\\chart\\renderer\\category\\junit\\AbstractCategoryItemRendererTests.java', logger=logging)


#     env.ASSERT_METHODS = ('assertTrue', 'assertFalse', 'fail', 'assertEquals', 'assertNotEquals', 'assertArrayEquals', 'assertNotNull', 'assertNull', 'assertSame', 'assertNotSame')
#     env.ORG_TESTING_ASSERT_METHODS = ('assertThrows',)
#     y = gtc.constructFullTestCode(r'() throws ParseException { \
#     // create options\
#     Options options = new Options(); \
#     options.addOption("h", "help", false, "print help message");\
#     options.addOption("v", "version", false, "print version information");\
#     options.addOption("f", "file", true, "specify input file");\
# \
#     // create parser\
#     Parser parser = new Parser();\
# \
#     // test parsing with valid arguments\
#     String[] validArgs = {"-f", "input.txt"};\
#     CommandLine validCmd = parser.parse(options, validArgs);\
#     Assert.assertEquals("input.txt", validCmd.getOptionValue("f"));\
# \
#     // test parsing with invalid arguments\
#     String[] invalidArgs = {"-x", "input.txt"};\
#     assertThrows(ParseException.class, () -> parser.parse(options, invalidArgs));\
# }'
# )

    # gtc.assertThrowsToTryCatch('assertThrows(ParseException.class, () -> parser.parse(options, invalidArgs));', 0)

    # f = gd.getFocalContext(os.path.join("C:\\Users\\leihan\\Desktop\\Code\\tool\\output\\JacksonDatabind\\", "7\\1"))
    
    # x = gd.getFocalContext('C:\\Users\\leihan\\Desktop\\output_full\\Cli\\15\\1')

    # print(''.join(gd.getFocalContext('/Code/tool/output/JacksonDatabind/7/1')[1]))

    # tree = javalang.parse.parse(open('C:\\Users\\leihan\\Desktop\\Code\\test.java', 'r', encoding=utils.checkEncoding('C:\\Users\\leihan\\Desktop\\Code\\test.java'), errors='ignore').read())


    # test_ = ['123', '123456', '888']
    # test_back = test_
    # test_ = test_[:2] + ['777'] + test_[2:]
    # print(1)
    
    openai.api_key = "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"

    # test_code = ['public void test(){\n',
    #              '    PeriodParser parser = new PeriodParser();\n',
    #              '    String invalidInput = "invalid input";\n',
    #              '    assertThrows(IllegalArgumentException.class, () -> {\n',
    #              '        parser.parsePeriod(invalidInput);\n',
    #              '    });\n',
    #              '    System.out.println(6);\n',
    #              '}']
    # test_code = gtc.assertThrowsToTryCatch(test_code, 3)
    # print(1)

    # proxies = {
    #     "http": "http://127.0.0.1:7890",
    #     "https": "http://127.0.0.1:7890",
    # }

    # session = requests.Session()
    # session.proxies.update(proxies)

    # # 为openai库的全局配置设置新的Session对象
    # openai.api.session = session

    # # 现在，您可以使用openai库发起请求，它将通过设置的代理进行
    # # 例如，列出您的模型
    # models = openai.Model.list()
    # print(models)





    # a = {'Test Type':['Unit Test', 'Oracle Test'], 'Count':[5, 11]}
    # data = [[], [], []]
    # for key, values in a.items():
    #     data[0].append(key)
    #     data[1].append(values[0])
    #     data[2].append(values[1])

    # with open('result.csv', 'w', newline='') as output_file:
    #     writer = csv.writer(output_file)
    #     for line in data:
    #         writer.writerow(line)
    
    # test_try = "@Test\npublic void ttt(){\nSystem.out.println(\"666\");\n}"
    # test_tree = javalang.parse.parse(f"public class Test{'{'}\n{test_try}\n{'}'}")

    # content = "@Test\npublic void test(){\nSystem.out.println(6);\n}\n"
    # content_lines = re.split("([\n])", content)
    # content_lines = ["".join(i) for i in zip(content_lines[0::2], content_lines[1::2])]

    # test1 = ['1', '2', '3', '4']
    # a = ''.join(test1[:0])
    # b = ''.join(test1[:1])
    # c = ''.join(test1[:2])

    env.PATH_SPLITER = os.sep
    env.LLM = ('gpt-3.5-turbo')
    env.BUILD_FAILED = 'Build failed'
    env.TEST_FAILED = 'Test failed'
    env.TEST_SUCCESS = 'Test passed'
    env.PARSE_FAILED = 'Parse failed'
    env.INVOKE_LLM_FAILED = 'Invoke LLM failed'
    env.PREPARATION_FAILED = 'Preparation failed'
    env.ASSERT_METHODS = ('assertTrue', 'assertFalse', 'fail', 'assertEquals', 'assertNotEquals', 'assertArrayEquals', 'assertNotNull', 'assertNull', 'assertSame', 'assertNotSame')
    env.ORG_TESTING_ASSERT_METHODS = ('assertThrows',)
    env.SAMPLE_COUNT = 3
    code_path = 'C:\\Users\\leihan\\Desktop\\output_test'
    bug_folder = 'Cli\\1\\1'
    llm = 'gpt-3.5-turbo'
    focal_context = gd.getFocalContext(os.path.join(code_path, bug_folder))
    # test_class_path, test_prefix = gd.getTestPrefix(os.path.join(code_path, bug_folder))
    unit_test_prompt = cp.constructSimpleUnitTestPrompt(focal_context)
    # oracle_test_prompt = cp.constructSimpleOracleTestPrompt(focal_context, test_prefix)
    unit_test_response = invokeLLMs.invokeLLM(unit_test_prompt, llm)
    if not os.path.exists('./output-test-Cli11/'):
        os.makedirs('./output-test-Cli11/')
    for i in range(len(unit_test_response.choices)):
        with open(f'./output-test-Cli11/{i+1}_unit_test_response.txt', 'w') as f:
            f.write(unit_test_response.choices[i].message.content)
    # oracle_test_response = invokeLLMs.invokeLLM(oracle_test_prompt, llm)
    # if oracle_test_response == None:
    #     exit
    # gtc.constructFullTestCode(oracle_test_response)
    # with open(os.path.join(os.path.join(code_path, bug_folder), 'unit_test_prompt.txt'), 'w') as f:
    #     f.write(unit_test_prompt[0]['content'])
    # with open(os.path.join(os.path.join(code_path, bug_folder), 'unit_test_response.java'), 'w') as f:
    #     f.write(unit_test_response.choices[0].message.content)
    # with open(os.path.join(os.path.join(code_path, bug_folder), 'oracle_test_prompt.txt'), 'w') as f:
    #     f.write(oracle_test_prompt[0]['content'])
    # with open(os.path.join(os.path.join(code_path, bug_folder), 'oracle_test_response.java'), 'w') as f:
    #     f.write(oracle_test_response.choices[0].message.content)

    # print(gtc.addingAssertLibInStatement('assertThrows(NumberIsTooLargeException.class, () -> new HypergeometricDistribution(10, 5, 15));'))

    # archive_path = 'C:\\Users\\leihan\\Desktop\\Code\\Eval\\test\\Math\\evosuite\\1'
    # for name in os.listdir(archive_path):
    #     if os.path.isdir(os.path.join(archive_path, name)):
    #         root_path_name = name
    #         break
    # test_results = sp.Popen('ipconfig', shell=True, stdin=sp.PIPE, stdout=sp.PIPE, stderr=sp.PIPE)
    # for test_result in test_results.stdout.readlines():
    #     line = test_result.decode('GBK')
    #     print(line)

    # devnull = open(os.devnull, 'wb')
    # archive_path = '/Code/Eval/test/'
    # compress_name = project + '-' + str(bug_number) + 'f-evosuite.1.tar.bz2'
    # compress_cmd = ['cd', archive_path, '&', 'tar', '-jcvf', compress_name, root_path_name]
    # sp.check_call(compress_cmd, shell=True, stdout=devnull, stderr=sp.STDOUT)

    # tree = javalang.parse.parse(open('C:\\Users\\leihan\\Desktop\\Code\\org\\apache\\commons\\math3\\fraction\\BigFraction_ESTest.java').read())
    # for path, node in tree.filter(javalang.tree.MethodInvocation):
    #     print(2)
    # print(1)

    # trs = sp.Popen(' '.join(['defects4j', 'test', '-w', '/Code/Eval/src', '-s', '/Code/Eval/test/Math/evosuite/1/Math-1f-evosuite.1.tar.bz2']), shell=True, stdin=sp.PIPE, stdout=sp.PIPE, stderr=sp.PIPE)
    # trs.wait()
    # print('stdout:-------------------------------')
    # for test_result in trs.stdout.readlines():
    #     line = test_result.decode()
    #     print(line)

    # res = Parallel(n_jobs=cpu_count(), backend='multiprocessing')(delayed(test)(x) for x in '1234567890')
    # print(1)
    
    # m = Manager()
    # q = m.Value('i', 0)
    # counting_process = Process(target=counting, name='counting', args=(10, q))
    # counting_process.start()
    # results = Parallel(n_jobs=cpu_count(), backend='multiprocessing')(delayed(test)(x, q) for x in '1234567890')
    # counting_process.terminate()

    # cmd = ['ipconfig']
    # r = sp.Popen(cmd, shell=True, stdin=sp.PIPE, stdout=sp.PIPE, stderr=sp.PIPE)
    # for i in r.stdout.readlines():
    #     print(i)
    #     print(i.decode('GBK'))

    # f = open("C:\\Users\\leihan\\Desktop\\Code\\testParse.java", 'r')
    # lines = f.readlines()
    # tree = javalang.parse.parse(''.join(lines))
    # nodes = []
    # class_dec = tree.types[0]
    # for class_dec_body in class_dec.body:
    #     if isinstance(class_dec_body, javalang.tree.MethodDeclaration) and 'public' in class_dec_body.modifiers and 'abstract' not in class_dec_body.modifiers:
    #         override_flag = False
    #         for annotion in class_dec_body.annotations:
    #             if annotion.name == 'Override':
    #                 override_flag = True
    #                 break
    #         if not override_flag:
    #             nodes.append(class_dec_body)

    print(1)

    # openai.api_key = "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"
    # list = openai.Model.list()
    # a = 1