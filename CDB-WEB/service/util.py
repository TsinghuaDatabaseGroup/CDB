# -*- coding:utf-8 -*-
import json
from functools import wraps
import hashlib

from flask import jsonify, request
from bson import ObjectId

from datetime import datetime, timedelta
#import pytz
import time

from math import radians, cos, sin, asin, sqrt
import math

import urllib2
import re

import imghdr
import StringIO

from checks import *
from error import error, success

def password_hash(string):
    return hashlib.md5(string).hexdigest()


def datetime_round2day(dt):
	return dt - timedelta(hours=dt.hour, minutes=dt.minute, seconds=dt.second)

def datetime2timestamp(dt):
	return int(time.mktime(dt.timetuple()))

def timestamp2datetime(stamp, timezone_str="UTC"):
	tzinfo = pytz.timezone(timezone_str)
	return datetime.fromtimestamp(stamp).replace(tzinfo=pytz.utc).astimezone(tzinfo)

def get_current_datetime():
	return datetime.utcnow().replace(tzinfo=pytz.utc)

def get_current_timestamp():
	return datetime2timestamp(get_current_datetime())

def image_type(content):
	return imghdr.what(StringIO.StringIO(content))

class json_api(object):

    checkers = None

    def __init__(self, **checkers):
        self.checkers = checkers

    def __call__(self, func):
        @wraps(func)
        def wrapped(*args, **kwargs):
            if request.method != 'GET':
                j = None
                try:
                    j = json.loads(request.data)
                except ValueError, e:
                    return jsonify(error('JSON_PARSE'))
                errs = []
                if not all([check_one_field(
                        j, k, v, errs) for k, v in self.checkers.iteritems()]):
                    return jsonify(error('ARGUMENTS', remark=errs))
                kwargs.update(
                    {k: j[k] for k in self.checkers if k in j})
            return jsonify(func(*args, **kwargs))
        return wrapped
