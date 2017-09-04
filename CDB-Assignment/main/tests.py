from django.test import TestCase

# Create your tests here.

questions = [
    {
        'id': 1,
        'content': 'First question',
        'fields': ['first', 'second'],
    },
    {
        'id': 2,
        'content': 'Second Question',
        'fields': ['first', 'second'],
    },
    {
        'id': 3,
        'content': 'Third with new line----\n\n\n----three times',
        'fields': ['first', 'second'],
    }
]


collect_test = {
    'option': {'q_type': 'COLLECT', 'task_id': 'collect_3', 'title': 'collect_test_1'},
    'questions': [{
        "id": "q-1-op-1-hit-e856e60g",
        "columns": [
            "name",
            "school",
            "birthday"
        ],
        "limit": 3,
        "content": "Please judge whether 450 is equal to 450 ?"
    }]
}