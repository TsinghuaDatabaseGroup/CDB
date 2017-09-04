# -*- coding:utf-8 -*-

from flask import Flask, render_template
from flask_cors import CORS

from werkzeug.contrib.cache import SimpleCache
from werkzeug import secure_filename
from flask import Flask, url_for, jsonify, request, current_app, abort, _request_ctx_stack, Response, stream_with_context
from flask_jwt import *

from datetime import datetime, timedelta
from app import *
from model import db, User
import hashlib
from util import *
from error import *
from dbcontrol import *
import traceback
import socket
import csv
import os
from time import *

def authenticate(username, password):
    user = User.query.filter_by(username=username).first()
    if user and user.password == password_hash(password):
        return user

def identity(payload):
    return User.query.filter_by(id=payload['identity']).first()

jwt = JWT(app, authenticate, identity)

@jwt.jwt_error_handler
def auth_error_handler(e):
    return jsonify(OrderedDict([
        ('status_code', e.status_code),
        ('error', e.error),
        ('description', e.description),
        ('resetAuthToken', True)
    ])), e.status_code, e.headers

old_request_handler = jwt.request_callback

@jwt.request_handler
def my_req_hendler():
    token = request.args.get("token", None)
    if not token:
        return old_request_handler()
    else:
        return token


def send_crowd_request(queryId):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    print "start socket server"
    s.connect((CROWD_HOST, int(CROWD_PORT)))
    print "bind {}:{}".format(CROWD_HOST, CROWD_PORT)
    queryId = str(queryId)
    s.send(queryId)
    print "send {}".format(queryId)

def create_user_with_exist_dbuser(username, password, dbname):
    db_str = user_db_template % (username, password, dbname)
    user = User(username=username, password=password_hash(password), dbname=dbname, dbstring=db_str)
    db.session.add(user)
    db.session.commit()


@app.route("/api/register", methods=["POST"])
@json_api(username=len_in(3, 32), password=len_in(1, 256))
def api_register(username, password):
    dbname = username+"_db"
    try:
        db_str = create_user_env(username, password, dbname)
        user = User(username=username, password=password_hash(password), dbname=dbname, dbstring=db_str)
        if not db_str:
            return error('USER_EXISTS')
        db.session.add(user)
        db.session.commit()
        with get_connect(db_str) as user_conn:
            with get_crowd_connect() as crowd_conn:
                create_sample(user_conn, crowd_conn, user.id, user.dbname)
        return success(user=user.to_json())
    except Exception as e:
        print e
        traceback.print_exc()
        return error('USER_EXISTS')

@app.route("/api/test_auth", methods=["POST"])
@jwt_required()
@json_api()
def api_test_auth():
    return success()

@app.route("/api/run_simple_sql", methods=["POST"])
@jwt_required()
@json_api(sql=len_in(1, 512),
    max_rows=optional(int_in(1,1000)),
    return_dict=optional(is_bool))
def api_run_simple_sql(sql, max_rows=50, return_dict=True):
    try:
        with  get_connect(current_identity.dbstring) as c:
            r = c.execute(sql)
            if r.returns_rows:
                ks = r.keys()
                cnt = 0
                rst = []
                if return_dict:
                    for i in r:
                        obj = {}
                        for idx in xrange(len(ks)):
                            obj[ks[idx]] = i[idx]
                        rst.append(obj)
                        cnt = cnt+1
                        if cnt > max_rows:
                            break
                else:
                    for i in r:
                        rst.append(list(i))
                        cnt = cnt+1
                        if cnt > max_rows:
                            break
                return success(header=ks, rows=rst)
            else:
                return success()
    except Exception as e:
        print e
        traceback.print_exc()
        return error("SQL_ERROR", remark=str(e))

@app.route("/api/run_crowd_sql", methods=["POST"])
@jwt_required()
@json_api(sql=len_in(1, 512),
    result_table=len_in(1,128),
    task_title=len_in(0,256),
    platform=len_in(2,3),
    gmodel=is_bool)
def api_run_crowd_sql(sql, result_table, task_title, platform, gmodel):
    try:
        with get_crowd_connect() as c:
            r = insert_crowd_sql(c, sql, task_title, platform, result_table, current_identity.id, current_identity.dbname, gmodel=gmodel)
            send_crowd_request(r.lastrowid)
            return success(sql_id=r.lastrowid)
    except Exception as e:
        print e
        traceback.print_exc()
        return error("SQL_ERROR", remark=str(e))

@app.route("/api/delete_crowd_sql", methods=["POST"])
@jwt_required()
@json_api(sql_ids=list_check(1, 100, len_in(1,10)))
def apt_delete_crowd_sql(sql_ids):
    try:
        with get_crowd_connect() as c:
            r = remove_crowd_sqls(c, sql_ids)
            return success(sql_ids=r.lastrowid)
    except Exception as e:
        print e
        traceback.print_exc()
        return error("SQL_ERROR", remark=str(e))


@app.route("/api/get_all_crowd_sql", methods=["POST"])
@jwt_required()
@json_api(return_dict=optional(is_bool))
def api_get_all_crowd_sql(return_dict=True):
    try:
        with get_crowd_connect() as c:
            meta_sql = "SELECT * FROM query WHERE user_id=%d ORDER BY id DESC" % current_identity.id
            r = c.execute(meta_sql)
            if r.returns_rows:
                ks = r.keys()
                rst = []
                if return_dict:
                    for i in r:
                        obj = {}
                        for idx in xrange(len(ks)):
                            obj[ks[idx]] = i[idx]
                        rst.append(obj)
                else:
                    rst = [list(i) for i in r]
                return success(header=ks, rows=rst)
            else:
                return success()
    except Exception as e:
        print e
        traceback.print_exc()
        return error("SQL_ERROR", remark=str(e))

@app.route("/upload/<table_name>", methods=["POST"])
@jwt_required()
def upload_csv_file(table_name):
    try:
        print request.files
        f = request.files['file']
        file_path = os.path.join(app.config['UPLOAD_FOLDER'], strftime("file_%Y%m%d_%H_%M_%S_", gmtime())+f.filename)
        f.save(file_path)
        with  get_connect(current_identity.dbstring) as c:
            with open(file_path, 'rb') as csvfile:
                reader = csv.reader(csvfile, skipinitialspace=True)
                headers = reader.next()
                if not all(headers):
                    raise Exception("Invalid CSV File!")
                headers_str = ','.join(headers)
                vheaders_str = ','.join(["%s" for i in headers])
                base_sql = "INSERT INTO {} ({}) VALUES ({})".format(table_name, headers_str, vheaders_str)
                params = []
                for i in reader:
                    if len(i) != len(headers):
                        raise Exception("Invalid CSV File!")
                    params.append(tuple(i))
                    if len(params) > 9999:
                        r = c.execute(base_sql, params)
                        params = []
                if len(params) > 0:
                    r = c.execute(base_sql, params)
        return jsonify(success())
    except Exception as e:
        print e
        traceback.print_exc()
        return jsonify(error("SQL_ERROR", remark=str(e)))

@app.route("/download/<file_name>")
@jwt_required()
def download_csv_file(file_name):
    table_name = file_name
    if table_name.endswith(".csv"):
        table_name = table_name[:-4]
    c = get_connect(current_identity.dbstring)
    sql = "SELECT * FROM " + table_name
    r = c.execute(sql)
    si = StringIO.StringIO()
    writer = csv.writer(si)
    writer.writerow(r.keys())
    def generate():
        for i in r:
            writer.writerow(i)
            si.seek(0)
            yield si.read()
            si.truncate(0)
        c.close()
    output = Response(stream_with_context(generate()))
    output.headers["Content-Disposition"] = "attachment; filename="+table_name+".csv"
    output.headers["Content-type"] = "text/csv"
    return output

@app.route("/")
def index():
    return render_template("index.html")

if __name__ == '__main__':
    app.run(host="0.0.0.0",port=8080, debug=True, threaded=True)
