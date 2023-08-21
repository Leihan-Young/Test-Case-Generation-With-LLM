import utils
import env
import logging
import javalang
import cchardet
import Start

if __name__ == '__main__':
    argv = ['Start.py', '-d', '/Code/defects4j', '-o', '/Code/output/', '-t', '/Code/tmp/', '-D']
    # Start.main(argv)

    env.ASSERT_METHODS = ('assertTrue', 'assertFalse', 'fail', 'assertEquals', 'assertNotEquals', 'assertArrayEquals', 'assertNotNull', 'assertNull', 'assertSame', 'assertNotSame')
    with open('C:\\Users\\leihan\\Desktop\\Code\\test.java', 'r') as f:
        lines = f.readlines()

    new_lines = utils.regularTestMethod(lines)
    print(1)

    # with open('a.txt', 'w') as output:
    #     with open('test.java', 'r') as input:
    #         output.write(input.read())
    # env.PATH_SPLITER = '\\'
    # env.PROJECTS = ('Chart',)
    # env.PROJECTS = ("Chart", "Cli","Closure","Codec","Collections",
    #                         "Compress","Csv","Gson","JacksonCore","JacksonDatabind"
    #                         ,"JacksonXml","Jsoup","JxPath","Lang","Math",
    #                         "Mockito","Time")
    # env.SPECIAL_PROJECTS = ('Time',)
    # utils.parseTriggerTestsFile('C:\\Users\\leihan\\Desktop\\1.txt', 'Chart')
    # logging.basicConfig(filename='test.log', level=logging.INFO)
    # logging.info('test')
    # with open("C:\\Users\\leihan\\Desktop\\ReverseAbstractInterpreter.java", 'r') as f:
    #     tree = javalang.parse.parse(f.read())
    #     for path, node in tree.filter(javalang.tree.Declaration):
    #         pass

    # utils.getJavaCodeMethod('C:\\Users\\leihan\\Desktop\\NonStandardUnquotedNamesTest.java', 'testUnquotedIssue510', 49)
    # y = utils.getModifiedClasses('C:\\Users\\leihan\\Desktop\\Code\\defects4j', 'Cli', '15')
    # x = utils.getSrcCodeFiles('C:\\Users\\leihan\\Desktop\\Code\\tmp\\Codec\\src\\main\\java')
    # utils.getExtendsFiles( ".\\bug_additional\\", 'C:\\Users\\leihan\\Desktop\\Code\\tmp\\Codec\\src\\main\\java\\org\\apache\\commons\\cli2\\Argument.java', x)
    # print(1)
    # with open('C:\\Users\\leihan\\Desktop\\Code\\Eval\\src\\src\\test\\java\\org\\apache\\commons\\math3\\fraction\\BigFractionTest.java', 'r', encoding=utils.check_encoding('C:\\Users\\leihan\\Desktop\\Code\\Eval\\src\\src\\test\\java\\org\\apache\\commons\\math3\\fraction\\BigFractionTest.java'), errors='ignore') as f:
    #     tree = javalang.parse.parse(f.read())
    #     nodes = []
    #     for path, node in tree.filter(javalang.tree.MethodDeclaration):
    #         if 'private' in node.modifiers:
    #             nodes.append(node)
    
    # utils.writeJavaClassWithLines('C:\\Users\\leihan\\Desktop\\test.txt', 'C:\\Users\\leihan\\Desktop\\SparseRealVectorTest.java', ['txt'])
    # with open('C:\\Users\\leihan\\Desktop\\1.txt', 'rb') as f:
    #     data = f.read()
    #     encoding = cchardet.detect(data)
    #     a = encoding.get('encoding')
    #     pass