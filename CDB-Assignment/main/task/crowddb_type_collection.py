
import os
import sys
sys.path.append("..")
import csv
import crowdflower
import api_key

def upload_questions_to_crowdflower(title, job_distinguish_tag, question_content, max_judgments_per_worker, units_per_assignment,
                                    judgments_per_unit, payment_cents):
    
    conn = crowdflower.Connection(api_key = api_key.api_key_string)
    refined_question_content = []
    columns_content = question_content[0]['columns']
    for i in range(len(question_content)):
        limit = int(question_content[i]['limit'])
        repeats = int(question_content[i]['repeats'])
        total_repeat_times = limit * repeats
        new_dict = {}
        for key in question_content[i]:
            if key != 'columns':
                new_dict[key] = question_content[i][key]
        count = 0
        for item in columns_content:
            if '.' in item:
                first_part = item.split('.')[0]
                second_part = item.split('.')[1]
                new_dict[str(first_part) + '_dot_' + str(second_part) + 'answer_attr' + str(count)] = item.split('.')[1]
                count += 1
            elif '.' not in item:
                new_dict[str(item) + 'answer_attr' + str(count)] = item
                count += 1
        new_dict['number_counting'] = str(i)
        for j in range(total_repeat_times):
            refined_question_content.append(new_dict)
    
    job = conn.upload(refined_question_content)
    
    first_line = "<p>{{content}}</p>"
    question_strings = first_line
    for i in range(len(columns_content)):
        if '.' in columns_content[i]:
            first_part = str(columns_content[i].split('.')[0])
            second_part = str(columns_content[i].split('.')[1])
            item_first_line = "<p>{{" + first_part + '_dot_' + second_part + 'answer_attr' + str(i) + "}}</p>"
            item_second_line = '''<cml:text label="" validates="required" gold="true" name = "fill_''' + str(i) + '''1" />'''
            item = item_first_line + item_second_line
            question_strings = question_strings + item
        elif '.' not in columns_content[i]:
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
    job.launch(len(refined_question_content), channels = ('on_demand', 'cf_internal'))

def collect_answers_from_crowdflower(job_distinguish_tag):
    conn = crowdflower.Connection(api_key = api_key.api_key_string)
    result = []
    out_dir = os.path.dirname(os.path.realpath(__file__)) + '/cf_temp'
    for job in conn.jobs():
        if job_distinguish_tag in job.tags:
            job.download_csv(out_dir + os.path.sep + str(job.id) + '.csv')
            reader = csv.reader(file(out_dir + os.path.sep + str(job.id) + '.csv', 'rb'))
            first_line = True
            for line in reader:
                if first_line == True:
                    for index, item in enumerate(line):
                        if item == 'id':
                            id_index = index
                    col_list = {}
                    for index, item in enumerate(line):
                        if "answer_attr" in item:
                            attr_string = item.split("answer_attr")[1]
                            if '_dot_' in item:
                                attr_col_string_list = item.split("answer_attr")[0].split('_dot_')
                                attr_col_string = attr_col_string_list[0] + '.' + attr_col_string_list[1]
                                col_list[int(attr_string)] = attr_col_string
                            elif '_dot_' not in item:
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
                    else:
                        first_line = False
    return job_distinguish_tag, result


