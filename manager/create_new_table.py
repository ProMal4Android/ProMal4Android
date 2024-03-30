import os
import django
import sys
import csv

from django.db.models import Q

from tools.utils import str_str_version2

sys.path.append('../')

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'mwep.settings')

django.setup()

from common import models


def extract_behaviors_from_txt():
    with open('./output/print_kg_all_upgrade.txt', 'r') as file:
        lines = file.readlines()

    blocks = []
    current_block = []

    for line in lines:
        if line.strip() == '':
            if current_block:
                blocks.append(''.join(current_block))
                current_block = []
        else:
            current_block.append(line)
    if current_block:
        blocks.append(''.join(current_block))

    num = 1
    for block in blocks:
        block = block.split('\n')
        malName = block[1].split('-',1)[1][:-1].strip()
        malPer = block[2].split(':',1)[1]
        malAPI = block[3].split(':',1)[1]
        # print(malName, malPer, malAPI, '\n')

        # insert into the database
        perIDList=[]
        if malPer != '':
            malPerList = malPer.split(',')
            for one in malPerList:
                try:
                    perID=models.MalPermission.objects.get(perName=one.strip()).apiIDperID
                    perIDList.append(str(perID))
                except:
                    len_per = len(list(models.MalPermission.objects.values()))
                    len_per = len_per + 1
                    perIDList.append(str(len_per))
                    models.MalPermission.objects.create(perID=len_per, perName=one.strip())
                    models.MalPermission.objects.filter(perID=len_per).update(id=len_per)

        apiIDList=[]
        if malAPI != '':
            malAPIList = malAPI.split(',')
            for one in malAPIList:
                try:
                    apiID=models.MalAPI.objects.get(apiName=one.strip()).apiID
                    apiIDList.append(str(apiID))
                except:
                    len_api = len(list(models.MalAPI.objects.values()))
                    len_api = len_api + 1
                    apiIDList.append(str(len_api))
                    models.MalAPI.objects.create(apiID=len_api, apiName=one.strip())
                    models.MalAPI.objects.filter(apiID=len_api).update(id=len_api)

        models.MalOperation.objects.create(nodeID=num, actionName=malName, perList=','.join(perIDList),
                                            apiList=','.join(apiIDList))
        models.MalOperation.objects.filter(nodeID=num).update(id=num)
        num=num+1


# models.MalAPI.objects.all().delete()
# models.MalPermission.objects.all().delete()
# models.MalOperation.objects.all().delete()
# extract_behaviors_from_txt()

def extract_relation_from_csv():
    models.MalRelation.objects.all().delete()
    b = 0
    with open('./output/common_augmentrelin.csv', newline='') as csvfile:
        csv_reader = csv.DictReader(csvfile)
        for row in csv_reader:
            try:
                sourceID=models.MalOperation.objects.filter(actionName=row['sourceAct'])[0].nodeID
            except:
                sourceID=0
            try:
                targetID = models.MalOperation.objects.filter(actionName=row['targetAct'])[0].nodeID
            except:
                targetID=0
            b = b + 1
            models.MalRelation.objects.create(sourceID=sourceID, sourceAct=row['sourceAct'],
                                              targetID=targetID, targetAct=row['targetAct'],
                                              relation=row['relation'])
            try:
                models.MalRelation.objects.filter(Q(sourceID=sourceID) & Q(targetID=targetID)).update(id=b)
            except Exception as e:
                print(e)
                print(str(row['sourceID']) + str(row['targetID']))

# extract_relation_from_csv()


def simplfiy_API(apis):
    apis=apis.split('\n')
    ret=[]
    for one in apis:
        if one.find('(')!=-1:
            index=one.find('(')
            api=one[:index]
        else:
            api=one
        if api[0]=='L':
            api=api[1:]
        try:
            apiID=models.MalAPI.objects.get(apiName=api).apiID
            ret.append(str(apiID))
        except:
            len_api=len(list(models.MalAPI.objects.values()))
            len_api = len_api + 1
            ret.append(str(len_api))
            models.MalAPI.objects.create(apiID=len_api, apiName=api.strip())
            models.MalAPI.objects.filter(apiID=len_api).update(id=len_api)

    return ret


def add_new_records():
    with open('./output/new_behaviors.csv', newline='') as csvfile:
        csv_reader = csv.DictReader(csvfile)
        for row in csv_reader:
            operation=row['Operation']
            apis=row['API']
            apiList=simplfiy_API(apis)
            len_opr=len(list(models.MalOperation.objects.values()))
            models.MalOperation.objects.create(nodeID=len_opr+1, actionName=operation,
                                               apiList=','.join(apiList))
            models.MalOperation.objects.filter(nodeID=len_opr+1).update(id=len_opr+1)

# add_new_records()


def sdk_or_sim_number():
    sdkAPIs=list(models.ApiSDK.objects.values())
    for group in sdkAPIs:
        id=group['listID']
        apis=group['list'].split(',')
        for api in apis:
            if models.MalAPI.objects.filter(apiName=api.strip()):
                models.MalAPI.objects.filter(apiName=api.strip()).update(repList=id)
            else:
                len_api = len(list(models.MalAPI.objects.values()))
                models.MalAPI.objects.create(apiID=len_api + 1, apiName=api.strip(),repList=id)
                models.MalAPI.objects.filter(apiID=len_api + 1).update(id=len_api + 1)

    simAPIs = list(models.ApiSim.objects.values())
    for group in simAPIs:
        id = group['listID']
        apis = group['list'].split(',')
        for api in apis:
            if models.MalAPI.objects.filter(apiName=api.strip()):
                models.MalAPI.objects.filter(apiName=api.strip()).update(addList=id)
            else:
                len_api = len(list(models.MalAPI.objects.values()))
                models.MalAPI.objects.create(apiID=len_api + 1, apiName=api.strip(), addList=id)
                models.MalAPI.objects.filter(apiID=len_api + 1).update(id=len_api + 1)

# sdk_or_sim_number()

def find_levels():
    apis=models.MalAPI.objects.values()
    for api in apis:
        try:
            obj=models.augmentAPiIn.objects.get(apiName=api['apiName'])
            models.MalAPI.objects.filter(apiName=api['apiName']).update(inLevel=obj.inLevel,outLevel=obj.outLevel)
        except:
            print(e)

# find_levels()

def add_new_rel():
    # models.MalRelation.objects.all().delete()
    with open('./output/new_rel.csv', newline='') as csvfile:
        csv_reader = csv.DictReader(csvfile)
        for row in csv_reader:
            try:
                sourceID = models.MalOperation.objects.filter(actionName=row['sourceAct'])[0].nodeID
            except:
                sourceID = 0
            try:
                targetID = models.MalOperation.objects.filter(actionName=row['targetAct'])[0].nodeID
            except:
                targetID = 0
            len_opr = len(list(models.MalRelation.objects.values()))
            models.MalRelation.objects.create(sourceID=sourceID, sourceAct=row['sourceAct'],
                                              targetID=targetID, targetAct=row['targetAct'],
                                              relation=row['relation'])
            try:
                models.MalRelation.objects.filter(Q(sourceID=sourceID) & Q(targetID=targetID)).update(id=len_opr+1)
            except Exception as e:
                print(e)
                print(str(row['sourceID']) + str(row['targetID']))

# add_new_rel()