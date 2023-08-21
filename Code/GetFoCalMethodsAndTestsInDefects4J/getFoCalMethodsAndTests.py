import os
import sys
import csv
import logging
import utils
import env
from tqdm import *



def getFoCalMethodsAndTests(defects4j_path, output_path, temp_path):
    # prepare
    logging.debug('getFoCalMethodsAndTests(defects4j_path=' + defects4j_path + ",output_path=" + output_path + ',temp_path=' + temp_path + ')')
    projects_path = defects4j_path + env.PATH_SPLITER + "framework" + env.PATH_SPLITER + "projects"

    if not os.path.exists(projects_path):
        print("Error: defects4j projects don't exist.")
        sys.exit()
    if os.path.exists(output_path):
        os.system("rm -rf " + output_path)    
    os.makedirs(output_path)
    
    if os.path.exists(temp_path):
        os.system("rm -rf " + temp_path)
    os.makedirs(temp_path)

    # start
    for project in tqdm(env.PROJECTS):
        project_path = projects_path + env.PATH_SPLITER + project
        triggers_path = project_path + env.PATH_SPLITER + "trigger_tests"
        active_bugs_file = open(project_path + env.PATH_SPLITER + "active-bugs.csv", 'r')
        reader = csv.DictReader(active_bugs_file)
        bug_ids = [row['bug.id'] for row in reader]
        for bug_id in tqdm(bug_ids):
            logging.debug('project=' + project + ',bug_id=' + bug_id)
            project_bug_src_path = temp_path + env.PATH_SPLITER + project + env.PATH_SPLITER + 'bug'
            project_fix_src_path = temp_path + env.PATH_SPLITER + project + env.PATH_SPLITER + 'fix'
            utils.checkOutSrcCode(defects4j_path, project, bug_id + 'b', project_bug_src_path)
            utils.checkOutSrcCode(defects4j_path, project, bug_id + 'f', project_fix_src_path)
            parse_result = utils.parseTriggerTestsFile(triggers_path + env.PATH_SPLITER + bug_id, project)
            loaded_classes = utils.getLoadedClasses(defects4j_path, project, bug_id) 
            _cur_output_path = output_path + env.PATH_SPLITER + project + env.PATH_SPLITER + bug_id + env.PATH_SPLITER
            count = 1
            for (test_code_path, parse_stack) in parse_result:
                logging.debug('test_code_path=' + test_code_path + ',parse_stack=' + str(parse_stack))
                cur_output_path = _cur_output_path + str(count) + env.PATH_SPLITER
                if len(parse_stack) == 0: 
                    continue
                lines_to_write = []
                trigger_test_src_code_path = None
                for (trigger_test_file_path, test_method_name, target_line_number) in parse_stack:
                    if trigger_test_src_code_path == None:
                        trigger_test_src_code_path = utils.getTestSrcCodePath(project_bug_src_path) + trigger_test_file_path
                    lines_to_write = lines_to_write + utils.getJavaCodeMethod(trigger_test_src_code_path, test_method_name, target_line_number)
                if trigger_test_src_code_path == None:
                    continue
                utils.writeJavaClassWithLines(cur_output_path + "triggerCode.java", trigger_test_src_code_path, utils.regularTestMethod(lines_to_write))
                utils.getSrcPatch(cur_output_path + "src.patch", defects4j_path, project, bug_id)
                related_class = None
                length = 0
                # for loaded_class in loaded_classes:
                #     if test_code_path.find(loaded_class.split('.')[-1].replace('\n','')) != -1 and len(loaded_class) > length:
                #         related_class = loaded_class
                #         length = len(loaded_class)
                # if related_class == None:
                #     continue
                # project_bug_related_src_code_path = utils.getJavaSrcCodePath(project_bug_src_path) + env.PATH_SPLITER + related_class.replace('.', env.PATH_SPLITER) + '.java'
                # project_fix_related_src_code_path = utils.getJavaSrcCodePath(project_fix_src_path) + env.PATH_SPLITER + related_class.replace('.', env.PATH_SPLITER) + '.java'
                # bug_methods_of_related_class = utils.getMethodsOfJavaClass(project_bug_related_src_code_path)
                # fix_methods_of_related_class = utils.getMethodsOfJavaClass(project_fix_related_src_code_path)
                # focal_method_names = utils.getFocalMethodNamesOfTriggerCode(cur_output_path + "triggerCode.java")
                # bug_methods_lines = []
                # fix_methods_lines = []
                # for focal_method_name in focal_method_names:
                #     if focal_method_name in bug_methods_of_related_class:
                #         bug_methods_lines = bug_methods_lines + utils.getJavaCodeMethodWithNodes(project_bug_related_src_code_path, bug_methods_of_related_class[focal_method_name])
                #     if focal_method_name in fix_methods_of_related_class:
                #         fix_methods_lines = fix_methods_lines + utils.getJavaCodeMethodWithNodes(project_fix_related_src_code_path, fix_methods_of_related_class[focal_method_name])
                # utils.writeJavaClassWithLines(cur_output_path + "bugSrcCode.java", project_bug_related_src_code_path, bug_methods_lines)
                # utils.writeJavaClassWithLines(cur_output_path + "fixSrcCode.java", project_fix_related_src_code_path, fix_methods_lines)
                # utils.copyFile(cur_output_path + "bugSrcCode.java", project_bug_related_src_code_path)
                # utils.getExtendsFiles(cur_output_path + "bug_additional" + env.PATH_SPLITER, project_bug_related_src_code_path, src_code_files)
                # utils.copyFile(cur_output_path + "fixSrcCode.java", project_fix_related_src_code_path)
                # utils.getExtendsFiles(cur_output_path + "fix_additional" + env.PATH_SPLITER, project_fix_related_src_code_path, src_code_files)
                # count = count + 1

                modified_classes = utils.getModifiedClasses(defects4j_path, project, bug_id)
                src_code_files = utils.getSrcCodeFiles(utils.getJavaSrcCodePath(project_bug_src_path))
                for modified_class in modified_classes:
                    if modified_class.split('.')[-1].replace('\n', '') not in src_code_files:
                        continue
                    file_name = modified_class.split('.')[-1].replace('\n', '') + '.java'
                    project_bug_related_src_code_path = utils.getJavaSrcCodePath(project_bug_src_path) + env.PATH_SPLITER + modified_class.replace('.', env.PATH_SPLITER) + '.java'
                    utils.copyFile(cur_output_path + "bugSrcCode" + env.PATH_SPLITER + file_name, project_bug_related_src_code_path)
                    utils.getExtendsFiles(cur_output_path + "bug_additional" + env.PATH_SPLITER, project_bug_related_src_code_path, src_code_files)
                    