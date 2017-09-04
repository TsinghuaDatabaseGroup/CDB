# -*- coding:utf-8 -*-

# -*- coding:utf-8 -*-

from app import app

from flask_sqlalchemy import SQLAlchemy
from flask_script import Manager
from flask_migrate import Migrate, MigrateCommand

from sqlalchemy import *
from sqlalchemy.orm import relationship, backref

db = SQLAlchemy(app)
migrate = Migrate(app, db)

manager = Manager(app)
manager.add_command('db', MigrateCommand)

Base = db.Model

class User(Base):
    __tablename__ = 'users'

    id = Column(Integer, primary_key=True)
    username = Column(String(128), unique=True)
    password = Column(String(256))

    dbname = Column(String(128))
    dbstring = Column(String(256))

    def to_json(self):
        return {
            "id":self.id,
            "username":self.username
        }

if __name__ == '__main__':
	#print dir(db)
    manager.run()
