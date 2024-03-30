import csv
import re
import glob
import json
import shutil
import datetime
import time
import codecs  # turn ansi into utf-8
import os
from operator import itemgetter, eq
from collections import Counter

import django
import sys
from django.http import HttpResponse

from detect.module1_detect_nodes import main_module1
from detect.module2_extract_relation import main_module2_relations, dict_print
from detect.module3_graph_chain import only_match_relations_on_KG, match_relations_kgAndCfg, generate_chains
from exper.program_analysis import list_print
from tools.use_data import define_use_data, get_apis_from_test_after_augment, get_pers_apis_after_augment
from tools.utils import find_relations_all

sys.path.append('../')

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'mwep.settings')

django.setup()

from common import models

# original
# report_path = '../detect/output/proMal/report.txt'
match_report = '../detect/output/proMal/match_report.txt'
report_log = '../detect/output/proMal/log.txt'
csv_file = '../detect/output/proMal/test.csv'
# csv_file = '../detect/output/proMal/xmal_output.csv'
statics_behavior = []

fileID = 0


def get_malicious_nodes():
    """
    获取知识图谱上那些mark为2的节点
    """
    ret = []
    model = models.augmentNodeIn.objects.values()
    model = list(model)
    for node in model:
        if node['mark'] == '2':
            ret.append(node['nodeID'])
    return ret


def do_output_str(input_list, flag):
    output = ''
    if flag == 'binary':
        for one in input_list:
            output = output + '\n' + ' -> '.join(one)
    elif flag == 'single':
        output = '\n'.join(input_list)
    elif flag == 'dict':
        for key, val in input_list.items():
            str = key + ': ' + ', '.join(val)
            output = output + '\n' + str

    output = output.strip('\n')
    return output


def ProMal():
    """
    map app
    """
    with open(report_log, "a", encoding='utf-8') as f:
        f.truncate(0)
    with open(match_report, "a", encoding='utf-8') as f:
        f.truncate(0)
    global malicious_nodes
    global kg_apis, kg_permissions, kg_features, apis_from_test, kg
    kg_apis, kg_permissions, kg_features, apis_from_test, kg = define_use_data()
    malicious_nodes = get_malicious_nodes()

    # 1、apk location
    sample_apks_folder_path = '../webapp/uploadFiles'
    file_list = os.listdir(sample_apks_folder_path)
    sorted_files = sorted(file_list)

    match_report_ans = []

    # clean folder and create folder
    # shutil.rmtree('../detect/outputCG')
    # shutil.rmtree('../detect/output_features')
    # os.mkdir('../detect/outputCG')
    # os.mkdir('../detect/output_features')
    with open(csv_file, "a", encoding='utf-8', newline='') as report:
        writer = csv.writer(report)
        writer.writerow(
            ["ID", "Apk Name", "Final behaviors", "Chains", "Match KG Behaviors", "KG relations", "KGre num",
             "CFG relations",
             "Deleted KG relations", "KGdre num", "Final Graph"])

    global flag
    file_id = 0

    for f in sorted_files:
        file_id = file_id + 1
        flag = 0
        apk_name = f.split('.')[0]  # filename
        # report.write("****************** APK " + str(file_id) + " ******************\n")
        # report.write("Apk name：" + apk_name + '\n')
        print("******************" + str(file_id) + " ******************")
        print("Apk name: " + apk_name)

        start_time = time.time()
        # if os.path.exists('../detect/output_features/' + apk_name + '_features.txt'):
        #     continue
        # else:
        match_nodes = main_module1(apk_name, f, kg_apis, kg_permissions, kg_features, apis_from_test, kg)
        print('***Match nodes***')
        list_print(match_nodes)
        matched_behaviors = []
        for node in match_nodes:
            if node['actionName'] not in matched_behaviors:
                matched_behaviors.append(node['actionName'])

        all_path, path_malicious, path_others = find_relations_all(match_nodes)
        relations_cfg = main_module2_relations(match_nodes, apk_name, f, kg_apis, kg_permissions, kg_features,
                                               apis_from_test, kg,
                                               all_path)
        kg_relations_list, cfg_relations_list, delete_kg_list, delete_cfg_list, final_graph, final_behaviors = match_relations_kgAndCfg(
            match_nodes, relations_cfg)
        for node in match_nodes:
            if node['actionName'] not in final_behaviors:
                if models.augmentNodeIn.objects.get(nodeID=node['nodeID']).independent=='1':
                    final_behaviors.append(node['actionName'])

        print('\n *****final graph*****')
        dict_print(final_graph)

        print('\n *****final behaviors*****')
        list_print(final_behaviors)

        chains, chains_for_des = generate_chains(final_graph)
        print('\n *****final chains*****')
        list_print(chains)
        print('\n')
        list_print(chains_for_des)
        end_time = time.time()
        print('\n***Complete! Time cost: {:.5f} s'.format(end_time - start_time) + '***\n')

        kg_relation_str = do_output_str(kg_relations_list, "binary")
        cfg_relations_str = do_output_str(cfg_relations_list, "binary")
        delete_kg_str = do_output_str(delete_kg_list, "binary")
        # delete_cfg_list = do_output_str(delete_cfg_list,"binary")
        matched_behaviors_str = do_output_str(matched_behaviors, "single")
        final_behaviors_str = do_output_str(final_behaviors, "single")
        final_graph_str = do_output_str(final_graph, "dict")
        chains = do_output_str(chains_for_des, "binary")
        records = [file_id, apk_name, final_behaviors_str, chains, matched_behaviors_str, kg_relation_str,
                   len(kg_relations_list), cfg_relations_str,
                   delete_kg_str, len(delete_kg_list), final_graph_str]
        with open(csv_file, "a", encoding='utf-8', newline='') as report:
            writer = csv.writer(report)
            writer.writerow(records)


ProMal()
