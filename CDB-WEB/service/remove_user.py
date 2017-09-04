# -*- coding:utf-8 -*-

from dbcontrol import *
from model import db, User
import sys

def remove_user(username):
    user = User.query.filter_by(username=username).first()
    if user:
        remove_user_env(user.username, user.dbname)
        db.session.delete(user)
        db.session.commit()
    else:
        raise Exception("User "+ username+ " not exists!")

if __name__ == "__main__":
    print "Remove", sys.argv[1], "..."
    remove_user(sys.argv[1])
