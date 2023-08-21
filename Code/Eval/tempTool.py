import os
import utils


log_path = 'C:\\Users\\leihan\\Desktop\\Code\\Eval\\run_result_retry3\\logs'

for file in os.listdir(log_path):
    if file == 'root-debug.log':
        continue
    with open(os.path.join(log_path, file), 'r', encoding=utils.checkEncoding(os.path.join(log_path, file)), errors='ignore') as f:
        line = f.readlines()[-1]
    if line.find('unit_res_in_bug') == -1:
        continue
    unit_res_in_bug = line.split(',')[0].split('=')[-1]
    unit_res_in_fix = line.split(',')[1].split('=')[-1]
    if unit_res_in_bug == 'Test failed' and unit_res_in_fix == 'Test passed':
        print(file)