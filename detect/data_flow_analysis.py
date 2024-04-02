import django
import sys
import os
import subprocess
import xml.etree.ElementTree as ET

sys.path.append('../')

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'mwep.settings')

django.setup()


def execute_cmd(command, working_directory):
    try:
        result = subprocess.run(command, cwd=working_directory, check=True, shell=True, stdout=subprocess.PIPE,
                                stderr=subprocess.PIPE)
        return result.stdout.decode('utf-8'), None
    except subprocess.CalledProcessError as e:
        return None, e.stderr.decode('utf-8')


def get_analysis_xml(apkName):
    working_directory = "/home/wuyang/FlowDroid"
    cmd = "java -jar soot-infoflow-cmd/target/soot-infoflow-cmd-jar-with-dependencies.jar -a DroidBench/" + apkName + ".apk -p android-platforms/ -s soot-infoflow-android/SourcesAndSinks.txt -o sootOutput/" + apkName + "_output.xml"
    stdout, stderr = execute_cmd(cmd, working_directory)
    if stderr is None:
        print("Flowdroid analysis succeed.\n")
    else:
        print("Flowdroid failed.\n")


def change_api_format(api):
    api = api.replace('<', '').replace('>', '')
    api_class = api.split(':')[0]
    api_return = api.split(' ')[1]
    api_name = api.split(' ')[2].split('(')[0]

    api_database = api_class.replace('.', '/') + ';->' + api_name

    return api_class, api_return, api_name, api_database


def get_parse_xml(apkName):
    file_path = "/home/wuyang/FlowDroid/sootOutput/" + apkName + "_output.xml"
    tree = ET.parse(file_path)
    root = tree.getroot()

    ret_dict = {}

    for result in root.findall('.//Result'):
        sink = result.find('Sink')
        sink_definition = sink.get('MethodSourceSinkDefinition')
        sink_api = change_api_format(sink_definition)[3]

        source_list = []
        sources = result.find('Sources')
        for source in sources.findall('Source'):
            source_definition = source.get('MethodSourceSinkDefinition')
            source_list.append(change_api_format(source_definition)[3])

        ret_dict[sink_api] = source_list

    return ret_dict

# ***** Test *****
# get_analysis_xml('9e')
# dict=get_parse_xml('9e')
# for key, value in dict.items():
#     print(f"Key: {key}, Value: {value}")
