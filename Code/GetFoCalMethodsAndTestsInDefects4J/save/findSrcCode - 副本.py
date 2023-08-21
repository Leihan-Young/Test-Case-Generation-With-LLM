import os
import re
import logging
import chardet
import javalang
import utils

PATH_SPLITER = '/'

def getTriggerTestCode(output_path, project_src_path, result_set):
    logging.debug("getTriggerTestCode(output_path=" + output_path + ",project_src_path=" + project_src_path + ",result_set=" + str(result_set) + ")")
    if not os.path.exists(output_path):
        os.makedirs(output_path)
    trigger_test_file_path = result_set[0][0]
    trigger_test_file_path = utils.getTriggerTestFilePath(project_src_path) + trigger_test_file_path 
    class_name = trigger_test_file_path.split(PATH_SPLITER)[-1].split('.')[0]
    trigger_test_file = open(trigger_test_file_path, 'r', encoding=utils.check_encoding(trigger_test_file_path))
    test_file_lines = trigger_test_file.readlines()
    if not os.path.exists(output_path + 'triggerCode.java'):
        comment_flag = False
        with open(output_path + "triggerCode.java", 'w') as f: 
            for line in test_file_lines:
                if comment_flag and line.find('*/') != -1:
                    comment_flag = False
                elif comment_flag:
                    continue
                elif line.find('/*') != -1 and line.find('*/') == -1:
                    comment_flag = True
                elif line.startswith('import') or line.startswith('package'):
                    f.write(line)
                elif line.find('public ') != -1 and line.find('class ') != -1:
                    f.write(line)
                elif (line.find('{') == -1 or line.find('}') != -1) and (line.find('public ') != -1 or line.find('private ') != -1):
                    f.write(line)
                elif (line.find('return ') != -1 and line.find(';') != -1) or (line.find('class') == -1 and line.find(class_name) != -1):
                    break
    for (trigger_test_file_path, test_function_name, target_line) in result_set:
        utils.getTriggerTestCodeMethod(output_path, project_src_path, trigger_test_file_path, test_function_name, target_line)
    with open(output_path + 'triggerCode.java', 'a+') as f:
        f.write('}\n')

def findMethodsRecursively(qualifier, member, arguments, import_dic):
    logging.debug("findMethodsRecursively(qualifier=" + qualifier + ",member=" + member + ",..)")
    if qualifier == None or member == None:
        return None
    result = []
    for argument in arguments:
        if hasattr(argument, 'qualifier') and hasattr(argument, 'member') and hasattr(argument, 'arguments'):
            result = result + findMethodsRecursively(argument.qualifier, argument.member, argument.arguments, import_dic)
    result.append((qualifier, member, arguments))
    return result

def getSrcCodeTested(output_path, project_src_path, method_name, arguments):
    logging.debug("getSrcCodeTested(output_path=" + output_path + ",project_src_path=" + project_src_path + ",method_name=" + method_name + ",arguments=..)")
    if not os.path.exists(project_src_path):
        raise Exception("Error:can't get src code in getSrcCodeTested()")
    src_code_file = open(project_src_path, 'r')
    src_code = src_code_file.readlines()
    comment_flag = False
    with open(output_path, 'w') as f:
        for line in src_code:
            logging.debug("line=" + line)
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
    src_code_file.close()
    src_code_file = open(project_src_path, 'r', encoding=utils.check_encoding(project_src_path))
    src_code_tree = javalang.parse.parse(src_code_file.read())
    target_lines = []
    for path, node in src_code_tree.filter(javalang.tree.Declaration):
        if type(node) == javalang.tree.MethodDeclaration and node.name == method_name and len(node.parameters) == len(arguments):
            target_lines.append(node.position.line)
    for target_line in target_lines:
        utils.getSrcCodeMethod(output_path, project_src_path, target_line)
    with open(output_path, 'a+') as f:
        f.write('}\n')

def parseTriggerCode(output_path, project_bug_src_path, project_fix_src_path):
    logging.debug("parseTriggerCode(output_path=" + output_path + ",project_bug_src_path=" + project_bug_src_path + ",project_fix_src_path=" + project_fix_src_path + ")")
    trigger_code_file = open(output_path + 'triggerCode.java', 'r')
    trigger_code_tree = javalang.parse.parse(trigger_code_file.read())
    import_list = []
    for import_element in trigger_code_tree.imports:
        if hasattr(import_element, 'path'):
            import_list.append((import_element.path.split('.')[-1], PATH_SPLITER + import_element.path.replace('.', PATH_SPLITER) + '.java'))
    global_vars_dic = {}
    test_class_name = None
    methods_names = []
    for path, node in trigger_code_tree.filter(javalang.tree.Declaration):
        if type(node) == javalang.tree.ClassDeclaration:
            test_class_name = node.name
        if type(node) == javalang.tree.MethodDeclaration:
            methods_names.append(node.name)
    if test_class_name == None:
        raise Exception("Error:Can't get test class name in parseTriggerCode()")
    class_paths = []
    for (name, rel_class_path) in import_list:
        if test_class_name.find(name) != -1:
            class_paths.append(rel_class_path)
        else:
            for method_name in methods_names:
                if method_name.find(name) != -1:
                    class_paths.append(rel_class_path)
                    break
    class_path = utils.chooseBestMatchClassPath(class_paths)
    bug_class_path = project_bug_src_path + class_path
    fix_class_path = project_fix_src_path + class_path
    bug_class_methods = utils.getSrcCodeMethods(bug_class_path)
    fix_class_methods = utils.getSrcCodeMethods(fix_class_path)
    method_invocations = []
    for path, node in trigger_code_tree.filter(javalang.tree.MethodInvocation):
        if hasattr(node, 'qualifier') and node.qualifier != None and node.qualifier != '':
            method_invocations.append(node.member)
    bug_target_node = []
    fix_target_node = []
    for method_invocation in reversed(method_invocations):
        if method_invocation in bug_class_methods:
            bug_target_node = bug_target_node + bug_class_methods[method_invocation]
        if method_invocation in fix_class_methods:
            fix_target_node = fix_target_node + fix_class_methods[method_invocation]
    utils.getSrcCode(output_path + 'bugSrcCode.java', bug_class_path, bug_target_node)
    utils.getSrcCode(output_path + 'fixSrcCode.java', fix_class_path, fix_target_node)

def parseTriggerCode1(output_path, project_bug_src_path, project_fix_src_path):
    logging.debug("parseTriggerCode(output_path=" + output_path + ",project_bug_src_path=" + project_bug_src_path + ",project_fix_src_path=" + project_fix_src_path + ")")
    trigger_code_file = open(output_path + 'triggerCode.java', 'r')
    trigger_code_tree = javalang.parse.parse(trigger_code_file.read())
    import_dic = {}
    for import_element in trigger_code_tree.imports:
        if hasattr(import_element, 'path'):
            import_dic[import_element.path.split('.')[-1]] = import_element.path
    global_vars_dic = {}
    class_name = None
    # get global vars first
    for path, node in trigger_code_tree.filter(javalang.tree.Declaration):
        if type(node) == javalang.tree.ClassDeclaration:
            class_name = node.name
        if type(node) == javalang.tree.FieldDeclaration:
            global_vars_declarators = node.declarators
            for global_var_declarator in global_vars_declarators:
                global_vars_dic[global_var_declarator.name] = (node.type, global_var_declarator.dimensions, global_var_declarator.initializer)
    class_name = utils.getClassToTestName(class_name)

    # get src codes that need to be found
    src_to_find = []
    for path, node in trigger_code_tree.filter(javalang.tree.Declaration):
        if type(node) == javalang.tree.MethodDeclaration:
            vars_dic = {}
            to_find = []
            methods = []
            for statement_dec in node.body:
                if type(statement_dec) == javalang.tree.LocalVariableDeclaration:
                    local_vars_declarators = statement_dec.declarators
                    for local_var_declarator in local_vars_declarators:
                        vars_dic[local_var_declarator.name] = (statement_dec.type, local_var_declarator.initializer)
                elif type(statement_dec) == javalang.tree.StatementExpression:
                    statement_expression = statement_dec.expression
                    if hasattr(statement_expression, 'qualifier'):
                        to_find.append((statement_expression.qualifier, statement_expression.member, statement_expression.arguments))
                    elif hasattr(statement_expression, 'type') and statement_expression.type == '=':
                        expressional = statement_expression.expressionl
                        val = statement_expression.value
                        if expressional.member in vars_dic:
                            vars_dic[expressional.member] = (val.type, val)

            for (qualifier, member, arguments) in reversed(to_find):
                methods = methods + findMethodsRecursively(qualifier, member, arguments, import_dic)
            for (qualifier, member, arguments) in methods:
                if qualifier == '':
                    continue
                sub_type = None
                while qualifier in vars_dic or qualifier in global_vars_dic:
                    if qualifier in vars_dic:
                        (qualifier_type, initializer) = vars_dic[qualifier]
                        if hasattr(initializer, 'type'):
                            sub_type = initializer.type.name
                        qualifier = qualifier_type.name
                    elif qualifier in global_vars_dic:
                        (qualifier_type, initializer) = global_vars_dic[qualifier]
                        if hasattr(initializer, 'type'):
                            sub_type = initializer.type.name
                        qualifier = qualifier_type.name
                if qualifier in import_dic:
                    src_to_find.append((import_dic[qualifier], sub_type, member, arguments))
    with open(output_path + "bugSrcCode.java", 'w') as f:
        pass
    with open(output_path + "fixSrcCode.java", 'w') as f:
        pass
    for (import_path, sub_type, member, arguments) in src_to_find:
        if import_path.split('.')[-1] != class_name:
            continue
        getSrcCodeTested(output_path + 'bugSrcCode.java', project_bug_src_path + PATH_SPLITER + import_path.replace('.', PATH_SPLITER) + ".java", member, arguments)
        getSrcCodeTested(output_path + 'fixSrcCode.java', project_fix_src_path + PATH_SPLITER + import_path.replace('.', PATH_SPLITER) + ".java", member, arguments)
        