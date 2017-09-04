### chinacrowd interface
import chinacrowd

api = chinacrowd.ChinaBaoApi()


def publish(option, questions):
    api.TaskCreated(questions, option['task_id'], option['q_type'], option['title'])


def is_complete(taskid):
    return api.TaskDetected(taskid)


def get_result(taskid, q_type):
    return api.TaskResult(taskid, q_type)