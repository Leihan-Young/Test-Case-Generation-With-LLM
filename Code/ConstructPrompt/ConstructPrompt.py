import os
import utils
import javalang
import openai

def generatePrompt(code_path, output_path, rel_path):
    test_method_simple = []
    trigger_code_file_path = os.path.join(code_path, 'triggerCode.java')
    trigger_code_file = open(trigger_code_file_path, 'r', encoding=utils.check_encoding(trigger_code_file_path), errors='ignore')
    trigger_code_tree = javalang.parse.parse(trigger_code_file.read())
    trigger_code_file.seek(0)
    trigger_code_lines = trigger_code_file.readlines()
    for path, node in trigger_code_tree.filter(javalang.tree.MethodDeclaration):
        start = node.position.line - 1
        #不加入注释，所以start不继续向前找
        test_method_simple = test_method_simple + utils.getSimpleTriggerCode(trigger_code_lines, start, node.name)
    bug_src_code_methods = []
    bug_src_code_file_path = os.path.join(code_path, 'bugSrcCode.java')
    bug_src_code_file = open(bug_src_code_file_path, 'r', encoding=utils.check_encoding(bug_src_code_file_path), errors='ignore')
    bug_src_code_tree = javalang.parse.parse(bug_src_code_file.read())
    bug_src_code_file.seek(0)
    bug_src_code_lines = bug_src_code_file.readlines()
    for path, node in bug_src_code_tree.filter(javalang.tree.MethodDeclaration and javalang.tree.ConstructorDeclaration):
        start = node.position.line - 1
        bug_src_code_methods = bug_src_code_methods + utils.getSrcCodeMethod(bug_src_code_lines, start)

    # 删去assert中的空
    prompt1 = utils.generatePromptOne(test_method_simple, bug_src_code_methods)
    # 删去assert语句
    prompt2 = utils.generatePromptTwo(test_method_simple, bug_src_code_methods)
    # 给测试代码模板，要求生成测试代码能揭露bug
    prompt3 = utils.generatePromptThree(test_method_simple, bug_src_code_methods)
    # 只给源码，要求生成测试代码能揭露bug
    prompt4 = utils.generatePromptFour(test_method_simple, bug_src_code_methods)

    # 调用 chatgpt api
    openai.api_key = "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"
    # response1 = openai.ChatCompletion.create(model='gpt-3.5-turbo', messages=prompt1)
    # response2 = openai.ChatCompletion.create(model='gpt-3.5-turbo', messages=prompt2)
    response4 = openai.ChatCompletion.create(model='gpt-3.5-turbo', messages=prompt4)
    output_folder = os.path.join(output_path, rel_path)
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)
    # with open(os.path.join(output_folder, 'prompt1.txt'), 'w') as f:
    #     f.write(prompt1[0]['content'])
    # with open(os.path.join(output_folder, 'response_prompt1.java'), 'w') as f:
    #     f.write(response1.choices[0].message.content)
    # with open(os.path.join(output_folder, 'prompt2.txt'), 'w') as f:
    #     f.write(prompt2[0]['content'])
    # with open(os.path.join(output_folder, 'response_prompt2.java'), 'w') as f:
    #     f.write(response2.choices[0].message.content)
    with open(os.path.join(output_folder, 'prompt4.txt'), 'w') as f:
        f.write(prompt4[0]['content'])
    with open(os.path.join(output_folder, 'response_prompt4.java'), 'w') as f:
        f.write(response4.choices[0].message.content)
    
    

def constructPrompt(code_path, output_path, rel_path):
    flag = False
    for file in os.listdir(code_path):
        file_path = os.path.join(code_path, file)
        if os.path.isdir(file_path):
            constructPrompt(file_path, output_path, os.path.join(rel_path, file))
        else:
            flag = True
            break
    if flag:
        generatePrompt(code_path, output_path, rel_path)