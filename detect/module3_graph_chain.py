"""
This is the third module to analyse an apk.
We will:
1. generate chains based on the output graph;

2023-10-29
"""
import copy

import django
import sys
import os
import dashscope
from http import HTTPStatus
import time

from tools.utils import search_graph_back, list_print, find_relations_all

sys.path.append('../')

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'mwep.settings')

django.setup()
from common import models

dashscope.api_key = "sk-9bdbdd6cd8dd4904873d8c414a1c5c66"


def main_module3(output_node, match_nodes_plus, graph, apkname):
    root, leaf = get_root_leaf(match_nodes_plus, graph)
    chains = []
    chains_for_des = []
    for r in root:
        for l in leaf:
            query_ret = search_graph_back(graph, r, l)
            if not query_ret:
                pass
            else:
                if len(query_ret[-1]) >= 2:
                    chains_for_des.append(query_ret[-1])
                for i in query_ret:
                    if len(i) >= 2:
                        chains.append(i)

    print('chains_for_des:', chains_for_des)
    # des = generate_des(chains_for_des)
    # write_apk_record(output_node, graph, chains_for_des, des, apkname)
    write_apk_record_plus(output_node, graph, chains_for_des, apkname)
    return chains, chains_for_des


def get_root_leaf(match_nodes_plus, graph):
    allbehav = []
    leaf = []
    targets = []
    for key, value in graph.items():
        if key not in allbehav:
            allbehav.append(key)
        if not value and key not in leaf:
            leaf.append(key)
        elif value:
            targets = targets + value
            for one in value:
                if one not in allbehav:
                    allbehav.append(one)
    root = list(set(allbehav).difference(set(targets)))

    new_root = []
    for one in match_nodes_plus:
        behav = one['actionName']
        if behav in root:
            new_root.append(behav)

    return new_root, leaf


def get_root_leaf_reduce(graph):
    allbehav = []
    leaf = []
    targets = []
    for key, value in graph.items():
        if key not in allbehav:
            allbehav.append(key)
        targets = targets + value
        for one in value:
            if one not in allbehav:
                allbehav.append(one)
    allbehav_copy=copy.deepcopy(allbehav)
    root = list(set(allbehav).difference(set(targets)))
    dict_keys=list(graph.keys())
    for one in allbehav_copy:
        if one not in root and one not in dict_keys:
            leaf.append(one)

    return root, leaf

def generate_chains(graph):
    root, leaf = get_root_leaf_reduce(graph)
    # print('root:',root)
    # print('leaf:', leaf)
    chains = []
    chains_for_des = []
    for r in root:
        for l in leaf:
            query_ret = search_graph_back(graph, r, l)
            if not query_ret:
                pass
            else:
                if len(query_ret[-1]) >= 2:
                    chains_for_des.append(query_ret[-1])
                for i in query_ret:
                    if len(i) >= 2:
                        chains.append(i)
    return chains,chains_for_des

def generate_des(chains_for_des):
    start_time = time.time()
    print('Generate descriptions...')
    des = []
    count = 1
    pre = "I give you a series of actions to perform, can you help me generate a description of them in a more human way and tell me the possible malicious consequences of those actions? They are: "
    tail = ''
    for chain in chains_for_des:
        tail = tail + chain[0] + ', ' + chain[1] + '. '
    input = pre + tail
    while count > 0:
        count = count - 1
        response = dashscope.Generation.call(
            model=dashscope.Generation.Models.qwen_turbo,
            prompt=input
        )
        # The response status_code is HTTPStatus.OK indicate success,
        # otherwise indicate request is failed, you can get error code
        # and message from code and message.
        if response.status_code == HTTPStatus.OK:
            print(response.output)  # The output text
            des.append(response.output.text)
            print(response.usage)  # The usage information
        else:
            print(response.code)  # The error code.
            print(response.message)  # The error message.

    end_time = time.time()
    print('Time of Generate descriptions：', str(end_time - start_time))

    return des


def write_apk_record(behavs, graph, chains, des, apkname):
    """
    write the generated des into database
    """
    try:
        q = models.ApkRecord.objects.get(apkName=apkname)
        # models.ApkRecord.objects.filter(apkName=apkname).update(behaviors=behavs, graph=graph, chains=chains,
        #                                                         des1=des[0], des2=des[1], des3=des[2])
        models.ApkRecord.objects.filter(apkName=apkname).update(behaviors=behavs, graph=graph, chains=chains,
                                                                des1=des[0])
    except:
        # models.ApkRecord.objects.create(apkName=apkname, behaviors=behavs, graph=graph, chains=chains, des1=des[0],
        #                                 des2=des[1], des3=des[2])
        models.ApkRecord.objects.create(apkName=apkname, behaviors=behavs, graph=graph, chains=chains, des1=des[0])


def write_apk_record_plus(behavs, graph, chains, apkname):
    try:
        q = models.ApkRecord.objects.get(apkName=apkname)
        models.ApkRecord.objects.filter(apkName=apkname).update(behaviors=behavs, graph=graph, chains=chains)
    except:
        models.ApkRecord.objects.create(apkName=apkname, behaviors=behavs, graph=graph, chains=chains)


def match_relations_kgAndCfg(match_nodes, relations_graph):
    all_path, path_malicious, path_others = find_relations_all(match_nodes)
    kg_relations_list = []
    cfg_relations_list = []
    delete_cfg_list = []
    final_behaviors=[]
    for one in all_path:
        kg_relations_list.append(one['semantics'])
    for key, value in relations_graph.items():
        # if key not in final_behaviors:
        #     final_behaviors.append(key)
        for one in value:
            tmp = [key, one]
            cfg_relations_list.append(tmp)
            if tmp in kg_relations_list:
                pass
            else:
                delete_cfg_list.append(tmp)
            # if one not in final_behaviors:
            #     final_behaviors.append(one)

    delete_kg_list = []
    final_graph = {}
    for one in all_path:
        path = one['semantics']
        if path[0] in relations_graph and path[1] in relations_graph[path[0]]:
            if path[0] not in final_graph:
                if path[1] in final_graph and path[0] in final_graph[path[1]]:
                    pass
                else:
                    final_graph[path[0]] = [path[1]]
            elif path[1] not in final_graph[path[0]]:
                final_graph[path[0]].append(path[1])
        else:
            delete_kg_list.append(path)

    for key, value in final_graph.items():
        if key not in final_behaviors:
            final_behaviors.append(key)
        for one in value:
            if one not in final_behaviors:
                final_behaviors.append(one)

    return kg_relations_list, cfg_relations_list, delete_kg_list, delete_cfg_list, final_graph,final_behaviors


def only_match_relations_on_KG(match_nodes):
    relations_kg = {}
    all_path, path_malicious, path_others = find_relations_all(match_nodes)
    for one in all_path:
        key = one['semantics'][0]
        val = one['semantics'][1]
        if key not in relations_kg:
            relations_kg[key] = [val]
        elif val not in relations_kg[key]:
            relations_kg[key].append(val)

    return relations_kg
