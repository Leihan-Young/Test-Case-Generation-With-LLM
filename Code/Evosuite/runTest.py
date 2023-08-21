import os
import subprocess as sp
import env
import utils
import javalang
import logging
import constructPrompt as cp
import func_timeout
from func_timeout import func_set_timeout

# select the best result for multi sample
def rankForMultiSampleResult(res_in_bug, res_in_fix):
    # state:
    # 0: nothing
    # 1: parse failed
    # 2: build failed
    # 3: test bug passed, fix failed
    # 4: test both passed
    # 5: test both failed
    # 6: test found bug
    state = 0
    ind = 0
    for i in range(len(res_in_bug)):
        if state < 1 and res_in_bug[i] == env.PARSE_FAILED:
            state = 1
            ind = i
        if state < 2 and res_in_bug[i] == env.BUILD_FAILED:
            state = 2
            ind = i
        if state < 3 and res_in_bug[i] == env.TEST_SUCCESS and res_in_fix[i] == env.TEST_FAILED:
            state = 3
            ind = i
        if state < 4 and res_in_bug[i] == env.TEST_FAILED and res_in_fix[i] == env.TEST_FAILED:
            state = 4
            ind = i
        if state < 5 and res_in_bug[i] == env.TEST_SUCCESS and res_in_fix[i] == env.TEST_SUCCESS:
            state = 5
            ind = i
        if state < 6 and res_in_bug[i] == env.TEST_FAILED and res_in_fix[i] == env.TEST_SUCCESS:
            state = 6
            ind = i
            break
    return ind
    

# check out the related program
def checkOutProject(project, bug_number, bug_version, temp_path):
    logger = logging.getLogger(f'{project}-{bug_number}')
    if bug_version:
        version = str(bug_number) + 'b'
    else:
        version = str(bug_number) + 'f'
    checkout_path = temp_path
    if os.path.exists(checkout_path):
        return
    # clear_cmd = "rm -rf " + checkout_path
    # os.system(clear_cmd)
    os.makedirs(checkout_path)
    checkout_cmd = ['defects4j', 'checkout', '-p', project, '-v', version, '-w', checkout_path, '>/dev/null', '2>&1']
    logger.debug(f"checkout_cmd={' '.join(checkout_cmd)}")
    os.system(' '.join(checkout_cmd))

# clear the checked out project
def removeCheckedOutProject(project, bug_number, temp_path):
    clear_cmd = "rm -rf " + os.path.join(os.path.join(temp_path, project), f'{bug_number}b')
    os.system(clear_cmd)
    clear_cmd = "rm -rf " + os.path.join(os.path.join(temp_path, project), f'{bug_number}f')
    os.system(clear_cmd)

# try to run developer written test
def runDeveloperTest(trigger_code, check_out_path, logger):
    test_code_path = utils.getTestSrcCodePath(check_out_path)
    try:
        trigger_tree = javalang.parse.parse(''.join(trigger_code))
    except Exception:
        logger.error(f"Failed to parse trigger_code!")
        return env.PARSE_FAILED
    for line in trigger_code:
        if line.startswith('package'):
            package_path = line.split(' ')[-1].replace(';', '').replace('\n', '')
            test_code_path = os.path.join(test_code_path, package_path.replace('.', env.PATH_SPLITER))
            break
    class_name = ''
    for path, node in trigger_tree.filter(javalang.tree.ClassDeclaration):
        class_name = node.name
    logger.debug(f'class_name={class_name}')
    test_code_file_path = os.path.join(test_code_path, class_name + '.java')
    nodes = []
    class_dec = trigger_tree.types[0]
    for class_dec_body in class_dec.body:
        if isinstance(class_dec_body, javalang.tree.MethodDeclaration) and 'public' in class_dec_body.modifiers and 'abstract' not in class_dec_body.modifiers:
            override_flag = False
            for annotion in class_dec_body.annotations:
                if annotion.name == 'Override':
                    override_flag = True
                    break
            if not override_flag:
                nodes.append(class_dec_body)
    trigger_method_lines = utils.getJavaCodeMethodWithNodes(trigger_code, nodes)
    logger.debug(f'trigger_method_lines={"".join(trigger_method_lines)}')
    new_test_method_count = 0
    for node in nodes:
        for i in range(len(trigger_method_lines)):
            if trigger_method_lines[i].find(node.name) != -1:
                new_test_method_count = new_test_method_count + 1
                trigger_method_lines[i] = trigger_method_lines[i].replace(node.name, f'newTestMethod{new_test_method_count}')
    test_code = []
    with open(test_code_file_path, 'r', encoding=utils.checkEncoding(test_code_file_path), errors='ignore') as origin_file:
        test_code = origin_file.readlines()
    test_code_bak = test_code
    for i in reversed(range(len(test_code))):
        if test_code[i].startswith('}'):
            test_code = test_code[:i] + trigger_method_lines + test_code[i:]
            break
    with open(test_code_file_path, 'w') as over_write_file:
        over_write_file.writelines(test_code)

    fail_flag = False
    for i in range(new_test_method_count):
        test_cmd = ['defects4j', 'test', '-w', check_out_path, '-t', f'{package_path}.{class_name}::newTestMethod{i+1}']
        logger.debug(f'test_cmd={" ".join(test_cmd)}')
        test_results = sp.Popen(' '.join(test_cmd), shell=True, stdin=sp.PIPE, stdout=sp.PIPE, stderr=sp.PIPE)
        (test_stdout, test_stderr) = test_results.communicate()
        logger.debug(f'{package_path}.{class_name}::newTestMethod{i+1}:')
        logger.debug('test_results.stderr')
        for line in test_stderr.decode().split('\n'):
            logger.debug(f'{line}')
            if line.find('BUILD FAILED') != -1:
                return env.BUILD_FAILED
        logger.debug('test_results.stdout')
        for line in test_stdout.decode().split('\n'):
            logger.debug(f'{line}')
            if line.find('Failing tests:') != -1:
                if int(line.split(':')[-1]) > 0:
                    fail_flag = True
                break
        if fail_flag:
            break
        logger.debug('-----------------------------------------------')
    
    # restore
    with open(test_code_file_path, 'w') as restore_file:
        restore_file.writelines(test_code_bak)

    if fail_flag:
        return env.TEST_FAILED
    return env.TEST_SUCCESS

# try to run test code in working dir of defects4j
@func_set_timeout(600)
def runTestCodeInWorkDir(trigger_code, check_out_path, test_class_path, logger):
    logger.debug(f'check_out_path={check_out_path}')
    test_code_path = utils.getTestSrcCodePath(check_out_path)
    logger.debug(f'test_code_path={test_code_path}')
    trigger_code_lines = f"public class Test{'{'}\n{''.join(trigger_code)}\n{'}'}"
    trigger_code_lines = trigger_code_lines.split('\n')
    for i in range(len(trigger_code_lines)):
        trigger_code_lines[i] = trigger_code_lines[i] + '\n'
    try:
        trigger_tree = javalang.parse.parse(''.join(trigger_code_lines))
    except Exception:
        logger.error(f"Failed to parse trigger_code!")
        return None, env.PARSE_FAILED
    test_code_file_path = os.path.join(test_code_path, test_class_path)
    nodes = []
    class_dec = trigger_tree.types[0]
    for class_dec_body in class_dec.body:
        if isinstance(class_dec_body, javalang.tree.MethodDeclaration) and 'public' in class_dec_body.modifiers and 'abstract' not in class_dec_body.modifiers:
            override_flag = False
            for annotion in class_dec_body.annotations:
                if annotion.name == 'Override':
                    override_flag = True
                    break
            if not override_flag:
                nodes.append(class_dec_body)
    trigger_method_lines = utils.getJavaCodeMethodWithNodes(trigger_code_lines, nodes)
    logger.debug(f'trigger_method_lines={"".join(trigger_method_lines)}')
    new_test_method_count = 0
    for node in nodes:
        for i in range(len(trigger_method_lines)):
            if trigger_method_lines[i].find(node.name) != -1:
                new_test_method_count = new_test_method_count + 1
                trigger_method_lines[i] = trigger_method_lines[i].replace(node.name, f'newTestMethod{new_test_method_count}')
                break
    test_code = []
    with open(test_code_file_path, 'r', encoding=utils.checkEncoding(test_code_file_path), errors='ignore') as origin_file:
        test_code = origin_file.readlines()
    with open(test_code_file_path + '.bak', 'w') as backup_file:
        backup_file.writelines(test_code)
    # os.system(f"cp {test_code_file_path} {test_code_file_path + '.bak'}")

    needed_deps = utils.getNeededDep(trigger_code_lines)
    project_classes = utils.getProjectClasses(utils.getJavaSrcCodePath(check_out_path))
    
    index, deps = utils.getDepsAndStartIndex(test_code)
    
    dep_packages = ['org.junit.Assert']
    for needed_dep in needed_deps:
        if needed_dep in project_classes:
            dep_packages.append(project_classes[needed_dep])
        elif needed_dep in env.JAVA_INNER_CLASSES:
            dep_packages.append(env.JAVA_INNER_CLASSES[needed_dep])

    dep_packages_filtered = []
    for dep_package in dep_packages:
        if dep_package.split('.')[-1] not in deps:
            dep_packages_filtered.append(dep_package)

    test_code = test_code[:index] + utils.getImportLines(dep_packages_filtered) + test_code[index:]

    for i in reversed(range(len(test_code))):
        if test_code[i].startswith('}'):
            test_code = test_code[:i] + trigger_method_lines + test_code[i:]
            break
    with open(test_code_file_path, 'w') as over_write_file:
        over_write_file.writelines(test_code)

    fail_flag = False
    for i in range(new_test_method_count):
        test_cmd = ['defects4j', 'test', '-w', check_out_path, '-t', f'{test_class_path.replace(".java","").replace(env.PATH_SPLITER, ".")}::newTestMethod{i+1}']
        logger.debug(f'test_cmd={" ".join(test_cmd)}')
        test_results = sp.Popen(' '.join(test_cmd), shell=True, stdin=sp.PIPE, stdout=sp.PIPE, stderr=sp.PIPE)
        (test_stdout, test_stderr) = test_results.communicate()
        logger.debug(f'{test_class_path.replace(".java","")}::newTestMethod{i+1}:')
        logger.debug('test_results.stderr')
        messages = []
        for line in test_stderr.decode().split('\n'):
            logger.debug(f'{line}')
            messages.append(line)
            if line.find('BUILD FAILED') != -1:
                # restore
                test_code_bak = []
                with open(test_code_file_path + '.bak', 'r', encoding=utils.checkEncoding(test_code_file_path), errors='ignore') as backup_file:
                    test_code_bak = backup_file.readlines()
                with open(test_code_file_path, 'w') as origin_file:
                    origin_file.writelines(test_code_bak)
                return messages, env.BUILD_FAILED
        logger.debug('test_results.stdout')
        for line in test_stdout.decode().split('\n'):
            logger.debug(f'{line}')
            if line.find('Failing tests:') != -1:
                if int(line.split(':')[-1]) > 0:
                    fail_flag = True
                break
        if fail_flag:
            break
        logger.debug('-----------------------------------------------')
    
    # restore
    test_code_bak = []
    with open(test_code_file_path + '.bak', 'r', encoding=utils.checkEncoding(test_code_file_path), errors='ignore') as backup_file:
        test_code_bak = backup_file.readlines()
    with open(test_code_file_path, 'w') as origin_file:
        origin_file.writelines(test_code_bak)
    # os.system(f"mv -f {test_code_file_path + '.bak'} {test_code_file_path}")
    # with open(test_code_file_path, 'w') as restore_file:
    #     restore_file.writelines(test_code_bak)

    if fail_flag:
        return None, env.TEST_FAILED
    return None, env.TEST_SUCCESS


# 无用代码
'''
# add a new test method into the test class
def addNewTestCode(class_name, test_code, archive_path):
    for root, dirs, files in os.walk(archive_path):
        for file in files:
            if file.startswith(class_name) and file.find('scaffolding') == -1:
                test_code_lines = []
                with open(os.path.join(root, file), 'r', encoding=utils.checkEncoding(os.path.join(root, file)), errors='ignore') as origin_file:
                    test_code_lines = origin_file.readlines()
                test_code_lines = utils.writeMethodIntoClass(test_code, test_code_lines)
                with open(os.path.join(root, file), 'w') as output_file:
                    output_file.writelines(test_code_lines)
                return

# replace a test code with developer written test
def replaceWithDeveloperTest(trigger_code, archive_path):
    logging.debug(f'replaceWithDeveloperTest(trigger_code={"".join(trigger_code)}, archive_path={archive_path})')
    imports_ = []
    for line in trigger_code:
        if line.startswith('import'):
            imports_.append((line.split('.')[-1].replace(';', '').replace('\n', ''), line))
    tree = javalang.parse.parse(''.join(trigger_code))
    qualifier = set()
    class_name = ''
    for path, node in tree.filter(javalang.tree.ClassDeclaration):
        class_name = node.name
    for path, node in tree.filter(javalang.tree.MethodInvocation):
        qualifier.add(node.qualifier)
    for path, node in tree.filter(javalang.tree.MethodDeclaration):
        if node.throws != None:
            for throw in node.throws:
                qualifier.add(throw)
    imports = []
    for name, line in imports_:
        if name in qualifier:
            imports.append(line)

    append_string_imports = []
    for ip in imports:
        append_string_imports.append('"' + ip.split(' ')[-1].replace(';', '').replace('\n', '') + '",\n')

    logging.debug('\n'.join(imports))

    for root, dirs, files in os.walk(archive_path):
        for file in files:
            if file.find('_ESTest.java') != -1 and file.find('scaffolding') == -1 and class_name.find(file[:file.find('_ESTest.java')]) != -1 and len(class_name) - len(file[:file.find('_ESTest.java')]) <= 4:
                another_file = file[:file.find('.java')] + '_scaffolding.java'
                test_code_lines = []
                with open(os.path.join(root, file), 'r', encoding=utils.checkEncoding(os.path.join(root, file)), errors='ignore') as origin_file:
                    test_code_lines = origin_file.readlines()
                for i in range(len(test_code_lines)):
                    if test_code_lines[i].startswith('import'):
                        test_code_lines = test_code_lines[:i] + imports + test_code_lines[i:]
                        break
                trigger_methods = utils.getTriggerCodeMethods(trigger_code)
                test_code_lines = utils.getJavaClassCodeWithLines(test_code_lines, trigger_methods)
                with open(os.path.join(root, file), 'w') as output_file:
                    output_file.writelines(test_code_lines)
                
                scaffolding_code_lines = []
                with open(os.path.join(root, another_file), 'r', encoding=utils.checkEncoding(os.path.join(root, another_file)), errors='ignore') as origin_file:
                    scaffolding_code_lines = origin_file.readlines()
                for i in range(len(scaffolding_code_lines)):
                    if scaffolding_code_lines[i].find('classhandling.ClassStateSupport.initializeClasses') != -1:
                        scaffolding_code_lines = scaffolding_code_lines[:i+1] + append_string_imports + scaffolding_code_lines[i+1:]
                for i in range(len(scaffolding_code_lines)):
                    if scaffolding_code_lines[i].find('classhandling.ClassStateSupport.resetClasses') != -1:
                        scaffolding_code_lines = scaffolding_code_lines[:i+1] + append_string_imports + scaffolding_code_lines[i+1:]
                with open(os.path.join(root, another_file), 'w') as output_file:
                    output_file.writelines(scaffolding_code_lines)

                return

# try to run developer written test
def runDeveloperTestOld(trigger_code, project, bug_number, temp_path):
    gen_test_cmd = ['gen_tests.pl', '-g', 'evosuite', '-p', project, '-v', str(bug_number) + 'f', "-n", '1', '-o', os.path.join(temp_path, 'test'), '-b', '10']
    devnull = open(os.devnull, 'wb')
    logging.debug(' '.join(gen_test_cmd))
    sp.check_call(gen_test_cmd, stdout=devnull, stderr=sp.STDOUT)
    archive_path = os.path.join(os.path.join(os.path.join(os.path.join(temp_path, 'test'), project), 'evosuite'), '1')
    compress_name = project + '-' + str(bug_number) + 'f-evosuite.1.tar.bz2'
    uncompress_cmd = ['tar', '-jxvf', os.path.join(archive_path , compress_name), '-C', archive_path]
    logging.debug(' '.join(uncompress_cmd))
    sp.check_call(uncompress_cmd, stdout=devnull, stderr=sp.STDOUT)
    replaceWithDeveloperTest(trigger_code, archive_path)
    root_path_name = ''
    for name in os.listdir(archive_path):
        if os.path.isdir(os.path.join(archive_path, name)):
            root_path_name = name
            break
    compress_cmd = ['cd', archive_path, '&&', 'tar', '-jcvf', os.path.join(archive_path, compress_name), f'./{root_path_name}', '>/dev/null', '2>&1', '&&', 'cd', f'..{env.PATH_SPLITER}..{env.PATH_SPLITER}..{env.PATH_SPLITER}..']
    logging.debug(' '.join(compress_cmd))
    os.system(' '.join(compress_cmd))
    # sp.check_call(compress_cmd, shell=True, stdout=devnull, stderr=sp.STDOUT)
    os.system('rm -rf ' + os.path.join(archive_path, root_path_name))
    test_cmd = ['defects4j', 'test', '-w', os.path.join(temp_path, 'src'), '-s', os.path.join(archive_path, compress_name)]
    logging.debug(' '.join(test_cmd))
    test_results = sp.Popen(' '.join(test_cmd), shell=True, stdin=sp.PIPE, stdout=sp.PIPE, stderr=sp.PIPE)
    test_results.wait()
    failed_tests = []
    failed_count = 0
    logging.debug(f'test_results.stderr:')
    for test_result in test_results.stderr.readlines():
        line = test_result.decode()
        logging.debug(line)
        if line.find('BUILD FAILED') != -1:
            return env.BUILD_FAILED
    logging.debug(f'test_results.stdout:')
    for test_result in test_results.stdout.readlines():
        line = test_result.decode()
        logging.debug(line)
        if failed_count > 0:
            failed_tests.append(line.split('::')[-1].replace('\n', ''))
        elif line.find('Failing tests:') != -1:
            failed_count = int(line.split(':')[-1])
    method_names = utils.getTriggerCodeMethodNames(trigger_code)
    logging.debug('failed_tests:' + ''.join(failed_tests))
    logging.debug('method_names:' + ''.join(method_names))
    for method_name in method_names:
        if method_name in failed_tests:
            return env.TEST_FAILED
    return env.TEST_SUCCESS

# try to run the test
def runTestInSpecifiedVersion(class_name, test_code, project, bug_number, temp_path):
    # replace test code
    if os.path.exists(os.path.join(temp_path, 'test')):
        os.system('rm -rf ' + os.path.join(temp_path, 'test'))
    os.makedirs(os.path.join(temp_path, 'test'))
    gen_test_cmd = ['gen_test.pl', '-g', 'evosuite', '-p', project, '-v', str(bug_number) + 'f', "-n", '1', '-o', os.path.join(temp_path, 'test'), '-b', '10']
    devnull = open(os.devnull, 'wb')
    sp.check_call(gen_test_cmd, stdout=devnull, stderr=sp.STDOUT)
    archive_path = os.path.join(os.path.join(os.path.join(os.path.join(temp_path, 'test'), project), 'evosuite'), str(bug_number))
    compress_name = project + '-' + str(bug_number) + 'f-evosuite.1.tar.bz2'
    uncompress_cmd = ['tar', '-jxvf', os.path.join(archive_path , compress_name), '-C', archive_path]
    sp.check_call(uncompress_cmd, stdout=devnull, stderr=sp.STDOUT)
    addNewTestCode(class_name, test_code, archive_path)
    root_path_name = ''
    for name in os.listdir(archive_path):
        if os.path.isdir(os.path.join(archive_path, name)):
            root_path_name = name
            break
    compress_cmd = ['cd', archive_path, '&&', 'tar', '-jcvf', compress_name, f'./{root_path_name}']
    sp.check_call(compress_cmd, stdout=devnull, stderr=sp.STDOUT)
    os.system('rm -rf ' + os.path.join(archive_path, root_path_name))
    test_cmd = ['defects4j', 'test', '-w', os.path.join(temp_path, 'src'), '-s', os.path.join(archive_path, compress_name)]
    test_results = sp.Popen(' '.join(test_cmd), shell=True, stdin=sp.PIPE, stdout=sp.PIPE, stderr=sp.PIPE)
    test_results.wait()
    failed_tests = []
    failed_count = 0
    for test_result in test_results.stderr.readlines():
        line = test_result.decode()
        if line.find('BUILD FAILED') != -1:
            return env.BUILD_FAILED
    for test_result in test_results.stdout.readlines():
        line = test_result.decode()
        if failed_count > 0:
            failed_tests.append(line.split('::')[-1])
        elif line.find('Failing tests:') != -1:
            failed_count = int(line.split(':')[-1])
    method_names = utils.getMethodNamesFromMethodLines(test_code)
    for method_name in method_names:
        if method_name in failed_tests:
            return env.TEST_FAILED
    return env.TEST_SUCCESS
'''