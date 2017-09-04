### http requests handlers
from django.shortcuts import render
from django.http import HttpResponse, JsonResponse
import random
import json
from task import assign
import traceback

test_tasks = {}


def index(request):
    return HttpResponse('works!')


def upload(request):
    data = json.loads(request.POST['data'])
    option = data['options']
    questions = data['questions']
    test_tasks[option['task_id']] = {'option': option, 'questions': questions}
    res = {}
    print option, questions
    platform = option['platform']
    code = 1
    try:
        assign.publish(option, questions, platform)
        code = 0
    except Exception as e:
        print '[[ERROR]] -----upload-----'
        print option
        traceback.print_exc()
    res['code'] = code
    return JsonResponse(res, safe=False)


def check(request):
    task_id = request.GET['task_id']
    code = 1
    try:
        status = assign.is_complete(task_id)
        code = 0
    except Exception as e:
        print '[[ERROR]] -----check-----: ', task_id
        traceback.print_exc()
    res = {'code': code}
    if code == 0:
        res['status'] = status
    return JsonResponse(res, safe=False)


def results(request):
    task_id = request.GET['task_id']
    code = 1
    try:
        task_results = assign.get_aggregated_result(task_id)
        code = 0
    except Exception as e:
        print '[[ERROR]] -----results-----: ', task_id
        traceback.print_exc()
    res = {'code': code}
    if code == 0:
        res['data'] = task_results
        print task_results

    return JsonResponse(res, safe=False)
