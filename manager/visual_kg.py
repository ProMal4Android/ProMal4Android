"""
This file is used to visualize knowledge graph using neo4j

2023-10-30
"""
import django
import sys
import os

sys.path.append('../')

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'mwep.settings')

django.setup()

from common import models
from py2neo import Graph, Node, Relationship, NodeMatcher

def create_pers(graph):
    pers = models.MalPermission.objects.values()
    for per in pers:
        perID = per['perID']
        perName = per['perName']
        des = per['des']
        inLevel = per['inLevel']
        outLevel = per['outLevel']
        # level = per['level']
        # per_node = Node('permission', ID=perID, name=perName, des=des, inLevel=inLevel, outLevel=outLevel, level=level)
        per_node = Node('permission', ID=perID, name=perName, des=des, inLevel=inLevel, outLevel=outLevel)
        graph.create(per_node)

def create_apis(graph):
    apis = models.MalAPI.objects.values()
    for api in apis:
        apiID = api['apiID']
        apiName = api['apiName']
        des = api['des']
        inLevel = api['inLevel']
        outLevel = api['outLevel']
        # level = per['level']
        api_node = Node('API', ID=apiID, name=apiName, des=des, inLevel=inLevel, outLevel=outLevel)
        graph.create(api_node)

    # 3. Create relationships between apis and permissions
    node_matcher = NodeMatcher(graph)
    rel = models.apiRequsetPer.objects.values()
    for one in rel:
        node_source = one['api']
        node_target_query = one['per']
        if node_matcher.match('API').where(name=node_source.strip()).first():
            node_target_list=node_target_query.split(',')
            for node_target in node_target_list:
                if models.MalPermission.objects.filter(perName=node_target.strip()):
                    pass
                else:
                    len_per = len(list(models.MalPermission.objects.values()))
                    len_per = len_per + 1
                    models.MalPermission.objects.create(perID=len_per, perName=node_target.strip())
                    models.MalPermission.objects.filter(perID=len_per).update(id=len_per)
                    per_node = Node('permission', ID=len_per, name=node_target)
                    graph.create(per_node)
                node_source = node_matcher.match("API").where(name=node_source).first()
                # print('node source:',node_source)
                node_target = node_matcher.match("permission").where(name=node_target).first()
                # print('node target:', node_target)
                r = Relationship(node_source, "requests", node_target)
                graph.create(r)

def create_similiar(graph):
    node_matcher = NodeMatcher(graph)
    sim_list = models.ApiSim.objects.values()
    for one in sim_list:
        apilist=one['list'].split(',')
        for index,one in enumerate(apilist[:-1]):
            if node_matcher.match("API").where(name=one).first():
                pass
            else:
                api_name = one
                apiID=models.MalAPI.objects.get(apiName=one).apiID
                node = Node('API', ID=apiID, name=api_name)
                graph.create(node)
            # print('node source:',node_source)
            another=apilist[index+1]
            if node_matcher.match("API").where(name=another).first():
                pass
            else:
                apiID = models.MalAPI.objects.get(apiName=another).apiID
                node = Node('API', ID=apiID, name=another)
                graph.create(node)
            node_source = node_matcher.match("API").where(name=one).first()
            node_target = node_matcher.match("API").where(name=another).first()
            r = Relationship(node_source, "parallels", node_target)
            graph.create(r)


def create_sdk(graph):
    node_matcher = NodeMatcher(graph)
    sdk_list = models.ApiSDK.objects.values()
    for one in sdk_list:
        apilist=one['list'].split(',')
        dict_inlevel = {}
        dict_outlevel={}

        def custom_sort(key):
            return dict_inlevel[key], dict_outlevel[key]

        order = []
        for api in apilist:
            inLevel=models.MalAPI.objects.get(apiName=api).inLevel
            outLevel = models.MalAPI.objects.get(apiName=api).outLevel
            dict_inlevel[api]=int(inLevel)
            if outLevel!='':
                dict_outlevel[api] = int(outLevel)
            else:
                dict_outlevel[api] = 99

        # sorted_keys = sorted(dict.keys())
        sorted_keys = sorted(dict_inlevel.keys(), key=custom_sort)

        for key in sorted_keys:
            # print(key, ':', dict_inlevel[key], ',', dict_outlevel[key],'\n')
            order.append(key)

        for index,one in enumerate(order[:-1]):
            if node_matcher.match("API").where(name=one).first():
                pass
            else:
                api_name = one
                apiID=models.MalAPI.objects.get(apiName=one).apiID
                node = Node('API', ID=apiID, name=api_name)
                graph.create(node)
            another=order[index+1]
            if node_matcher.match("API").where(name=another).first():
                pass
            else:
                apiID = models.MalAPI.objects.get(apiName=another).apiID
                node = Node('API', ID=apiID, name=another)
                graph.create(node)
            node_source = node_matcher.match("API").where(name=one).first()
            node_target = node_matcher.match("API").where(name=another).first()
            r = Relationship(node_source, "updates", node_target)
            graph.create(r)


def create_behaviors(graph):
    node_matcher = NodeMatcher(graph)
    kg = models.MalOperation.objects.values()
    for one in kg:
        action_id = one['id']
        action_name = one['actionName']
        node = Node('Behavior', ID=action_id, name=action_name, action=action_name)
        graph.create(node)

        per_id_list = one['perList']
        api_id_list = one['apiList']
        per_list = str_list(per_id_list)
        api_list = str_list(api_id_list)
        permissions = []
        apis = []
        if len(per_list) > 0:
            for per in per_list:
                try:
                    per_obj = models.MalPermission.objects.get(id=per)
                    permissions.append(per_obj.perName)
                except:
                    pass
        if len(api_list) > 0:
            for api in api_list:
                try:
                    api_obj = models.MalAPI.objects.get(id=api)
                    apis.append(api_obj.apiName)
                    # Create relationships between behaviors and apis
                    try:
                        node_source = node_matcher.match("Behavior").where(ID=action_id).first()
                        node_target = node_matcher.match("API").where(ID=api_obj.apiID).first()
                        r = Relationship(node_source, "calls", node_target)
                        graph.create(r)
                    except Exception as e:
                        len_api = len(list(models.MalAPI.objects.values()))
                        len_api = len_api + 1
                        models.MalAPI.objects.create(apiID=len_api, apiName=api_obj.apiName.strip())
                        models.MalAPI.objects.filter(apiID=len_api).update(id=len_api)
                        api_node = Node('API', ID=len_api, name=api_obj.apiName)
                        graph.create(api_node)

                        node_source = node_matcher.match("Behavior").where(ID=action_id).first()
                        node_target = node_matcher.match("API").where(ID=api_obj.apiID).first()
                        r = Relationship(node_source, "calls", node_target)
                        graph.create(r)
                        print('node source:', action_name)
                        print('node target:', api_obj.apiName)
                        print(e)
                except:
                    pass

    # 3. Create relationships between behaviors
    rel = models.MalRelation.objects.values()
    for one in rel:
        node_id_source = one['sourceID']
        node_id_target = one['targetID']
        relation = one['relation']
        if relation == '':
            relation = 'next'
        try:
            node_source = node_matcher.match("Behavior").where(ID=node_id_source).first()
            node_target = node_matcher.match("Behavior").where(ID=node_id_target).first()
            r = Relationship(node_source, relation, node_target)
            graph.create(r)
        except Exception as e:
            print(e)

            print('\nnode_id_source:',node_id_source)
            print('node_id_target:', node_id_target)


def create_graph():
    # 1. access the neo4j, input username and password
    graph = Graph("http://localhost:7687", auth=("neo4j", '11111111'), name='promal')
    # avoid multiple draw
    # graph.delete()
    graph.run('match (n) detach delete n')

    # 2. Create nodes
    kg = models.MalOperation.objects.values()
    for one in kg:
        action_id = one['id']
        action_name = one['actionName']
        per_id_list = one['perList']
        api_id_list = one['apiList']
        per_list = str_list(per_id_list)
        api_list = str_list(api_id_list)
        permissions = []
        apis = []
        if len(per_list) > 0:
            for per in per_list:
                try:
                    per_obj = models.MalPermission.objects.get(id=per)
                    permissions.append(per_obj.perName)
                except:
                    pass
        if len(api_list) > 0:
            for api in api_list:
                try:
                    api_obj = models.MalAPI.objects.get(id=api)
                    apis.append(api_obj.apiName)
                except:
                    pass
        node = Node('Behavior', ID=action_id, name=action_id, apis=apis, permissions=permissions, action=action_name)
        graph.create(node)

    # 3. Create relationships
    node_matcher = NodeMatcher(graph)
    rel = models.MalRelation.objects.values()
    for one in rel:
        node_id_source = one['sourceID']
        node_id_target = one['targetID']
        relation = one['relation']
        if relation == '':
            relation = 'next'
        node_source = node_matcher.match("Behavior").where(ID=node_id_source).first()
        node_target = node_matcher.match("Behavior").where(ID=node_id_target).first()
        r = Relationship(node_source, relation, node_target)
        graph.create(r)


def str_list(str_list):
    """
    :param str_list: string list
    """
    ret_list = []
    if str_list != '':
        if str_list.find(',') != -1:
            ret_list = str_list.split(',')
        else:
            ret_list.append(int(str_list))
    return ret_list


def create_main():
    graph = Graph("bolt://localhost:7687", auth=("neo4j", '11111111'), name='promal3')
    # graph = Graph("http://localhost:7687", auth=("main", '88888888'), name='mwep')
    # avoid multiple draw
    # graph.delete()
    # graph.run('match (n) detach delete n')

    # create_pers(graph)
    # create_apis(graph)
    # create_behaviors(graph)
    # create_similiar(graph)
    create_sdk(graph)

create_main()
# create_graph()