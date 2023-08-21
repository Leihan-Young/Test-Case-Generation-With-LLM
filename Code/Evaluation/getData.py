import os
import javalang
import utils
import env
import logging

count_for_tmp_vars = 0
count_for_test_method = 0

# get triggerCode.java file
def getTriggerCode(bug_folder):
    return open(os.path.join(bug_folder, "triggerCode.java"), encoding=utils.checkEncoding(os.path.join(bug_folder, "triggerCode.java")), errors='ignore')

# get example test code (Different Class, Same Project)
def getExamples(bug_folder, work_dir, example_count):
    if example_count == 0:
        return []
    if not os.path.exists(os.path.join(bug_folder, "triggerCode.java")):
        return []
    package_name = utils.getPackageName(os.path.join(bug_folder, "triggerCode.java"))
    if package_name == None:
        return []
    package_name = package_name.split('.')
    class_name = utils.getClassName(os.path.join(bug_folder, "triggerCode.java"))
    if class_name == None:
        return []
    test_code_root_path = utils.getTestSrcCodePath(work_dir)
    examples = []
    for i in range(len(package_name)):
        walk_root = os.path.join(test_code_root_path, env.PATH_SPLITER.join(package_name[:len(package_name)-i]))
        for file in os.listdir(walk_root):
            if os.path.isfile(os.path.join(walk_root, file)) and file.find(class_name) == -1:
                with open(os.path.join(walk_root, file), 'r', encoding=utils.checkEncoding(os.path.join(walk_root, file)), errors='ignore') as f:
                    file_lines = f.readlines()
                file_tree = javalang.parse.parse(''.join(file_lines))
                class_body = file_tree.types[0].body
                for node in reversed(class_body):
                    if (not hasattr(node, 'body')) or (node.body == None) or (len(node.body) < 3):
                        continue
                    if isinstance(node, javalang.tree.MethodDeclaration) and 'public' in node.modifiers:
                        examples.append(utils.getJavaCodeMethod(file_lines, node.name, node.position.line - 1))
                        break
            if len(examples) >= example_count:
                break
        if len(examples) >= example_count:
            break
    return examples
    
    

# get the focal context
def getFocalContext(bug_folder):
    if not os.path.exists(os.path.join(bug_folder, "src.patch")) or not os.path.exists(os.path.join(bug_folder, "bugSrcCode")):
        return None
    focal_context = []
    related_classes, line_with_class_name = utils.parseSrcPatch(os.path.join(bug_folder, "src.patch"))
    file_with_path = {}
    bug_src_code_root = os.path.join(bug_folder, 'bugSrcCode')
    bug_additional_root = os.path.join(bug_folder, 'bug_additional')
    for file in os.listdir(bug_src_code_root):
        file_with_path[file.replace('.java', '')] = os.path.join(bug_src_code_root, file)
    if os.path.exists(bug_additional_root):
        for file in os.listdir(bug_additional_root):
            file_with_path[file.replace('.java', '')] = os.path.join(bug_additional_root, file)
    class_nodes = {}
    for related_class in related_classes:
        if related_class not in file_with_path:
            continue
        class_nodes[related_class] = []
        with open(file_with_path[related_class], 'r', encoding=utils.checkEncoding(file_with_path[related_class]), errors='ignore') as file:
            tree = javalang.parse.parse(file.read())
        class_dec = tree.types[0]
        for class_dec_body in class_dec.body:
            if isinstance(class_dec_body, javalang.tree.MethodDeclaration):
                class_nodes[related_class].append(class_dec_body)
        class_nodes[related_class].sort(key=lambda node:node.position.line, reverse=False)
    class_method_nodes = {}
    for key in class_nodes:
        class_method_nodes[key] = set()
    for (line, class_name) in line_with_class_name:
        if class_name not in class_nodes:
            continue
        for i in range(len(class_nodes[class_name])):
            if class_nodes[class_name][i].position.line > line:
                class_method_nodes[class_name].add(class_nodes[class_name][0 if i == 0 else i - 1])
                break
    
    # 加入triggerCode中出现的method
    if os.path.exists(os.path.join(bug_folder, "triggerCode.java")) or not os.path.exists(os.path.join(bug_folder, "bugSrcCode")):
        trigger_method_names = getMethodNamesInTriggerCode(bug_folder)
        for key, value in class_nodes.items():
            for node in value:
                if node.name in trigger_method_names:
                    class_method_nodes[key].add(node)

    for key, value in class_method_nodes.items():
        with open(file_with_path[key], 'r', encoding=utils.checkEncoding(file_with_path[key]), errors='ignore') as class_file:
            code_lines = class_file.readlines()

        # 减少token数量，不使用下面statements
        # new_lines = utils.getMethodDefinitionLines(code_lines, class_nodes[key], value)
        new_lines = utils.getJavaCodeMethodWithNodes(code_lines, list(value))
        # 去除implementation
        # new_lines = utils.getMethodDefinitionWithNodes(code_lines, list(value))

        # new_lines = utils.getJavaCodeMethodWithNodes(code_lines, list(value))
        focal_context += utils.getJavaClassCodeWithLines(code_lines, new_lines)
    return focal_context

def getOracleFocalMethod(bug_folder):
    # Based on triggerCode ↓
    if not os.path.exists(os.path.join(bug_folder, "triggerCode.java")) or not os.path.exists(os.path.join(bug_folder, "bugSrcCode")):
        return None
    focal_context = []
    trigger_method_names = getMethodNamesInTriggerCode(bug_folder)
    root_dir = os.path.join(bug_folder, 'bug_additional')
    for file in os.listdir(root_dir):
        extends_code_method_lines = []
        with open(os.path.join(root_dir, file), 'r', encoding=utils.checkEncoding(os.path.join(root_dir, file)), errors='ignore') as f:
            extends_code_nodes = utils.getMethodDeclarationNodesInSrcCode(f)
        with open(os.path.join(root_dir, file), 'r', encoding=utils.checkEncoding(os.path.join(root_dir, file)), errors='ignore') as f:
            extends_code_lines = f.readlines()
        for trigger_method_name in trigger_method_names:
            if trigger_method_name in extends_code_nodes:
                extends_code_method_lines = extends_code_method_lines + utils.getJavaCodeMethodWithNodes(extends_code_lines, extends_code_nodes[trigger_method_name])
        if len(extends_code_method_lines) > 0:
            focal_context = focal_context + utils.getJavaClassCodeWithLines(extends_code_lines, extends_code_method_lines)
    root_dir = os.path.join(bug_folder, 'bugSrcCode')
    for file in os.listdir(root_dir):
        method_lines = []
        with open(os.path.join(root_dir, file), 'r', encoding=utils.checkEncoding(os.path.join(root_dir, file)), errors='ignore') as f:
            src_code_nodes = utils.getMethodDeclarationNodesInSrcCode(f)
        with open(os.path.join(root_dir, file), 'r', encoding=utils.checkEncoding(os.path.join(root_dir, file)), errors='ignore') as f:
            src_code_lines = f.readlines()
        for trigger_method_name in trigger_method_names:
            if trigger_method_name in src_code_nodes:
                method_lines = method_lines + utils.getJavaCodeMethodWithNodes(src_code_lines, src_code_nodes[trigger_method_name])
        if len(method_lines) > 0:
            focal_context = focal_context + utils.getJavaClassCodeWithLines(src_code_lines, method_lines)
    if len(focal_context) == 0:
        return None
    return focal_context
    

# get all the method names and its position that appear in the triggerCode.java
def getMethodNamesInTriggerCode(bug_folder):
    trigger_code_file = getTriggerCode(bug_folder)
    trigger_code_tree = javalang.parse.parse(trigger_code_file.read())
    focal_method_names = set()
    for path, node in trigger_code_tree.filter(javalang.tree.MethodInvocation):
        if node.member == 'fail' or node.member == 'verifyException' or 'assert' in node.member:
            continue
        focal_method_names.add(node.member)
    for path, node in trigger_code_tree.filter(javalang.tree.ClassCreator):
        focal_method_names.add(node.type.name)
    return focal_method_names

# get all the method names that appear in the src code
def getMethodNamesInSrcCode(file):
    src_code_tree = javalang.parse.parse(file.read())
    method_declaration_nodes = {}
    for path, node in src_code_tree.filter(javalang.tree.MethodDeclaration):
        if node.name in method_declaration_nodes:
            method_declaration_nodes[node.name].append(node)
        else:
            method_declaration_nodes[node.name] = [node]
    for path, node in src_code_tree.filter(javalang.tree.ConstructorDeclaration):
        if node.name in method_declaration_nodes:
            method_declaration_nodes[node.name].append(node)
        else:
            method_declaration_nodes[node.name] = [node]
    return method_declaration_nodes



# get simplified assert statement
def getSimpleStatement(statement):
    global count_for_tmp_vars
    result = []
    if utils.countSymbol(statement, '(') <= 0:
        return [statement]
    if statement.find('catch') != -1:
        catch_exception = statement[statement.find('(')+1:statement.rfind(')')].lstrip().rstrip()
        tmp = catch_exception.split(' ')
        tmp[0] = '$EXCEPTION$'
        return statement[:statement.find('(')+1] + ' '.join(tmp) + statement[statement.rfind(')'):]
    if statement.find('assert') == -1:
        return [statement] 
    start = statement.find('(')
    end = statement.rfind(')')
    arguments = utils.splitOutter(statement[start+1:end], ',')
    args = ""
    prefix = statement[:len(statement)-len(statement.lstrip())]
    for argument in arguments:
        result.append(prefix + '_tmp_var_' + str(count_for_tmp_vars) + '_ = ' + argument + ';\n')
        if args == "":
            args = args + '_tmp_var_' + str(count_for_tmp_vars) + '_'
        else:
            args = args + ', _tmp_var_' + str(count_for_tmp_vars) + '_'
        count_for_tmp_vars = count_for_tmp_vars + 1
    result.append(statement[:len(statement)-len(statement.lstrip())] + "$ASSERT$\n")
    return result

# get simplified trigger code
def getSimpleTriggerCode(trigger_code_lines, start, method_name):
    global count_for_tmp_vars
    global count_for_test_method
    count_for_tmp_vars = 0
    result = []
    prefix = trigger_code_lines[start][:len(trigger_code_lines[start])-len(trigger_code_lines[start].lstrip())]
    # 方法开头写入
    tmp = start
    while utils.countSymbol(trigger_code_lines[tmp], '{') <= 0:
        result.append(trigger_code_lines[tmp].replace(method_name, f'testMethod{count_for_test_method}'))
        tmp = tmp + 1
    result.append(trigger_code_lines[tmp].replace(method_name, f'testMethod{count_for_test_method}'))
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
        if utils.countSymbol(trigger_code_lines[end], ';') != 0 and count == None:
            break
        left_count = utils.countSymbol(trigger_code_lines[end], '{')
        right_count = utils.countSymbol(trigger_code_lines[end], '}')
        diff = left_count - right_count
        if count == None and diff != 0:
            count = diff
        elif count != None:
            count = count + diff
        if count != None and count <= 0:
            break
    end = end + 1
    # start = tmp + 1
    if end >= len(trigger_code_lines):
        raise Exception("Error:Unexpected error in getSimpleTriggerCode()")
    
    result_ = trigger_code_lines[start:end]
    result = []
    for i in range(len(result_)):
        result += getSimpleStatement(result_[i])
    return result

    # 提取statements
    # statements = []
    # statement = ""
    # comment_flag = False
    # while start < end:
    #     line = trigger_code_lines[start]
    #     string_flag = False
    #     for i in range(1, len(line)):
    #         if not string_flag and line[i] == '/' and line[i-1] == '/':
    #             line = line[:i-1] + '\n'
    #             break
    #         elif line[i] == '"' and line[i-1] != '\\':
    #             string_flag = not string_flag
    #         elif line[i] == '*' and line[i-1] == '/':
    #             comment_flag = True
    #         elif line[i] == '/' and line[i-1] == '*':
    #             comment_flag = False
    #     if len(line.lstrip().replace('\n', '')) == 0:
    #         start = start + 1
    #         continue
    #     if not comment_flag:
    #         statement = statement + line
    #         if utils.countSymbol(line, ';') > 0:
    #             statements.append(statement.replace('\n', '') + '\n')
    #             statement = ""
    #     start = start + 1
    # statements.append(statement)
    # for statement in statements:
    #     result += getSimpleStatement(statement)
    # for i in range(len(result)):
    #     if result[i].find('@Test') != -1:
    #         break
    #     elif result[i].find('{')  != -1:
    #         result = result[:i] + [result[i][:len(result[i])-len(result[i].lstrip())] + '@Test\n'] + result[i:]
    #         break
    # return result

# get related test method name with a line number
def getRelatedTestMethodName(file):
    file_tree = javalang.parse.parse(file.read())
    for path, node in file_tree.filter(javalang.tree.MethodDeclaration):
        return node.position.line, node.name

# try to get test_class_path
def tryToGetTestClassPath(bug_folder):
    try:
        if not os.path.exists(os.path.join(bug_folder, "triggerCode.java")):
            return None
        trigger_tree = javalang.parse.parse(getTriggerCode(bug_folder).read())
        trigger_code_lines = getTriggerCode(bug_folder).readlines()
        test_class_path = None
        for line in trigger_code_lines:
            if line.startswith('package'):
                test_class_path = line.split(' ')[-1].replace(';', '').replace('\n', '')
        for path, node in trigger_tree.filter(javalang.tree.ClassDeclaration):
            test_class_path = f'{test_class_path}.{node.name}'
            test_class_path = test_class_path.replace('.', env.PATH_SPLITER) + ".java"
        return test_class_path
    except Exception:
        return None

# get test prefix in the trigger code
def getTestPrefix(bug_folder):
    if not os.path.exists(os.path.join(bug_folder, "triggerCode.java")) or not os.path.exists(os.path.join(bug_folder, "bugSrcCode")):
        return None, None
    trigger_tree = javalang.parse.parse(getTriggerCode(bug_folder).read())
    trigger_code_lines = getTriggerCode(bug_folder).readlines()
    test_class_path = None
    for line in trigger_code_lines:
        if line.startswith('package'):
            test_class_path = line.split(' ')[-1].replace(';', '').replace('\n', '')
    if test_class_path == None:
        return None, None
    for path, node in trigger_tree.filter(javalang.tree.ClassDeclaration):
        test_class_path = f'{test_class_path}.{node.name}'
        test_class_path = test_class_path.replace('.', env.PATH_SPLITER) + '.java'
        break
    public_nodes = []
    private_nodes = []
    class_dec = trigger_tree.types[0]
    for class_dec_body in class_dec.body:
        if isinstance(class_dec_body, javalang.tree.MethodDeclaration) and 'abstract' not in class_dec_body.modifiers:
            override_flag = False
            for annotion in class_dec_body.annotations:
                if annotion.name == 'Override':
                    override_flag = True
                    break
            if not override_flag:
                if 'public' in class_dec_body.modifiers:
                    public_nodes.append(class_dec_body)
                else:
                    private_nodes.append(class_dec_body)
    lines = []
    global count_for_test_method
    count_for_test_method = 0
    for node in private_nodes:
        lines = lines + utils.getJavaCodeMethod(trigger_code_lines, node.name, node.position.line - 1)
    for node in public_nodes:
        count_for_test_method = count_for_test_method + 1
        lines = lines + getSimpleTriggerCode(trigger_code_lines, node.position.line - 1, node.name)
    return test_class_path, lines

'''
# get test prefix in the trigger code
def getTestPrefixOld(bug_folder):
    if not os.path.exists(os.path.join(bug_folder, "triggerCode.java")) or not os.path.exists(os.path.join(bug_folder, "bugSrcCode.java")) or not os.path.exists(os.path.join(bug_folder, "fixSrcCode.java")):
        return None
    trigger_method_names = getMethodNamesInTriggerCode(bug_folder)
    src_method_names = getMethodNamesInSrcCode(getBugSrcCode(bug_folder))
    for trigger_method_name in trigger_method_names:
        if trigger_method_name in src_method_names:
            start_line, related_method_name = getRelatedTestMethodName(getTriggerCode(bug_folder))
            return getSimpleTriggerCode(getTriggerCode(bug_folder).readlines(), start_line - 1, related_method_name)
'''
            
# walk through the codes extracted
def walkThroughCodes(code_path, rel_path):
    # logging.debug(f'code_path={code_path},rel_path={rel_path}')
    res = []
    once = False
    if len(rel_path.split(env.PATH_SPLITER)) > 1:
        once = True
    for file in os.listdir(code_path):
        file_path = os.path.join(code_path, file)
        if os.path.isdir(file_path) and file != 'bugSrcCode' and file != 'bug_additional':
            res = res + walkThroughCodes(file_path, os.path.join(rel_path, file))
            if once:
                break
        else:
            res.append(rel_path)
            break
    return res