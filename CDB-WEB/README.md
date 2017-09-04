# CDB-WEB  -  An Web Service for CrowdDB 

## Introduction

This is a web service demo UI for CrowdDB. By using the web UI, you can create your own crowd-sql, check the processing progress, and collect the sql results in your own database.

## About The Service

To manage your crowd-sql with this service, you should provide an database user with high privileges (including GRANT privileges) in service/app.py. Then the service would create a database user for every user registered who registered in the service with the username and password, and grant privileges for it.

All the crowd-sql operations would be stored and managed in the database using the registered database user.

## Pre Requirements

Node.js >= v5.7.1
Python2
pip


## Build and Run

``` bash
# build web page of the service
cd front
npm install
npm run build
cd ..

# copy the web page into the service folder
cp front/dist/index.html service/templates/
cp front/dist/static/* service/static/

# install python dependencies
apt-get install libmysqlclient-dev
apt-get install python-mysqldb
cd service
pip install -r requirements.txt

# configure the database with your own settings in app.py
vim service/app.py

# initialize database 
python model.py db init
python model.py db upgrade

# run demo
python web.py

```
