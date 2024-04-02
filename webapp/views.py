import shutil

from django.shortcuts import render
import json
import os

from django.http import HttpResponse
from webapp import origin_plus

from common import models
from common.models import ApiTest, PerTest, KgBackup


# Create your views here.
def get_upload_file(request):
    try:
        print('get upload file')
        received_file = request.FILES.get("file")  # upload_name是input按钮的name，必须一样
        filename = os.path.join("webapp/uploadFiles", received_file.name)
        print('filename:', filename)
        saveFile(received_file, filename)
        return HttpResponse(status=200)
    except Exception as e:
        return HttpResponse(e)


def saveFile(received_file, filename):
    with open(filename, 'wb') as f:
        f.write(received_file.read())


def readFile(filename):
    with open(filename, 'r') as f:
        content = f.read()
    return content


def get_deal_status(request):
    # 构造json返回内容，通过HttpResponse返回
    try:
        name = request.POST.get('filename')
        print('name', type(name))
        data = origin_plus.XMalChain_v1(name)
        # tmp = {}
        # data = json.loads(json.dumps(tmp))
        # data['status'] = 30
        ret = json.dumps(data, ensure_ascii=False)
        return HttpResponse(ret, status=200)
    except Exception as e:
        return HttpResponse(e)
