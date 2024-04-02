"""
This file is mainly used for program analysis, but this is the initial version.
The functions in this file may be called in other optimization sections.
"""
import glob
import re
import time

from itertools import groupby
from operator import itemgetter

import django
import sys
import os
import numpy as np

from exper.augment import create_cfg, get_location_name
from tools.utils import do_file_name

sys.path.append('../')

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'mwep.settings')

django.setup()

"""
Create CFG of a apk:
    androguard decompile -o outputfolder -f png -i apkpath/someapp.apk --limit "^Lcom/elite/.*"
"""


def analyse(data):
    pattern = re.compile('edge \[\n(.*?)]', re.S)
    return_edge_list = pattern.findall(data)
    pattern = re.compile('node \[\n(.*?)external', re.S)
    return_node_list = pattern.findall(data)
    return return_node_list, return_edge_list


def get_data(url):
    f = open(url, "r", encoding='utf-8')
    data = f.read()
    f.close()
    return data


def backwardDataflow(id, apk_name):
    """
    input the cg node id, output its backward nodes
    id: int
    apk_name: string
    """
    ret = []
    id_ret = []
    data = get_data(os.path.join('../detect/outputCG/', apk_name + '.txt'))
    node_list, edge_list = analyse(data)
    data = data.replace("\n", '')
    backwardNodeID = re.findall('source ' + str(id) + '\s*target\s*(\d+)',
                                data)  # these may be many targets nodes(>=2)
    if backwardNodeID:
        for nodeID in backwardNodeID:
            node = node_list[int(nodeID)]
            if node.find('>') != -1 and node.find('(') != -1:
                star = node.find('>')
                end = node.find('(')
                name = node[star + 1:end]  # delete those nodes which have been confused
                if len(name) > 1:
                    id = int(re.findall('id\s(\d+)\n', node)[0])
                    label = re.findall('label\s"(.*?)"\n', node)[0]
                    # ret.append(node)
                    ret.append({'id': id, 'label': label})
                    id_ret.append(id)

    return ret, id_ret


def backwardDataflow_plus(id, cg_file_data, cg_node_list):
    """
    input the cg node id, output its backward nodes
    id: int
    apk_name: string
    """
    ret = []
    id_ret = []
    backwardNodeID = re.findall('source ' + str(id) + '\s*target\s*(\d+)',
                                cg_file_data)  # these may be many targets nodes(>=2)
    # print('\nbackward id:',id)
    if backwardNodeID:
        for nodeID in backwardNodeID:
            node = cg_node_list[int(nodeID)]
            if node.find('>') != -1 and node.find('(') != -1:
                # print(node)
                # star = node.find('>')
                # end = node.find('(')
                # name = node[star + 1:end]
                # if len(name) > 1:
                #     id = int(re.findall('id\s(\d+)\n', node)[0])
                #     label = re.findall('label\s"(.*?)"\n', node)[0]
                #     ret.append({'id': id, 'label': label})
                #     id_ret.append(id)
                id = int(re.findall('id\s(\d+)', node)[0])
                label = re.findall('label\s"(.*?)"', node)[0]
                ret.append({'id': id, 'label': label})
                id_ret.append(id)
    return ret, id_ret


def forwardDataflow(id, apk_name):
    """
    input the cg node id, output its forward nodes
    id: int
    apk_name: string
    """
    ret = []
    id_ret = []
    data = get_data(os.path.join('../detect/outputCG/', apk_name + '.txt'))
    node_list, edge_list = analyse(data)
    data = data.replace("\n", '')
    forwardNodeID = re.findall('source\s*(\d+)' + '\s*target\s' + str(id) + '\s',
                               data)  # these may be many targets nodes(>=2)
    if forwardNodeID:
        for nodeID in forwardNodeID:
            node = node_list[int(nodeID)]
            if node.find('>') != -1 and node.find('(') != -1:
                star = node.find('>')
                end = node.find('(')
                name = node[star + 1:end]
                if len(name) > 1:
                    id = int(re.findall('id\s(\d+)\n', node)[0])
                    label = re.findall('label\s"(.*?)"\n', node)[0]
                    ret.append({'id': id, 'label': label})
                    id_ret.append(id)
    return ret, id_ret


def forwardDataflow_plus(id, cg_file_data, cg_node_list):
    """
    input the cg node id, output its forward nodes
    id: int
    apk_name: string
    where plus? Reduce the number of times you open and read files.
    """
    ret = []
    id_ret = []
    class_location, class_name, method_name, filename = do_file_name(cg_node_list[id])
    if method_name == 'doInBackground':  # android.os.AsyncTask
        find_str = 'L' + class_location + '/' + class_name + ';->execute'
        find_str = find_str.replace('$', '\$')
        pattern = re.compile('id\s(\d+)\s\s\s\slabel\s"' + find_str, re.S)
        ans = pattern.findall(cg_file_data)
        if ans:
            cgID = int(ans[0])
            label = re.findall('label\s"(.*?)"', cg_node_list[cgID])[0]
            ret.append({'id': id, 'label': label})
            id_ret.append(cgID)
    if method_name == 'onPostExecute':  # android.os.AsyncTask
        find_str = 'L' + class_location + '/' + class_name + ';->doInBackground'
        find_str = find_str.replace('$', '\$')
        pattern = re.compile('id\s(\d+)\s\s\s\slabel\s"' + find_str, re.S)
        ans = pattern.findall(cg_file_data)
        if ans:
            cgID = int(ans[0])
            label = re.findall('label\s"(.*?)"', cg_node_list[cgID])[0]
            ret.append({'id': id, 'label': label})
            id_ret.append(cgID)
    forwardNodeID = re.findall('source\s*(\d+)' + '\s*target\s' + str(id) + '\s',
                               cg_file_data)  # these may be many targets nodes(>=2)
    if forwardNodeID:
        for nodeID in forwardNodeID:
            node = cg_node_list[int(nodeID)]
            if node.find('>') != -1 and node.find('(') != -1:
                id = int(re.findall('id\s(\d+)', node)[0])
                label = re.findall('label\s"(.*?)"', node)[0]
                ret.append({'id': id, 'label': label})
                id_ret.append(id)
    return ret, id_ret


def query_node(id, apkname):
    data = get_data(os.path.join('../detect/outputCG/', apkname + '.txt'))
    node_list, edge_list = analyse(data)
    id = int(re.findall('id\s(\d+)\n', node_list[id])[0])
    label = re.findall('label\s"(.*?)"\n', node_list[id])[0]
    return {'id': id, 'label': label}


def test(id, apkname):
    data = get_data(os.path.join('../detect/outputCG/', apkname + '.txt'))
    node_list, edge_list = analyse(data)
    data = data.replace("\n", '')
    backward, idl = backwardDataflow_plus(id, data, node_list)
    forward, idl = forwardDataflow_plus(id, data, node_list)
    id = int(re.findall('id\s(\d+)\n', node_list[id])[0])
    label = re.findall('label\s"(.*?)"\n', node_list[id])[0]
    print("\n*****Query node*****\n", {'id': id, 'label': label})
    print("\n*****Backward*****")
    for one in backward:
        print(one)

    print("\n*****Forward*****")
    for one in forward:
        print(one)



# test(16450, 'System_update')
# print('\n______________\n')
# test(12043, 'StealJob')


def key_sort_group(data, keywords):
    """
    :function group dict with specified keywords
    :param data: dict like {'cg_node':null,'behavior':null}
    """
    result = dict()
    data = sorted(data, key=itemgetter(keywords))
    for keywords, items in groupby(data, key=itemgetter(keywords)):
        result[keywords] = list(items)
    # print('sort group:', result)

    return result


def list_print(listdata):
    for one in listdata:
        print(one)


def change_ag_txt(base_location, file_location):
    """
    :function turn ag to txt
    :param base_location: where the apk excutes androguard commands
    :param file_location: ag file's location related to api class
    :return cfg with txt format
    """
    if file_location[0] == 'L':  # ag文件的位置（也是classname）往往以L开头，但是androguard decompile后的文件路径是没有L的
        file_location = file_location[1:]
    ag_path = os.path.join(base_location, file_location + '/')
    files = glob.glob(ag_path + '/*.ag')
    ag_file_name = ''
    for f in files:
        filename = os.path.split(f)[1]
        ag_file_name = filename.split('.')[0]  # ag file's name

    txt_file = ag_path + ag_file_name + '.txt'
    if os.path.exists(txt_file):
        return txt_file
    else:
        ag_file = ag_path + ag_file_name + '.ag'
        if os.path.exists(ag_file):
            os.rename(ag_file, txt_file)
            return txt_file
        else:
            return None


def find_relation_in_CFG_father(behv1, behv2, father_id, apkname):
    """
    some behaviors have the same parent father node, therefore,
    we generate the cfg of father node, and sort the two behaviors
    :param behav1:<dict> a behavior, like {'behavior': 'Send SMS', 'cg_node': 4165}
    :param hehav2: same as a hebavior
    :param father_id:<int> the cg node id of parent node
    :param apkname
    """
    data = get_data(os.path.join('../detect/outputCG/', apkname + '.txt'))
    node_list, edge_list = analyse(data)
    # *** parent node ***
    label = re.findall('label\s"(.*?)"\n', node_list[father_id])[0]
    # *** behav1 and hebav2 ***
    label1 = re.findall('label\s"(.*?)"\n', node_list[int(behv1['cg_node'])])[0]
    if label1.find('[access_flags') != -1:
        l = label1.find('[')
        label1 = label1[:l - 1]
    behv1['cfg_node'] = -1
    label2 = re.findall('label\s"(.*?)"\n', node_list[int(behv2['cg_node'])])[0]
    if label2.find('[access_flags') != -1:
        l = label2.find('[')
        label2 = label2[:l - 1]
    behv2['cfg_node'] = -1

    # print('label:', label)
    # print('label1:', label1)
    # print('label2:', label2)

    # *** Create CFG ***
    apk_location = '/home/wuyang/Experiments/Datas/malwares/googlePlay/apk_sample'
    base_location = '../detect/output_augment/processing/'  # excute androguard command
    base_location = base_location + apkname + '/'
    file_location, function_name = get_location_name(label)
    create_cfg(apkname, function_name, apk_location)
    cfg_file = change_ag_txt(base_location, file_location)

    # *** Read and Sort ***
    if cfg_file is not None:
        if os.path.exists(cfg_file):
            cfg_file = open(cfg_file, 'r', encoding='utf-8', newline="")
            for row in cfg_file.readlines():
                if row.find(label1) != -1:
                    pattern = re.compile('\s*(\d*)\s*?\(', re.S)
                    location = pattern.findall(row)[0]
                    if location:
                        behv1['cfg_node'] = int(location)
                    else:
                        behv1['cfg_node'] = -1
                if row.find(label2) != -1:
                    pattern = re.compile('\s*(\d*)\s*?\(', re.S)
                    location = pattern.findall(row)[0]
                    if location:
                        behv2['cfg_node'] = int(location)
                    else:
                        behv2['cfg_node'] = -1
    ret = []
    if behv1['cfg_node'] > -1 and behv2['cfg_node'] > -1:
        if behv1['cfg_node'] > behv2['cfg_node']:
            ret.append(behv2)
            ret.append(behv1)
        else:
            ret.append(behv1)
            ret.append(behv2)

    return ret


# find_relation_in_CFG_father({'behavior': 'Send SMS', 'cg_node': 4165},
#                             {'behavior': 'Get user\'s phone number', 'cg_node': 4182}, 4192, 'AceCard')


def compare_hexadecimal(hex_str1, hex_str2):
    # 将十六进制字符串转换为十进制整数
    decimal1 = int(hex_str1[5:], 16)
    decimal2 = int(hex_str2[5:], 16)
    flag=-1
    # 比较大小
    if decimal1 > decimal2:
        print(f"{hex_str1} is greater than {hex_str2}")
        ret=[hex_str2,hex_str1]
        flag=0
    elif decimal1 < decimal2:
        print(f"{hex_str1} is less than {hex_str2}")
        ret = [hex_str1, hex_str2]
        flag=1
    else:
        print(f"{hex_str1} is equal to {hex_str2}")
        ret=[]

    return ret,flag

# compare_hexadecimal('BB@0x3a','BB@0xf0',)

def find_relation_in_CFG_father_plus(common_record, apkname, node_list):
    """
    for module2_extract_relation
    common_record: single record
    """
    father_id = common_record['common']
    behav1 = {}
    behav2 = {}

    # *** parent node ***
    label = re.findall('label\s"(.*?)"\n', node_list[father_id])[0]
    # *** behav1 and hebav2 ***
    behav1['actionName'] = common_record['action1']
    behav1['kg_node'] = common_record['kg_node1']
    behav1['cg_node'] = common_record['p1'][len(common_record['p1']) - 2]
    label1 = re.findall('label\s"(.*?)"\n', node_list[behav1['cg_node']])[0]
    if label1.find('[access_flags') != -1:
        l = label1.find('[')
        label1 = label1[:l - 1]
    behav1['cfg_node'] = -1
    behav1['cg_node'] = common_record['p1'][0]

    behav2['cg_node'] = common_record['p2'][len(common_record['p2']) - 2]
    behav2['actionName'] = common_record['action2']
    behav2['kg_node'] = common_record['kg_node2']
    label2 = re.findall('label\s"(.*?)"\n', node_list[int(behav2['cg_node'])])[0]
    if label2.find('[access_flags') != -1:
        l = label2.find('[')
        label2 = label2[:l - 1]
    behav2['cfg_node'] = -1
    behav2['cg_node'] = common_record['p2'][0]

    # print('label:', label)
    # print('label1:', label1)
    # print('label2:', label2)

    # *** Create CFG ***
    apk_location = '../webapp/uploadFiles'
    base_location = '../detect/output_augment/processing/'  # excute androguard command
    base_location = base_location + apkname + '/'
    file_location, function_name = get_location_name(label)
    create_cfg(apkname, function_name, apk_location)
    cfg_file = change_ag_txt(base_location, file_location)

    # *** Read and Sort ***
    if cfg_file is not None:
        if os.path.exists(cfg_file):
            cfg_file = open(cfg_file, 'r', encoding='utf-8', newline="")
            for row in cfg_file.readlines():
                if row.find(label1) != -1:
                    pattern = re.compile('\s*(\d*)\s*?\(', re.S)
                    location = pattern.findall(row)[0]
                    if location:
                        behav1['cfg_node'] = int(location)
                    else:
                        behav1['cfg_node'] = -1
                if row.find(label2) != -1:
                    pattern = re.compile('\s*(\d*)\s*?\(', re.S)
                    location = pattern.findall(row)[0]
                    if location:
                        behav2['cfg_node'] = int(location)
                    else:
                        behav2['cfg_node'] = -1
    ret = []
    if behav1['cfg_node'] > -1 and behav2['cfg_node'] > -1:
        if behav1['cfg_node'] > behav2['cfg_node']:
            ret.append(behav2)
            ret.append(behav1)
        else:
            ret.append(behav1)
            ret.append(behav2)

    return ret


def generate_subgraph_back(source_cgn, apkname, subgraph):
    """
    extract a subgraph from the cg begining at the given cg node
    :param source_cgn: (int) node id
    :param apkname: (string) apk name
    """
    # global subgraph
    backward, idl = backwardDataflow(source_cgn, apkname)
    if len(idl) > 0:
        key = source_cgn
        subgraph[key] = idl
        for one in idl:
            generate_subgraph_back(one, apkname, subgraph)
    else:
        key = source_cgn
        subgraph[key] = idl

    return subgraph


def generate_subgraph_forw(source_cgn, apkname, subgraph, depth_count):
    """
    extract a subgraph from the cg end at the given cg node
    :param source_cgn: (int) node id
    :param apkname: (string) apk name
    """
    forward, idl = forwardDataflow(source_cgn, apkname)
    # print('generating sub-graph from source_cgn...')
    if len(idl) > 0 and depth_count < 10:
        key = source_cgn
        subgraph[key] = idl
        for one in idl:
            generate_subgraph_forw(one, apkname, subgraph, depth_count + 1)
    else:
        key = source_cgn
        subgraph[key] = idl

    return subgraph


def generate_subgraph_forw_plus(source_cgn, apkname, subgraph, cg_file_data, cg_node_list, depth_count):
    """
    extract a subgraph from the cg end at the given cg node
    :param source_cgn: (int) node id
    :param apkname: (string) apk name
    """
    forward, idl = forwardDataflow_plus(source_cgn, cg_file_data, cg_node_list)
    # print('idls:',idl)
    if len(idl) > 0 and depth_count < 5:
        key = source_cgn
        keys = list(subgraph.keys())  # Be careful to avoid loops
        # print('key:', key, ' forgraph:', idl)
        if key not in subgraph:
            tmp = []
            for index,one in enumerate(idl):
                label=re.findall('label\s"(.*?)"',cg_node_list[one])[0]
                if label.find('doInBackground')!=-1:
                    class_location, class_name, method_name, filename = do_file_name(cg_node_list[one])
                    find_str = 'L' + class_location + '/' + class_name + ';->execute'
                    find_str = find_str.replace('$', '\$')
                    pattern = re.compile('id\s(\d+)\s\s\s\slabel\s"' + find_str, re.S)
                    ans = pattern.findall(cg_file_data)
                    if ans:
                        idl[index] = int(ans[0])
                        one=int(ans[0])
                if one not in tmp and one not in keys:
                    tmp.append(one)
            subgraph[key] = tmp
        else:
            tmp = subgraph[key]
            for index,one in enumerate(idl):
                label = re.findall('label\s"(.*?)"', cg_node_list[one])[0]
                if label.find('doInBackground') != -1:
                    class_location, class_name, method_name, filename = do_file_name(cg_node_list[one])
                    find_str = 'L' + class_location + '/' + class_name + ';->execute'
                    find_str = find_str.replace('$', '\$')
                    pattern = re.compile('id\s(\d+)\s\s\s\slabel\s"' + find_str, re.S)
                    ans = pattern.findall(cg_file_data)
                    if ans:
                        idl[index] = int(ans[0])
                        one = int(ans[0])
                if one not in tmp and one not in keys:
                    tmp.append(one)
            subgraph[key] = tmp
        for one in idl:
            if one not in keys:
                generate_subgraph_forw_plus(one, apkname, subgraph, cg_file_data, cg_node_list, depth_count + 1)
    else:
        key = source_cgn
        # subgraph[key] = idl
        subgraph[key] = []
        # print('final key:', key, 'final subgraph:', subgraph[key])

    return subgraph



def generate_subgraph_back_plus(source_cgn, apkname, subgraph, cg_file_data, cg_node_list, depth_count):
    """
    extract a subgraph from the cg begining at the given cg node
    :param source_cgn: (int) node id
    :param apkname: (string) apk name
    """
    # global subgraph
    backward, idl = backwardDataflow_plus(source_cgn, cg_file_data, cg_node_list)
    if len(idl) > 0 and depth_count < 5:
        key = source_cgn
        keys = list(subgraph.keys())  # Be careful to avoid loops
        # print('key:', key, ' forgraph:', idl)
        if key not in subgraph:
            tmp = []
            for one in idl:
                if one not in tmp and one not in keys:
                    tmp.append(one)
            subgraph[key] = tmp
        else:
            tmp = subgraph[key]
            for one in idl:
                if one not in tmp and one not in keys:
                    tmp.append(one)
            subgraph[key] = tmp
        for one in idl:
            if one not in keys:
                generate_subgraph_back_plus(one, apkname, subgraph, cg_file_data, cg_node_list, depth_count + 1)
    else:
        key = source_cgn
        # subgraph[key] = idl
        subgraph[key] = []
        # print('final key:', key, 'final subgraph:', subgraph[key])

    return subgraph


def search_graph_back(graph, start, end):
    """
    output paths from start node to end node
    """
    results = []
    generate_path_back(graph, [start], end, results)
    results.sort(key=lambda x: len(x))
    return results


def generate_path_back(graph, path, end, results):
    state = path[-1]
    if state == end:
        results.append(path)
        path = []

    else:
        try:
            for arc in graph[state]:
                if arc not in path:
                    generate_path_back(graph, path + [arc], end, results)
        except:
            path.append(state)
            pass


def search_graph_forw(graph, end):
    """
    output paths from start node to end node
    """
    results = []
    generate_path_forw(graph, [end], results)
    results.sort(key=lambda x: len(x))
    return results


def generate_path_forw(graph, path, results):
    state = path[-1]  # 4097
    for one in graph[state]:
        if one in graph.keys():
            if not graph[one]:  # 4091
                path.append(one)
                results.append(path)
            else:
                if one not in path:
                    generate_path_forw(graph, path + [one], results)  # path:[4225,4174,4097,]


def extract_relations(matched_node_for_path, apkname):
    """
    get relations between behaviors with the help of Call graph and Control flow graph
    """
    ret_relations = []
    behavior_group = key_sort_group(matched_node_for_path, 'behavior')
    large_group = []
    for key, value in behavior_group.items():
        behavior = key
        cg_nlist = value
        tmp = []
        for one in cg_nlist:
            current_cgn = one['cg_node']
            back_cgn, back_id = backwardDataflow(int(current_cgn), apkname)
            forw_cgn, forw_id = forwardDataflow(int(current_cgn), apkname)
            tmp.append({'current': current_cgn, 'back': back_id, 'forw': forw_id})
        large_group.append({'behavior': behavior, 'cg_relation': tmp})

    for index, one in enumerate(large_group):
        current_behavior = one['behavior']
        for rel in one['cg_relation']:
            father_nodes = rel['forw']
            child_nodes = rel['back']
            current_cgn = rel['current']
            for another in large_group[index + 1:]:
                other_behavior = another['behavior']
                for other_rel in another['cg_relation']:
                    other_father_nodes = other_rel['forw']
                    other_child_nodes = other_rel['back']
                    other_cgn = other_rel['current']
                    if father_nodes:
                        # Case 1: having the same parent node
                        interse = list(set(father_nodes).intersection(set(other_father_nodes)))
                        if interse:
                            for i in interse:
                                father_id = i
                                behav1 = {'behavior': current_behavior, 'cg_node': int(current_cgn)}
                                behav2 = {'behavior': other_behavior, 'cg_node': int(other_cgn)}
                                find_rel = find_relation_in_CFG_father(behav1, behav2, father_id, apkname)
                                if find_rel:
                                    # print('Find relation:', find_rel)
                                    path = [find_rel[0]['behavior'], find_rel[1]['behavior']]
                                    if path not in ret_relations:
                                        ret_relations.append(path)
                                else:
                                    continue

                        # Case 2: having the same node end to end
                        interse = list(set(father_nodes).intersection(set(other_child_nodes)))
                        if interse:
                            for i in interse:
                                behav1 = {'behavior': current_behavior, 'cg_node': int(current_cgn)}
                                behav2 = {'behavior': other_behavior, 'cg_node': int(other_cgn)}
                                # print('interse 2:', interse)
                                # print('current 1.1:', current_behavior)
                                # print('current 1.2:', other_behavior)
                                find_rel = [behav2['behavior'], behav1['behavior']]
                                # print('Find relation2.1:', find_rel)
                                if find_rel not in ret_relations:
                                    ret_relations.append(find_rel)

                    if child_nodes:
                        # Case 2: having the same node end to end
                        interse = list(set(child_nodes).intersection(set(other_father_nodes)))
                        if interse:
                            for i in interse:
                                # call graph: from current behavior to another behavior
                                common_id = i
                                behav1 = {'behavior': current_behavior, 'cg_node': int(current_cgn)}
                                behav2 = {'behavior': other_behavior, 'cg_node': int(other_cgn)}
                                find_rel = [behav1['behavior'], behav2['behavior']]
                                # print('Find relation2.2:', find_rel)
                                if find_rel not in ret_relations:
                                    ret_relations.append(find_rel)

                        # Case 3: having the common child node
                        interse = list(set(child_nodes).intersection(set(other_child_nodes)))
                        # if interse:
                        #     print('Having the same child node, it violates the definition of a tree!')

    return large_group, ret_relations


def group_large_whole(match_nodes, apkname):
    """
    more complete than extract_relations
    :param match_nodes:<list> like [{'cg_node':null,'behavior':null}]
    :param apkname:<string> apk name
    """
    global depth_count
    large_group_complete = []
    behavior_group = key_sort_group(match_nodes, 'behavior')
    for key, value in behavior_group.items():
        # print('当前行为：', key)
        behavior = key
        cg_nlist = value
        tmp = []
        # for one in cg_nlist:
        one = cg_nlist[-1]
        # print('当前行为下的CG NODE：', one)
        cg_node = one['cg_node']
        depth_count = 0
        sub_graph = generate_subgraph_forw(cg_node, apkname, {})
        if sub_graph:
            root_leaf_path = search_graph_forw(sub_graph, cg_node)
            # sub_graph=generate_subgraph_forw(cg_node,apkname,{})
            # root_leaf_path=search_graph_forw(sub_graph,cg_node)
            # print('搜索到的：', root_leaf_path)
            tmp.append(root_leaf_path)
        large_group_complete.append({'behavior': behavior, 'forward': tmp})

    return large_group_complete


def extarct_relations_whole(group_nodes, apkname):
    """
    more complete than extract_relations
    :param group_nodes:<list> like [{'behavior':null,'forward':[[[]]]}]
    :param apkname:<string> apk name
    """
    ret_relations = []
    for index, one in enumerate(group_nodes):
        behavior = one['behavior']
        forward = one['forward']
        if forward:
            curretn_path_list = forward[0]
            # print('curretn_path_list:', curretn_path_list)
            for another in group_nodes[index + 1:]:
                if another['forward']:
                    another_path_list = another['forward'][0]
                    tmp = []
                    cur_path = []
                    ano_path = []
                    for current_path in curretn_path_list:
                        for anoth_path in another_path_list:
                            interse = list(set(current_path).intersection(set(anoth_path)))
                            if interse:
                                if len(interse) > len(tmp):
                                    tmp = interse
                                    cur_path = current_path
                                    ano_path = anoth_path
                    if tmp:
                        # print('\n********************')
                        # print(behavior + ' <--> ' + another['behavior'])
                        # print(tmp)
                        # print('current path:', cur_path)
                        # print('another path:', ano_path)
                        cur_index = len(cur_path)
                        ano_index = len(ano_path)
                        cfg_common_node = -1
                        if len(tmp) == 1:  # only interset at only one node
                            cfg_common_node = tmp[0]
                        else:
                            for t in tmp:
                                if cur_path.index(t) < cur_index:
                                    cur_index = cur_path.index(t)
                                    cfg_common_node = t
                        cur_index = cur_path.index(cfg_common_node) - 1
                        ano_index = ano_path.index(cfg_common_node) - 1
                        cur_father_node = cur_path[cur_index]
                        ano_father_node = ano_path[ano_index]
                        # find their location in CFG
                        param1 = {'behavior': behavior, 'cg_node': cur_father_node}
                        param2 = {'behavior': another['behavior'], 'cg_node': ano_father_node}
                        # print('cfg_common_node:', cfg_common_node)
                        # print('pa1:', param1)
                        # print('pa2:', param2)
                        ret = find_relation_in_CFG_father(param1,
                                                          param2,
                                                          cfg_common_node, apkname)
                        if len(ret) > 0:
                            ret_relations.append([ret[0]['behavior'], ret[1]['behavior']])
                            # print('new rel2:', ret)

    return ret_relations


group_nodes = [{'behavior': 'Active when start-up broadcast is detected', 'forward': [[]]},
               {'behavior': 'Block broadcast', 'forward': [[]]}, {'behavior': 'Get IMEI', 'forward': [
        [[4225, 4174, 4097, 4091], [4225, 4174, 4115, 4114], [4225, 4174, 4116, 4114], [4225, 4174, 4195, 10692],
         [4225, 4174, 4192, 8340, 10690], [4225, 4174, 4164, 4153, 4121, 4114], [4225, 4174, 4166, 4158, 4121, 4114],
         [4225, 4174, 4168, 4160, 4121, 4114], [4225, 4174, 4169, 4152, 4121, 4114],
         [4225, 4174, 4169, 4159, 4121, 4114]]]}, {'behavior': "Get user's phone number", 'forward': [
        [[4182, 4192, 8340, 10690], [4182, 4164, 4153, 4121, 4114]]]}, {'behavior': 'Hide icon', 'forward': [[]]},
               {'behavior': 'Parsing SMS content', 'forward': [[[4105, 4114]]]},
               {'behavior': 'Read SMS', 'forward': [[[10118, 10121]]]},
               {'behavior': 'Request activation of device manager', 'forward': [[[8322, 8331]]]},
               {'behavior': 'Send SMS', 'forward': [
                   [[4165, 4097, 4091], [4165, 4115, 4114], [4165, 4116, 4114], [4165, 4195, 10692],
                    [4165, 4157, 4121, 4114], [4165, 4158, 4121, 4114], [4165, 4192, 8340, 10690],
                    [4165, 4164, 4153, 4121, 4114], [4165, 4168, 4160, 4121, 4114], [4165, 4169, 4152, 4121, 4114],
                    [4165, 4169, 4159, 4121, 4114]]]}]


# list_print(group_nodes)
# extarct_relations_whole(group_nodes, 'AceCard')


# test(4189,'AceCard')


def get_all_nodes_rel(ret_relations):
    """
    find all nodes in relations
    :param ret_relations:<list> the relations between two nodes
    """
    all_nodes = []
    for rel in ret_relations:
        source = rel[0]
        target = rel[1]
        if source not in all_nodes:
            all_nodes.append(source)
        if target not in all_nodes:
            all_nodes.append(target)
    return all_nodes


def generate_output_graph(all_nodes, ret_relations):
    """
    generate a output graph accoring to ret-relations
    :param ret_relations:<list> the relations between two nodes,like [[source,target],...]
    :param all_nodes:<list> the output of get_all_nodes_rel
    """
    graph = {}
    root = []  # no edges indicate it
    leaf = []  # no edges come from it
    for node in all_nodes:
        targets = []
        for rel in ret_relations:
            if rel[0] == node and rel[1] not in targets:
                targets.append(rel[1])

        if not targets:
            leaf.append(node)
        graph[node] = targets

    relations = np.array(ret_relations)
    # print('generate_output_graph-ret_relations:',relations)
    all_targets = relations[:, -1].tolist()
    root = list(set(all_nodes).difference(set(all_targets)))

    return graph, root, leaf


graph = {"Get user's phone number": ['Get IMEI', 'Send SMS'], 'Get IMEI': ['Send SMS'],
         'Parsing SMS content': ['Get IMEI', "Get user's phone number", 'Send SMS'], 'Send SMS': [],
         'Block broadcast': ['Send SMS'], 'Read SMS': ['Send SMS']}
root = ['Block broadcast', 'Read SMS', 'Parsing SMS content']
leaf = ['Send SMS']


def program_static_analysis(match_node_for_path, apkname):
    ret = {}
    with open('./analysis_log.txt', "a", encoding='utf-8') as f:
        f.truncate(0)
    with open('./analysis_log.txt', "a", encoding='utf-8') as log:
        s = time.time()
        log.write('***** Starting program analysis*****\n1. extracting direct relation...\n')
        large_group, ret_relations1 = extract_relations(match_node_for_path, apkname)
        group_nodes = group_large_whole(match_node_for_path, apkname)
        e = time.time()
        log.write('Complete! Time cost: {:.5f} s'.format(e - s) + '\n')

        s = time.time()
        log.write('2. extracting complete relations...\n')
        ret_relations2 = extarct_relations_whole(group_nodes, apkname)
        for one in ret_relations1:
            if one not in ret_relations2:
                ret_relations2.append(one)
        e = time.time()
        log.write('Complete! Time cost: {:.5f} s'.format(e - s) + '\n')

        print('rel2:', ret_relations2)
        if ret_relations2:
            s = time.time()
            log.write('3. generate output graph...\n')
            all_nodes = get_all_nodes_rel(ret_relations2)
            ggraph, groot, gleaf = generate_output_graph(all_nodes, ret_relations2)
            e = time.time()
            log.write('Complete! Time cost: {:.5f} s'.format(e - s) + '\n')
            print('*********graph***********')
            print(ggraph)
            print('*********root***********')
            print(groot)
            print('*********leaf***********')
            print(gleaf)

            s = time.time()
            log.write('4. generate output paths...\n')
            all_path = []
            for r in groot:
                print('r:', r)
                for l in gleaf:
                    print('l:', l)
                    query_ret = search_graph_back(ggraph, r, l)
                    if not query_ret:
                        # print('Sorry, no this path...')
                        pass
                    else:
                        for i in query_ret:
                            all_path.append(i)
                            # print(i)
            e = time.time()
            log.write('Complete! Time cost: {:.5f} s'.format(e - s) + '\n')

            ret['paths'] = all_path
            ret['nodes'] = all_nodes
            ret['relations'] = ret_relations2
            ret['graph'] = ggraph

        return ret

# match_node = [{'cg_node': 628, 'behavior': 'Active when start-up broadcast is detected'},
#               {'cg_node': 641, 'behavior': 'Active when start-up broadcast is detected'},
#               {'cg_node': 691, 'behavior': 'Active when start-up broadcast is detected'},
#               {'cg_node': 695, 'behavior': 'Active when start-up broadcast is detected'},
#               {'cg_node': 696, 'behavior': 'Active when start-up broadcast is detected'},
#               {'cg_node': 4063, 'behavior': 'Request activation of device manager'},
#               {'cg_node': 4071, 'behavior': 'Hide icon'},
#               {'cg_node': 4091, 'behavior': 'Read SMS'}, {'cg_node': 4105, 'behavior': 'Parsing SMS content'},
#               {'cg_node': 4114, 'behavior': 'Block broadcast'}, {'cg_node': 4165, 'behavior': 'Send SMS'},
#               {'cg_node': 4182, 'behavior': "Get user's phone number"}, {'cg_node': 4225, 'behavior': 'Get IMEI'},
#               {'cg_node': 6523, 'behavior': 'Active when start-up broadcast is detected'},
#               {'cg_node': 8322, 'behavior': 'Request activation of device manager'},
#               {'cg_node': 10073, 'behavior': 'Active when start-up broadcast is detected'},
#               {'cg_node': 10118, 'behavior': 'Read SMS'}]

# s = time.time()
# r = static_program_analysis(match_node, 'AceCard')
# e = time.time()
# print('Time of program analysis {:.5f} s'.format(e - s))
# print(r)
