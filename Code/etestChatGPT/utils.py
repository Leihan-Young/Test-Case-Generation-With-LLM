import javalang
import cchardet
import re
import env
import os

# 获取all_nodes中去除except_nodes外所有nodes的定义
def getMethodDefinitionLines(code_lines, all_nodes, except_nodes):
    res_lines = []
    for node in all_nodes:
        if node not in except_nodes:
            def_line = node.position.line - 1
            end_line = def_line
            if code_lines[def_line].find('abstract ') == -1:
                while end_line < len(code_lines) and countSymbol(code_lines[end_line], '{') == 0:
                    end_line += 1
                if end_line >= len(code_lines):
                    end_line = def_line
                    while countSymbol(code_lines[end_line], ';') == 0:
                        end_line += 1
                if len(code_lines[end_line].replace('{', '').lstrip().rstrip()) == 0:
                    end_line -= 1
                tmp = code_lines[def_line:end_line+1]
                tmp[-1] = tmp[-1].replace('{', '').replace('\n', '').rstrip() + ';\n'
                res_lines += tmp
            
    return res_lines

# 将dependence转为import行
def getImportLines(dep_packages):
    lines = []
    for dep in dep_packages:
        lines.append(f'import {dep};\n')
    return lines

# 获取已有dependence以及import开始的下标
def getDepsAndStartIndex(test_code):
    imports = set()
    index = -1
    for i in range(len(test_code)):
        if test_code[i].startswith('import'):
            imports.add(test_code[i].split(' ')[-1].replace(';', '').replace('\n', '').split('.')[-1])
            if index == -1:
                index = i
    return index, imports

# 获取项目内类的字典
def getProjectClasses(path):
    if not path.endswith(env.PATH_SPLITER):
        path += env.PATH_SPLITER
    res = {}
    for root, dirs,files in os.walk(path):
        for file in files:
            if file.endswith('.java') and os.path.isfile(os.path.join(root, file)):
                class_name = file[:file.rfind('.java')]
                res[class_name] = os.path.join(root, class_name).replace(path, '').replace(env.PATH_SPLITER, '.')
    return res

# 获取Java自带类的字典，如HashMap
def getJavaInnerClasses(rt_path):
    if not rt_path.endswith(env.PATH_SPLITER):
        rt_path += env.PATH_SPLITER
    res = {}
    for root, dirs, files in os.walk(rt_path):
        for file in files:
            if os.path.isfile(os.path.join(root, file)):
                class_name = file[:file.rfind('.class')]
                res[class_name] = os.path.join(root, class_name).replace(rt_path, '').replace(env.PATH_SPLITER, '.')
    return res
                
# 获取method中所有needed dependence
def getNeededDep(lines):
    try:
        tree = javalang.parse.parse(''.join(['public class test {\n'] + lines + ['}\n']))
        needed_dep = set()
        for path, node in tree.filter(javalang.tree.ReferenceType):
            needed_dep.add(node.name)
        for path, node in tree.filter(javalang.tree.MemberReference):
            if hasattr(node, 'qualifier') and node.qualifier != '':
                needed_dep.add(node.qualifier)
        return needed_dep
    except Exception:
        return set()
    

# 获取所有srcCode中的文件与路径，返回字典
def getSrcCodeFiles(src_path):
    res = {}
    for root, dirs, files in os.walk(src_path):
        for file in files:
            if os.path.isfile(os.path.join(root, file)):
                name = file[:file.rfind('.')]
                res[name] = os.path.join(root, file)
    return res

# 将prompt和response写入文件
def writePromptAndResponse(output_path, prompt, response):
    if not os.path.exists(output_path):
        os.makedirs(output_path)
    with open(os.path.join(output_path, 'test_prompt.txt'), 'w') as f:
        for item in prompt:
            f.write(f"[{item['role']}]:{item['content']}\n")
    if response != None:
        for i in range(len(response.choices)):
            with open(os.path.join(output_path, f'{i+1}_sample_test_response.txt'), 'w') as f:
                f.write(response.choices[i].message.content)

# 解析patch文件，提取出关键信息
def parseSrcPatch(file_path):
    patch = open(file_path, 'r', encoding=checkEncoding(file_path), errors='ignore')
    patch_lines = patch.readlines()
    related_classes = set()
    line_with_class = []
    for line in patch_lines:
        if line.startswith('+++') or line.startswith('---'):
            related_class = line.split('/')[-1][:line.split('/')[-1].find('.java')]
            related_classes.add(related_class)
            class_name = related_class
        elif line.startswith('@@'):
            line_str = line[line.find('@@') + 2:line.rfind('@@')].lstrip().rstrip()
            positions = line_str.split(' ')
            for position in positions:
                line = abs(int(position.split(',')[0]))
                line_with_class.append((line, class_name))
    return related_classes, line_with_class


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

# input test code method lines
# output the method names
def getMethodNamesFromMethodLines(test_code):
    try:
        tree = javalang.parse.parse(''.join(['public class test {\n'] + test_code + ['}\n']))
        node_names = []
        for path, node in tree.filter(javalang.tree.MethodDeclaration):
            node_names.append(node.name)
        return node_names
    except Exception:
        return []

# get trigger code methods
def getTriggerCodeMethods(trigger_code):
    tree = javalang.parse.parse(''.join(trigger_code))
    nodes = []
    for path, node in tree.filter(javalang.tree.MethodDeclaration):
        nodes.append(node)
    return getJavaCodeMethodWithNodes(trigger_code, nodes)

# get trigger code methods' names
def getTriggerCodeMethodNames(trigger_code):
    tree = javalang.parse.parse(''.join(trigger_code))
    names = []
    for path, node in tree.filter(javalang.tree.MethodDeclaration):
        names.append(node.name)
    return names

# write a method into a class
def writeMethodIntoClass(method_lines, class_lines):
    cur = len(class_lines) - 1
    while not class_lines[cur].startswith('}'):
        cur = cur - 1
    return class_lines[:cur] + method_lines + class_lines[cur:]

# concat a list to a string
def getStringFromList(list):
    if list == None:
        return 'null'
    str = ''
    return str.join(list)

# get a single src code method with start line number
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

# 从java源码中提取出package、import、class name，class内容为输入lines
# 同时class中第一个method之前的变量也会记录
def getJavaClassCodeWithLines(java_code, method_lines):
    res = []
    comment_flag = False
    start_class_flag = False
    inner_flag = False
    close_flag = False
    leading_comment = True
    for code_line in java_code:
        if start_class_flag and code_line.find('{') != -1:
            res.append(code_line)
            start_class_flag = False
        elif start_class_flag:
            res.append(code_line)
        elif code_line.startswith('package') or code_line.startswith('import'):
            leading_comment = False
            res.append(code_line)
        elif leading_comment:
            continue
        elif comment_flag and code_line.find('*/') != -1:
            comment_flag = False
            res.append(code_line)
        elif comment_flag:
            res.append(code_line)
        elif code_line.find('/*') != -1 and code_line.find('*/') == -1 and code_line[:code_line.find('/*')].find('"') == -1 and not inner_flag:
            comment_flag = True
            res.append(code_line)
        elif (code_line.find('class ') != -1 or code_line.find('interface ') != -1) and not inner_flag:
            res.append(code_line)
            inner_flag = True
            if code_line.find('{') != -1 and code_line.find('}') != -1:
                close_flag = True
            if code_line.find('{') == -1:
                start_class_flag = True
        elif code_line.find(';') != -1 and code_line.find('{') == -1 and (code_line.find('public ') != -1 or code_line.find('private ') != -1):
            res.append(code_line)
        elif (code_line.find('return ') != -1 and code_line.find(';') != -1) or (code_line.find('{') != -1 and code_line.find('}') == -1):
            break
    res = res + method_lines
    if not close_flag:
        res.append('}\n')
    return res

# 从java源码文件中获取对应行的方法
def getJavaCodeMethod(java_code, method_name, target_line_number):
    start = target_line_number
    # 如果方法前有@Test等修饰，加入
    define_line = start
    while start > 0:
        start = start - 1
        if java_code[start].find('@') == -1:
            break
    start = start + 1
    # 寻找方法注释
    tmp = start
    while start >= 0:
        if java_code[start].find('/*') != -1 and java_code[start][:java_code[start].find('/*')].find('*') == -1:
            break
        elif re.match('\s*(})\s*', java_code[start]) or java_code[start].find('class ') != -1 or java_code[start].find('interface ') != -1:
            start = tmp
            break
        start = start - 1
    if start < 0:
        raise Exception("Error:" + method_name + " not found in ")
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
        if comment:
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
        raise Exception("Error:Unexpected error in getJavaCodeMethod()")
    return java_code[start:end]

# get Java code through nodes
def getJavaCodeMethodWithNodes(code_lines, nodes):
    lines = []
    for node in nodes:
        lines = lines + getJavaCodeMethod(code_lines, node.name, node.position.line - 1)
    return lines

# check the encoding way of the file
def checkEncoding(file_path):
    with open(file_path, 'rb') as f:
        data = f.read()
        encoding = cchardet.detect(data)
        return encoding.get('encoding')

# get all method declaration nodes in the src code (include constructor declaration)
def getMethodDeclarationNodesInSrcCode(file):
    java_src_code_tree = javalang.parse.parse(file.read())
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


# count for a symbol in a line 
# symbol in the brackets or comments will be ignored
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

# split according to the outter symbol
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