## CrowdAssign
### Introduction
Assignment module of CrowdDB

This module acts as a server to receive requests with well-formatted crowdsourcing tasks data from CDB-Server and publish the tasks to different platforms including AMT (Amazon Mechanical Turk), CF (CrowdFlower), CC (Chinacrowds).


### Environment Specs
This module has been tested on MySQL 5.6.19, Python 2.7.12 with packages below:

|Package|Version|
|:-----:|:-----:|
|Django | 1.10.1|
|boto   | 2.43.0|
|MySQL-python|1.2.5|
|crowdflower|0.1.4|
|poster|0.8.1|

### Setup
#### Software install
First install **mysql** and **python**, then install required packages using commands like below:

```
pip install django
pip install boto
pip install mysql-python
pip install crowdflower
pip install poster
```

#### Configuration
1. Configure your MySQL connection parameters in **CrowdAssign/settings.py**, around line 78, in **DATABASE** dict, fill in your database name, username and password.
2.  Use commands below to create corresponding tables in MySQL:
    ```
    python manage.py makemigrations main
    python manage.py migrate
    ```
3.  Fill in your API-KEY or username/password and switch to production/test environment in corresponding file in:

    |Platform| File |
    |:------:|:----:|
    | AMT    |main/task/mtc.py|
    |CrowdFlower|main/task/api_key.py|
    |Chinacrowds|main/property.py|


4.  Run server (listen on localhost:9000) to see if everything goes fine.
    ```
    python manage.py runserver 9000
    ```
