
import os
import sys
sys.path.append("..")
import csv
import crowdflower
import api_key

def upload_questions_to_crowdflower(title, job_distinguish_tag, question_content, max_judgments_per_worker = 50, units_per_assignment = 3,
                                    judgments_per_unit = 1, payment_cents = 3):
    conn = crowdflower.Connection(api_key = api_key.api_key_string)
    refined_question_content = []
    columns_content = question_content[0]['columns']
    for i in range(len(question_content)):
        new_dict = {}
        for key in question_content[i]:
            if key != 'columns':
                new_dict[key] = question_content[i][key]
        count = 0
        for item in columns_content:
            new_dict[str(item) + "answer_attr" + str(count)] = item
            count += 1
        refined_question_content.append(new_dict)
    job = conn.upload(refined_question_content)
    first_line = "<p>{{content}}</p>"
    question_strings = first_line
    for i in range(len(columns_content)):
        item_first_line = "<p>{{" + columns_content[i] + "answer_attr" + str(i) + "}}</p>"
        item_second_line = '''<cml:text label="" validates="required" gold="true" name = "fill_''' + str(i) + '''1" />'''
        item = item_first_line + item_second_line
        question_strings = question_strings + item
    
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
            question_strings
            ,
        'options': {
            'front_load': 0, # quiz mode = 1; turn off with 0
        }
    })
    job.tags = [job_distinguish_tag]
    job.launch(len(refined_question_content), channels = ('on_demand', 'cf_internal'))

def collect_answers_from_crowdflower(job_distinguish_tag):
    conn = crowdflower.Connection(api_key = api_key.api_key_string)
    result = []
    out_dir = os.path.dirname(os.path.realpath(__file__)) + '/cf_temp'
    for job in conn.jobs():
        if job_distinguish_tag in job.tags:
            job.download_csv(out_dir + str(job.id) + '.csv')
            reader = csv.reader(file(out_dir + str(job.id) + '.csv', 'rb'))
            first_line = True
            count_col = 0
            for line in reader:
                if first_line == True:
                    for index, item in enumerate(line):
                        if item == 'id':
                            id_index = index
                        if "answer_attr" in item:
                            count_col += 1
                    col_list = {}
                    for index, item in enumerate(line):
                        if "answer_attr" in item:
                            attr_string = item.split("answer_attr")[1]
                            col_list[int(attr_string)] = item.split("answer_attr")[0]
                    fill_list = []
                    for index, item in enumerate(line):
                        if 'fill' in item and 'gold' not in item:
                            number_this_time = int(item[-2])
                            fill_list.append((number_this_time, index))
                    first_line = False
                else:
                    worker_id = line[7]
                    attr_answers = {}
                    unique_id = line[id_index]
                    for i in range(len(fill_list)):
                        attr_answers[col_list[i]] = line[fill_list[i][1]]
                    if first_line == False:
                        result.append((unique_id, worker_id, attr_answers))
                        print (unique_id, worker_id, attr_answers)
                    else:
                        first_line = False
            break
    return job_distinguish_tag, result

if __name__ == "__main__":
    question_content = [
        {'id': '1','content': 'iPad Two 16GB WiFi White. iPad 2nd generation 16GB WiFi White.', 'columns':['name', 'school', 'birthday']},
        {'id': '2', 'content':'iPhone 4th generation White 16GB. Apple iPhone 3rd generation Black 16GB.', 'columns':['name', 'school', 'birthday']},
        {'id': '3', 'content':'Apple iPod shuffle 2GB Blue. Apple iPod shuffle USB Cable.', 'columns':['name', 'school', 'birthday']}
    ]
    job_distinguish_tag = '12.14.2'
#     collect_answers_from_crowdflower(job_distinguish_tag)
    upload_questions_to_crowdflower(job_distinguish_tag, question_content)

