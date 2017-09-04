
import os
import sys
sys.path.append("..")
import csv
import crowdflower
import api_key

def query_the_status_of_the_job(job_distinguish_tag):
    conn = crowdflower.Connection(api_key = api_key.api_key_string)
    for job in conn.jobs():
        if job_distinguish_tag in job.tags:
            status_of_the_job = job.properties['state']
            break
    print status_of_the_job
    return status_of_the_job == 'finished'

def upload_questions_to_crowdflower(title, job_distinguish_tag, question_content, max_judgments_per_worker = 50, units_per_assignment = 3,
                                    judgments_per_unit = 1, payment_cents = 3):
    
    conn = crowdflower.Connection(api_key = api_key.api_key_string)
    job = conn.upload(question_content)
    options = question_content[0]['options']
    first_line = '''
    <img src="{{url}}" id="">
    <p>{{content}}</p>
    <cml:checkboxes validates="required" label="Choose the best option" name="category" class="">
    '''
    question_strings = first_line
    for i in range(len(options)):
        item = '''<cml:checkbox label="''' + str(options[i]) + '''"  value = "fill_''' + str(options[i]) + '''" />'''
        question_strings = question_strings + item + '\n'
    question_strings = question_strings + '''</cml:checkboxes>'''
    
    job.update({
        'title': title,
        'max_judgments_per_worker': max_judgments_per_worker,
        'units_per_assignment': units_per_assignment,
        'judgments_per_unit': judgments_per_unit,
        'payment_cents': payment_cents,
        'instructions': 
            '''
            <h3><em><strong>Tell us the details about the country</strong>&nbsp;</em></h3>
            ''',
        'cml': 
            question_strings
            ,
        'options': {
            'front_load': 0, # quiz mode = 1; turn off with 0
        }
    })
    job.tags = [job_distinguish_tag]
    job.launch(len(question_content), channels = ('on_demand', 'cf_internal'))

def collect_answers_from_crowdflower(job_distinguish_tag):
    conn = crowdflower.Connection(api_key = api_key.api_key_string)
    result = []
    out_dir = os.path.dirname(os.path.realpath(__file__)) + os.path.sep + 'cf_temp/'
    for job in conn.jobs():
#         if job_distinguish_tag in job.tags and job.properties['state'] == 'finished':
        if job_distinguish_tag in job.tags:
            job.download_csv(out_dir + str(job.id) + '.csv')
            reader = csv.reader(file(out_dir + str(job.id) + '.csv', 'rb'))
            first_line = True
            for line in reader:
                if first_line == True:
                    first_line = False
                    for index, item in enumerate(line):
                        if item == 'id':
                            id_index = index
                        if item == 'category':
                            category_index = index
                else:
                    worker_id = line[7]
                    unique_id = line[id_index]
                    option_single_answer_list = line[category_index].split('\n')
                    option_single_answer = []
                    for item in option_single_answer_list:
                        option_single_answer.append(item.split('_')[1])
                    result.append((unique_id, worker_id, option_single_answer))
                    print (unique_id, worker_id, option_single_answer)
            break
    return job_distinguish_tag, result

if __name__ == "__main__":
    question_content = [
        {'id': '1', 'content': 'Germany', 'url': 'http://farm9.staticflickr.com/8199/8252746471_7cd4cccc3b_n.jpg', 'options':['A', 'B', 'C']},
        {'id': '2', 'content': 'China', 'url':'http://farm8.staticflickr.com/7287/8745135210_b556f8f586_n.jpg', 'options':['A', 'B', 'C']},
        {'id': '3', 'content': 'USA', 'url':'http://farm9.staticflickr.com/8195/8093245580_d7c95a2eca_n.jpg', 'options':['A', 'B', 'C']}
    ]
    job_distinguish_tag = 'test-a-3'
    query_the_status_of_the_job(job_distinguish_tag)
    collect_answers_from_crowdflower(job_distinguish_tag)
#     upload_questions_to_crowdflower(job_distinguish_tag, question_content)



