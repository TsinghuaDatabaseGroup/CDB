# -*- coding:utf-8 -*-

from app import db_conn_str, user_db_template, DB_USERNAME, crowd_conn_str
from sqlalchemy import create_engine
import traceback


engine = create_engine(db_conn_str)
connect = engine.connect()
connect.execute("commit")

def show_databases():
    global connect
    r = connect.execute("show databases")
    for i in r:
        print i[0]

def show_users():
    r = connect.execute("select User, Host from mysql.user")
    for i in r:
        print i

def sql_func(func):
    global connect
    def wraped_func(*args, **kwargs):
        global connect
        try:
            if connect.closed:
                connect = engine.connect()
            if connect.in_transaction():
                connect.execute("commit")
            return True, func(*args, **kwargs)
        except Exception as e:
            print e
            return False, e
    return wraped_func

@sql_func
def create_user(username, password):
    global connect
    return connect.execute("CREATE USER '{}'@'%%' IDENTIFIED BY '{}'".format(username, password))

@sql_func
def create_database(dbname):
    global connect
    r = connect.execute("CREATE DATABASE {}".format(dbname))
    connect.execute("GRANT ALL PRIVILEGES ON {}.* TO '{}'@'%%'".format(dbname, DB_USERNAME))
    connect.execute("FLUSH PRIVILEGES")
    return r

@sql_func
def grant_privileges(username, dbname):
    global connect
    r = connect.execute("GRANT ALL PRIVILEGES ON {}.* TO '{}'@'%%'".format(dbname, username))
    connect.execute("FLUSH PRIVILEGES")
    return r

@sql_func
def drop_user(username):
    global connect
    return connect.execute("DROP USER '{}'@'%%'".format(username))

@sql_func
def drop_database(dbname):
    global connect
    return connect.execute("DROP DATABASE {}".format(dbname))

def user_env_valid(username, dbname):
    global connect
    for x in xrange(3):
        try:
            if connect.closed:
                connect = engine.connect()
            if connect.in_transaction():
                connect.execute("commit")
            for i in connect.execute("select User, Host from mysql.user"):
                if username == i[0]:
                    return False
            for i in connect.execute("show databases"):
                if dbname == i[0]:
                    return False
            return True
        except Exception as e:
            print e
            traceback.print_exc()
            print "tried", x+1, "times"
            pass
    return False

def create_user_env(username, password, dbname):
    if not user_env_valid(username, dbname):
        return None
    r1 = create_user(username, password)
    r2 = create_database(dbname)
    r3 = grant_privileges(username, dbname)
    if not all([r1[0], r2[0], r3[0]]):
        print "user:", r1[1]
        print "  db:", r2[1]
        print "perm:", r3[1]
        raise Exception("create user env failed!!")
    return user_db_template % (username, password, dbname)

def insert_crowd_sql(crowd_conn, sql, task_title, platform, result_table, user_id, dbname, gmodel=True, status="init"):
    if gmodel:
        g = 1
    else:
        g = 0
    meta_sql = "INSERT INTO query (qsql, status, user_id, result_table, db_name, task_title, platform, gmodel) VALUES (%s, %s, %s, %s, %s, %s, %s, %s)"
    meta_params = (sql, status, user_id, result_table, dbname, task_title, platform, str(g))
    r = crowd_conn.execute(meta_sql, meta_params)
    return r

def remove_crowd_sqls(crowd_conn, sql_ids):
    delete_sql = "DELETE FROM query WHERE id IN (%s)" % ','.join(sql_ids)
    r = crowd_conn.execute(delete_sql)
    return r

def insert_lines(conn, table_name, headers, datas):
    headers_str = ','.join(headers)
    vheaders_str = ','.join(["%s" for i in headers])
    base_sql = "INSERT INTO {} ({}) VALUES ({})".format(table_name, headers_str, vheaders_str)
    params = []
    for i in datas:
        if len(i) != len(headers):
            raise Exception("Insert Lines ERROR: Invalid Data!")
        params.append(tuple(i))
        if len(params) > 9999:
            r = conn.execute(base_sql, params)
            params = []
    if len(params) > 0:
        r = conn.execute(base_sql, params)

def create_sample(user_conn, crowd_conn, user_id, dbname, status="finished"):
    if user_conn.in_transaction():
        user_conn.execute("commit")
    user_conn.execute('''CREATE TABLE example_author (
        id int NOT NULL AUTO_INCREMENT,
        name varchar(255) NOT NULL,
        school varchar(255),
        PRIMARY KEY (id)
        )''')
    user_conn.execute('''CREATE TABLE example_paper (
        id int NOT NULL AUTO_INCREMENT,
        name varchar(255) NOT NULL,
        title varchar(255),
        PRIMARY KEY (id)
        )''')
    insert_lines(user_conn, "example_author", ["name", "school"], [
        ("Cheng k. Li", "University of the Saarland"),
        ("Jiawei Han", "Chinese University of Hong Kong"),
        ("Unnikrishnan", "Case Western Reserve University School of Medicine"),
        ])
    insert_lines(user_conn, "example_paper", ["name", "title"], [
        ("C. Unnikrishnan", "The InfoSleuth Project"),
        ("Jiawei Han", "Data Mining Techniques"),
        ("Chengkai Li", "ROLEX: Relational On-Line Exchange with XML"),
        ])
    insert_crowd_sql(crowd_conn,
        "select example_author.name, example_paper.title, example_author.school from example_author, example_paper where example_author.name CROWD_EQ example_paper.name",
        "example_select",
        "CC",
        "example_select_result",
        user_id,
        dbname,
        status)
    insert_crowd_sql(crowd_conn,
        'collect name, school from example_author on "Which is the best scientist you think?" limit 1;',
        "example_collect",
        "AMT",
        "example_collect_result",
        user_id,
        dbname,
        status)
    user_conn.execute('''CREATE TABLE example_select_result (
        name varchar(255) NOT NULL,
        title varchar(255),
        school varchar(255)
        )''')
    insert_lines(user_conn, "example_select_result", ["name", "title", "school"], [
        ("Cheng k. Li", "ROLEX: Relational On-Line Exchange with XML", "University of the Saarland"),
        ("Jiawei Han", "Data Mining Techniques", "Chinese University of Hong Kong"),
        ("Unnikrishnan", "The InfoSleuth Project", "Case Western Reserve University School of Medicine"),
        ])
    user_conn.execute('''CREATE TABLE example_collect_result (
        name varchar(255) NOT NULL,
        school varchar(255)
        )''')
    insert_lines(user_conn, "example_collect_result", ["name", "school"], [
        ("Jiawei Han", "Chinese University of Hong Kong"),
        ])
    pass


def remove_user_env(username, dbname):
    drop_user(username)
    drop_database(dbname)

def get_connect(conn_str):
    return create_engine(conn_str).connect()

def get_crowd_connect():
    return get_connect(crowd_conn_str)
