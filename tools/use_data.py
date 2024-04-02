import django
import sys
import os

sys.path.append('../')

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'mwep.settings')

django.setup()

from common import models

KG_PERS = []  # all permissions in kg/database
KG_APIS = []  # all apis in kg/database
MAL_APIS = []  # Apitest中的apis
KG_FEATURES = []  # all features(permissions+apis) in kg
KG = []

def define_use_data():
    global KG_APIS, KG_PERS, KG_FEATURES, MAL_APIS, KG
    kg_permissions, kg_apis, kg_features = get_pers_apis_after_augment()
    apis_from_test = get_apis_from_test_after_augment()
    kg = get_all_list()
    return kg_apis, kg_permissions, kg_features, apis_from_test, kg


def get_pers_apis_after_augment():
    """
    :return: :list: all permissions in kg
            :list: all apis in kg
    """
    api_list = get_apis_from_wkg_after_augment()
    per_list = get_pers_from_wkg_after_augment()
    kg_list = list(per_list)
    # permissions + apis = all features in kg
    for one in api_list:
        kg_list.append(one)

    return per_list, api_list, kg_list


def get_apis_from_test_after_augment():
    api_list = models.augmentAPiIn.objects.values('apiName')
    api_list = dict_list(api_list, 'apiName')
    return api_list


def get_apis_from_wkg_after_augment():
    """
    apis in the kg
    """
    api_list = models.augmentNodeIn.objects.values('apiList')
    api_list = dict_list(api_list, 'apiList')
    api_num = []
    apis_list = []
    for one in api_list:
        if one != '' and one != ' ':
            if one.find(',') != -1:
                tmp = one.split(',')
                for api in tmp:
                    if api not in api_num and api != '':
                        api_num.append(api)
            elif one not in api_num:
                api_num.append(one)
    for one in api_num:
        try:
            ans = models.augmentAPiIn.objects.get(id=int(one))
            api = ans.apiName.replace(' ', '')
            apis_list.append(api)
        except:
            print('augmentAPiIn matching query does not exist:', one)
    return apis_list


def get_pers_from_wkg_after_augment():
    """
    permissions in the kg
    """
    per_list = models.augmentNodeIn.objects.values('perList')
    per_list = dict_list(per_list, 'perList')
    per_num = []
    pers_list = []
    for one in per_list:
        if one != '' and one != ' ':
            if one.find(',') != -1:
                tmp = one.split(',')
                for api in tmp:
                    if api not in per_num and api != '':
                        per_num.append(api)
            elif one not in per_num:
                per_num.append(one)
    for one in per_num:
        try:
            ans = models.augmentPerIn.objects.get(id=int(one))
            per = ans.perName.replace(' ', '')
            pers_list.append(per)
        except:
            print('augmentPerIn matching query does not exist:', one)
    return pers_list


def dict_list(demo_list, _flag):
    """
    :param demo_list: QuerySet，like：[{'perName': 'android.permission.ACCESS_BACKGROUND_LOCATION'}, {'perName': 'android.permission.ACCESS_COARSE_LOCATION'}]
    :param _flag: indicate permissions,apis or node
    :return a sample list
    """
    try:
        ret_list = []
        for i in demo_list:
            ret_list.append(i[_flag])
        return ret_list
    except:
        print('Error: dict_list throw a exception!')


def get_all_list():
    """
    get all api list and per list corresponding relevant nodes in the kg
    """
    model = list(models.augmentNodeIn.objects.values())
    ret = []
    for node in model:
        json = {}
        nodeID = node['nodeID']
        actionName = node['actionName']
        mark = node['mark']
        perlist = str_list(node['perList'])
        apilist = str_list(node['apiList'])
        constStr = node['constStr']
        constStr = constStr.split(',')
        json['nodeID'] = nodeID
        json['actionName'] = actionName
        json['mark'] = mark
        json['perList'] = perlist
        json['apiList'] = apilist
        json['constList'] = constStr
        ret.append(json)

    return ret


def str_list(s):
    """
    turn perlist<str> and apilist<str> into list<int>
    """
    ret = []
    if len(s) > 0 and s != '':
        s = s.replace(' ', '')
        if s.find(',') == -1:
            ret.append(int(s))
        else:
            tmp = s.split(',')
            for one in tmp:
                ret.append(int(one))

    return ret
