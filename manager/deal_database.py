import os
import django
import sys

from tools.utils import str_str_version2

sys.path.append('../')

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'mwep.settings')

django.setup()

from common import models

def name_action():
    """
    get action name for each coloumn
    """
    rels = list(models.augmentRelIn.objects.values())
    for one in rels:
        print('id:', one['id'])
        try:
            source = models.augmentNodeIn.objects.get(nodeID=one['sourceID']).actionName
            print('source:', source)
            target = models.augmentNodeIn.objects.get(nodeID=one['targetID']).actionName
            print('target:', target)
            models.augmentRelIn.objects.filter(id=one['id']).update(sourceAct=source, targetAct=target)
        except:
            # models.augmentRelIn.objects.get(id=one['id']).delete()
            print('error')
            pass

# name_action()


def print_kg_all_upgrade():
    """
    print all nodes after upgrade
    """
    output_path = './output/print_kg_all_upgrade.txt'
    model = list(models.augmentNodeIn.objects.values())
    # clean reports
    with open(output_path, "a", encoding='utf-8') as report:
        report.truncate(0)
    with open(output_path, "a", encoding='utf-8') as report:
        report.write("DataBase: AUGMENT\n\n")
        for one in model:
            apistr = one['apiList']
            perstr = one['perList']
            node_id = one['nodeID']
            node_name = one['actionName']
            api_list = str_str_version2(apistr, 0)
            per_list = str_str_version2(perstr, 1)
            report.write('********************\n')
            ret = str(node_id) + '-' + node_name + ':\n' + perstr + ':' + ','.join(
                str(i) for i in per_list) + '\n' + apistr + ':' + ','.join(
                str(i) for i in api_list) + '\n'
            report.write(ret)
            report.write('********************\n\n')


# print_kg_all_upgrade()

def print_kg_rel_version2():
    """
    print all relations in the kg
    """
    output_path = './output/print_kg_rel_upgrade.txt'
    node_num = len(list(models.augmentRelIn.objects.values()))
    # clean report
    with open(output_path, "a", encoding='utf-8') as report:
        report.truncate(0)
    with open(output_path, "a", encoding='utf-8') as report:
        report.write("DataBase: AUGMENT\n\n")
        for i in range(1, node_num + 1):
            try:
                ans = models.augmentRelIn.objects.get(id=i)
                id = ans.id
                source = ans.sourceID
                target = ans.targetID
                relation = ans.relation
                # print('source->target:', str(source) + '->' + str(target))
                ret = '**********' + str(id) + '**********' + '\n**orientation**:' + str(source) + '--->' + str(
                    target) + '\n' + '**relation**:' + relation + '\n'
                source = models.augmentNodeIn.objects.get(nodeID=source).actionName
                target = models.augmentNodeIn.objects.get(nodeID=target).actionName
                report.write(ret)
                report.write(source + '--->' + target)
                report.write('\n********************\n\n')
            except:
                pass


print_kg_rel_version2()


"""
 ***优化数据库***
"""


def get_list():
    """获取Node表中每个节点的ID和其perlist, apilist的对应关系"""
    model = list(models.augmentNodeIn.objects.values())
    ret = []
    nodelist=[]
    for one in model:
        dict = {}
        nodeID = one['nodeID']
        perlist = one['perList']
        apiList = one['apiList']

        dict['nodeID'] = nodeID
        dict['perList'] = perlist
        dict['apiList'] = apiList

        nodelist.append(nodeID)

        ret.append(dict)
    # print('ret:\n ', ret)
    return ret,nodelist


def optim_API():
    """
    优化API数据表
    """
    ret = get_list()[0]
    model = list(models.augmentAPiIn.objects.values())
    num = len(model)
    newID = 0  # 新的、递增的ID
    for one in model:
        newID = newID + 1
        oldAPIID = one['apiID']
        models.augmentAPiIn.objects.filter(apiID=oldAPIID).update(id=newID)  # 更新ID

        # 先更新Node表中的API
        for node in ret:
            nodeID = node['nodeID']
            apilist = node['apiList']
            old_apilist=apilist
            if str(oldAPIID) in apilist:
                if apilist.find(',') == -1:  # 只有一个api
                    if str(oldAPIID) == apilist:
                        apilist = apilist.replace(str(oldAPIID), str(newID))
                elif apilist.find(',') != -1:  # 有多个api
                    apilist = apilist.split(',')
                    for index, value in enumerate(apilist):
                        if value == str(oldAPIID):
                            apilist[index] = str(newID)
                            break
                    apilist = ','.join(apilist)
                if old_apilist!=apilist:
                    print('**** old ', str(oldAPIID) + '***')
                    print('before: node-', str(nodeID) + ', apilist-' + old_apilist)
                    print('**** new ', str(newID) + '***')
                    print('after: node-', str(nodeID) + ', apilist-' + apilist+'\n')
                    models.augmentNodeIn.objects.filter(nodeID=nodeID).update(apiList=apilist)

        models.augmentAPiIn.objects.filter(id=newID).update(apiID=newID)  # 更新ID


# optim_API()


def delete_Rel():
    """
    删除Relation表中多于的关系
    """
    nodelist = get_list()[1]
    model = list(models.augmentRelIn.objects.values())
    print('nodelist:', nodelist)
    for rel in model:
        id=rel['id']
        source=rel['sourceID']
        target=rel['targetID']
        if source in nodelist and target in nodelist:
            continue
        else:
            print('delete:', rel['id'])
            models.augmentRelIn.objects.filter(id=id).delete()
        # if source not in nodelist or target not in nodelist:
        #     print('delete:', rel['id'])
        #     models.augmentRelIn.objects.filter(Q(sourceID=source) | Q(targetID=target)).delete()

# delete_Rel()

def optim_Node():
    """
    优化Node数据表
    """
    model = list(models.augmentNodeIn.objects.values())
    newID = 0  # 新的、递增的ID
    for one in model:
        newID = newID + 1
        oldNodeID = one['nodeID']
        if newID != oldNodeID:  # 只更新那些有问题的
            models.augmentNodeIn.objects.filter(nodeID=oldNodeID).update(id=newID)  # 更新ID

            # 先更新Relation表中的Node id
            try:
                ans=models.augmentRelIn.objects.filter(sourceID=oldNodeID)
                if ans:
                    for i in ans:
                        models.augmentRelIn.objects.filter(sourceID=oldNodeID).update(sourceID=newID)
            except:
                pass
            try:
                ans = models.augmentRelIn.objects.filter(targetID=oldNodeID)
                if ans:
                    for i in ans:
                        models.augmentRelIn.objects.filter(targetID=oldNodeID).update(targetID=newID)
            except:
                pass

        # 更新Node表中的nodeID
        models.augmentNodeIn.objects.filter(id=newID).update(nodeID=newID)  # 更新Node ID
        # models.augmentAPiIn.objects.filter(id=newID).update(apiID=newID)  # 更新ID

# optim_Node()

def optim_Relation():
    """
    重排序Relation数据表
    """
    model = list(models.augmentRelIn.objects.values())
    newID = 0
    for one in model:
        newID = newID + 1
        oldRelID = one['id']
        if newID != oldRelID:
            models.augmentRelIn.objects.filter(id=oldRelID).update(id=newID)

# optim_Relation()