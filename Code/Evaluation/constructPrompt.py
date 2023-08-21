import env

# extracting error messages in the java building progress
def extractingErrorMessages(build_output_messages):
    res = []
    stripped_error_messages = []
    error_flag = False
    for line in build_output_messages:
        if not line.lstrip().startswith('[javac]'):
            continue
        if line.find('error:') != -1:
            if len(stripped_error_messages) > 0:
                res.append(stripped_error_messages)
            stripped_error_messages = []
            stripped_error_messages.append('[javac]' + line[line.find('error:'):])
            error_flag = True
        elif (line.find('warning:') != -1 or line.find(' errors') != -1 or line.find('1 error') != -1) and len(stripped_error_messages) > 0:
            res.append(stripped_error_messages)
            stripped_error_messages = []
            error_flag = False
        elif error_flag:
            stripped_error_messages.append(line.lstrip())

    return res


# construct a new prompt for retry
def constructRetryPrompt(prompt, choice, additional_messages, res, logger):
    dict_ai = {}
    dict_ai['role'] = choice.message.role
    dict_ai['content'] = choice.message.content
    prompt.append(dict_ai)
    if res == env.PARSE_FAILED:
        new_content = "The test method you provided is not able to be parsed. It doesn't satisfy the grammar of Java. Please complete the markdown file again.\n"
    elif res == env.BUILD_FAILED:
        new_content = "The test method you provided is not able to be built. Please complete the markdown file again with the error information. The error occurred while building the test method is:\n"
        build_fail_error_message = extractingErrorMessages(additional_messages)
        logger.debug(f"build_fail_error_message:\n")
        for build_fail_error in build_fail_error_message:
            logger.debug('\n'.join(build_fail_error) + '\n')
            new_content += '\n'.join(build_fail_error) + '\n'
    elif res == env.TEST_FAILED or res == env.TEST_SUCCESS:
        new_content = 'The test method you provided fails to find the bug in the java program. Please complete the markdown file again.\n'
    new_content += '\n```\npublic void test'
    prompt.append({'role': 'user', 'content': new_content})
    return prompt

# construct a simple prompt for unit test generation
def constructSimpleUnitTestPrompt(focal_context, examples, llm='gpt-3.5-turbo', test_prefix=None):
    if llm == 'santacoder':
        if len(examples) == 0:
            example_text = ''
        elif len(examples) == 1:
            example_text = "//A test method that tests above java program.\n" + "".join(examples[0])
        elif len(examples) > 1:
            temp_text = ''
            for example in examples:
                temp_text += ''.join(example)
            example_text = "//Test methods that test above java program.\n" + temp_text

        prefix = ''

        # 减少token数量
        for line in focal_context:
            if line.startswith('import') or line.startswith('package'):
                continue
            prefix += line

        prefix += '\n' + example_text + '\npublic void test(){\n    //A test method that reveals a bug of the java program above\n    //This test method invokes java program above and uses assert statements to reveal the bug\n    '
        # prefix += '\n' + 'public class Test { \n    '
        suffix = '\n}\n'
        return {'prefix': prefix, 'suffix': suffix}
    
    elif llm == 'starcoder':
        prefix = ''.join(focal_context)
        prefix += '\n' + 'public class Test { \n    '
        suffix = '\n}\n'
        return {'prefix': prefix, 'suffix': suffix}
    
    elif llm == 'starcoder-evosuite':
        prefix = ''.join(focal_context)
        prefix += '\n' + test_prefix
        suffix = '\n}\n'
        return {'prefix': prefix, 'suffix': suffix}

    elif llm == 'gpt-3.5-turbo':
        content = "# Java Program:\nA java program with one or more bugs. The method that is implemented needs to be tested.\n```\n$FOCAL_CONTEXT$\n```\n# Test Code:\n$EXAMPLES$A test method that can reveal the bug of the java program.\n```\npublic void test"
        content = content.replace('$FOCAL_CONTEXT$', ''.join(focal_context))
        if len(examples) == 0:
            example_text = ''
        elif len(examples) == 1:
            example_text = f'An example test method that tests the same java project.\n```\n{"".join(examples[0])}\n```\n'
        elif len(examples) > 1:
            temp_text = ''
            for example in examples:
                temp_text += ''.join(example)
            example_text = f'Example test methods that test the same java project.\n```\n{temp_text}\n```\n'
        content = content.replace('$EXAMPLES$', example_text)
        req = [{'role': 'user', 'content': content}]
        return req
    else:
        return None

# construct a simple prompt for oracle test generation
def constructSimpleOracleTestPrompt(focal_context, test_prefix, llm='gpt-3.5-turbo'):
    if llm == 'santacoder':
        prefix = ''.join(focal_context)
        prefix += '\n' + 'public void test() throws Throwable{ \n    '
        prefix += test_prefix
        suffix = '\n}\n'
        return {'prefix': prefix, 'suffix': suffix}
    elif llm == 'starcoder':
        prefix = ''.join(focal_context)
        prefix += '\n' + '// A test method that reveals a bug of the code above'
        prefix += '\n' + 'public void test() throws Throwable { \n'
        prefix += test_prefix
        suffix = '\n}\n'
        return {'prefix': prefix, 'suffix': suffix}
    else:
        content = "# Java Program:\nA java program with one or more bugs. The method that is implemented needs to be tested.\n```\n$FOCAL_CONTEXT$\n```\n# Incomplete Test Code:\nA test method that is incomplete. To complete the test code, $ASSERT$ should be replaced with assert statements (assertTrue, assertFalse, fail, assertEquals, assertNotEquals, assertArrayEquals, assertNotNull, assertNull, assertSame, assertNotSame), and $EXCEPTION$ should be replaced with exception class. Other statements stay the same.\n```\n$TEST_PREFIX$\n```\n# Test Code:\nA test method that completes the incomplete test code. And it contains no $ASSERT$ or $EXCEPTION$\n```\npublic void test"
        content = content.replace('$FOCAL_CONTEXT$', ''.join(focal_context))
        content = content.replace('$TEST_PREFIX$', ''.join(test_prefix))
        req = [{'role': 'user', 'content': content}]
        return req
