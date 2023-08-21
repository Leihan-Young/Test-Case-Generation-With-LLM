import os
import re
import logging
import chardet
import javalang

special_projects = ('Time',)

PATH_SPLITER = '/'

def checkOutSrcCode(defects4j_path, project, trigger_id, project_src_path, is_bug):
    logging.debug('checkOutSrcCode(defects4j_path=' + defects4j_path + ',project=' + project + ',trigger_id=' + trigger_id + ',project_src_path=' + project_src_path + 'is_bug=' + str(is_bug) + ')')
    cmd = defects4j_path + PATH_SPLITER + "framework" + PATH_SPLITER + "bin" + PATH_SPLITER + "defects4j checkout -p " + project + " -v " + trigger_id
    if is_bug:
        cmd = cmd + 'b'
    else:
        cmd = cmd + 'f'
    cmd = cmd + " -w " + project_src_path
    if os.system(cmd) == 1:
        if is_bug:
            fail_message = "Error:Fail to get " + project + ":" + trigger_id + "b source code"
        else:
            fail_message = "Error:Fail to get " + project + ":" + trigger_id + "f source code"
        print(fail_message)

def parseTriggerTestsFile(trigger_tests_path, project):
    logging.debug('parseTriggerTestsFile(trigger_tests_path=' + trigger_tests_path + ')')
    result = []
    flag = False
    with open(trigger_tests_path) as f:
        for line in f.readlines():
            if re.match('.*(Tests.).*(Tests.java:).*', line) or re.match('.*(Test.).*(Test.java:).*', line):
                flag = True
                for token in line.split(' '):
                    logging.debug('line=' + line + ',token=' + token)
                    if re.match('.*(Tests.).*(Tests.java:).*', token) or re.match('.*(Test.).*(Test.java:).*', token):
                        tmp = token.split('(')
                        folders = tmp[0].split('.')
                        test_function_name = folders[-1]
                        trigger_test_file_path = ''
                        for i in range(len(folders) - 2):
                            trigger_test_file_path = trigger_test_file_path + PATH_SPLITER + folders[i]
                        
                        file_name_and_line = tmp[1].split(':')
                        trigger_test_file_path = trigger_test_file_path + PATH_SPLITER + file_name_and_line[0]
                        target_line = int("".join(filter(str.isdigit, file_name_and_line[1])))
                        logging.debug('trigger_test_file_path=' + trigger_test_file_path + ',test_function_name=' + test_function_name + ',target_line=' + str(target_line))
                        result.append((trigger_test_file_path, test_function_name, target_line))
            elif project in special_projects:
                if re.match('.*(org.joda.).*(.Test).*(Test).*(.java:).*', line):
                    flag = True
                    for token in line.split(' '):
                        logging.debug('line=' + line + ',token=' + token)
                    if re.match('.*(org.joda.).*(.Test).*(Test).*(.java:).*', token):
                        tmp = token.split('(')
                        folders = tmp[0].split('.')
                        test_function_name = folders[-1]
                        trigger_test_file_path = ''
                        for i in range(len(folders) - 2):
                            trigger_test_file_path = trigger_test_file_path + PATH_SPLITER + folders[i]
                        
                        file_name_and_line = tmp[1].split(':')
                        trigger_test_file_path = trigger_test_file_path + PATH_SPLITER + file_name_and_line[0]
                        target_line = int("".join(filter(str.isdigit, file_name_and_line[1])))
                        logging.debug('trigger_test_file_path=' + trigger_test_file_path + ',test_function_name=' + test_function_name + ',target_line=' + str(target_line))
                        result.append((trigger_test_file_path, test_function_name, target_line))
            elif flag:
                break
    return result

def getTriggerTestFilePath(project_src_path):
    if os.path.exists(project_src_path + PATH_SPLITER + "tests"):
        return project_src_path + PATH_SPLITER + "tests"
    elif os.path.exists(project_src_path + PATH_SPLITER + "test"):
        return project_src_path + PATH_SPLITER + "test"
    elif os.path.exists(project_src_path + PATH_SPLITER + "bug" + PATH_SPLITER + "src" + PATH_SPLITER + "test"):
        return project_src_path + PATH_SPLITER + "bug" + PATH_SPLITER + "src" + PATH_SPLITER + "test"
    elif os.path.exists(project_src_path + PATH_SPLITER + "src" + PATH_SPLITER + "test" + PATH_SPLITER + "java"):
        return project_src_path + PATH_SPLITER + "src" + PATH_SPLITER + "test" + PATH_SPLITER + "java"
    elif os.path.exists(project_src_path + PATH_SPLITER + "src" + PATH_SPLITER + "test"):
        return project_src_path + PATH_SPLITER + "src" + PATH_SPLITER + "test"
    elif os.path.exists(project_src_path + PATH_SPLITER + "gson" + PATH_SPLITER + "src" + PATH_SPLITER + "test" + PATH_SPLITER + "java"):
        return project_src_path + PATH_SPLITER + "gson" + PATH_SPLITER + "src" + PATH_SPLITER + "test" + PATH_SPLITER + "java"

def getSrcCodeFilePath(project_src_path):
    if os.path.exists(project_src_path + PATH_SPLITER + "source"):
        return project_src_path + PATH_SPLITER + "source"
    elif os.path.exists(project_src_path + PATH_SPLITER + "src" + PATH_SPLITER + "main" + PATH_SPLITER + "java"):
        return project_src_path + PATH_SPLITER + "src" + PATH_SPLITER + "main" + PATH_SPLITER + "java"
    elif os.path.exists(project_src_path + PATH_SPLITER + "src" + PATH_SPLITER + "java"):
        return project_src_path + PATH_SPLITER + "src" + PATH_SPLITER + "java"
    elif os.path.exists(project_src_path + PATH_SPLITER + "src"):
        return project_src_path + PATH_SPLITER + "src"
    elif os.path.exists(project_src_path + PATH_SPLITER + "gson" + PATH_SPLITER + "src" + PATH_SPLITER + "main" + PATH_SPLITER + "java"):
        return project_src_path + PATH_SPLITER + "gson" + PATH_SPLITER + "src" + PATH_SPLITER + "main" + PATH_SPLITER + "java"

def getClassToTestName(class_name):
    logging.debug("getClassToTestName(class_name=" + class_name + ")")
    return class_name[0:class_name.index('Test')]

def check_encoding(file_path):
    with open(file_path, 'rb') as f:
        data = f.read()
        encoding = chardet.detect(data)
        return encoding.get('encoding')
    
def getTriggerTestCodeMethod(output_path, project_src_path, trigger_test_file_path, test_function_name, target_line):
    logging.debug('getTriggerTestCodeMethod(output_path=' + output_path + ',project_src_path=' + project_src_path + ',trigger_test_file_path=' + trigger_test_file_path + ',test_function_name=' + test_function_name + ',target_line=' + str(target_line) + ')')
    trigger_test_file_path = getTriggerTestFilePath(project_src_path) + trigger_test_file_path 
    test_file = open(trigger_test_file_path, 'r', encoding=check_encoding(trigger_test_file_path))
    test_file_lines = test_file.readlines()
    start = target_line
    end = target_line
    while start >= 0:
        if test_file_lines[start].find(test_function_name) != -1:
            break
        start = start - 1
    tmp = start
    while start >= 0:
        if test_file_lines[start].find('/**') != -1:
            break
        elif re.match('\s*(})\s*', test_file_lines[start]) or test_file_lines[start].find(' class ') != -1:
            start = tmp
            break
        start = start - 1
    if start < 0:
        raise Exception("Error:" + test_function_name + " not found in " + trigger_test_file_path)
    while end < len(test_file_lines):
        if re.match('(}\n)', test_file_lines[end]) or re.match('(})', test_file_lines[end]) or test_file_lines[end].find('/**') != -1 or test_file_lines[end].find('public ') != -1 or test_file_lines[end].find('private ') != -1 or test_file_lines[end].find('@Test') != -1:
            break
        end = end + 1
    if end >= len(test_file_lines):
        logging.debug(test_file_lines[target_line:len(test_file_lines)])
        raise Exception("Error:Unexpected error in getTriggerTestCode()")
    logging.debug('start=' + str(start) + ',end=' + str(end))
    with open(output_path + "triggerCode.java", 'a+') as f:
        f.writelines(test_file_lines[start:end])

def getSrcCodeMethods(class_path):
    methods = {}
    with open(class_path, 'r', encoding=check_encoding(class_path)) as src_file:
        src_code_tree = javalang.parse.parse(src_file.read())
        for path, node in src_code_tree.filter(javalang.tree.Declaration):
            if type(node) == javalang.tree.MethodDeclaration:
                methods[node.name].append(node)
    return methods

def getSrcCodeMethod(output_path, project_src_path, target_line):
    logging.debug("getSrcCodeMethod(output_path=" + output_file + ",project_src_path=" + project_src_path + ",target_line=" + str(target_line) + ")")
    output_file = open(output_path, 'a+')
    src_code_file = open(project_src_path, 'r', encoding=check_encoding(project_src_path))
    src_code = src_code_file.readlines()
    while target_line > 0:
        target_line = target_line - 1
        if src_code[target_line].find('@') == -1:
            break
    start = target_line
    while start >= 0:
        if src_code[start].find('/**') != -1:
            break
        elif re.match('\s*(})\s*', src_code[start]) or src_code[start].find(' class ') != -1:
            start = target_line
            break
        start = start - 1
    if start < 0:
        raise Exception("Error:Unexpected error in getSrcCodeMethod()")
    while end < len(src_code):
        if re.match('(}\n)', src_code[end]) or re.match('(})', src_code[end]) or src_code[end].find('/**') != -1 or src_code[end].find('public ') != -1 or src_code[end].find('private ') != -1 or src_code[end].find('@Test') != -1:
            break
        end = end + 1
    if end >= len(src_code):
        logging.debug(src_code[target_line:len(src_code)])
        raise Exception("Error:Unexpected error in getTriggerTestCode()")
    output_file.writelines(src_code[start, end])

def chooseBestMatchClassPath(strs):
    # 除去Test单词后，选出最长的str
    if len(strs) <= 0:
        raise Exception("Error:Input strs is of length 0 in chooseBestMatchClassPath()")
    res = strs[0]
    max_len = len(strs[0].split(PATH_SPLITER)[-1].replace('Test',''))
    for str in strs:
        if len(str) > max_len:
            max_len = len(str.split(PATH_SPLITER)[-1].replace('Test',''))
            res = str
    return res

def getSrcCode(output_path, project_src_path, target_nodes):
    if not os.path.exists(project_src_path):
        return
    src_code_file = open(project_src_path, 'r', encoding=check_encoding(project_src_path))
    src_code = src_code_file.readlines()
    with open(output_path, 'w') as f:
        for line in src_code:
            if comment_flag and line.find('*/') != -1:
                comment_flag = False
            elif comment_flag:
                continue
            elif line.find('/*') != -1 and line.find('*/') == -1:
                comment_flag = True
            elif line.find('public ') != -1 and line.find('class ') != -1:
                f.write(line)
            elif line.find('{') == -1 and (line.find('public ') != -1 or line.find('private ') != -1):
                f.write(line)
            elif line.find('return ') != -1 and line.find(';') != -1:
                break
    for node in target_nodes:
        getSrcCodeMethod(output_path, project_src_path, node.position.line)
    with open(output_path, 'w') as f:
        f.write('}\n')