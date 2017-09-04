### assign tasks to different platforms
from main.models import Task
from infer import EM
from task_const import *

def get_task_publisher(taskid):
    task = Task.objects.get(task_id=taskid)
    return platforms.get(task.platform, amt)


def alter_questions(option, questions):
    if option['q_type'] == COLLECT:
        questions[0]['repeats'] = 2


def publish(option, questions, platform):
    platform = platform.strip()
    if platform not in platforms:
        platform = default_platform
    Task.objects.create(task_id=option['task_id'], platform=platform, q_type=option['q_type'])
    publisher = platforms.get(platform, amt)
    alter_questions(option, questions)
    publisher.publish(option, questions)


def is_complete(taskid):
    return get_task_publisher(taskid).is_complete(taskid)


def get_all_result(taskid, q_type):
    return get_task_publisher(taskid).get_result(taskid, q_type)


def get_aggregated_result(taskid):
    task = Task.objects.get(pk=taskid)
    result = get_all_result(taskid, task.q_type)
    if task.q_type == Y_N:
        result = EM.infer(result)
        for i in result:
            result[i] = int(result[i])
        result_dict = {}
        for i in result:
            result_dict[i] = {'answer': result[i], 'id': i}
        result = result_dict
    elif task.q_type == M_TO_O:
        result = EM.infer(result)
        result_dict = {}
        for i in result:
            result_dict[i] = {'answer': [result[i]], 'id': i}
        result = result_dict
    elif task.q_type == FREE:
        result_dict = {}
        for line in result:
            # line: [q, w, a]
            result_dict[line[0]] = {'answer': line[2], 'id': line[0]}
        result = result_dict
    elif task.q_type == M_TO_M:
        result_dict = {}
        for line in result:
            result_dict[line[0]] = {'answer': line[2], 'id': line[0]}
        result = result_dict
    elif task.q_type == COLLECT:
        result_dict = {}
        qid = result[0][0].rsplit(free_sep, 1)[0]
        answer = []
        for line in result:
            answer.append(line[2])
        result_dict[qid] = {'answer': answer, 'id': qid}
        result = result_dict

    return result


if __name__ == '__main__':
    # option = {'q_per_hit': 2}
    # questions = [
    #     {
    #         'id': 1,
    #         'content': 'First question'
    #     },
    #     {
    #         'id': 2,
    #         'content': 'Second Question'
    #     },
    #     {
    #         'id': 3,
    #         'content': 'Third with new line----\n\n\n----three times'
    #     }
    # ]
    #
    # publish(option, questions, "AMT")
    get_all_result('1010')

