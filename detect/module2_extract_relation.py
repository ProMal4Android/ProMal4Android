"""
This is the second module to analyse an apk.
We will:
Output the relations between behaviors based on identified behaviors.

2023-11-10
"""
import re
import copy

import django
import sys
import os
from exper.program_analysis import forwardDataflow_plus, search_graph_back, generate_subgraph_forw_plus, key_sort_group, \
    list_print, \
    find_relation_in_CFG_father_plus, backwardDataflow_plus, compare_hexadecimal
from tools.utils import get_data, analyse, get_location_name, create_cfg_specific, do_file_name

sys.path.append('../')

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'mwep.settings')

django.setup()

from common import models


def main_module2_relations(match_nodes, apkname, f, p1, p2, p3, p4, p5, all_path):
    global kg_apis, kg_permissions, kg_features, apis_from_test, kg
    kg_apis = p1
    kg_permissions = p2
    kg_features = p3
    apis_from_test = p4
    kg = p5

    data = get_data(os.path.join('../detect/outputCG/', apkname + '.txt'))
    node_list, edge_list = analyse(data)
    data = data.replace("\n", '')

    step1_find_trace_path(match_nodes, apkname)
    # print('\n***Data***')
    # for node in match_nodes:
    #     print(node,'\n')
    # print('****')

    """
    new method
    """
    relations_graph = {}
    relations_id_list=[]
    case1_1, case1_2, case1_3, case2_1, case2_2 = step2_find_possible_path_plus(match_nodes, data, node_list,
                                                                                apkname,all_path)
    relations_graph = step3_find_relations_special(case1_1, apkname, node_list, relations_graph)
    # print('\n1-1行为都以api表示并出现在同一个函数中:')
    # dict_print(relations_graph)
    # print('\n************\n')
    relations_graph = step3_find_relations_plus(case1_2, apkname, node_list, relations_graph)
    # print('\n1-2行为都以函数表示并且有相同的父函数:')
    # dict_print(relations_graph)
    # print('\n************\n')
    relations_graph = step3_find_relations_plus(case1_3, apkname, node_list, relations_graph)
    # print('\n1-3行为以函数或api表示,通过溯源找到相同的父函数:')
    # dict_print(relations_graph)
    # print('\n************\n')
    direct_relations, relations_graph = step3_find_relations_direct(case2_1, case2_2, match_nodes, relations_graph)
    # print('\n2行为通过线程关联:')
    # dict_print(relations_graph)
    # print('\n************\n')

    """
      old method
    """
    # has_common, source_target, thread_direct, special_node = step2_find_possible_path(match_nodes, data, node_list,
    #                                                                                        apkname)
    # relations1 = step3_find_relations_common(has_common, data, node_list, match_nodes, apkname,all_path)
    # print('relations1:')
    # list_print(relations1)
    # relations2 = step3_find_relations_source2target(source_target, match_nodes,all_path)
    # print('relation2:')
    # list_print(relations2)
    # relations3 = step3_find_relations_thread(thread_direct, match_nodes)
    # print('relation3:', relations3)
    # step_final_output_relations(relations1, relations2, relations3)

    return relations_graph


def dict_print(dict):
    for key, value in dict.items():
        print(key, ' : ', value)


def get_leaf_root(subgraph):
    targets = []
    root = []
    leaf = []
    keys = list(subgraph.keys())
    for key, value in subgraph.items():
        if len(value) < 1:
            leaf.append(key)
        elif not list(set(value).intersection(set(keys))):
            leaf = leaf + value
            targets = targets + value
        else:
            targets = targets + value

    if targets:
        root = list(set(keys).difference(targets))

    return root, leaf


def step1_find_trace_path(match_nodes, apkname):
    """
    find forwarding nodes and paths of every node
    """
    data = get_data(os.path.join('../detect/outputCG/', apkname + '.txt'))
    node_list, edge_list = analyse(data)
    data = data.replace("\n", '')
    for node in match_nodes:
        # print(node)
        cgID = node['cgID']
        # class_location, class_name, method_name, filename = do_file_name(node_list[cgID])
        # if method_name=='doInBackground':  # android.os.AsyncTask
        #     find_str='L'+class_location+'/'+class_name+';->execute'
        #     find_str = find_str.replace('$', '\$')
        #     pattern = re.compile('id\s(\d+)\s\s\s\slabel\s"' + find_str, re.S)
        #     ans = pattern.findall(data)
        #     if ans:
        #         cgID=int(ans[0])

        forward_graph = generate_subgraph_forw_plus(cgID, apkname, {}, data, node_list, 0)

        # print('forward_graph:',forward_graph)
        root, leaf = get_leaf_root(forward_graph)
        all_path = []
        if root and leaf:
            for r in root:
                for l in leaf:
                    query_ret = search_graph_back(forward_graph, r, l)
                    if not query_ret:
                        # print('Sorry, no this path...')
                        pass
                    else:
                        for i in query_ret:
                            all_path.append(i)
                            # print(i)
        node['forw_graph'] = forward_graph
        node['forw_path'] = all_path


def step2_find_possible_path(match_nodes, cg_data, node_list, apkname):
    """
    find relations based on their trace path
    """
    # Group by the same nodes
    groups = []
    action_group = key_sort_group(match_nodes, 'actionName')
    for key, val in action_group.items():
        # print(key + ':')
        # for one in val:
        #     print(str(one['cgID']) + ':' + str(one['forw_path']))
        groups.append(val)
    print('***groups***')
    list_print(groups)
    has_common = []
    special_node = []
    source_target = []
    thread_direct = []
    for index, cut_group in enumerate(groups):
        flag = 0
        if index < len(groups) - 1:
            for cut_one in cut_group:
                # print('\ncut one:',cut_one)
                cut_cg_id = cut_one['cgID']
                cut_forw_path = cut_one['forw_path']
                for nex_group in groups[index + 1:]:
                    for nex_one in nex_group:
                        # print('nex one:', nex_one)
                        nex_cg_id = nex_one['cgID']
                        nex_forw_path = nex_one['forw_path']
                        # Case 1.1: Behaviors are represented by apis
                        if cut_cg_id == nex_cg_id:
                            tmp = {'cgID': cut_cg_id,
                                   'kgNode1': {'kgID': cut_one['nodeID'], 'actionName': cut_one['actionName']},
                                   'kgNode2': {'kgID': nex_one['nodeID'], 'actionName': nex_one['actionName']}}
                            if tmp not in special_node:
                                special_node.append(tmp)
                            break
                        if not cut_forw_path and not nex_forw_path:
                            continue
                        if not cut_forw_path and nex_forw_path:
                            for p in nex_forw_path:
                                if cut_cg_id in p and len(p) > 1:
                                    # print('cut cg id:',cut_cg_id)
                                    # print('p:', p)
                                    location = p.index(cut_cg_id)
                                    path = p[:location + 1]
                                    # print('path:', path)
                                    path.reverse()
                                    if path not in source_target:
                                        source_target.append(path)
                                    flag = 1
                                    break
                        if cut_forw_path and not nex_forw_path:
                            for p in cut_forw_path:
                                if nex_cg_id in p and len(p) > 1:
                                    # print('nex cg id:', nex_cg_id)
                                    # print('p:', p)
                                    location = p.index(nex_cg_id)
                                    path = p[:location + 1]
                                    path.reverse()
                                    # print('path:', path)
                                    if path not in source_target:
                                        source_target.append(path)
                                    flag = 1
                                    break
                        if cut_forw_path and nex_forw_path:
                            for cutp in cut_forw_path:
                                for nexp in nex_forw_path:
                                    common = list(set(cutp).intersection(set(nexp)))
                                    if len(common) > 0:
                                        # print('common:', common)
                                        common.sort()
                                        common_cg_id = common[0]
                                        location1 = cutp.index(common_cg_id)
                                        location2 = nexp.index(common_cg_id)
                                        obj = {'p1': cutp[:location1 + 1], 'p2': nexp[:location2 + 1],
                                               'common': common_cg_id, 'kgNode1': cut_one['nodeID'],
                                               'kgNode2': nex_one['nodeID']}
                                        add = 1
                                        for tt in has_common:
                                            if obj['p1'][0] == tt['p1'][0] and obj['p2'][0] == tt['p2'][0]:
                                                add = 0
                                                break
                                        if add == 1:
                                            tmp = []
                                            if common_cg_id == obj['p1'][0] or obj['p1'][0] in obj['p2'][1:]:
                                                tmp.append(obj['p1'][0])
                                                tmp.append(obj['p2'][0])
                                                if tmp not in source_target:
                                                    source_target.append(tmp)
                                            elif common_cg_id == obj['p2'][0] or obj['p2'][0] in obj['p1'][1:]:
                                                tmp.append(obj['p2'][0])
                                                tmp.append(obj['p1'][0])
                                                if tmp not in source_target:
                                                    source_target.append(tmp)
                                            else:
                                                has_common.append(obj)
                                        flag = 1
                                        break
                                    else:
                                        # related to multiple threads
                                        # print('cut p:', cutp)
                                        # print('nex label:', nexp)
                                        for cutnode in cutp[1:]:
                                            cutlabel = re.findall('label\s"(.*?)\(', node_list[cutnode])[0]
                                            for nexnode in nexp[1:]:
                                                nexlabel = re.findall('label\s"(.*?)\(', node_list[nexnode])[0]
                                                # if cutlabel.find('$') != -1 and nexlabel.find('$') != -1:
                                                if re.findall('(.*?)\$\d', cutlabel) and re.findall('(.*?)\$\d',
                                                                                                    nexlabel) and cutlabel.endswith(
                                                    'run') and nexlabel.endswith('run'):
                                                    cutindex = cutlabel.find('$')
                                                    nexindex = nexlabel.find('$')
                                                    if cutlabel[0:cutindex] == nexlabel[0:nexindex]:
                                                        # print('cut label:', cutlabel)
                                                        # print('nex label:', nexlabel)
                                                        # length of ';->run' is 6
                                                        cutlist = cutlabel[:-6].split('$')
                                                        nexlist = nexlabel[:-6].split('$')
                                                        if cutlist[1] == nexlist[1]:
                                                            # print('cutlist:',cutlist)
                                                            # print('nexlist:', nexlist)
                                                            if len(cutlist) > len(nexlist):
                                                                # current node is in the child thread
                                                                tmp = []
                                                                tmp.append(nex_cg_id)
                                                                tmp.append(cut_cg_id)
                                                                if tmp not in thread_direct:
                                                                    thread_direct.append(tmp)
                                                                flag = 1
                                                                break
                                                            elif len(cutlist) < len(nexlist):
                                                                # next node is in the child thread
                                                                tmp = []
                                                                tmp.append(cut_cg_id)
                                                                tmp.append(nex_cg_id)
                                                                if tmp not in thread_direct:
                                                                    thread_direct.append(tmp)
                                                                flag = 1
                                                                break
                                                            else:
                                                                # the same lengths
                                                                if cutlist[-1] > nexlist[-1]:
                                                                    tmp = []
                                                                    tmp.append(nexnode)
                                                                    tmp.append(cutnode)
                                                                    if tmp not in thread_direct:
                                                                        thread_direct.append(tmp)
                                                                    flag = 1
                                                                    break
                                                                elif cutlist[-1] < nexlist[-1]:
                                                                    # next node is in the child thread
                                                                    tmp = []
                                                                    tmp.append(cutnode)
                                                                    tmp.append(nexnode)
                                                                    if tmp not in thread_direct:
                                                                        thread_direct.append(tmp)
                                                                    flag = 1
                                                                    break
                                                                else:
                                                                    print('error')

                                        pass
                            # break

        # if flag == 0:
        #     for one in cut_group:
        #         cut_cg_id = one['cgID']
        #         label = re.findall('label\s"(.*?)\(', node_list[cut_cg_id])[0]
        #         backwards, bklist = backwardDataflow_plus(cut_cg_id, cg_data, node_list)
        #         for bk in backwards:
        #             if bk['label'].find('java/lang/Thread;-><init>') != -1:
        #                 # print('cut cg id:', cut_cg_id)
        #                 isThread, newlabel = judge_contain_thread(cut_cg_id, label, cg_data, node_list, apkname)
        #                 if isThread == 1:
        #                     # print('new label:', newlabel)
        #                     newlabel = newlabel.replace('$', '\$')
        #                     pattern = re.compile('id\s(\d+)\s\s\s\slabel\s"' + newlabel + '\(', re.S)
        #                     ans = pattern.findall(cg_data)
        #                     # print('ans:', ans)
        #                     if ans:
        #                         # print('ans 0:', ans[0])
        #                         backwards, bklist = backwardDataflow_plus(int(ans[0]), cg_data, node_list)
        #                         # print('bklist:', bklist)
        #                         for bk in bklist:
        #                             for mnode in match_nodes:
        #                                 if mnode['cgID'] == bk:
        #                                     # print('mnnode:', bk)
        #                                     tmp = []
        #                                     tmp.append(cut_cg_id)
        #                                     tmp.append(bk)
        #                                     # print('newnenwenw:', tmp)
        #                                     if tmp not in thread_direct:
        #                                         thread_direct.append(tmp)
        #                                     break
        #                 break

    print('common:')
    list_print(has_common)
    print('\n')
    print('source 2 target:')
    list_print(source_target)
    print('thread direct:')
    list_print(thread_direct)
    print('special node:')
    list_print(special_node)
    return has_common, source_target, thread_direct, special_node


def step2_find_possible_path_plus(match_nodes, cg_data, node_list, apkname,all_path):
    """
    find relations based on their trace path
    """
    # Group by the same nodes
    groups = []
    action_group = key_sort_group(match_nodes, 'actionName')
    for key, val in action_group.items():
        groups.append(val)
    print('***groups***')
    list_print(groups)

    case1_1 = []
    case1_2 = []
    case1_3 = []
    case2_1 = []
    case2_2 = []
    has_record = []  # Avoid duplicate lookups
    for index, cut_group in enumerate(groups):
        findre = 0
        if index < len(groups) - 1:
            for cut_one in cut_group:
                cut_cg_id = cut_one['cgID']
                cut_forw_path = cut_one['forw_path']
                for nex_group in groups[index + 1:]:
                    flag = 0
                    has_related=0
                    for apath in all_path:
                        semantics=apath['semantics']
                        if cut_group[0]['actionName'] in semantics and nex_group[0]['actionName'] in semantics:
                            has_related = 1
                            break
                    if has_related==1:
                        print('\n*******\ncut action:', cut_group[0]['actionName'])
                        print('nex action:', nex_group[0]['actionName'])
                        for nex_one in nex_group:
                            # print('nex one:', nex_one)
                            nex_cg_id = nex_one['cgID']
                            nex_forw_path = nex_one['forw_path']
                            # Case 1.1: Behaviors are represented by apis
                            if cut_cg_id == nex_cg_id:
                                tmp = {'cgID': cut_cg_id,
                                       'kgNode1': {'kgID': cut_one['nodeID'], 'actionName': cut_one['actionName'],
                                                   'flag': 'api'},
                                       'kgNode2': {'kgID': nex_one['nodeID'], 'actionName': nex_one['actionName'],
                                                   'flag': 'api'}}
                                records = [cut_one['nodeID'], nex_one['nodeID']]
                                if records not in has_record and tmp not in case1_1:
                                    case1_1.append(tmp)
                                    has_record.append(records)
                                # if tmp not in case1_1:
                                #     case1_1.append(tmp)
                                # break. Starts matching the current action with the next action
                                flag = 1
                                break
                            else:
                                # Case 1.3: One behavior node is the parent of another behavior node
                                for p in nex_forw_path:
                                    if len(p) > 1 and cut_cg_id in p[1:]:
                                        print('cut cg id:', cut_cg_id)
                                        print('p:', p)
                                        location = p.index(cut_cg_id)
                                        path = p[:location + 1]
                                        # print('path:', path)
                                        tmp = {'cgID': cut_cg_id,
                                               'kgNode1': {'kgID': nex_one['nodeID'], 'actionName': nex_one['actionName'],
                                                           'cgID': path[-2], 'flag': 'fun'},
                                               'kgNode2': {'kgID': cut_one['nodeID'], 'actionName': cut_one['actionName'],
                                                           'cgID': cut_cg_id, 'flag': 'api'}}
                                        records = [nex_one['nodeID'], cut_one['nodeID']]
                                        if records not in has_record and tmp not in case1_3:
                                            case1_3.append(tmp)
                                            has_record.append(records)
                                        # if tmp not in case1_3:
                                        #     case1_3.append(tmp)
                                        flag = 1
                                        break
                                # if flag==1:
                                #     break
                                for p in cut_forw_path:
                                    if len(p) > 1 and nex_cg_id in p[1:]:
                                        print('nex cg id:', nex_cg_id)
                                        print('p:', p)
                                        location = p.index(nex_cg_id)
                                        path = p[:location + 1]
                                        tmp = {'cgID': nex_cg_id,
                                               'kgNode1': {'kgID': nex_one['nodeID'], 'actionName': nex_one['actionName'],
                                                           'cgID': nex_cg_id, 'flag': 'api'},
                                               'kgNode2': {'kgID': cut_one['nodeID'], 'actionName': cut_one['actionName'],
                                                           'cgID': path[-2], 'flag': 'fun'}}
                                        records = [nex_one['nodeID'], cut_one['nodeID']]
                                        if records not in has_record and tmp not in case1_3:
                                            case1_3.append(tmp)
                                            has_record.append(records)
                                        # if tmp not in case1_3:
                                        #     case1_3.append(tmp)
                                        flag = 1
                                        break
                                # if flag==1:
                                #     break
                                if flag == 0:
                                    for cutp in cut_forw_path:
                                        for nexp in nex_forw_path:
                                            common = list(set(cutp[1:]).intersection(set(nexp[1:])))
                                            # Case 1.2: Behavior is expressed as a function
                                            if len(common) > 0:
                                                print('\ncommon:', common)
                                                common_cg_id = common[0]
                                                for common_one in common:
                                                    if cutp.index(common_one) < cutp.index(common_cg_id):
                                                        common_cg_id = common_one
                                                location_cut = cutp.index(common_cg_id)
                                                location_nex = nexp.index(common_cg_id)
                                                print(cut_forw_path)
                                                print(nex_forw_path)
                                                print(cutp)
                                                print(nexp)
                                                obj = {'cgID': common_cg_id,
                                                       'kgNode1': {'kgID': nex_one['nodeID'],
                                                                   'actionName': nex_one['actionName'],
                                                                   'cgID': nexp[location_nex - 1], 'flag': 'fun'},
                                                       'kgNode2': {'kgID': cut_one['nodeID'],
                                                                   'actionName': cut_one['actionName'],
                                                                   'cgID': cutp[location_cut - 1], 'flag': 'fun'}}
                                                # Avoid duplicate additions
                                                # print(obj, '\n')
                                                records = [nex_one['nodeID'], cut_one['nodeID']]
                                                if records not in has_record and obj not in case1_2:
                                                    case1_2.append(obj)
                                                    has_record.append(records)
                                                # if obj not in case1_2:
                                                #     case1_2.append(obj)
                                                flag = 1
                                                break
                                            # elif flag == 1:
                                            #     break  # Exit the match between the current action and the next action
                                            else:
                                                # related to multiple threads
                                                # Case 2.1: The thread is determined by the node name
                                                for cutnode in cutp[1:]:
                                                    cutlabel = re.findall('label\s"(.*?)\(', node_list[cutnode])[0]
                                                    for nexnode in nexp[1:]:
                                                        nexlabel = re.findall('label\s"(.*?)\(', node_list[nexnode])[0]
                                                        # if cutlabel.find('$') != -1 and nexlabel.find('$') != -1:
                                                        if re.findall('(.*?)\$\d', cutlabel) and re.findall('(.*?)\$\d',
                                                                                                            nexlabel) and \
                                                                cutlabel.split('$')[0] == nexlabel.split('$')[
                                                            0] and cutlabel.endswith('run') and nexlabel.endswith('run'):
                                                            # pass
                                                            # if re.findall('(.*?)\$\d', cutlabel) and re.findall('(.*?)\$\d',nexlabel) and cutlabel.endswith('run') and nexlabel.endswith('run'):
                                                            print('\ncut p:', cutp)
                                                            print('nex p:', nexp)
                                                            print('cut label:', cutlabel)
                                                            print('nex label:', nexlabel, '\n')
                                                            # length of ';->run' is 6
                                                            cutlist = cutlabel[:-6].split('$')[1:]
                                                            nexlist = nexlabel[:-6].split('$')[1:]
                                                            source = -1
                                                            target = -1
                                                            judge_len = len(cutlist) if len(cutlist) > len(
                                                                nexlist) else len(nexlist)
                                                            while len(cutlist) < judge_len:
                                                                cutlist.append('0')
                                                            while len(nexlist) < judge_len:
                                                                nexlist.append('0')
                                                            index = 0
                                                            while judge_len > 0:
                                                                if cutlist[index] == nexlist[index]:
                                                                    index = index + 1
                                                                else:
                                                                    if cutlist[index].isdigit() and nexlist[index].isdigit():
                                                                        if int(cutlist[index]) > int(nexlist[index]):
                                                                            source = nex_cg_id
                                                                            target = cut_cg_id
                                                                        else:
                                                                            source = cut_cg_id
                                                                            target = nex_cg_id
                                                                    else:
                                                                        break
                                                                judge_len = judge_len - 1

                                                            if source != -1 and target != -1:
                                                                tmp = [source, target]
                                                                # tmp.append(source)
                                                                # tmp.append(target)
                                                                # records1 = [nex_one['nodeID'], cut_one['nodeID']]
                                                                # if records not in has_record and obj not in case1_2:
                                                                #     case1_2.append(obj)
                                                                #     has_record.append(records)
                                                                if tmp not in case2_1:
                                                                    case2_1.append(tmp)
                                                                flag = 1
                                                        elif re.findall('(.*?)\$\d', cutlabel) and cutlabel.endswith('run') and nexlabel.endswith('onReceive') and cutlabel.split('$')[0] == nexlabel.split(';')[0] or re.findall('(.*?)\$\d', nexlabel) and nexlabel.endswith('run') and cutlabel.endswith('onReceive') and cutlabel.split(';')[0] == nexlabel.split('$')[0]:
                                                            source = -1
                                                            target = -1
                                                            if cutlabel.find('$')!=-1:
                                                                source=nex_cg_id
                                                                target=cut_cg_id
                                                            elif nexlabel.find('$')!=-1:
                                                                source = cut_cg_id
                                                                target = nex_cg_id
                                                            if source != -1 and target != -1:
                                                                tmp = [source, target]
                                                                if tmp not in case2_1:
                                                                    case2_1.append(tmp)
                                                                flag = 1
                                                pass
                                    # break
                        if flag == 1:
                            findre = 1

        if findre == 0:
            for one in cut_group:
                cut_cg_id = one['cgID']
                label = re.findall('label\s"(.*?)\(', node_list[cut_cg_id])[0]
                backwards, bklist = backwardDataflow_plus(cut_cg_id, cg_data, node_list)
                for bk in backwards:
                    if bk['label'].find('java/lang/Thread;-><init>') != -1:
                        # print('\ncut cg id:', cut_cg_id)
                        isThread, newlabel = judge_contain_thread(cut_cg_id, cg_data, node_list, apkname)
                        if isThread == 1:
                            print('new label:', newlabel)
                            newlabel = newlabel.replace('$', '\$')
                            pattern = re.compile('id\s(\d+)\s\s\s\slabel\s"' + newlabel + '\(', re.S)
                            ans = pattern.findall(cg_data)
                            if ans:
                                # print('ans 0:', ans[0])
                                backwards, bklist = backwardDataflow_plus(int(ans[0]), cg_data, node_list)
                                # print('bklist:', bklist)
                                for bk in bklist:
                                    for mnode in match_nodes:
                                        if mnode['cgID'] == bk:
                                            # print('mnnode:', bk)
                                            tmp = [cut_cg_id, bk]
                                            # print('newnenwenw:', tmp)
                                            if tmp not in case2_2:
                                                case2_2.append(tmp)
                                            break
                        break

    print('\ncase1_1:')
    list_print(case1_1)
    print('\ncase1_2:')
    list_print(case1_2)
    print('\ncase1_3:')
    list_print(case1_3)
    print('\ncase2_1:')
    list_print(case2_1)
    print('\ncase2_2:')
    list_print(case2_2)
    return case1_1, case1_2, case1_3, case2_1, case2_2


# cg_data = get_data(os.path.join('../detect/outputCG/', 'bc28a09c5f2b11b626e5d2bd398418c14465dde1' + '.txt'))
# node_list, edge_list = analyse(cg_data)
# cg_data = cg_data.replace("\n", '')
# # print('data:',cg_data)
# label='Lcom/xLMcS0/V3y0PM/MainActivity;->startService'
# pattern=re.compile('id\s(\d+)\s\s\s\slabel\s"'+label+'\(',re.S)
# ans=pattern.findall(cg_data)
# print('\nans:',ans)


def query_behav(cgID, match_nodes):
    actionName = ''
    kgNode = -1
    for one in match_nodes:
        if one['cgID'] == cgID:
            actionName = one['actionName']
            kgNode = one['nodeID']
            break
    return actionName, kgNode


def judge_contain_thread(cgid, data, node_list, apkname):
    flag = 0
    new_label = ''
    backwards, bkid = backwardDataflow_plus(cgid, data, node_list)
    for one in backwards:
        if one['label'].find('java/lang/Thread;-><init>') != -1:
            flag = 1
            # create cfg
            cfg_file = create_cfg_specific(apkname, node_list[cgid])
            # *** Read and Sort ***
            if cfg_file is not None:
                if os.path.exists(cfg_file):
                    cfg_file = open(cfg_file, 'r', encoding='utf-8', newline="")
                    begin = 0
                    for row in cfg_file.readlines():
                        if row.find('java/lang/Thread;') != -1:
                            begin = 1
                        if begin == 1:
                            thread = re.findall('L.*?\$\d+;-><init>', row)
                            if thread:
                                thread = thread[0]
                                new_label = thread[0:len(thread) - 6] + 'run'
                                break
            break
    return flag, new_label


def step3_find_relations_common(has_common, cg_data, node_list, match_nodes, apkname, all_path):
    relations = []
    for one in has_common:
        # kg_set=[]
        # kg_set.append(one['kgNode1'])
        # kg_set.append(one['kgNode2'])
        # print('kg set:',kg_set)
        # for path in all_path:
        #     if len(list(set(path['path']).intersection(set(kg_set))))==2:
        ret = []
        label = re.findall('label\s"(.*?)\(', node_list[one['common']])[0]
        if label.endswith("run"):
            # related to thread
            id1 = one['p1'][0]
            label1 = re.findall('label\s"(.*?)\(', node_list[id1])[0]
            id2 = one['p2'][0]
            label2 = re.findall('label\s"(.*?)\(', node_list[id2])[0]
            flag1, l1 = judge_contain_thread(id1, cg_data, node_list, apkname)
            flag2, l2 = judge_contain_thread(id2, cg_data, node_list, apkname)
            if flag1 > flag2:
                # node1 is the primary thread
                forwards, fwid = forwardDataflow_plus(id2, cg_data, node_list)
                for fw in forwards:
                    if fw['label'].find(l1)!=-1:
                        # print(str(id1) + '->' + str(id2))
                        ret1 = query_behav(id1, match_nodes)
                        ret2 = query_behav(id2, match_nodes)
                        ret.append({'cg_node': id1, 'kg_node': ret1[1], 'actionName': ret1[0]})
                        ret.append({'cg_node': id2, 'kg_node': ret2[1], 'actionName': ret2[0]})
                        relations.append(ret)
                        break
            elif flag1 < flag2:
                # node2 is the primary thread
                forwards, fwid = forwardDataflow_plus(id1, cg_data, node_list)
                for fw in forwards:
                    if fw['label'].find(l2)!=-1:
                        # print(str(id2) + '->' + str(id1))
                        ret1 = query_behav(id1, match_nodes)
                        ret2 = query_behav(id2, match_nodes)
                        ret.append({'cg_node': id2, 'kg_node': ret2[1], 'actionName': ret2[0]})
                        ret.append({'cg_node': id1, 'kg_node': ret1[1], 'actionName': ret1[0]})
                        relations.append(ret)
                        break
            elif flag1 == 0 and flag2 == 0:
                # find relations in CFG
                one['action1'], one['kg_node1'] = query_behav(one['p1'][0], match_nodes)
                one['action2'], one['kg_node2'] = query_behav(one['p2'][0], match_nodes)
                ret = find_relation_in_CFG_father_plus(one, apkname, node_list)
                if ret:  # can directly find relation
                    relations.append(ret)
        else:
            # find relations in CFG
            one['action1'], one['kg_node1'] = query_behav(one['p1'][0], match_nodes)
            one['action2'], one['kg_node2'] = query_behav(one['p2'][0], match_nodes)
            ret = find_relation_in_CFG_father_plus(one, apkname, node_list)
            if ret:  # can directly find relation
                relations.append(ret)
        # break

    return relations


# has_comone = [{'p1': [3223, 3255], 'p2': [3122, 3255], 'common': 3255}]
# apkname = 'bc28a09c5f2b11b626e5d2bd398418c14465dde1'
# step3_find_relations_common(has_comone, apkname, [])

def step3_find_relations_thread(thread_direct, match_nodes):
    relations = []
    for one in thread_direct:
        tmp = []
        for nodeid in one:
            if query_behav(nodeid, match_nodes):
                query_ret = query_behav(nodeid, match_nodes)
                tmp.append({'cg_node': nodeid, 'kg_node': query_ret[1], 'actionName': query_ret[0]})
        if len(tmp) > 0:
            relations.append(tmp)

    return relations


def step3_find_relations_source2target(source_target, match_nodes, all_path):
    relations = []
    for one in source_target:
        # kg_set = []
        # kg_set.append(query_behav(one[0],match_nodes)[1])
        # kg_set.append(query_behav(one[-1],match_nodes)[1])
        # for path in all_path:
        #     if len(list(set(path['path']).intersection(set(kg_set)))) == 2:
        tmp = []
        for nodeid in one:
            if query_behav(nodeid, match_nodes)[1] != -1:
                query_ret = query_behav(nodeid, match_nodes)
                tmp.append({'cg_node': nodeid, 'kg_node': query_ret[1], 'actionName': query_ret[0]})
        if len(tmp) > 1:
            relations.append(tmp)

    return relations


def step3_find_relations_direct(case2_1, case2_2, match_nodes, relations_graph):
    relations = []
    source_target = case2_1 + case2_2
    for one in source_target:
        tmp = []
        for nodeid in one:
            if query_behav(nodeid, match_nodes)[1] != -1:
                query_ret = query_behav(nodeid, match_nodes)
                tmp.append({'cg_node': nodeid, 'kg_node': query_ret[1], 'actionName': query_ret[0]})
        if len(tmp) > 1:
            relations.append(tmp)
            for index, value in enumerate(tmp):
                if index < len(tmp) - 1:
                    source = value['actionName']
                    target = tmp[index + 1]['actionName']
                    if source not in relations_graph:
                        relations_graph[source] = [target]
                    else:
                        if target not in relations_graph[source]:
                            relations_graph[source].append(target)

    return relations, relations_graph


def parse_cfg_file(cfg_file, method_name):
    """
    Split the contents of the cfg file into blocks and find the relationships between blocks
    """
    cfg_block = {}
    cfg_relations_graph = {}
    if os.path.exists(cfg_file):
        cfg_file = open(cfg_file, 'r', encoding='utf-8', newline="")
        tmp_block = []
        bbindex = ''
        for row in cfg_file.readlines():
            if row in ['\n', '\r\n']:
                if len(tmp_block) > 0 and bbindex:
                    cfg_block[bbindex] = tmp_block
                tmp_block = []
            elif not row.startswith('#'):
                if row.startswith(method_name):
                    bbindex = re.findall('-(BB@.*?)\s:', row)[0]
                    indicates = []
                    if re.findall('\[\s(.*?)\s]', row):
                        tmp_indicates = re.findall('\[\s(.*?)\s]', row)[0]
                        tmp_indicates = tmp_indicates.replace(method_name, '').replace('-', '').split(' ')
                        """
                        some times, the row may be "onReceive-BB@0x5c : [ D:onReceive-BB@0x6a 1:onReceive-BB@0x6c 2:onReceive-BB@0x6a ]"
                        """
                        for tt in tmp_indicates:
                            indicates.append(tt.split(':')[-1])
                    if bbindex in cfg_relations_graph:
                        cfg_relations_graph[bbindex] = cfg_relations_graph[bbindex] + indicates
                    else:
                        cfg_relations_graph[bbindex] = indicates

                else:
                    row = row.replace('\t', '').replace('\n', '')
                    tmp_block.append(row)

    return cfg_block, cfg_relations_graph


# apk_name = '2-beauty'
# data = get_data(os.path.join('../detect/outputCG/', apk_name + '.txt'))
# node_list, edge_list = analyse(data)
# new_file=create_cfg_specific(apk_name,node_list[3569])


# ret = search_graph_back(cfg_relations_graph, 'BB@0x0', 'BB@0x86')
# print('ret:', ret)


def output_common_substr(str1, str2):
    # With the shorter string, the search is iterated from the longest to the shortest
    str1, str2 = (str2, str1) if len(str1) > len(str2) else (str1, str2)
    f = []
    for i in range(len(str1), 0, -1):
        for j in range(len(str1) + 1 - i):
            e = str1[j:j + i]
            if e in str2:
                f.append(e)

        if f:
            break
    return f


def find_relations_in_parse_cfg(objects_list, cfg_block, cfg_relations_graph, relation_graph):
    """
    More accurate relational search algorithm
    :param objects_list: a list stored the behaviors the criterion is api or function,
        objects_list={'read sms':{flag:''api',content:['query','getContentResolver']},'delete sms':{'flag':'api',content:['delete','getContentResolver']}}
        the value of flag is 'api' or 'fun'
    """
    objects_copy = copy.deepcopy(objects_list)
    locations = {}
    # step 1. locate
    for key, value in cfg_block.items():
        if len(locations) == len(objects_list):
            break
        else:
            value_str = ','.join(value)
            # print('\nvalue str:', value_str)
            for action, gq in objects_copy.items():
                if gq['flag'] == 'api':
                    apis = gq['content']
                    # print('apis:', apis)
                    apis_data = apis.copy()
                    for api in apis_data:
                        if value_str.find(api) != -1:
                            # print('one api:', api)
                            objects_copy[action]['content'].remove(api)
                        elif models.ApiSim.objects.filter(list__contains=api):
                            ans = models.ApiSim.objects.filter(list__contains=api)
                            for q in ans:
                                intersect = output_common_substr(q.list, value_str)
                                api_list = q.list.split(',')
                                for one in intersect:
                                    if one in api_list:
                                        objects_copy[action]['content'].remove(api)
                    if objects_copy[action]['content'] == [] and action not in locations:
                        locations[action] = key
                elif gq['flag'] == 'fun':
                    fun = gq['content'][0]
                    if value_str.find(fun) != -1:
                        if action not in locations:
                            locations[action] = key

    print('locations:', locations)
    # step 2. find relations
    actions_list = list(locations.keys())
    print('actions_list:', actions_list)
    relations_list=[]
    for index, cut in enumerate(actions_list):
        if index < len(actions_list) - 1:
            for nex in actions_list[index + 1:]:
                if locations[cut] == locations[nex]:
                    block = cfg_block[locations[cut]]
                    source, target = '', ''
                    for line in block:
                        if line.find(objects_list[cut]['content'][-1])!=-1:
                            source = cut
                            target = nex
                            break
                        if line.find(objects_list[nex]['content'][-1])!=-1:
                            source = nex
                            target = cut
                            break
                    print('\nsource->target:',source,'->',target)
                    relations_list.append([source,target])
                    if source not in relation_graph:
                        tmp = [target]
                        relation_graph[source] = tmp
                    else:
                        tmp = relation_graph[source]
                        if target not in tmp:
                            tmp.append(target)
                        relation_graph[source] = tmp
                else:
                    has=0
                    if search_graph_back(cfg_relations_graph, locations[cut], locations[nex]):
                        has=1
                        print('\nsource->target1:', cut, '->', nex)
                        relations_list.append([cut, nex])
                        if cut not in relation_graph:
                            # tmp = []
                            # tmp.append(nex)
                            relation_graph[cut] = [nex]
                        else:
                            if nex not in relation_graph[cut]:
                                tmp = relation_graph[cut]
                                if nex not in tmp:
                                    tmp.append(nex)
                                relation_graph[cut] = tmp
                    if search_graph_back(cfg_relations_graph, locations[nex], locations[cut]):
                        has=1
                        print('\nsource->target2:', locations[nex], '->', locations[cut])
                        relations_list.append([nex, cut])
                        if nex not in relation_graph:
                            # tmp = []
                            # tmp.append(cut)
                            relation_graph[nex] = [cut]
                        else:
                            if cut not in relation_graph[nex]:
                                relation_graph[nex].append(cut)
                    if has==0:
                        compare_ret=compare_hexadecimal(locations[cut],locations[nex])[1]
                        if compare_ret!=-1:
                            if compare_ret==1:
                                source=cut
                                target=nex
                            else:
                                source = nex
                                target = cut
                            print('\nsource->target3:', source,'->', target)
                            relations_list.append([source, target])
                            if source not in relation_graph:
                                tmp = [target]
                                relation_graph[source] = tmp
                            else:
                                tmp = relation_graph[source]
                                if target not in tmp:
                                    tmp.append(target)
                                relation_graph[source] = tmp

    print('relation_graph:', relation_graph,'\n')
    return relation_graph,relations_list


# cfg_file = '../detect/output_augment/processing/2-beauty/com/wyzf/receiver/ReceiveSmsReceiver/onReceive.txt'
# # cfg_file = '../detect/output_augment/processing/2-beauty/com/comment/one/a/a/a.txt'
# cfg_block, cfg_relations_graph = parse_cfg_file(cfg_file, 'onReceive')
# print('cfg_block:', cfg_block)
# print('cfg_relations_graph:', cfg_relations_graph)
#
# object_list = {
#     'monitor sms': {'flag': 'api', 'content': ['android/content/Intent;->getAction', 'java/lang/String;->equals']},
#     'parse sms': {'flag': 'api', 'content': ['android/telephony/SmsMessage;->getOriginatingAddress',
#                                              'android/telephony/SmsMessage;->getMessageBody']},
#     'test': {'flag': 'fun', 'content': ['Lcom/wyzf/c/l;->c(Ljava/lang/Object;)Z']}}
#
# find_relations_in_parse_cfg(object_list, cfg_block, cfg_relations_graph)


def get_apis_str(kgID):
    ret = []
    apiList = models.augmentNodeIn.objects.get(nodeID=kgID).apiList
    apiList = apiList.split(',')
    for id in apiList:
        api = models.augmentAPiIn.objects.get(apiID=int(id)).apiName
        ret.append(api)

    return ret


def step3_find_relations_special(special_nodes, apkname, node_list, relation_graph):
    # first, group according to the same cg nodes
    action_group = key_sort_group(special_nodes, 'cgID')
    # relation_graph = {}
    for key, group in action_group.items():
        # print('\ngroup:', group)
        cgID = key
        method_name = re.findall('label\s.*?;->(.*?)\(', node_list[cgID])[0]
        cfg_file = create_cfg_specific(apkname, node_list[cgID])
        # cfg_file = '../detect/output_augment/processing/2-beauty/com/comment/one/a/a/a.txt'
        cfg_block, cfg_relations_graph = parse_cfg_file(cfg_file, method_name)
        # print('cfg_relations_graph:', cfg_relations_graph)
        object_list = {}
        for one in group:
            if one['kgNode1']['actionName'] not in object_list:
                apis_str_list = get_apis_str(one['kgNode1']['kgID'])
                tmp = {'flag': 'api', 'content': apis_str_list}
                object_list[one['kgNode1']['actionName']] = tmp
            if one['kgNode2']['actionName'] not in object_list:
                apis_str_list = get_apis_str(one['kgNode2']['kgID'])
                tmp = {'flag': 'api', 'content': apis_str_list}
                object_list[one['kgNode2']['actionName']] = tmp
        # print('object_list:', object_list)
        relation_graph,relations_list = find_relations_in_parse_cfg(object_list, cfg_block, cfg_relations_graph, relation_graph)

    return relation_graph


def step3_find_relations_plus(casedata, apkname, node_list, relation_graph):
    # first, group according to the same cg nodes
    action_group = key_sort_group(casedata, 'cgID')
    for key, group in action_group.items():
        print('\nstep3 group:', group)
        method_name = re.findall('label\s.*?;->(.*?)\(', node_list[key])[0]
        cfg_file = create_cfg_specific(apkname, node_list[key])
        cfg_block, cfg_relations_graph = parse_cfg_file(cfg_file, method_name)
        print('cfg_relations_graph:', cfg_relations_graph)
        object_list = {}
        for one in group:
            if one['kgNode1']['actionName'] not in object_list:
                tmp = {}
                if one['kgNode1']['flag'] == 'api':
                    apis_str_list = get_apis_str(one['kgNode1']['kgID'])
                    tmp = {'flag': 'api', 'content': apis_str_list}
                elif one['kgNode1']['flag'] == 'fun':
                    if re.findall('label\s"(.*?)\s\[', node_list[one['kgNode1']['cgID']]):
                        label = re.findall('label\s"(.*?)\s\[', node_list[one['kgNode1']['cgID']])[0]
                    else:
                        label = re.findall('label\s"(.*?)\)', node_list[one['kgNode1']['cgID']])[0]
                    tmp = {'flag': 'fun', 'content': [label]}
                object_list[one['kgNode1']['actionName']] = tmp

            if one['kgNode2']['actionName'] not in object_list:
                tmp = {}
                if one['kgNode2']['flag'] == 'api':
                    apis_str_list = get_apis_str(one['kgNode2']['kgID'])
                    tmp = {'flag': 'api', 'content': apis_str_list}
                elif one['kgNode2']['flag'] == 'fun':
                    if re.findall('label\s"(.*?)\s\[', node_list[int(one['kgNode2']['cgID'])]):
                        label = re.findall('label\s"(.*?)\s\[', node_list[int(one['kgNode2']['cgID'])])[0]
                    else:
                        label = re.findall('label\s"(.*?)\)', node_list[int(one['kgNode2']['cgID'])])[0]
                    tmp = {'flag': 'fun', 'content': [label]}
                object_list[one['kgNode2']['actionName']] = tmp
        print('object_list:', object_list)
        if len(object_list) >= 2:
            relation_graph,relations_list = find_relations_in_parse_cfg(object_list, cfg_block, cfg_relations_graph, relation_graph)

    return relation_graph


# apkname = '2-beauty'
# data = get_data(os.path.join('../detect/outputCG/', apkname + '.txt'))
# node_list, edge_list = analyse(data)
# special = [{'cgID': 4913, 'kgNode1': {'kgID': 1, 'actionName': 'Access the Internet'},
#             'kgNode2': {'kgID': 21, 'actionName': 'Get network info'}},
#            {'cgID': 10003, 'kgNode1': {'kgID': 1, 'actionName': 'Access the Internet'},
#             'kgNode2': {'kgID': 43, 'actionName': 'Get SD path'}},
#            {'cgID': 10095, 'kgNode1': {'kgID': 77, 'actionName': 'Check permissions'},
#             'kgNode2': {'kgID': 21, 'actionName': 'Get network info'}},
#            {'cgID': 10095, 'kgNode1': {'kgID': 77, 'actionName': 'Check permissions'},
#             'kgNode2': {'kgID': 138, 'actionName': 'Look for installed Apk'}},
#            {'cgID': 10623, 'kgNode1': {'kgID': 77, 'actionName': 'Check permissions'},
#             'kgNode2': {'kgID': 21, 'actionName': 'Get network info'}},
#            {'cgID': 10623, 'kgNode1': {'kgID': 77, 'actionName': 'Check permissions'},
#             'kgNode2': {'kgID': 138, 'actionName': 'Look for installed Apk'}}]
# step3_find_relations_special(special, apkname, node_list)


def step_final_output_relations(relations1, relations2, relations3):
    all_relations = relations1 + relations2 + relations3
    graph = {}
    for record in all_relations:
        for index, one in enumerate(record):
            actionName = one['actionName']
            if actionName in graph:
                if index < len(record) - 1:
                    tmp = graph[actionName]
                    if record[index + 1]['actionName'] not in tmp and record[index + 1]['actionName'] != actionName:
                        tmp.append(record[index + 1]['actionName'])
                        graph[actionName] = tmp
            else:
                graph[actionName] = []
                if index < len(record) - 1:
                    tmp = graph[actionName]
                    if record[index + 1]['actionName'] not in tmp and record[index + 1]['actionName'] != actionName:
                        tmp.append(record[index + 1]['actionName'])
                        graph[actionName] = tmp

    keylist = graph.keys()
    print('\n\n*****Graph******')
    for key, value in graph.items():
        print(key, ' : ', value)
    return graph, list(keylist)
