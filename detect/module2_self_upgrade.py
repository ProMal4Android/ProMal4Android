"""
This is the second module to analyse an apk.
We will:
1. Based on identified behaviors, self-upgrade the existing knowledge graph;
2. Output the relations between behaviors.

2023-10-19
"""
import re
import shutil
import time
from http import HTTPStatus

import dashscope

import django
import sys
import os
from exper.program_analysis import backwardDataflow, forwardDataflow, test, forwardDataflow_plus
from tools.utils import get_data, analyse, get_location_name, create_cfg, change_ag_txt

sys.path.append('../')

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'mwep.settings')

django.setup()

from common import models
from django.db.models import Q

dashscope.api_key = "sk-9bdbdd6cd8dd4904873d8c414a1c5c66"


def main_module2(match_nodes, apkname, f, p1, p2, p3, p4, p5):
    global kg_apis, kg_permissions, kg_features, apis_from_test, kg
    kg_apis = p1
    kg_permissions = p2
    kg_features = p3
    apis_from_test = p4
    kg = p5

    print('***Data***')
    step2_record = []
    for node in match_nodes:
        print(node)
        cgID = node['cgID']

        # forward, idl = forwardDataflow(cgID, apkname)
        # print(forward)
        # for o in forward:
        #     print(o)
        # print('-----')

        step1 = find_related_behavior_step1(cgID, apkname, node, [], [], [])
        # if step1:
        #     print('Step1 结果:')
        #     for b in step1:
        #         print(b)
        #     print('****************')
        # print('\n')

        step2 = find_related_behavior_step2(step1, match_nodes)
        # ae=list(set(ae))
        if step2:
            # print('Step2 结果:')
            for b in step2:
                if b not in step2_record:
                    step2_record.append(b)
                # print(b)
            # print('****************')
        # print('\n')
    print('****************\n')

    for one in step2_record:
        print(one)
    print('****************\n')
    find_relations, match_nodes_plus = find_related_behavior_step3(step2_record, apkname, match_nodes)
    # trace_record=graph_trace(step2_record,apkname)
    # for key,val in trace_record.items():
    #     print(key)
    #     print(val)
    #     print('\n')
    # print('ret:',ret)
    print('********Step3********')
    print('********find_relations********')
    for one in find_relations:
        print(one)
    print('\n********match_nodes_plus********')
    for one in match_nodes_plus:
        print(one)

    relations = find_related_behavior_step4(find_relations, apkname)
    print('\n************Find relations*************')
    for one in relations:
        print(one)

    graph=find_related_behavior_step5(relations,match_nodes_plus)
    print('graph:\n',graph)

    return match_nodes_plus, relations, graph

def find_related_behavior_step1(cgID, apkname, node, behaviors_record, fford, has_record):
    """
    :param cgID: <int>
    :param apkname: <string>
    """
    # start = time.time()
    # print('1-Find cg nodes...')
    data = get_data(os.path.join('../detect/outputCG/', apkname + '.txt'))
    node_list, edge_list = analyse(data)
    data = data.replace("\n", '')
    forward, idl = forwardDataflow_plus(cgID, data,node_list)
    # print('idl:',idl)
    fford = fford + idl
    if not idl:
        # print('super father')
        # test(cgID, apkname)
        pass
    else:
        for one in idl:
            brother, bidl = backwardDataflow(one, apkname)
            if len(bidl) == 1:
                find_related_behavior_step1(idl[0], apkname, node, behaviors_record, fford, has_record)
            else:
                # print('找到兄弟拉')
                for b in brother:
                    tmp = []
                    tmp.append({'loc': cgID, 'nodeID': node['nodeID'], 'actionName': node['actionName'], 'api': [],
                                'super': one})
                    # print(b)
                    if b['id'] not in fford and b['id'] != cgID:
                        if b['label'].find('[access_flags') != -1 and b['label'].find('<init>') == -1:
                            api_list = []
                            leaf = 1
                            backward, bdl = backwardDataflow(b['id'], apkname)
                            for bw in backward:
                                if bw['label'].find('access_flags') != -1 and bw['label'].find('<init>') == -1:
                                    api_list.append(str(bw['id']) + ':' + re.findall('L(.*?;->.*?)\(', bw['label'])[0])
                                    leaf = 0
                                api = re.findall('L(.*?;->.*?)\(', bw['label'])
                                # if api and (models.sensitiveApi.objects.filter(
                                #         api__contains=api[0]) or models.sensitiveApiMini.objects.filter(
                                #     api__contains=api[0])):
                                #     api_list.append(api[0])
                                if api:
                                    api = api[0]
                                    if api[0:4] == 'java' or (api[0:3] == 'com' and api[4:11] == 'android') or api[
                                                                                                               0:7] == 'android':
                                        api_list.append(api)
                            if len(api_list) > 0:
                                method_name = re.findall('->(.*?)\(', b['label'])[0]
                                if b['id'] not in has_record:
                                    has_record.append(b['id'])
                                    tmp.append(
                                        {'loc': b['id'], 'nodeID': -1, 'actionName': method_name, 'api': api_list,
                                         'leaf': leaf, 'super': one})
                    if len(tmp) > 1:
                        behaviors_record.append(tmp)

    # end = time.time()
    # print('Time of find cg nodes：', str(end - start))

    return behaviors_record


def find_related_behavior_step2(behaviors_record, match_nodes):
    """
    transform some cg node to known kg node
    """
    # start_time=time.time()
    # print('2-Transform cg node to kg node...')
    trans_behaviors_record = []
    if behaviors_record:
        for record in behaviors_record:
            tmp = []
            for one in record:
                if one['nodeID'] == -1:
                    flag = 0
                    for node in match_nodes:
                        if one['loc'] == node['cgID']:
                            flag = 1
                            tmp.append({'loc': node['cgID'], 'nodeID': node['nodeID'], 'actionName': node['actionName'],
                                        'api': []})
                            break
                    if flag == 0:
                        tmp.append(one)
                else:
                    tmp.append(one)
            trans_behaviors_record.append(tmp)

    # end_time = time.time()
    # print('Time of transforming：', str(end_time - start_time))
    return trans_behaviors_record


def find_common_cgID(cgID1, cgID2, trace_record):
    node1 = trace_record[cgID1]
    node2 = trace_record[cgID2]
    trace1 = node1['trace']
    trace2 = node2['trace']
    for one in trace1:
        for another in trace2:
            insect = list(set(one).intersection(set(another)))
            if insect:
                # find common id
                return insect[0]


def generate_subgraph_back(source_cgn, apkname, subgraph):
    """
    extract a subgraph from the cg end at the given cg node
    :param source_cgn: (int) node id
    :param apkname: (string) apk name
    """
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
        for arc in graph[state]:
            if arc not in path:
                generate_path_back(graph, path + [arc], end, results)


def graph_trace(step2_record, apkname):
    candidates = []
    ret = {}
    for record in step2_record:
        for one in record:
            if one not in candidates:
                candidates.append(one)
    for one in candidates:
        subgraph = {}
        trace = []
        subgraph = generate_subgraph_back(one['loc'], apkname, {}, 0)
        if subgraph:
            trace = search_graph_back(subgraph, one['loc'])
        one['subgraph'] = subgraph
        one['trace'] = trace
        ret[one['loc']] = one

    return ret


def construct_new_nodes(record):
    nodeID = -1
    per_list = ''
    api_list = ''
    pers = []
    apis = record['api']
    if len(apis) > 0:
        for api in apis:
            # query relevant permissions
            q = models.apiRequsetPer.objects.filter(api=api)
            if q:
                for one in q:
                    permissions = one.per
                    if permissions.find(',') != -1:
                        li = permissions.split(',')
                        for l in li:
                            pers.append(l)
                    else:
                        pers.append(one.per)
            try:
                api_ans = models.augmentAPiIn.objects.get(apiName=api.strip())
                if api_ans:
                    api_list = api_list + str(api_ans.apiID) + ','
            except Exception as e:
                # print('add new api:', api)
                now_len = list(models.augmentAPiIn.objects.values())[-1]['apiID']
                models.augmentAPiIn.objects.create(apiName=api, apiID=now_len + 1)
                api_ans = models.augmentAPiIn.objects.get(apiName=api)
                ans_id = api_ans.apiID
                models.augmentAPiIn.objects.filter(apiID=ans_id).update(id=ans_id)
                api_list = api_list + str(ans_id) + ','
        api_list = api_list.strip(',')

    if len(pers) > 0:
        for per in pers:
            try:
                per_ans = models.augmentPerIn.objects.get(perName=per)
                if per_ans:
                    per_list = per_list + str(per_ans.perID) + ','
            except Exception as e:
                print('qury per fail：', per)
                now_len = list(models.augmentPerIn.objects.values())[-1]['perID']
                models.augmentPerIn.objects.create(perName=per, perID=now_len + 1)
                per_ans = models.augmentPerIn.objects.get(perName=per)
                ans_id = per_ans.perID
                models.augmenTestPer.objects.filter(perID=ans_id).update(id=ans_id)
        per_list = per_list.strip(',')

    action_name = record['actionName']
    try:
        if models.augmentNodeIn.objects.filter(Q(actionName=action_name) & Q(apiList=api_list)):
            pass
        else:
            # add new node
            model = list(models.augmentNodeIn.objects.values())
            now_id = model[-1]['nodeID'] + 1
            nodeID = now_id
            mark = ''
            models.augmentNodeIn.objects.create(nodeID=now_id, actionName=action_name,
                                                perList=per_list,
                                                apiList=api_list,
                                                mark=mark)
            models.augmentNodeIn.objects.filter(nodeID=now_id).update(id=now_id)
    except Exception as e:
        pass

    return nodeID


def construct_new_rel(relations):
    for rel in relations:
        source=rel[0]
        target=rel[1]
        try:
            q=models.augmentRelIn.objects.get(Q(sourceID=source['nodeID'])&Q(targetID=target['nodeID']))
        except:
            print('新的关系:',rel)
            l=list(models.augmentRelIn.objects.values())
            num=l[-1]['id']
            models.augmentRelIn.objects.create(id=num+1,sourceID=source['nodeID'],sourceAct=source['actionName'],targetID=target['nodeID'],targetAct=target['actionName'],relation='')


def get_leaf(subgraph):
    leaf = []
    for key, value in subgraph.items():
        if not value and key not in leaf:
            leaf.append(key)
    return leaf


def judge_sensitive(api):
    sensitive = False
    if models.sensitiveApi.objects.filter(api__contains=api):
        sensitive = True
    elif models.sensitiveApiMini.objects.filter(api__contains=api):
        sensitive = True
    elif models.augmentAPiIn.objects.filter(apiName=api):
        ans = models.augmentAPiIn.objects.filter(apiName=api)
        for q in ans:
            if q.mark == '1':
                sensitive = True
                break
    return sensitive


def name_node(method_name):
    """ This method is too simple to be accurate """
    if method_name[0].islower():
        method_name = method_name[0].upper() + method_name[1:]
    ret = re.findall('[A-Z][^A-Z]*', method_name)
    ret = ' '.join(ret)
    return ret


def name_node_plus(api_set):
    """ use large models to automatically name behaviors based on API sets"""
    behavior=''
    pre = "In no more than 5 English words, summarize the behavior that these apis can accomplish: "
    tail = ''
    for api in api_set:
        tail = tail + api + ','
    input=pre+tail[:-1]+'.'
    # print('input:',input)
    response = dashscope.Generation.call(
        model=dashscope.Generation.Models.qwen_turbo,
        prompt=input
    )
    if response.status_code == HTTPStatus.OK:
        # print(response.output)  # The output text
        behavior = response.output.text
        # print(response.usage)  # The usage information
    else:
        # print(response.code)  # The error code.
        print(response.message)  # The error message.

    return behavior


# api_set=['android/media/MediaPlayer;->start','android/media/MediaPlayer;->pause','android/media/MediaPlayer;->getCurrentPosition']
# behavior=name_node_plus(api_set)
# print('behavior:',behavior)



def find_related_behavior_step3(step2_records, apkname, match_nodes):
    """
    construct new behavioral nodes
    """
    start_time=time.time()
    # print('3-Construct new behavioral nodes...')
    find_relations = []
    match_nodes_plus = []
    for record in step2_records:
        print('record 0:', record[0])
        print('record 1:', record[1])
        # case 1
        if record[1]['nodeID'] == -1 and record[1]['leaf'] == 1:
            # print('let us see')
            # *** create new behavioral node ***
            apis = record[1]['api']
            # print('apis:',apis)
            # *** find sensitive api ***
            addnew = 0
            for api in apis:
                if judge_sensitive(api):
                    addnew = 1
                    break
            # if addnew == 0 and record[1]['actionName'].find('get') != -1:
            #     addnew = 1
            if addnew == 1:
                print('create new\n')
                # record[1]['actionName'] = name_node(record[1]['actionName'])
                record[1]['actionName'] = name_node_plus(record[1]['api'])
                nodeID = construct_new_nodes(record[1])
                record[1]['nodeID'] = nodeID
                record[1]['api'] = []
                print('new', record[1])
            else:
                print('delete\n')

        # case 2
        elif record[1]['nodeID'] == -1 and record[1]['leaf'] == 0:
            # *** the current node is not a leaf node; Look down until we find a leaf node ***
            apis = record[1]['api']
            known = 0
            for api in apis:
                if api.find(':') != -1:
                    cgID = int(api.split(':')[0])
                    for node in match_nodes:
                        if node['cgID'] == cgID:
                            known = 1
                            print('known kg nodes\n')
                            record[1]['nodeID'] = node['nodeID']
                            record[1]['actionName'] = node['actionName']
                            record[1]['api'] = []
                            break
                if known == 1:
                    break
            if known == 0:
                pure_apis = []
                count = 0
                has_sensitive = 0
                cgID = -1
                for api in apis:
                    if api.find(':') == -1:
                        pure_apis.append(api)
                        if judge_sensitive(api):
                            has_sensitive = 1
                    else:
                        count = count + 1
                        cgID = int(api.split(':')[0])
                if has_sensitive == 1:
                    record[1]['api'] = pure_apis
                    print('create new2 sensitive api\n')
                    # record[1]['actionName'] = name_node(record[1]['actionName'])
                    record[1]['actionName'] = name_node_plus(record[1]['api'])
                    nodeID = construct_new_nodes(record[1])
                    record[1]['nodeID'] = nodeID
                    record[1]['api'] = []
                    print('new', record[1])
                elif count == 1:
                    addnew = 0
                    apilist = []
                    backward, bidl = backwardDataflow(cgID, apkname)
                    if backward:
                        for bw in backward:
                            if bw['label'].find('access_flags') == -1:
                                if re.findall('L(.*?;->.*?)\(', bw['label']):
                                    api = re.findall('L(.*?;->.*?)\(', bw['label'])[0]
                                    apilist.append(api)
                                    if judge_sensitive(api):
                                        addnew = 1
                            else:
                                addnew = 0
                                print('exit and delete\n')
                                break
                    else:
                        print('delete, too too\n')
                    if addnew == 1:
                        record[1]['api'] = apilist
                        print('create new3 child sensitive\n')
                        record[1]['actionName'] = name_node(record[1]['actionName'])
                        record[1]['actionName'] = name_node_plus(record[1]['api'])
                        nodeID = construct_new_nodes(record[1])
                        record[1]['nodeID'] = nodeID
                        record[1]['api'] = []
                        print('new', record[1])
                    else:
                        print('2 delete\n')
                else:
                    print('delete, too\n')

        # case 3: Find relation
        if record[0]['nodeID'] != -1 and record[1]['nodeID'] != -1:
            # *** Both are kg node, find their relation in cfg and add new relation ***
            print('both kg\n')
            # avoid duplicate lookups
            l = len(find_relations)
            if l < 1:
                find_relations.append([record[0], record[1]])
            else:
                flg = 0
                for rel in find_relations:
                    if (rel[0]['loc'] == record[0]['loc'] and rel[1]['loc'] == record[1]['loc']) or (
                            rel[0]['loc'] == record[1]['loc'] and rel[1]['loc'] == record[0]['loc']):
                        flg = 1
                        break
                if flg == 0:
                    find_relations.append([record[0], record[1]])
                    # find_relations.append([record[0], record[1]])
            # if record not in find_relations:
            #     find_relations.append([record[0], record[1]])

            loc_list = [item['loc'] for item in match_nodes_plus for key in item if key == 'loc']
            if record[0] not in match_nodes_plus and record[0]['loc'] not in loc_list:
                match_nodes_plus.append(record[0])
            if record[1] not in match_nodes_plus and record[1]['loc'] not in loc_list:
                match_nodes_plus.append(record[1])

    # end_time=time.time()
    # print('Time of constructing nodes：', str(end_time - start_time))
    return find_relations, match_nodes_plus


def find_related_behavior_step4(find_relations, apkname):
    """
    find relations in cfg
    """
    # start_time = time.time()
    # print('4-Construct relations...')
    ret = []
    for record in find_relations:
        father_id = record[0]['super']
        behv0 = record[0]
        behv1 = record[1]
        data = get_data(os.path.join('../detect/outputCG/', apkname + '.txt'))
        node_list, edge_list = analyse(data)
        # *** parent node ***
        label = re.findall('label\s"(.*?)"\n', node_list[father_id])[0]
        # *** behav1 and hebav2 ***
        label0 = re.findall('label\s"(.*?)"\n', node_list[behv0['loc']])[0]
        if label0.find('[access_flags') != -1:
            l = label0.find('[')
            label0 = label0[:l - 1]
        behv0['cfg_node'] = -1
        label1 = re.findall('label\s"(.*?)"\n', node_list[behv1['loc']])[0]
        if label1.find('[access_flags') != -1:
            l = label1.find('[')
            label1 = label1[:l - 1]
        behv1['cfg_node'] = -1

        # print('label:', label)
        # print('label1:', label1)
        # print('label2:', label2)

        # *** Create CFG ***
        apk_location = '../webapp/uploadFiles'
        base_location = '../detect/output_augment/processing/'  # excute androguard command
        shutil.rmtree(base_location)
        os.mkdir(base_location)
        base_location = base_location + apkname + '/'
        file_location, function_name = get_location_name(label)
        create_cfg(apkname, function_name, apk_location)
        cfg_file = change_ag_txt(base_location, file_location)

        # *** Read and Sort ***
        if cfg_file is not None:
            if os.path.exists(cfg_file):
                cfg_file = open(cfg_file, 'r', encoding='utf-8', newline="")
                for row in cfg_file.readlines():
                    if row.find(label0) != -1:
                        pattern = re.compile('\s*(\d*)\s*?\(', re.S)
                        location = pattern.findall(row)[0]
                        if location:
                            behv0['cfg_node'] = int(location)
                        else:
                            behv0['cfg_node'] = -1
                    if row.find(label1) != -1:
                        pattern = re.compile('\s*(\d*)\s*?\(', re.S)
                        location = pattern.findall(row)[0]
                        if location:
                            behv1['cfg_node'] = int(location)
                        else:
                            behv1['cfg_node'] = -1
        tmp = []
        if behv0['cfg_node'] > -1 and behv1['cfg_node'] > -1:
            if behv0['cfg_node'] > behv1['cfg_node']:
                tmp.append(behv1)
                tmp.append(behv0)
            else:
                tmp.append(behv0)
                tmp.append(behv1)
            ret.append(tmp)

    construct_new_rel(ret)

    # end_time = time.time()
    # print('Time of constructing relations：', str(end_time - start_time))
    return ret


def extract_behavior_from_relations(node_list):
    ret = []
    for one in node_list:
        ret.append({'nodeID': one['nodeID'], 'behav': one['actionName']})
    return ret


def find_related_behavior_step5(find_relations,match_nodes_plus):
    # print('5-Generate graph...')
    match_behavs=extract_behavior_from_relations(match_nodes_plus)
    graph={}
    for one in match_behavs:
        tmp=[]
        for rel in find_relations:
            if rel[0]['actionName']==one['behav']:
                tmp.append(rel[1]['actionName'])
        graph[one['behav']]=tmp

    return graph

