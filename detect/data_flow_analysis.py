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

