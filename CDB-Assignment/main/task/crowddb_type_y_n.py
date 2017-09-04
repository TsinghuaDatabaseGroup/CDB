
import os
import csv
import time
import crowdflower
import api_key

def upload_questions_to_crowdflower(title, job_distinguish_tag, question_content, max_judgments_per_worker = 50, units_per_assignment = 3,
                                    judgments_per_unit = 1, payment_cents = 3):
    conn = crowdflower.Connection(api_key = api_key.api_key_string)
    job = conn.upload(question_content)
    job.update({
        'title': title,
        'max_judgments_per_worker': max_judgments_per_worker,
        'units_per_assignment': units_per_assignment,
        'judgments_per_unit': judgments_per_unit,
        'payment_cents': payment_cents,
        'instructions': 
            '''
            <h3><em><strong>Semantic Discrimination</strong>&nbsp;</em></h3>
            <p><em><strong>Judge whether the two paragraphs describe the same object or not.</strong></em></p>
            ''',
        'cml': 
            '''
            <p>{{content}}</p>
            <cml:radios label="Are these two paragraphs describe the same object?" name="option" validates="required" gold="true">
                <cml:radio label="Yes, they describe the same object" value="same"></cml:radio>
                <cml:radio label="No, they describe the different object" value="different"></cml:radio>
            </cml:radios>
            ''',
        'options': {
            'front_load': 0, # quiz mode = 1; turn off with 0
        }
    })
    job.tags = [job_distinguish_tag]
    job.launch(len(question_content), channels = ('on_demand', 'cf_internal'))

def collect_answers_from_crowdflower(job_distinguish_tag):
    conn = crowdflower.Connection(api_key = api_key.api_key_string)
    rating_result = []
    out_dir = os.path.dirname(os.path.realpath(__file__)) + os.path.sep + 'cf_temp/'
    for job in conn.jobs():
        if job_distinguish_tag in job.tags:
            job.download_csv(out_dir + str(job.id) + '.csv')
            reader = csv.reader(file(out_dir + str(job.id) + '.csv', 'rb'))
            first_line = True
            for line in reader:
                for index, item in enumerate(line):
                    print index, item
                    if item == 'id':
                        id_index = index
                    if item == 'option':
                        option_index = index
                task_id = line[id_index]
                option = line[option_index]
                if option == 'same':
                    option_result = 1
                elif option == 'different':
                    option_result = 0
                worker_id = line[7]
                if first_line == False:
                    rating_result.append([task_id, worker_id, option_result])
                    print [task_id, worker_id, option_result]
                else:
                    first_line = False
    return job_distinguish_tag, rating_result

