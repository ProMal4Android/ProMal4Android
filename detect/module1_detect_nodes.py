"""
This is the first module to analyse an apk.
We will:
1. Decompile the malware and analyze its code in combination with ICFG to produce a feature file;
2. Identify malicious behavior node corresponding to the knowledge graph based on its feature file.
Finally, we can obtain what malicious behaviors the malware contains.

2023-10-19
"""

import re
import time

from operator import itemgetter, eq
from urllib.parse import urlparse

import django
import sys
import os
from exper.program_analysis import backwardDataflow, forwardDataflow, forwardDataflow_plus, key_sort_group, \
    backwardDataflow_plus
from tools.utils import analyse, get_data, generate_cg, gml_txt, do_feature_file, api_sdk_sim, list_print, \
    create_cfg_specific

sys.path.append('../')

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'mwep.settings')

django.setup()

from common import models

from androguard.misc import AnalyzeAPK
import xml.etree.ElementTree as ET


def main_module1(apk_name, f, p1, p2, p3, p4, p5):
    global kg_apis, kg_permissions, kg_features, apis_from_test, kg
    kg_apis = p1
    kg_permissions = p2
    kg_features = p3
    apis_from_test = p4
    kg = p5
    if os.path.exists('../detect/output_features/' + apk_name + '_features.txt'):
        pass
    else:
        if os.path.exists('../detect/outputCG/' + apk_name + '.txt'):
            pass
        else:
            print('Generate call graph...')
            gml, apk_name = generate_cg(f)  # turn apk into cg
            print('gml:',gml)
            gml_txt(gml, apk_name)  # turn cg into txt
        genrate_feature_file(apk_name, f)  # generate features file
    # if os.path.exists('../detect/outputCG/' + apk_name + '.txt'):
    #     pass
    # else:
    #     print('Generate call graph...')
    #     gml, apk_name = generate_cg(f)  # turn apk into cg
    #     print('gml:',gml)
    #     gml_txt(gml, apk_name)  # turn cg into txt
    genrate_feature_file(apk_name, f)  # generate features file
    match_nodes = indentify_behaviors(apk_name, kg_apis)
    return match_nodes


def alter_api(label_api):
    # print('alter api:', label_api)
    index = label_api.find('>')
    api_name = label_api[index + 1:]
    # print('api name:', api_name)
    try:
        qs = models.augmentAPiIn.objects.get(apiName__contains=api_name)
        return qs.apiName
    except:
        return ''


def get_id_from_label(label,data):
    # data = get_data(os.path.join('../detect/outputCG/', 'BaseBridge0' + '.txt'))
    # data = data.replace("\n", '')
    if re.findall('node\s\[\s+id\s(\d+)\s+label\s"'+label.replace('$','\$'), data):
        ans=re.findall('node\s\[\s+id\s(\d+)\s+label\s"'+label.replace('$','\$'), data)[0]
        return ans
    else:
        print('a?',label)

# get_id_from_label('Lcom/android/battery/a/su;->e')

def special_apis(d,data):
    start=time.time()
    ret={}
    api_ids=[1,2,619,620]
    raw_string=r""
    search_patterns=[]
    for id in api_ids:
        try:
            api_name=models.augmentAPiIn.objects.get(apiID=id).apiName
            search_patterns.append(raw_string+api_name.replace('$','\$'))
        except Exception:
            continue
    records=[]
    for dex in d:
        for current_class in dex.get_classes():
            for method in current_class.get_methods():
                code = method.get_code()
                if code:
                    for instruction in code.get_bc().get_instructions():
                        for pattern in search_patterns:
                            if re.search(pattern, instruction.get_output()):
                                method_label=current_class.get_name()+'->'+method.get_name()
                                method_id=get_id_from_label(method_label,data)
                                if method_id in records:
                                    ret[method_id]['apis'].append(pattern.replace('\$','$'))
                                else:
                                    ret[method_id]={'apis':[pattern.replace('\$','$')],'method_label':method_label}
                                    records.append(method_id)
                                # ret.append({'api':pattern,'method_label':method_label,'id':})
                                # print(f"Found pattern '{pattern}' in method {method.get_name()}")

    end=time.time()
    print('Time of extracting special apis：', str(end - start))
    return ret


def genrate_feature_file(apk_name, apk_file):
    """
    Androguard Version: 3.3.5
        :function add re pattern, save time
        :param a .txt file generated from .gml
        :return feature_txt: a .txt file consists of key apis & permissions of an app
        """
    start_time = time.time()
    print('Generate feature file...')
    print('apk_file:',apk_file)
    apk_path = "../webapp/uploadFiles/"+apk_file
    a, d, dx = AnalyzeAPK(apk_path)
    permissions = a.get_permissions()

    intent_filter=[]
    try:
        manifest_xml = a.get_android_manifest_axml().get_xml()
        root = ET.fromstring(manifest_xml)
        manifest_string = ET.tostring(root, encoding="utf-8").decode("utf-8")
        ans = re.findall('"(android\.intent\..*?)"', manifest_string)
        if ans:
            for one in ans:
                if one not in intent_filter:
                    intent_filter.append(one)
    except Exception as e:
        print(e)

    feature_filename = os.path.join('../detect/output_features/', apk_name + '_features.txt')
    feature_file = open(feature_filename, 'w', encoding='utf-8')
    # **********Write Information Belows*************
    # 1. write permissions
    for per in permissions:
        # if per in kg_permissions:
        feature_file.write(per + '\n')
    feature_file.write('\n')

    # 2. write permissions
    for int in intent_filter:
        feature_file.write(int + '\n')
    feature_file.write('\n')

    # 3. write apis through cg
    data = get_data(os.path.join('../detect/outputCG/', apk_name + '.txt'))
    node_list, edge_list = analyse(data)
    data = data.replace("\n", '')

    osBuild = special_apis(d, data)
    for key,value in osBuild.items():
        feature_file.write(str(key)+' - '+value['method_label'] + ':\n')
        build_apis=value['apis']
        for each_api in build_apis:
            feature_file.write(each_api+'\n')
        feature_file.write('\n')

    has_record = []
    for index, node in enumerate(node_list):
        # pattern1 = re.compile('label\s.*?;->.*?\[access_flag.*?]', re.S)
        # ans1 = pattern1.findall(node)
        # if ans1:
        #     pass
        # else:
        #     # find possible api
        #     backward = backwardDataflow(index, apk_name)[0]
        #     if not backward:
        # possible apis
        api = re.findall('label\s"(L.*?;->.*?)\(', node)
        if api:
            api = api[0]
            if api[0] == "L":
                api = api[1:]
            api = api.split('/')
            api = '/'.join(api)
            if api in str(apis_from_test):  # stand api that in the kg
                # whether this api is a sensitive api
                # print('api:', api)
                try:
                    # include sensitive api
                    qy = models.sensitiveApi.objects.get(api=api)
                    forward, idl = forwardDataflow_plus(index, data, node_list)
                    for father in idl:
                        if father not in has_record:
                            api_list = []
                            # father_function = re.findall('label\s"L.*?;->(.*?)\(', node_list[father])[0]
                            father_function = re.findall('label\s"(L.*?;->.*?)\(', node_list[father])[0]
                            brother_node, idb = backwardDataflow_plus(father, data, node_list)
                            for br in brother_node:
                                label_api = ''
                                if re.findall('L(.*?;->.*?)\(', br['label']):
                                    label_api = re.findall('L(.*?;->.*?)\(', br['label'])[0]
                                elif re.findall('L(.*?;->.*?)<', br['label']):
                                    label_api = re.findall('L(.*?;->.*?)<', br['label'])[0]
                                if label_api in str(apis_from_test):
                                    api_list.append(label_api)
                                # elif label_api[0:4] != 'java' and label_api[0:7] != 'android':
                                #     alterone = alter_api(label_api)
                                #     if len(alterone) > 0 and alterone in str(apis_from_test):
                                #         api_list.append(alterone)
                            if len(api_list) > 0:
                                has_record.append(father)
                                feature_file.write(str(father) + ' - ' + father_function + ':\n')
                                for api in api_list:
                                    feature_file.write(api + '\n')
                                feature_file.write('\n')
                except Exception as e:
                    ans = models.augmentAPiIn.objects.filter(apiName=api)
                    if ans:
                        # mark apis that cause sensitive behaviors
                        # if ans[0].mark == '1':
                            forward, idl = forwardDataflow_plus(index, data, node_list)
                            for father in idl:
                                if father not in has_record:
                                    api_list = []
                                    father_function = re.findall('label\s"(L.*?;->.*?)\(', node_list[father])[0]
                                    brother_node, idb = backwardDataflow_plus(father, data, node_list)
                                    for br in brother_node:
                                        label_api = ''
                                        if re.findall('L(.*?;->.*?)\(', br['label']):
                                            label_api = re.findall('L(.*?;->.*?)\(', br['label'])[0]
                                        elif re.findall('L(.*?;->.*?)<', br['label']):
                                            label_api = re.findall('L(.*?;->.*?)<', br['label'])[0]
                                        if label_api in str(apis_from_test):
                                            api_list.append(label_api)
                                        # elif label_api[0:4] != 'java' and label_api[0:7] != 'android':
                                        #     alterone = alter_api(label_api)
                                        #     # print('alter one:', alterone)
                                        #     if len(alterone) > 0 and alterone in str(apis_from_test):
                                        #         api_list.append(alterone)
                                    if len(api_list) > 0:
                                        has_record.append(father)
                                        feature_file.write(str(father) + ' - ' + father_function + ':\n')
                                        for api in api_list:
                                            feature_file.write(api + '\n')
                                        feature_file.write('\n')
            elif api.find('abortBroadcast') != -1:
                if index not in has_record:
                    has_record.append(index)
                    father_function = re.findall('label\s"(L.*?;->.*?)\(', node_list[index])[0]
                    feature_file.write(str(index) + ' - ' + father_function + ':\n')
                    feature_file.write('android/content/BroadcastReceiver;->abortBroadcast' + '\n')
                    feature_file.write('\n')
        # if len(api_list) > 0:
        #     feature_file.write(str(father_id) + ' - ' + father_function + ':\n')
        #     for api in api_list:
        #         feature_file.write(api + '\n')
        #     feature_file.write('\n')

    feature_file.close()

    end_time = time.time()
    print('Time of extracting features：', str(end_time - start_time))


def is_valid_url(url):
    """
    Determine whether a string/url is a valid URL
    """
    try:
        result = urlparse(url)
        return all([result.scheme, result.netloc])
    except ValueError:
        return False


def indentify_behaviors(apk_name, kg_apis):
    """
    :function: get all behaviors from the feature file
    :apk_name, app's name
    :return     match_node_rel: all nodes matched, may occur more than one time
                match_node_sin: all nodes matched which only occur one time
                match_node_for_path: extract behaviors from feature file and record their corresponding node id in call graph.
    """
    start_time = time.time()
    print('Identify behaviors...')
    perlist = []
    feature_data = do_feature_file(apk_name)
    data = feature_data.split("\n\n")

    cg_data = get_data(os.path.join('../detect/outputCG/', apk_name + '.txt'))
    node_list, edge_list = analyse(cg_data)
    # 0. get permission id
    permissions = data[0].split('\n')
    for per in permissions:
        try:
            id = models.augmentPerIn.objects.get(perName=per).perID
            if id not in perlist:
                perlist.append(id)
        except:
            # print('This permission couldn\'t be found：', per)
            pass
    intent_filters=data[1].split('\n')
    candidates = []  # possible nodes
    # read apis
    # print('data:',data[2:4])
    for one in data[2:]:
        apis = one.strip('\n').split('\n')[1:]
        apilist = []  # api id list in this block
        # 1. get api id, noting api sdk和api similar
        for api in apis:
            if api in kg_apis:
                pass
            else:
                api = api_sdk_sim(api, kg_apis)
            try:
                id = models.augmentAPiIn.objects.get(apiName=api).apiID
                apilist.append(id)
            except:
                # print('Api does not exist：', api)
                pass

        # 2. find candidate nodes
        cgID = one.strip('\n').split('\n')[0].split(' - ')[0]
        print('cgID:',cgID)
        for node in kg:
            # shallow copy
            detect_node = node.copy()
            tmp = apilist
            node_apiList = node['apiList']
            node_perList = node['perList']
            node_constList=node['constList']
            inset_api = list(set(node_apiList).intersection(set(tmp)))
            inset_per = list(set(node_perList).intersection(set(perlist)))
            inset_api.sort()
            inset_per.sort()
            node_apiList.sort()
            node_perList.sort()

            if eq(inset_api, node_apiList) and eq(inset_per, node_perList):
                constants = []
                for cstr in node_constList:
                    if cstr in intent_filters:
                        constants.append(cstr)
                    elif cstr.find('android.')==-1:
                        if cgID is not None:
                            cfg_file = create_cfg_specific(apk_name, node_list[int(cgID)])
                            if cfg_file is not None:
                                if os.path.exists(cfg_file):
                                    cfg_file_data = get_data(cfg_file)
                                    pattern = re.compile(cstr, re.S)
                                    ans = pattern.findall(cfg_file_data)
                                    if ans:
                                        # print('yes! ', node['actionName'])
                                        constants.append(cstr)
                inset_const = list(set(node_constList).intersection(set(constants)))
                inset_const.sort()
                node_constList.sort()
                if eq(inset_const,node_constList):
                    detect_node['cgID'] = int(cgID)
                    possible_nodes = detect_node
                    candidates.append(possible_nodes)
                    # for appi in inset_api:
                    #     apilist.remove(appi)

        # if possible_nodes:
        #     candidates.append(possible_nodes)
        # print('\n')
    end_time = time.time()
    print('Time of identify behaviors：', str(end_time - start_time))

    return candidates
