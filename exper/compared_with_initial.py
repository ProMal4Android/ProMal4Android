import csv
import re
import time

from operator import itemgetter, eq
from urllib.parse import urlparse

import django
import sys
import os
from exper.program_analysis import backwardDataflow, forwardDataflow, forwardDataflow_plus, key_sort_group, \
    backwardDataflow_plus
from tools.utils import analyse, get_data, generate_cg, gml_txt, do_feature_file, api_sdk_sim, list_print, \
    create_cfg_specific

sys.path.append('../')

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'mwep.settings')

django.setup()

from common import models

output_file = '../exper/output/compared_initial_results.csv'
input_file='../exper/input/input.csv'
output_rel_file='../exper/output/rel_output.csv'


def split_sequence(source):
    """divide sequence to two nodes and their relations"""
    all_list=[]
    all_rel=''
    all_str_list=[]
    nodes=[]
    sequence_list=source.split('\n')
    for seq in sequence_list:
        order=seq.split(' -> ')
        for index,value in enumerate(order):
            if value not in nodes:
                nodes.append(value)
            if index<len(order)-2:
                tmp=[value,order[index+1]]
                if tmp not in all_list:
                    all_list.append(tmp)
                    all_rel=all_rel+tmp[0]+' -> '+tmp[1]+'\n'
                    all_str_list.append(tmp[0] + ' - >' + tmp[1])
    all_rel=all_rel.strip('\n')
    return all_list,all_rel,nodes,all_str_list


def query_initial_rel(nodes):
    new_nodes_id=[]
    all_list=[]
    all_str_list = []
    all_str=''
    for node in nodes:
        try:
            # nodeID=models.augmentNodeIn.objects.get(actionName=node).nodeID
            nodeID_ans = models.augmentNodeIn.objects.filter(actionName=node)
            if nodeID_ans:
                for one in nodeID_ans:
                    if one.nodeID not in new_nodes_id:
                        new_nodes_id.append(one.nodeID)
            # oldName=models.KgBackup.objects.get(nodeID=nodeID).actionName
        except Exception as e:
            print('error action & new id & old name:',node)

    for node_id in new_nodes_id:
        source=models.relBackup.objects.filter(targetID=node_id)
        if source:
            for one in source:
                source_id=one.sourceID
                if source_id in new_nodes_id:
                    sourceAction=models.augmentNodeIn.objects.get(nodeID=source_id).actionName
                    targetAction=models.augmentNodeIn.objects.get(nodeID=node_id).actionName
                    tmp=[sourceAction,targetAction]
                    if tmp not in all_list:
                        all_list.append(tmp)
                        all_str_list.append(sourceAction+' - >'+targetAction)
                        all_str = all_str + tmp[0] + ' -> ' + tmp[1] + '\n'

        target = models.relBackup.objects.filter(sourceID=node_id)
        if target:
            for one in target:
                target_id = one.targetID
                if target_id in new_nodes_id:
                    sourceAction = models.augmentNodeIn.objects.get(nodeID=node_id).actionName
                    targetAction = models.augmentNodeIn.objects.get(nodeID=target_id).actionName
                    tmp = [sourceAction, targetAction]
                    if tmp not in all_list:
                        all_list.append(tmp)
                        all_str_list.append(sourceAction + ' - >' + targetAction)
                        all_str = all_str + sourceAction+' -> '+targetAction + '\n'
        all_str = all_str.strip('\n')

        return all_list,all_str, all_str_list


def main_process():
    flag_id=0
    with open(input_file, 'r') as input, open(output_file, "a", encoding='utf-8', newline='') as output:
        csv_reader = csv.reader(input)
        writer = csv.writer(output)
        for row in csv_reader:
            if flag_id == 0:
                writer.writerow(
                    ["ID", "Apk Name", "Source", "Current Results", "Initial Results", "Intersection", "Difference",
                     "Initial Num",
                     "Diff Num"])
                flag_id=flag_id+1
                continue

            apkName=row[0]
            source=row[1]
            # print('source:',source)
            current_results,cut_strs,nodes,cut_str_list=split_sequence(source)
            # print('cut_results:',current_results)
            # inital_results,init_strs,init_str_list=query_initial_rel(nodes)
            # inset=list(set(cut_str_list).intersection(set(init_str_list)))
            # diff=list(set(cut_str_list).difference(set(inset)))
            # inset_str,diff_str='',''
            # for one in inset:
            #     inset_str=inset_str+one[0]+' -> '+one[1]+'\n'
            # for one in diff:
            #     diff_str=diff_str+one[0]+' -> '+one[1]+'\n'
            # inset_str=inset_str.split('\n')
            # diff_str = diff_str.split('\n')
            # record=[flag_id,apkName,source,cut_strs,init_strs,inset_str,diff_str,len(inset),len(diff)]
            record=[flag_id,apkName,source,cut_strs]
            writer.writerow(record)

            flag_id=flag_id+1

# main_process()

def output_rel():
    rel_object=list(models.relBackup.objects.values())
    with open(output_rel_file, "w", encoding='utf-8', newline='') as output:
        writer = csv.writer(output)
        for one in rel_object:
            sourceId=one['sourceID']
            sourceAct=models.KgBackup.objects.get(nodeID=sourceId).actionName
            targetId=one['targetID']
            targetAct=models.KgBackup.objects.get(nodeID=targetId).actionName
            writer.writerow([sourceAct,targetAct])

output_rel()






