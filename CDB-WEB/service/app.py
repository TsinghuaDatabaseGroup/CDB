# -*- coding:utf-8 -*-

from flask import Flask
from flask_cors import CORS
from datetime import datetime, timedelta

## MySQL
DB_USERNAME = '???????????'
DB_PASSWORD = '??????????????????????????'
DB_HOST = 'localhost'
DB_PORT = '3306'
DB_DBNAME = 'crowddb_user_meta'
CROWD_META_DB = 'crowddb_meta'

db_conn_str = 'mysql+mysqldb://%s:%s@%s:%s/%s?charset=utf8' % (DB_USERNAME, DB_PASSWORD, DB_HOST, DB_PORT, DB_DBNAME)
crowd_conn_str = 'mysql+mysqldb://%s:%s@%s:%s/%s?charset=utf8' % (DB_USERNAME, DB_PASSWORD, DB_HOST, DB_PORT, CROWD_META_DB)
user_db_template = 'mysql+mysqldb://%s:%s@%s:%s/%s?charset=utf8' % ("%s", "%s", DB_HOST, DB_PORT, "%s")

CROWD_HOST = "127.0.0.1"
CROWD_PORT = 1234

### Server Settings

app = Flask(__name__)

app.config['SECRET_KEY'] = 'crowd_db_key_234rSd2#432s@234'
app.config['JWT_EXPIRATION_DELTA'] = timedelta(days=1)
app.config['JWT_AUTH_URL_RULE'] = None
app.config['JWT_AUTH_EMAIL_KEY'] = "email"
app.config['JWT_AUTH_URL_RULE'] = "/api/auth"
#app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///app.db'
app.config['SQLALCHEMY_DATABASE_URI'] = db_conn_str
app.config['TEMP_DIR'] = 'tmp'
app.config['UPLOAD_FOLDER'] = "./upload/"

CORS(app)
