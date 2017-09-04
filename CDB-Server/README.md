# CDB-Server

## Introduction

CDB-Server is the kernal of CDB database, and it has 6 parts:
##### crowdcore
It defines some core data structures and core componments. 

##### crowdexec
crowdexec is responsible for operator execution details. It will pack up operators into questions and interactive with CDB-Assignment platform. 

##### crowdplat
crowdplat is an interface between CDB-Server and CDB-platform.

##### crowdschedular
crowdschedular receive queries from clients and append them into schedula queue. In every cycle, crowdschedular will check every query if its current
operator has been finished. If it has been finished, crowdschedular stash the current operator's data and continue to the next operator.

##### crowdsql
crowdsql will recevice an input query and parse it to a synax tree or graphmodel.

##### crowdstorage
crowdstorage is responsible for table storage and intermediate data stash. 

## Install

### Install Dependences
CDB-Server is based on Ubuntu and developed by Java. We take `Maven` as the integrated build tools, and use `Mysql` as storage warehouse.

Dependences:
- Java: version 1.8.0
- Mysql: version 14.14
- Apache Maven: version 3.3.9
- Python 2: version 2.7
- Git: version 2.10.1

Please install those dependences following by its official documents.

### Config and Init DB
This step will init the database and create nesscery databases, users and tables in it.

open `CDB-Server/scripts/running_scripts/init_db.sh` and edit `host`, `port`, `user` and `password`.
```
HOST="127.0.0.1"
PORT="3306"
USER="root"
PASSWORD=""
```

execute the following command
```
sh CDB-Server/scripts/running_scripts/init_db.sh
```

### Edit CDB-Server configs
Go to `CDB-Server/resources/runtime.configs.properties` directory and edit the configs. Here listing some important configs.

```
# assignment server
ASSIGNMENT_SERVER_URL = http://127.0.0.1:9000
# server listening port
PORT = 1234
# The time interval of rolling queries
POLL_INTERVAL = 300

### Crowdsourcing Platform Settings
# the default crowdsourcing platform for answering questions, there are three options:
# 1. CC = ChinaCrowds, http://chinacrowds.com/
# 2. CF = Crowdflower, http://www.crowdflower.com/
# 3. AMT = Amazon Mechanical Turk, https://www.mturk.com/mturk/welcome
DEFAULT_PLATFORM = CC
```

### Build Project 
Go to the `CDB-Server` directory and run the following command. 
```
sh build.sh
```

### Run server
```
sh start.sh
```


### Test

edit `CDB-Server/scripts/test_scripts/send_request.py` and edit `host` and `port` for CDB-Server.
```
host = "127.0.0.1"
port = 1234
```

run the following command to test server and check logs from `logs/crowddb-[year]-[month].log`.
```
python send_request.py [query_id]
```
please replace the query_id to real query_id


## Contact
If you have any questions, please feel free to contact Xueping Weng(wxping715@gmail.com)
