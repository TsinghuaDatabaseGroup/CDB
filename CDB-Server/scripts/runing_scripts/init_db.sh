#!/bin/bash

#HOST="166.111.71.172"
HOST="127.0.0.1"
PORT="3306"
USER="root"
PASSWORD=""

mysql -h$HOST -P$PORT -u$USER -p$PASSWORD -e `dirname $0`/init_db.sql
