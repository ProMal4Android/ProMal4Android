import re
import time

from operator import itemgetter, eq
from urllib.parse import urlparse

import django
import sys
import os
import csv

from detect.module1_detect_nodes import genrate_feature_file
from tools.use_data import define_use_data
from tools.utils import do_feature_file, generate_cg_edit, gml_txt

sys.path.append('../')

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'mwep.settings')

django.setup()

from common import models

def get_behaviors(apk_name):
    csv_file_path = '../detect/output/proMal/test_copy.csv'
    with open(csv_file_path, 'r') as csv_file:
        csv_reader = csv.DictReader(csv_file)
        for row in csv_reader:
            apkName = row['Apk Name']
            # finalBehaviors=row['Final behaviors']
            # if apkName==apk_name:
            #     return finalBehaviors.split("\n")
            chains=row['Chains']
            if apkName==apk_name:
                ret=[]
                chains_list=chains.split('\n')
                for one in chains_list:
                    behaviors=one.split(' -> ')
                    for bhv in behaviors:
                        if bhv not in ret:
                            ret.append(bhv)
                return ret

        return []

def main():
    global kg_apis, kg_permissions, kg_features, apis_from_test, kg
    kg_apis, kg_permissions, kg_features, apis_from_test, kg = define_use_data()

    apk_name_list=['AceCard','AgentBKY','Aladdin','AndroidOSBrata','AsiaHitGroup','Bahamut','BeaverGang','BKotlindHRX','ChatSpyA','Clast82','ClickrAd','CoinHive','DoubleHidden', 'DroidPlugin', 'FlokiSpy', 'GhostTeam', 'Gooligan', 'GuerillaA', 'Haken','Inazigram', 'IndexY', 'InstaDetector', 'MalBus', 'MilkyDoor','PSWAndroidOSMyVK','PletorD','ProjectSpyHRX','RedDawn','Reputation1_2018','SimBad','Smesh','SpyBankerHU','TekyaHRX','ZtorgA']
    for apk in apk_name_list:
        feature_filename = os.path.join('../detect/output_desFeatures/', apk + '_desFeatures.txt')
        feature_file = open(feature_filename, 'w', encoding='utf-8')
        # try:
        #     feature_data = do_feature_file(apk)
        # except:
        #     f = os.path.join('../webapp/uploadFiles', apk + '.apk')
        #     if os.path.exists('../detect/outputCG/' + apk + '.txt'):
        #         pass
        #     else:
        #         gml, apk_name = generate_cg_edit(f)  # turn apk into cg
        #         gml_txt(gml, apk)  # turn cg into txt
        #     genrate_feature_file(apk, apk+'.apk')  # generate features file
        feature_data = do_feature_file(apk)
        data = feature_data.split("\n\n")
        permissions = data[0].split('\n')
        intent_filters = data[1].split('\n')
        for per in permissions:
            feature_file.write(per+'\n')
        if len(permissions)>0:
            feature_file.write('\n')
        for intent in intent_filters:
            feature_file.write(intent+'\n')
        if len(intent_filters)>0:
            feature_file.write('\n')

        behavior_list=get_behaviors(apk)
        for one in behavior_list:
            ans=models.augmentNodeIn.objects.filter(actionName=one)
            if ans:
                api_ids=ans[0].apiList.split(',')
                for id in api_ids:
                    apiname=models.augmentAPiIn.objects.get(apiID=int(id)).apiName
                    feature_file.write(apiname+'\n')
        feature_file.close()


# main()

