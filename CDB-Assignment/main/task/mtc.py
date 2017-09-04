### AMT configuration
from django.conf import settings
from boto.mturk.connection import MTurkConnection

if True: # SANDBOX:
    HOST="mechanicalturk.sandbox.amazonaws.com"
else:    # PRODUCTION
    HOST="mechanicalturk.amazonaws.com"

AWS_ACCESS_ID = 'Your AWS_ACCESS_ID'
AWS_SECRET_KEY = 'Your AWS_SECRET_KEY'

mtc = MTurkConnection(aws_access_key_id=AWS_ACCESS_ID, aws_secret_access_key=AWS_SECRET_KEY, host=HOST)
