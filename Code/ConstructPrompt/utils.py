import cchardet
import logging
import re
import json

count_for_tmp_vars = 0

prompt1 = "I'm providing you a java program and the test code for it. The test code's assert statement is not completed. I want you to use the variables to replace every $ARG$ in the assert statement in the test code carefully. Do not change anything except $ARG$. You should give me the replaced test code without any other words.\n"
prompt2 = "I'm providing you a java program and the test code for it. The test code needs assert statement so that it can tell if the program works. I want you to use assert statements to replace every $ASSERT$ in the test code carefully. Do not change anything except $ASSERT$. You should give me the replaced test code without any other words.\n"
prompt3 = "I'm providing you a java program and a test code template. I need you to write a test code which can reveal the bug of the program carefully. And the test code you give me should be runable. You should give me the test code without any other words.\n"
prompt4 = "I'm providing you a java program with bugs. I need you to write a test code which can reveal the bug of the program carefully. And the test code you give me should be runnable. You should give me the test code without any other words.\n"

template = ""

# 检查文件编码
def check_encoding(file_path):
    with open(file_path, 'rb') as f:
        data = f.read()
        encoding = cchardet.detect(data)
        return encoding.get('encoding')
    
# 判断一行中symbol的数量，除去引号内以及单行注释
def countSymbol(line, symbol):
    count = 0
    string_flag = False
    char_flag = False
    for i in range(len(line)):
        if line[i] == '/' and i + 1 < len(line) and line[i+1] == '/' and not string_flag and not char_flag:
            break
        if line[i] == '"' and (i == 0 or line[i - 1] != '\\'):
            string_flag = not string_flag
        if line[i] == '\'' and (i == 0 or line[i - 1] != '\\') and not string_flag:
            char_flag = not char_flag
        if string_flag or char_flag:
            continue
        if line[i] == symbol:
            count = count + 1
    return count

# 按最外层的符号分割
def splitOutter(str, symbol):
    br1 = False
    br2 = False
    br3 = False
    result = []
    start = 0
    for i in range(len(str)):
        if str[i] == '(':
            br1 = True
        elif str[i] == '[':
            br2 = True
        elif str[i] == '{':
            br3 = True
        if br1 or br2 or br3:
            if str[i] == ')':
                br1 = False
            elif str[i] == ']':
                br2 = False
            elif str[i] == '}':
                br3 = False
            continue
        if str[i] == symbol:
            result.append(str[start:i])
            start = i + 1
    result.append(str[start:])
    return result

# 将复杂assert statement拆分
def getSimpleStatement(statement):
    global count_for_tmp_vars
    result = []
    if countSymbol(statement, '(') <= 0:
        return [statement]
    if statement.find('assert') == -1:
        return [statement] 
    start = statement.find('(')
    end = statement.rfind(')')
    arguments = splitOutter(statement[start+1:end], ',')
    args = ""
    prefix = statement[:len(statement)-len(statement.lstrip())]
    for argument in arguments:
        result.append(prefix + '_tmp_var_' + str(count_for_tmp_vars) + '_ = ' + argument.replace(' ','') + ';\n')
        if args == "":
            args = args + '_tmp_var_' + str(count_for_tmp_vars) + '_'
        else:
            args = args + ', _tmp_var_' + str(count_for_tmp_vars) + '_'
        count_for_tmp_vars = count_for_tmp_vars + 1
    result.append(statement[:start+1] + args + statement[end:])
    return result

# 将复杂代码拆分简单代码
def getSimpleTriggerCode(trigger_code_lines, start, method_name):
    global count_for_tmp_vars
    count_for_tmp_vars = 0
    result = []
    prefix = trigger_code_lines[start][:len(trigger_code_lines[start])-len(trigger_code_lines[start].lstrip())]
    # 方法开头写入
    tmp = start
    while countSymbol(trigger_code_lines[tmp], '{') <= 0:
        result.append(trigger_code_lines[tmp].replace(method_name, 'testCode'))
        tmp = tmp + 1
    result.append(trigger_code_lines[tmp].replace(method_name, 'testCode'))
    # 找方法结尾
    end = start
    count = None
    comment = False
    end = end - 1
    while end + 1 < len(trigger_code_lines) or count == None:
        end = end + 1
        if comment and trigger_code_lines[end].find('*/') != -1:
            comment = False
            continue
        if trigger_code_lines[end].find('/*') != -1 and trigger_code_lines[end].find('*/') == -1 and trigger_code_lines[end][:trigger_code_lines[end].find('/*')].find('"') == -1:
            comment = True
            continue
        if countSymbol(trigger_code_lines[end], ';') != 0 and count == None:
            break
        left_count = countSymbol(trigger_code_lines[end], '{')
        right_count = countSymbol(trigger_code_lines[end], '}')
        diff = left_count - right_count
        if count == None and diff != 0:
            count = diff
        elif count != None:
            count = count + diff
        if count != None and count <= 0:
            break
    end = end + 1
    start = start + 1
    if end >= len(trigger_code_lines):
        logging.debug(trigger_code_lines[start:len(trigger_code_lines)])
        raise Exception("Error:Unexpected error in getSimpleTriggerCode()")
    if trigger_code_lines[end].find('public ') != -1 or trigger_code_lines[end].find('private ') != -1:
        end = end - 1
        while end > start and trigger_code_lines[end].find('@') != -1:
            end = end - 1
        if trigger_code_lines[end].find('*/') != -1:
            while end > trigger_code_lines and trigger_code_lines[end].find('/*') == -1:
                end = end - 1
    # 提取statements
    statements = []
    statement = ""
    while start < end:
        statement = statement.replace('\n', '') + trigger_code_lines[start]
        if countSymbol(trigger_code_lines[start], ';') > 0:
            statements.append(statement)
            statement = ""
        start = start + 1
    for statement in statements:
        result += getSimpleStatement(statement)
    result.append(prefix + "}\n")
    return result

def getSrcCodeMethod(java_code, start):
    define_line = start
    while start > 0:
        start = start - 1
        if java_code[start].find('@') == -1:
            break
    start = start + 1
    # 寻找方法注释
    tmp = start
    while start >= 0:
        if java_code[start].find('/*') != -1:
            break
        elif re.match('\s*(})\s*', java_code[start]) or java_code[start].find(' class ') != -1 or java_code[start].find(' interface ') != -1:
            start = tmp
            break
        start = start - 1
    # 找方法结尾
    end = define_line
    comment = False
    count = None
    end = end - 1
    while end + 1 < len(java_code) or count == None:
        end = end + 1
        if comment and java_code[end].find('*/') != -1:
            comment = False
            continue
        if java_code[end].find('/*') != -1 and java_code[end].find('*/') == -1 and java_code[end][:java_code[end].find('/*')].find('"') == -1:
            comment = True
            continue
        if countSymbol(java_code[end], ';') != 0 and count == None:
            break
        left_count = countSymbol(java_code[end], '{')
        right_count = countSymbol(java_code[end], '}')
        diff = left_count - right_count
        if count == None and diff != 0:
            count = diff
        elif count != None:
            count = count + diff
        if count != None and count <= 0:
            break
    end = end + 1

    if end >= len(java_code):
        logging.debug(java_code[define_line:len(java_code)])
        raise Exception("Error:Unexpected error in getSrcCodeMethod()")
    # 回退末尾不需要的内容
    if java_code[end].find('public ') != -1 or java_code[end].find('private ') != -1:
        end = end - 1
        while end > define_line and java_code[end].find('@') != -1:
            end = end - 1
        if java_code[end].find('*/') != -1:
            while end > define_line and java_code[end].find('/*') == -1:
                end = end - 1
    
    return java_code[start:end]

def generatePromptOne(test_code, src_code):
    for i in range(len(test_code)):
        if countSymbol(test_code[i], '(') > 0 and test_code[i].find('assert') != -1:
            start = test_code[i].find('(')
            end = test_code[i].rfind(')')
            length = len(test_code[i][start+1:end].split(','))
            if length != 0:
                test_code[i] = test_code[i][:start+1] + '$ARG$' + ((length-1)*', $ARG$') + test_code[i][end:]
    content = prompt1 + "Test code:\n" + ''.join(test_code) + "Java program:\n" + ''.join(src_code)
    req = [{'role': 'user', 'content': content}]
    return req

def generatePromptTwo(test_code, src_code):
    for i in range(len(test_code)):
        if countSymbol(test_code[i], '(') > 0 and test_code[i].find('assert') != -1:
            test_code[i] = test_code[i][:len(test_code[i])-len(test_code[i].lstrip())] + '$ASSERT$\n'
    content = prompt2 + "Test code:\n" + ''.join(test_code) + "Java program:\n" + ''.join(src_code)
    req = [{'role': 'user', 'content': content}]
    return req

def generatePromptThree(test_code, src_code):
    pass

def generatePromptFour(test_code, src_code):
    content = prompt4 + "Java program:\n" + ''.join(src_code)
    req = [{'role': 'user', 'content': content}]
    return req
