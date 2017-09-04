
import sys
sys.path.append("..")
import crowdflower
import api_key
import crowddb_type_y_n, crowddb_type_fillin, crowddb_type_collection, crowddb_type_one_label, crowddb_type_multi_label

def query_the_status_of_the_job(job_distinguish_tag):
    conn = crowdflower.Connection(api_key = api_key.api_key_string)
    for job in conn.jobs():
        if job_distinguish_tag in job.tags:
            status_of_the_job = job.properties['state']
            break
    print status_of_the_job
    return status_of_the_job == 'finished'
'''
def design_question(question_type, job_distinguish_tag, question_content, max_judgments_per_worker = 50, units_per_assignment = 3,
                                    judgments_per_unit = 1):
'''
def design_question(options, question_content, max_judgments_per_worker = 50, units_per_assignment = 3,
                                    judgments_per_unit = 1):
    question_type = options['q_type']
    job_distinguish_tag = options['task_id']
    title = options['title']
    
    payment_cents = units_per_assignment * 1
    if question_type == 'FREE':
        crowddb_type_fillin.upload_questions_to_crowdflower(title, job_distinguish_tag, question_content, max_judgments_per_worker, 
                                                            units_per_assignment, judgments_per_unit, payment_cents)
    if question_type == 'Y_N':
        crowddb_type_y_n.upload_questions_to_crowdflower(title, job_distinguish_tag, question_content, max_judgments_per_worker, 
                                                         len(question_type),judgments_per_unit, payment_cents)
    if question_type == 'COLLECT':
        summing = 0
        for item in question_content:
            summing += int(item['limit']) * int(item['repeats'])
        try:
            crowddb_type_collection.upload_questions_to_crowdflower(title, job_distinguish_tag, question_content, max_judgments_per_worker, 
                                                                    summing, judgments_per_unit, payment_cents)
        except Exception, e:
            print e
            crowddb_type_collection.upload_questions_to_crowdflower(title, job_distinguish_tag, question_content, max_judgments_per_worker, 
                                                                    summing / 2, judgments_per_unit, payment_cents)
    
    if question_type == 'M_TO_O':
        crowddb_type_one_label.upload_questions_to_crowdflower(title, job_distinguish_tag, question_content, max_judgments_per_worker,
                                                                units_per_assignment, judgments_per_unit, payment_cents)
    if question_type == 'M_TO_M':
        crowddb_type_multi_label.upload_questions_to_crowdflower(title, job_distinguish_tag, question_content, max_judgments_per_worker, 
                                                                 units_per_assignment, judgments_per_unit, payment_cents)
        
def get_answers(job_distinguish_tag, question_type):
    if question_type == 'FREE':
        job_distinguish_tag, result = crowddb_type_fillin.collect_answers_from_crowdflower(job_distinguish_tag)
    elif question_type == 'Y_N':
        job_distinguish_tag, result = crowddb_type_y_n.collect_answers_from_crowdflower(job_distinguish_tag)
    elif question_type == 'COLLECT':
        job_distinguish_tag, result = crowddb_type_collection.collect_answers_from_crowdflower(job_distinguish_tag)
    elif question_type == 'M_TO_O':
        job_distinguish_tag, result = crowddb_type_one_label.collect_answers_from_crowdflower(job_distinguish_tag)
    elif question_type == 'M_TO_M':
        job_distinguish_tag, result = crowddb_type_multi_label.collect_answers_from_crowdflower(job_distinguish_tag)
    return result
    
    
if __name__ == "__main__":
    question_content = [
        {'id': '1', 'content': 'Germany', 'url': 'http://farm9.staticflickr.com/8199/8252746471_7cd4cccc3b_n.jpg', 'options':['A', 'B', 'C']},
        {'id': '2', 'content': 'China', 'url':'http://farm8.staticflickr.com/7287/8745135210_b556f8f586_n.jpg', 'options':['A', 'B', 'C']},
        {'id': '3', 'content': 'USA', 'url':'http://farm9.staticflickr.com/8195/8093245580_d7c95a2eca_n.jpg', 'options':['A', 'B', 'C']}
    ]
    '''
    question_content = [
        {'id': '1','content': 'Germany', 'columns':['continent', 'capital', 'population'], 'limit':1, 'repeats':1},
        {'id': '2', 'content':'Denmark', 'columns':['continent', 'capital', 'population'], 'limit':1, 'repeats':1},
        {'id': '3', 'content':'United States', 'columns':['continent', 'capital', 'population'], 'limit':1, 'repeats':1}
    ]
    question_content = [
         {'id': '1','content': 'iPad Two 16GB WiFi White. iPad 2nd generation 16GB WiFi White.', 'columns':['name', 'school', 'birthday']},
        {'id': '2', 'content':'iPhone 4th generation White 16GB. Apple iPhone 3rd generation Black 16GB.', 'columns':['name', 'school', 'birthday']},
        {'id': '3', 'content':'Apple iPod shuffle 2GB Blue. Apple iPod shuffle USB Cable.', 'columns':['name', 'school', 'birthday']}
    ]
    '''
    options = {
        "reward": "0.01",
        "keywords": "Comparison",
        "approval_delay": "15",
        "lifetime": "15",
        "description": "Please compare those two entities",
        "task_id": "task-M_TO_O",
        "title": "Select One Option",
        "platform": "CC",
        "duration": "120",
        "max_assignments": "5",
        "task_type": "crowd_eq",
        "task_category": "text",
        "q_type": "M_TO_O",
    }
    tag = 'task-9de1641a'
#     design_question(options, question_content)
    query_the_status_of_the_job(tag)
    result = get_answers(tag, 'FREE')
    
    for item in result:
        print item
    
    
