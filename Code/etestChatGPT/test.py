import javalang
import env
import utils
import os
import subprocess as sp
import getTestCode
import logging

# try to run test code in working dir of defects4j
def runTestCodeInWorkDir(trigger_code, check_out_path, test_class_path, logger):
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
    logger.debug('needed_deps:')
    for dep in needed_deps:
        logger.debug(dep)
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

    logger.debug('dep_packages:')
    logger.debug('\n'.join(dep_packages))
    logger.debug('dep_packages_filtered:')
    logger.debug('\n'.join(dep_packages_filtered))

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

if __name__ == '__main__':
    env.RETEST_COUNT = 3
    env.PATH_SPLITER = os.sep
    env.LLM = ('gpt-3.5-turbo', 'use-output')
    env.BUILD_FAILED = 'Build failed'
    env.TEST_FAILED = 'Test failed'
    env.TEST_SUCCESS = 'Test passed'
    env.PARSE_FAILED = 'Parse failed'
    env.INVOKE_LLM_FAILED = 'Invoke LLM failed'
    env.PREPARATION_FAILED = 'Preparation failed'
    env.UNEXPECTED_ERROR = 'Unexpected error'
    env.ASSERT_METHODS = ('assertTrue', 'assertFalse', 'fail', 'assertEquals', 'assertNotEquals', 'assertArrayEquals', 'assertNotNull', 'assertNull', 'assertSame', 'assertNotSame')
    env.ORG_TESTING_ASSERT_METHODS = ('assertThrows',)
    logging.basicConfig(filename='log.txt',filemode='w', level=logging.DEBUG)
    env.JAVA_INNER_CLASSES = utils.getJavaInnerClasses('./java_classes')
    # Cli-15: No Example               : 5 Build failed
    # Cli-15: One Example Other Project: 5 Build failed
    with open('./One Example Other Class/5_sample_test_response.txt', 'r') as f:
        content = ''.join(f.readlines())
    test_code = getTestCode.constructFullTestCode(content)
    deps = utils.getNeededDep(test_code)
    additional, res = runTestCodeInWorkDir(test_code, '/Code/Eval/work/Cli-15/fix', 'org/apache/commons/cli2/bug/BugCLI158Test.java', logging)
    if additional != None:
        logging.debug(f'additional:{"".join(additional)}')
    logging.debug(f'res:{res}')
    print(res)