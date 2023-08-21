import logging
import env
import subprocess as sp
import cchardet
import re
import os
import javalang

# 将源码check out出来
def checkOutSrcCode(defects4j_path, project, version_id, project_src_path):
    logging.debug('checkOutSrcCode(defects4j_path=' + defects4j_path + ',project=' + project + ',version_id=' + version_id + ',project_src_path=' + project_src_path + ')')
    devnull = open(os.devnull, 'wb')
    sp.check_call(['defects4j', 'checkout', '-p', project, '-v', version_id, '-w', project_src_path], stdout=devnull, stderr=sp.STDOUT)
    
# 检查文件编码
def check_encoding(file_path):
    with open(file_path, 'rb') as f:
        data = f.read()
        encoding = cchardet.detect(data)
        return encoding.get('encoding')

# 解析trigger异常栈的一行
def parseExceptionStackLine(line):
    pattern = line.split(' ')[-1]
    folder = pattern.split('(')[0]
    test_method_name = folder[folder.rfind('.') + 1:]
    folder = folder[:folder.rfind('.')]
    trigger_test_file_path = env.PATH_SPLITER + folder.replace('.', env.PATH_SPLITER)
    if trigger_test_file_path.find('$') != -1:
        trigger_test_file_path = trigger_test_file_path[:trigger_test_file_path.find('$')] + '.java'
    else:
        trigger_test_file_path = trigger_test_file_path + '.java'
    target_line_number = int("".join(filter(str.isdigit, pattern.split('(')[1].split(':')[-1])))
    return [(trigger_test_file_path, test_method_name, target_line_number),]
    
# 解析trigger tests文件
def parseTriggerTestsFile(trigger_tests_file_path, project):
    logging.debug('parseTriggerTestsFile(trigger_tests_path=' + trigger_tests_file_path + ',project=' + project + ')')
    result = []
    trigger_tests_file = open(trigger_tests_file_path, 'r', encoding=check_encoding(trigger_tests_file_path), errors='ignore')
    trigger_tests_file_lines = trigger_tests_file.readlines()
    test_code_path = ''
    stack = []
    flag1 = False
    flag2 = False
    for line in trigger_tests_file_lines:
        if line.startswith('---'):
            if test_code_path != '':
                break
                result.append((test_code_path, stack))
            test_code_path = line.split(' ')[-1]
            flag1 = True
            flag2 = False
            stack = []
        # elif flag1 and ((re.match('.*(Tests.).*(Tests.java:).*', line) or re.match('.*(Test.).*(Test.java:).*', line)) or (project in env.SPECIAL_PROJECTS and re.match('.*(org.joda.).*(.Test).*(Test).*(.java:).*', line))):
        elif flag1 and line.find(test_code_path.split('::')[-1].replace('\n', '')) != -1:
            stack = stack + parseExceptionStackLine(line)
            flag2 = True
        elif flag2:
            flag1 = False
    result.append((test_code_path, stack))
    logging.debug('result=' + str(result))
    return result

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


# 从java源码文件中获取对应行的方法
def getJavaCodeMethod(java_file_path, method_name, target_line_number, define_line_number=False):
    logging.debug("getJavaCodeMethod(java_file_path=" + java_file_path + ",method_name=" + method_name + ",target_line_number=" + str(target_line_number) + ")")
    java_file = open(java_file_path, 'r', encoding=check_encoding(java_file_path), errors='ignore')
    java_code = java_file.readlines()
    start = target_line_number
    # 找到方法定义行
    if not define_line_number:
        while start >= 0:
            if java_code[start].find(method_name) != -1 and (java_code[start].find('public ') != -1 or java_code[start].find('private ') != -1):
                break
            start = start - 1
    # 如果方法前有@Test等修饰，也加入
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
    if start < 0:
        raise Exception("Error:" + method_name + " not found in " + java_file_path)
    # 找方法结尾
    end = define_line
    logging.debug('define_line=' + str(define_line))
    comment = False
    count = None
    end = end - 1
    while end + 1 < len(java_code) or count == None:
        end = end + 1
        if comment and java_code[end].find('*/') != -1:
            comment = False
            continue
        if java_code[end].find('/*') != -1 and java_code[end].find('*/') == -1 and java_code[end][:java_code[end].find('/*')].find('"') == -1 and java_code[end][:java_code[end].find('/*')].find('}') == -1:
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
    if java_code[end].find('/*') != -1 and java_code[end][:java_code[end].find('/*')].find('"') == -1 and java_code[end][:java_code[end].find('/*')].find('}') == -1:
        java_code[end] = java_code[end][:java_code[end].find('}') + 1]
    end = end + 1

    # while end < len(java_code):
    #     if re.match('(}\n)', java_code[end]) or re.match('(})', java_code[end]) or java_code[end].find('public ') != -1 or java_code[end].find('private ') != -1 or java_code[end].find('@Test') != -1:
    #         break
    #     end = end + 1
    if end >= len(java_code):
        logging.debug(java_code[target_line_number:len(java_code)])
        raise Exception("Error:Unexpected error in getJavaCodeMethod()")
    # 回退末尾不需要的内容
    if java_code[end].find('public ') != -1 or java_code[end].find('private ') != -1:
        end = end - 1
        while end > target_line_number and java_code[end].find('@') != -1:
            end = end - 1
        if java_code[end].find('*/') != -1:
            while end > target_line_number and java_code[end].find('/*') == -1:
                end = end - 1

    logging.debug("start=" + str(start) + ",end=" + str(end))
    return java_code[start:end]

# 获取bug->fix的patch
def getSrcPatch(output_path, defects4j_path, project, bug_id):
    patch_file_path = defects4j_path + env.PATH_SPLITER + 'framework' + env.PATH_SPLITER + 'projects' + env.PATH_SPLITER + project + env.PATH_SPLITER + 'patches' + env.PATH_SPLITER + f'{bug_id}.src.patch'
    if not os.path.exists(patch_file_path):
        logging.error(f'{project}-{bug_id}:patch file not exists')
        return
    with open(output_path, 'w') as output_file:
        with open(patch_file_path, 'r', encoding=check_encoding(patch_file_path), errors='ignore') as patch_file:
            output_file.write(patch_file.read())

# 将测试方法中的多行statement转为一行statement
def regularTestMethod(lines):
    new_lines = []
    new_line = ''
    i = 0
    while i < len(lines):
        new_line = lines[i]
        for assert_method in env.ASSERT_METHODS:
            if lines[i].find(assert_method) != -1:
                if countSymbol(lines[i], ';') == 0:
                    new_line = new_line.replace('\n', '') + ' '
                    temp_start = i + 1
                    while i + 1 < len(lines) and countSymbol(lines[i], ';') == 0:
                        i = i + 1
                    for j in range(temp_start, i):
                        new_line += lines[j].lstrip().replace('\n', '') + ' '
                    new_line += lines[i].lstrip()
                break
        new_lines.append(new_line)
        i = i + 1
    return new_lines

# 从java源码中提取出package、import、class name，class内容为输入lines
# 同时class中第一个method之前的变量也会记录
def writeJavaClassWithLines(output_path, java_code_path, lines):
    logging.debug("writeJavaClassWithLines(output_path=" + output_path + ",java_code_path=" + java_code_path + ",lines=" + str(lines) + ")")
    if not os.path.exists(output_path[:output_path.rfind(env.PATH_SPLITER)]):
        os.makedirs(output_path[:output_path.rfind(env.PATH_SPLITER)])
    java_code_file = open(java_code_path, 'r', encoding=check_encoding(java_code_path), errors='ignore')
    output_file = open(output_path, 'w')
    java_code = java_code_file.readlines()
    comment_flag = False
    start_class_flag = False
    inner_flag = False
    for code_line in java_code:
        if start_class_flag and code_line.find('{') != -1:
            output_file.write(code_line)
            start_class_flag = False
        elif start_class_flag:
            output_file.write(code_line)
        elif code_line.startswith('package') or code_line.startswith('import'):
            output_file.write(code_line)
        elif comment_flag and code_line.find('*/') != -1:
            comment_flag = False
            output_file.write(code_line)
        elif comment_flag:
            output_file.write(code_line)
        elif code_line.find('/*') != -1 and code_line.find('*/') == -1 and code_line[:code_line.find('/*')].find('"') == -1 and not inner_flag:
            comment_flag = True
            output_file.write(code_line)
        elif code_line.find('public ') != -1 and code_line.find('class ') != -1 and code_line.find('interface ') and not inner_flag:
            output_file.write(code_line)
            inner_flag = True
            if code_line.find('{') == -1:
                start_class_flag = True
        elif code_line.find(';') != -1 and code_line.find('{') == -1 and (code_line.find('public ') != -1 or code_line.find('private ') != -1):
            output_file.write(code_line)
        elif (code_line.find('return ') != -1 and code_line.find(';') != -1) or (code_line.find('{') != -1 and code_line.find('}') == -1):
            break
    # 写private
    nodes = []
    with open(java_code_path, 'r', encoding=check_encoding(java_code_path), errors='ignore') as f:
        tree = javalang.parse.parse(f.read())
        for path, node in tree.filter(javalang.tree.MethodDeclaration):
            if 'private' in node.modifiers:
                nodes.append(node)
    output_file.writelines(getJavaCodeMethodWithNodes(java_code_path, nodes))
    output_file.writelines(lines)
    output_file.write('}\n')

# 获取所有srcCode中的文件与路径，返回字典
def getSrcCodeFiles(src_path):
    res = {}
    for root, dirs, files in os.walk(src_path):
        for file in files:
            if os.path.isfile(os.path.join(root, file)):
                name = file[:file.rfind('.')]
                res[name] = os.path.join(root, file)
    return res

# 获取extends类
def getExtendsFiles(output_path, src_path, src_code_files):
    try:
        logging.debug(f'getExtendsFiles(output_path={output_path}, src_path={src_path})')
        if not os.path.exists(output_path):
            os.makedirs(output_path)
        with open(src_path, 'r', encoding=check_encoding(src_path), errors='ignore') as src_file:
            code_tree = javalang.parse.parse(src_file.read())
        node = code_tree.children[2][0]
        if not hasattr(node, 'extends') or node.extends == None:
            return
        if isinstance(node.extends, javalang.tree.ReferenceType):
            extends_name = node.extends
        else:
            extends_name = node.extends[0]
        while extends_name.sub_type != None:
            extends_name = extends_name.sub_type
        if extends_name.name not in src_code_files:
            return
        extends_src_code_path = src_code_files[extends_name.name]
        if not os.path.exists(extends_src_code_path):
            return
        file_name = extends_src_code_path.split(env.PATH_SPLITER)[-1]
        copyFile(output_path + file_name, extends_src_code_path)
        getExtendsFiles(output_path, extends_src_code_path, src_code_files)
    except Exception:
        return {}


# src_path内容复制到output_path
def copyFile(output_path, src_path):
    logging.debug("copyFile(output_path=" + output_path + ",src_path=" + src_path + ")")
    if not os.path.exists(output_path[:output_path.rfind(env.PATH_SPLITER)]):
        os.makedirs(output_path[:output_path.rfind(env.PATH_SPLITER)])
    src_file = open(src_path, 'r', encoding=check_encoding(src_path), errors='ignore')
    output_file = open(output_path, 'w')
    output_file.writelines(src_file.readlines())

# 获取修改的类，用于寻找method
def getModifiedClasses(defects4j_path, project, bug_id):
    logging.debug("getModifiedClasses(defects4j_path=" + defects4j_path + ",project=" + project + ",bug_id=" + bug_id + ")")
    modified_classes_file_path = defects4j_path + env.PATH_SPLITER + 'framework' + env.PATH_SPLITER + 'projects' + env.PATH_SPLITER + project + env.PATH_SPLITER + 'modified_classes' + env.PATH_SPLITER + bug_id + '.src'
    modified_classes_file = open(modified_classes_file_path, 'r', encoding=check_encoding(modified_classes_file_path), errors='ignore')
    classes = modified_classes_file.readlines()
    for i in range(len(classes)):
        classes[i] = classes[i].replace('\n', '')
    return classes

# 获取加载的类，用于寻找method
def getLoadedClasses(defects4j_path, project, bug_id):
    logging.debug("getLoadedClasses(defects4j_path=" + defects4j_path + ",project=" + project + ",bug_id=" + bug_id + ")")
    modified_classes_file_path = defects4j_path + env.PATH_SPLITER + 'framework' + env.PATH_SPLITER + 'projects' + env.PATH_SPLITER + project + env.PATH_SPLITER + 'loaded_classes' + env.PATH_SPLITER + bug_id + '.src'
    modified_classes_file = open(modified_classes_file_path, 'r', encoding=check_encoding(modified_classes_file_path), errors='ignore')
    classes = modified_classes_file.readlines()
    for i in range(len(classes)):
        classes[i] = classes[i].replace('\n', '')
    return classes

# Test源码路径
def getTestSrcCodePath(project_src_path):
    if os.path.exists(project_src_path + env.PATH_SPLITER + "tests"):
        return project_src_path + env.PATH_SPLITER + "tests"
    elif os.path.exists(project_src_path + env.PATH_SPLITER + "test"):
        return project_src_path + env.PATH_SPLITER + "test"
    elif os.path.exists(project_src_path + env.PATH_SPLITER + "bug" + env.PATH_SPLITER + "src" + env.PATH_SPLITER + "test"):
        return project_src_path + env.PATH_SPLITER + "bug" + env.PATH_SPLITER + "src" + env.PATH_SPLITER + "test"
    elif os.path.exists(project_src_path + env.PATH_SPLITER + "src" + env.PATH_SPLITER + "test" + env.PATH_SPLITER + "java"):
        return project_src_path + env.PATH_SPLITER + "src" + env.PATH_SPLITER + "test" + env.PATH_SPLITER + "java"
    elif os.path.exists(project_src_path + env.PATH_SPLITER + "src" + env.PATH_SPLITER + "test"):
        return project_src_path + env.PATH_SPLITER + "src" + env.PATH_SPLITER + "test"
    elif os.path.exists(project_src_path + env.PATH_SPLITER + "gson" + env.PATH_SPLITER + "src" + env.PATH_SPLITER + "test" + env.PATH_SPLITER + "java"):
        return project_src_path + env.PATH_SPLITER + "gson" + env.PATH_SPLITER + "src" + env.PATH_SPLITER + "test" + env.PATH_SPLITER + "java"

# 源码路径
def getJavaSrcCodePath(project_src_path):
    if os.path.exists(project_src_path + env.PATH_SPLITER + "source"):
        return project_src_path + env.PATH_SPLITER + "source"
    elif os.path.exists(project_src_path + env.PATH_SPLITER + "src" + env.PATH_SPLITER + "main" + env.PATH_SPLITER + "java"):
        return project_src_path + env.PATH_SPLITER + "src" + env.PATH_SPLITER + "main" + env.PATH_SPLITER + "java"
    elif os.path.exists(project_src_path + env.PATH_SPLITER + "src" + env.PATH_SPLITER + "java"):
        return project_src_path + env.PATH_SPLITER + "src" + env.PATH_SPLITER + "java"
    elif os.path.exists(project_src_path + env.PATH_SPLITER + "src"):
        return project_src_path + env.PATH_SPLITER + "src"
    elif os.path.exists(project_src_path + env.PATH_SPLITER + "gson" + env.PATH_SPLITER + "src" + env.PATH_SPLITER + "main" + env.PATH_SPLITER + "java"):
        return project_src_path + env.PATH_SPLITER + "gson" + env.PATH_SPLITER + "src" + env.PATH_SPLITER + "main" + env.PATH_SPLITER + "java"
    
# 获取Java类中的所有MethodDeclaration
def getMethodsOfJavaClass(src_code_path):
    logging.debug("getMethodsOfJavaClass(src_code_path=" + src_code_path + ")")
    java_src_code_file = open(src_code_path, 'r', encoding=check_encoding(src_code_path), errors='ignore')
    java_src_code_tree = javalang.parse.parse(java_src_code_file.read())
    method_declaration_nodes = {}
    for path, node in java_src_code_tree.filter(javalang.tree.MethodDeclaration):
        if node.name in method_declaration_nodes:
            method_declaration_nodes[node.name].append(node)
        else:
            method_declaration_nodes[node.name] = [node]
    for path, node in java_src_code_tree.filter(javalang.tree.ConstructorDeclaration):
        if node.name in method_declaration_nodes:
            method_declaration_nodes[node.name].append(node)
        else:
            method_declaration_nodes[node.name] = [node]
    return method_declaration_nodes

# 获取trigger code中调用的methods
def getFocalMethodNamesOfTriggerCode(trigger_code_path):
    logging.debug("getFocalMethodNamesOfTriggerCode(trigger_code_path=" + trigger_code_path + ")")
    trigger_code_file = open(trigger_code_path, 'r', encoding=check_encoding(trigger_code_path), errors='ignore')
    trigger_code_tree = javalang.parse.parse(trigger_code_file.read())
    focal_method_names = set()
    for path, node in trigger_code_tree.filter(javalang.tree.MethodInvocation):
        if node.member == 'fail' or node.member == 'verifyException' or 'assert' in node.member:
            continue
        focal_method_names.add(node.member)
    for path, node in trigger_code_tree.filter(javalang.tree.ClassCreator):
        focal_method_names.add(node.type.name)
    return focal_method_names

# 与getJavaCodeMethod一样，输入改为nodes
def getJavaCodeMethodWithNodes(java_file_path, nodes):
    lines = []
    for node in nodes:
        lines = lines + getJavaCodeMethod(java_file_path, node.name, node.position.line - 1, True)
    return lines