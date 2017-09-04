### AMT platform interface
import mtc
from boto.mturk.connection import MTurkConnection, MTurkRequestError
from boto.mturk.question import QuestionForm, SimpleField, QuestionContent, \
    AnswerSpecification, Question, SelectionAnswer, Constraints, LengthConstraint, \
    FreeTextAnswer, Overview, FormattedContent
from main.models import Hit, Task, Question as QuestionModel, Answer
from cgi import escape as url_escape
free_sep = '::'


def make_image_content(url):
    url = url_escape(url)
    template = '<img src="%(url)s" alt="" width="400" />'
    return template % vars()


def make_question_form(questions):
    if not questions:
        raise ValueError('Questions cannot be empty!')
    question_form = QuestionForm()

    for q in questions:
        qid = q['id']
        q_text = SimpleField('Text', q['content'])
        q_content = QuestionContent([q_text])
        selections = [('Yes', 1), ('No', 0)]
        answer_spec = AnswerSpecification(SelectionAnswer(style='radiobutton', selections=selections))
        question = Question(qid, q_content, answer_spec, True)
        question_form.append(question)

    return question_form


def make_free_text_question_form(questions):
    if not questions:
        raise ValueError('Questions cannot be empty!')
    question_form = QuestionForm()

    for q in questions:
        qid = q['id']
        for field in q['columns']:
            if field == q['columns'][0]:
                hint = SimpleField('Title', q['content'])
                question_form.append(Overview([hint]))
            field_id = str(qid) + free_sep + field
            q_text = SimpleField('Text', field)
            q_content = QuestionContent([q_text])
            cons = Constraints([LengthConstraint(min_length=1, max_length=100)])
            answer_spec = AnswerSpecification(FreeTextAnswer(constraints=cons))
            question = Question(field_id, q_content, answer_spec, True)
            question_form.append(question)

    return question_form


def make_many_to_one_form(questions):
    if not questions:
        raise ValueError('Questions cannot be empty!')
    question_form = QuestionForm()

    for q in questions:
        qid = q['id']
        q_text = SimpleField('Text', q['content'])
        contents = [q_text]
        if q.has_key('url'):
            contents.append(FormattedContent(make_image_content(q['url'])))
        q_content = QuestionContent(contents)
        selections = [(i, i) for i in q['options']]
        answer_spec = AnswerSpecification(SelectionAnswer(style='radiobutton', selections=selections))
        question = Question(qid, q_content, answer_spec, True)
        question_form.append(question)

    return question_form


def make_many_to_many_form(questions):
    if not questions:
        raise ValueError('Questions cannot be empty!')
    question_form = QuestionForm()

    for q in questions:
        qid = q['id']
        q_text = SimpleField('Text', q['content'])
        contents = [q_text]
        if q.has_key('url'):
            contents.append(FormattedContent(make_image_content(q['url'])))
        q_content = QuestionContent(contents)
        selections = [(i, i) for i in q['options']]
        answer_spec = AnswerSpecification(SelectionAnswer(min=1, max=len(q['options']), style='checkbox', selections=selections))
        question = Question(qid, q_content, answer_spec, True)
        question_form.append(question)

    return question_form


def create_hit(question, max_assignments, title, description,
               keywords, lifetime, duration, approval_delay, reward):
    hit, = mtc.mtc.create_hit(
        question=question, max_assignments=max_assignments, title=title,
        description=description, keywords=keywords, lifetime=lifetime,
        duration=duration, approval_delay=approval_delay, reward=reward)
    return hit


def is_complete(taskid):
    hits = Hit.objects.filter(task__task_id=taskid).values_list('hit_id', flat=True)

    for hitID in hits:
        try:
            hit, = mtc.mtc.get_hit(hitID)
            if hit.HITStatus != 'Reviewable':
                return False
        except MTurkRequestError as e:
            return False

    return True


def populate_questions(option, questions):
    q_type = option['q_type']
    if q_type == COLLECT:
        qs = []
        q = questions[0]
        qid = q['id']
        limit = q['limit']
        repeats = q['repeats']
        for i in range(limit * repeats):
            temp_q = {
                'id': qid + free_sep + str(i),
                'content': q['content'],
                'columns': q['columns'],
            }
            qs.append(temp_q)

    else:
        qs = questions

    return qs


def insert_questions(taskid, questions):
    created_qs = []
    task = Task.objects.get(task_id=taskid)
    for idx, q in enumerate(questions):
        created_qs.append(QuestionModel(id=q['id'], content=q['content'], sequence=idx, task=task))
    QuestionModel.objects.bulk_create(created_qs)


def publish(option, questions):
    taskid = option['task_id']
    questions = populate_questions(option, questions)

    insert_questions(taskid, questions)
    question_num = len(questions)
    q_per_hit = 10
    hit_num = question_num / q_per_hit
    if question_num % q_per_hit:
        hit_num += 1

    question_func = PUBLISH_HANDLERS.get(option['q_type'], make_question_form)
    created_qs = []
    for i in range(hit_num - 1):
        temp_qs = question_func(questions[i * q_per_hit: (i + 1) * q_per_hit])
        created_qs.append(temp_qs)

    temp_qs = question_func(questions[(hit_num - 1) * q_per_hit:])
    created_qs.append(temp_qs)

    keywords = option.get('keyword', 'key...')
    description = 'description....'
    lifetime = 1 * 1 * 3600
    duration = 15 * 60
    approval_delay = 1 * 3 * 3600
    reward = 0.03
    title = option.get('title', 'test_heihei')
    max_assignments = 1

    created_hits = []
    task = Task.objects.get(task_id=taskid)
    for qs in created_qs:
        hit = create_hit(qs, max_assignments, title, description,
                         keywords, lifetime, duration, approval_delay, reward)
        created_hits.append(Hit(hit_id=hit.HITId, task=task))

    Hit.objects.bulk_create(created_hits)


def get_hit_result(hitID):
    answers = []
    result = mtc.mtc.get_assignments(hitID)
    for assignment in result:
        worker = assignment.WorkerId
        for question_form_answer in assignment.answers[0]:
            question = question_form_answer.qid
            answer = question_form_answer.fields[0]
            answers.append([question, worker, answer])

    return answers


def get_many_to_many_result(hitID):
    answers = []
    result = mtc.mtc.get_assignments(hitID)
    for assignment in result:
        worker = assignment.WorkerId
        for question_form_answer in assignment.answers[0]:
            question = question_form_answer.qid
            answer = []
            for selected_item in question_form_answer.fields:
                answer.append(selected_item)
            answers.append([question, worker, answer])

    return answers


def get_free_result(hitID):
    answers = []
    result = mtc.mtc.get_assignments(hitID)
    for assignment in result:
        worker = assignment.WorkerId
        old = 0
        for question_form_answer in assignment.answers[0]:
            qid = question_form_answer.qid
            qid, field = qid.rsplit(free_sep, 1)
            if qid != old:
                if old != 0:
                    answers.append([old, worker, answer_dict])
                answer_dict = {field: question_form_answer.fields[0]}
                old = qid
            else:
                answer_dict[field] = question_form_answer.fields[0]
                old = qid
        answers.append([old, worker, answer_dict])

    return answers


def get_result(task_id, q_type):
    hits = Hit.objects.filter(task__task_id=task_id).values_list('hit_id', flat=True)
    result = []
    result_func = RESULT_HANDLERS.get(q_type, get_hit_result)
    for hitID in hits:
        temp_result = result_func(hitID)
        result.extend(temp_result)
    return result

from task_const import *

PUBLISH_HANDLERS = {
    Y_N: make_question_form,
    M_TO_O: make_many_to_one_form,
    M_TO_M: make_many_to_many_form,
    FREE: make_free_text_question_form,
    COLLECT: make_free_text_question_form,
}

RESULT_HANDLERS = {
    Y_N: get_hit_result,
    M_TO_O: get_hit_result,
    FREE: get_free_result,
    M_TO_M: get_many_to_many_result,
    COLLECT: get_free_result,
}
