import sys
import getopt
import os
import env
import logging
import getFoCalMethodsAndTests as g

def main(argv):
    defects4j_path = ""
    output_path = ""
    temp_path = os.path.dirname(os.path.abspath(__file__))
    opts, args = getopt.getopt(argv[1:], "hDd:o:t:", ["help", "defects4j_path=","output_path=","temp_path="])
    debug_flag = False
    env.PATH_SPLITER = '/'
    env.PROJECTS = ('Chart', )
    # env.PROJECTS = ("Chart", "Cli","Closure","Codec","Collections",
    #                         "Compress","Csv","Gson","JacksonCore","JacksonDatabind"
    #                         ,"JacksonXml","Jsoup","JxPath","Lang","Math",
    #                         "Mockito","Time")
    env.SPECIAL_PROJECTS = ('Time',)
    env.ASSERT_METHODS = ('assertTrue', 'assertFalse', 'fail', 'assertEquals', 'assertNotEquals', 'assertArrayEquals', 'assertNotNull', 'assertNull', 'assertSame', 'assertNotSame')
    for opt, arg in opts:
        if opt in ("-h", "--help"):
            print('Start.py -d <defects4j_path> -o <output_path> [-t <temp_path>]')
            print('temp_path will be the current path in default.')
            sys.exit()
        elif opt in ("-d", "--defects4j_path"):
            defects4j_path = arg
        elif opt in ("-o", "--output_path"):
            output_path = arg
        elif opt in ("-t", "--temp_path"):
            temp_path = arg
        elif opt in ("-D", "--debug"):
            debug_flag = True
    if defects4j_path == "":
        print("defects4j_path is needed")
        sys.exit()
    if output_path == "":
        print("output_path is needed")
    if defects4j_path[-1] == '/':
        defects4j_path = defects4j_path[0:-1]
    if output_path[-1] == '/':
        output_path = output_path[0:-1]
    if temp_path[-1] == '/':
        temp_path = temp_path[0:-1]
    if debug_flag:
        log_file = 'debug.log'
        logging.basicConfig(filename=log_file, filemode='w', level=logging.DEBUG)
    g.getFoCalMethodsAndTests(defects4j_path, output_path, temp_path)
    os.system("rm -rf " + temp_path)

if __name__ == "__main__":
    main(sys.argv)