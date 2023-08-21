import javalang
import utils
import re
import env

# 将assertThrows结构转为try catch
def assertThrowsToTryCatch(line, ind):
    start = ind
    count_for_brackets = 0
    end_flag = False
    while ind < len(line) and not end_flag:
        for i in range(len(line[ind])):
            if line[ind][i] == ';' and count_for_brackets == 0:
                end_flag = True
                break
            elif line[ind][i] == '(':
                count_for_brackets += 1
            elif line[ind][i] == ')':
                count_for_brackets -= 1
        ind = ind + 1
    end = ind
    assert_throws_statement = ''.join(line[start:end]).replace('\n', '')
    args_of_assert_throws = []
    count_for_brackets = 0
    ind = assert_throws_statement.find('assertThrows(') + len('assertThrows(')
    tmp = ind
    for i in range(ind, len(assert_throws_statement)):
        if assert_throws_statement[i] == ',':
            if count_for_brackets > 0:
                continue
            else:
                args_of_assert_throws.append(assert_throws_statement[tmp:i])
                tmp = i + 1
        elif assert_throws_statement[i] == '(' or assert_throws_statement[i] == '{':
            count_for_brackets += 1
        elif assert_throws_statement[i] == ')' or assert_throws_statement[i] == '}':
            count_for_brackets -= 1
            if count_for_brackets < 0:
                args_of_assert_throws.append(assert_throws_statement[tmp:i])
                break
    new_statement = 'try {$STATEMENTS$ Assert.fail(); } catch( $CATCH_EXCEPTION$ e ) {}\n'
    exception = args_of_assert_throws[0].split('.')[0]
    statements = args_of_assert_throws[1][args_of_assert_throws[1].find('->') + 2:]
    if statements.find('{') == -1:
        statements = statements + ';'
    else:
        statements = statements[statements.find('{')+1:statements.find('}')]
    new_statement = new_statement.replace('$STATEMENTS$', statements)
    new_statement = new_statement.replace('$CATCH_EXCEPTION$', exception)
    return line[:start] + [new_statement] + line[end:]

# 给assertEquals等方法加Assert.
def addingAssertLibInStatement(line) -> str:
    for assert_method in env.ASSERT_METHODS:
        ind = line.find(assert_method + '(')
        if ind != -1:
            if ind == 0:
                return 'Assert.' + line
            elif line[ind-1] != '.':
                return line[:ind] + 'Assert.' + line[ind:]
            else:
                return line
    return line

# construct full test code from response
def constructFullTestCode(choice):
    content = "public void test" + choice.message.content
    # content = "public void test" + response
    content_lines = content.split('\n')
    i = 0
    while i < len(content_lines):
        content_lines[i] = content_lines[i] + '\n'
        content_lines[i] = addingAssertLibInStatement(content_lines[i])
        if content_lines[i].find('assertThrows(') != -1:
            content_lines = assertThrowsToTryCatch(content_lines, i)
        i = i + 1
    for i in range(len(content_lines)):
        if content_lines[i].find('```') != -1:
            return content_lines[:i]
    return content_lines

def constructFullTestCodeFromLines(lines):
    content = "public void test" + ''.join(lines)
    content_lines = content.split('\n')
    i = 0
    while i < len(content_lines):
        content_lines[i] = content_lines[i] + '\n'
        content_lines[i] = addingAssertLibInStatement(content_lines[i])
        if content_lines[i].find('assertThrows(') != -1:
            content_lines = assertThrowsToTryCatch(content_lines, i)
        i = i + 1
    for i in range(len(content_lines)):
        if content_lines[i].find('```') != -1:
            return content_lines[:i]
    return content_lines


# 无用代码
'''
# extract unit test methods from response
def extractUnitTest(response):
    content = response.choices[0].message.content.split('\n')
    for i in range(len(content)):
        if content[i].find('import') != -1 or content[i].find('class') != -1:
            content = content[i:]
            break
    for i in reversed(range(len(content))):
        if content[i].find('}') != -1:
            content = content[:i+1]
    test_tree = javalang.parse.parse('\n'.join(content))
    nodes = []
    for path, node in test_tree.filter(javalang.tree.MethodDeclaration):
        nodes.append(node)
    test_code_lines = utils.getJavaCodeMethodWithNodes(content, nodes)
    return test_code_lines

# extract oracle test methods from response
def extractOracleTest(response):
    content = response.choices[0].message.content.split('\n')
    for i in range(len(content)):
        if content[i].find('testCode') != -1:
            return ['@Test\n'] + content[i:]
    return content

def constructOracleUnitTest(test_prefix, oracle):
    pass
'''