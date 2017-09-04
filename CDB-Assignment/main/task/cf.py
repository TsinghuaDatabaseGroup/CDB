### crowdflower interface
import crowddb_crowdflower


def publish(option, questions):
    crowddb_crowdflower.design_question(option['q_type'], option['task_id'], questions)


def is_complete(taskid):
    return crowddb_crowdflower.query_the_status_of_the_job(taskid)


def get_result(taskid, q_type):
    return crowddb_crowdflower.get_answers(taskid, q_type)
