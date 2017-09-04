### table models definition, save tasks, as well as questions and answers info
from __future__ import unicode_literals

from django.db import models


class Task(models.Model):
    PLATFORMS = (
        ('AMT', 'Amazon Mechanical Turk'),
        ('CF', 'CrowdFlower'),
        ('CC', 'ChinaCrowd'),
    )
    Q_TYPES = (
        ('Y_N', 'Yes or No'),
        ('M_TO_O', 'Many to One'),
        ('M_TO_M', 'Many to Many'),
        ('FREE', 'Free text'),
        ('COLLECT', 'Collection'),
    )
    task_id = models.CharField(max_length=20, primary_key=True)
    platform = models.CharField(max_length=10, choices=PLATFORMS)
    q_type = models.CharField(max_length=10, choices=Q_TYPES)
    created_time = models.DateTimeField(auto_now_add=True)


class Hit(models.Model):
    hit_id = models.CharField(max_length=50, primary_key=True)
    task = models.ForeignKey(Task)


class Question(models.Model):
    id = models.CharField(max_length=50, primary_key=True)
    task = models.ForeignKey(Task)
    content = models.CharField(max_length=500)
    sequence = models.IntegerField()


class Answer(models.Model):
    question = models.ForeignKey(Question)
    worker = models.CharField(max_length=50)
    answer = models.IntegerField()


class TaskProject(models.Model):
    task_db = models.CharField(max_length=20)
    task_bao = models.CharField(max_length=200)


class QueTask(models.Model):
    task_id = models.IntegerField()
    que_id = models.TextField()
    attr_id = models.TextField()
    unit_num = models.IntegerField(default=1)
    need_url = models.CharField(max_length=20, default="no")
