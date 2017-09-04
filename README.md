# CDB

CDB is a crowd-powered database system that supports crowd-based query optimizations with focus on join and selection. 
CDB has fundamental differences from existing systems. First, CDB employs a graph-based query model that provides 
more fine-grained query optimization. Second, CDB adopts a unified framework to perform the multi-goal optimization based on the graph
model. We have implemented our system and deployed it on Amazon Mechanical Turk, CrowdFlower and ChinaCrowd. 

## Framework Description
CDB is an intergated system with CDB-UI, CDB-Server and CDB-Assigment. End users will upload tables, submit queries and download results by CDB-UI. The
query request will be submited to CDB-Server, and CDB-Server will parse sql, build model and execute every operator one by one. Those questions need to 
answer will be assigned to CDB-Platfrom, which will make up real questions and publish them into crowdsourcing platforms with platform. 

#### CDB-WEB

CDB-WEB is a demo UI based on a web service for CrowdDB. By using the web UI, you can create your crowd-sql, check the processing progress, and collect the sql results in your own database.

#### CDB-Server

CDB-Server is the kernel component of CDB database. It provides two types of query model, one is straight-forward model and another is graph model. 
For both of them, it firstly parses the inputed query and build query model from it. Secondly, CDB-Server assign those questions generated from model
to different crowdsoucring platforms and collect answers. Finally, it store the query result into table. There are 5 major parts:

##### CrowdSQL:
Prase the input query and build syntax tree or graph model.
##### CrowdExec:
Define the crowd operation of each operator and execute them.
##### CrowdStorage:
Read data from databases, Store the final result to table, Stash intermediate data
##### CrowdPlat:
An interface to CDB-Assigment components. 
##### CrowdSchedular:
Schedule operator's execution order

#### CDB-Assignment

CDB-Assignment is the assignment module of CrowdDB.

This module acts as a server to receive requests with well-formatted crowdsourcing tasks data from CDB-Server and publish the tasks to different platforms including AMT (Amazon Mechanical Turk), CF (CrowdFlower), CC (Chinacrowds).

## Clone from Github
If you want to get the latest source code, please clone it from Github repo with the following command. 

```
git clone http://dbgroup.cs.tsinghua.edu.cn:8081/wxping715/CDB.git
cd CDB
```

## Install
Since CDB has 3 componments, we need install each of them independently.

##### Install CDB-Server
Follow this [link](CDB-Server/README.md)
##### Install CDB-WEB
Follow this [link](CDB-WEB/README.md)
##### Install CDB-Assignment
Follow this [link](CDB-Assignment/README.md)

## Contact

If you have any questions about it, please feel free to contact Guoliang Li(liguoliang@tsinghua.edu.cn) or Xueping Weng(wxping715@gmail.com).