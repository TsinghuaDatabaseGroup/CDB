# service

> A Flask project of CrowdDB Web Service

## Build Setup

``` bash
# install dependencies
apt-get install libmysqlclient-dev
apt-get install python-mysqldb
pip install -r requirements.txt

# configure the database with your own settings in app.py
vim app.py

# initialize database 
python model.py db init
python model.py db upgrade

# run demo
python web.py

```
