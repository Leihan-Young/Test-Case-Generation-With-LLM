import os
import sys
import csv
import logging
import utils
import pandas as pd
import javalang

projects = ("Codec",)

# projects = ("Chart", "Cli","Closure","Codec","Collections",
#                 "Compress","Csv","Gson","JacksonCore","JacksonDatabind"
#                 ,"JacksonXml","Jsoup","JxPath","Lang","Math",
#                 "Mockito","Time")

PATH_SPLITER = '/'

def getFoCalMethodsAndTests(defects4j_path, output_path, temp_path):
    logging.basicConfig(level=logging.DEBUG)
    logging.debug('getFoCalMethodsAndTests(defects4j_path=' + defects4j_path + ",output_path=" + output_path + ',temp_path=' + temp_path + ')')
    projects_path = defects4j_path + PATH_SPLITER + "framework" + PATH_SPLITER + "projects"
    if not os.path.exists(projects_path):
        print("Error: defects4j projects don't exist.")
        sys.exit()
    if os.path.exists(output_path):
        os.system("rm -rf " + output_path)    
    os.makedirs(output_path)
    
    if os.path.exists(temp_path):
        os.system("rm -rf " + temp_path)
    os.makedirs(temp_path)
    for project in projects:
        project_path = projects_path + PATH_SPLITER + project
        triggers_path = project_path + PATH_SPLITER + "trigger_tests"
        active_bugs_file = open(project_path + PATH_SPLITER + "active-bugs.csv", 'r')
        reader = csv.DictReader(active_bugs_file)
        bug_ids = [row['bug.id'] for row in reader]
        for trigger_id in bug_ids:
            logging.debug('project=' + project + ',trigger_id=' + trigger_id)
            project_bug_src_path = temp_path + PATH_SPLITER + project + PATH_SPLITER + 'bug'
            project_fix_src_path = temp_path + PATH_SPLITER + project + PATH_SPLITER + 'fix'
            utils.checkOutSrcCode(defects4j_path, project, trigger_id, project_bug_src_path, True)
            utils.checkOutSrcCode(defects4j_path, project, trigger_id, project_fix_src_path, False)
            result = utils.parseTriggerTestsFile(triggers_path + PATH_SPLITER + trigger_id, project)
            fsc.getTriggerTestCode(output_path + PATH_SPLITER + project + PATH_SPLITER + trigger_id + PATH_SPLITER, project_bug_src_path, result)
            fsc.parseTriggerCode(output_path + PATH_SPLITER + project + PATH_SPLITER + trigger_id + PATH_SPLITER, utils.getSrcCodeFilePath(project_bug_src_path), utils.getSrcCodeFilePath(project_fix_src_path))


if __name__ == "__main__":

    fsc.parseTriggerCode('C:\\users\\leihan\\desktop\\', '', '')