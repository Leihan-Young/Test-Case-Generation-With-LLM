import sys
import getopt
import os
import env
import logging
import ConstructPrompt as cp

def main(argv):
    output_path = ""
    code_path = ""
    opts, args = getopt.getopt(argv[1:], "hDc:o:", ["help", "code_path=", "output_path="])
    debug_flag = False
    env.PATH_SPLITER = '/'
    # env.PROJECTS = ('Math', 'Mockito', 'Time')
    env.PROJECTS = ("Chart", "Cli","Closure","Codec","Collections",
                            "Compress","Csv","Gson","JacksonCore","JacksonDatabind"
                            ,"JacksonXml","Jsoup","JxPath","Lang","Math",
                            "Mockito","Time")
    env.SPECIAL_PROJECTS = ('Time',)
    for opt, arg in opts:
        if opt in ("-h", "--help"):
            print('Start.py -o <output_path> [-t <temp_path>]')
            print('temp_path will be the current path in default.')
            sys.exit()
        elif opt in ("-c", "--code_path"):
            code_path = arg
        elif opt in ("-o", "--output_path"):
            output_path = arg
        elif opt in ("-D", "--debug"):
            debug_flag = True
    if code_path == "":
        print("code_path is needed")
        sys.exit()
    if output_path == "":
        print("output_path is needed")
        sys.exit()
    if code_path[-1] == '/':
        code_path = code_path[0:-1]
    if output_path[-1] == '/':
        output_path = output_path[0:-1]
    if debug_flag:
        log_file = 'debug.log'
        logging.basicConfig(filename=log_file, filemode='w', level=logging.DEBUG)
    cp.constructPrompt(code_path, output_path, "")

if __name__ == "__main__":
    main(sys.argv)