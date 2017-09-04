/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 10/13/16 11:58 AM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

package com.tsinghua.dbgroup.crowddb.crowdexec.operator;

import com.tsinghua.dbgroup.crowddb.crowdstorage.table.TableManager;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskCategory;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskType;
import com.tsinghua.dbgroup.crowddb.crowdsql.tree.SqlTreeNode;
import com.tsinghua.dbgroup.crowddb.crowdstorage.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class GTOperator extends BaseOperator implements IOperator {
    private static Logger LOG = LoggerFactory.getLogger(GTOperator.class);

    String from0,from1,column0,column1,DBNAME;

    TableManager tmpTableManager = new TableManager();


    public GTOperator(SqlTreeNode sqlTreeNode,String dbName) {
        super(sqlTreeNode);
        this.taskCategory = TaskCategory.TEXT;
        this.taskType = TaskType.CROWD_EQUAL;
        this.DBNAME= dbName;
    }


    private void initialFromColumn(){

        String[] part = sqlTreeNode.getWhereClause().split(" ");
        LOG.info(sqlTreeNode.getWhereClause());
        LOG.info(part[0]);
        from0= part[0].substring(0,part[0].indexOf("."));

        column0 = part[0].substring(part[0].indexOf('.')+1,part[0].length());
        column1 = part[2];


        column0 = Utils.packetColumn(from0, column0);
        if (sqlTreeNode.getLeft()!=null) {

            from0 = sqlTreeNode.getLeft().getTableName();
        }
        else {

            from0 = Utils.packetTable(DBNAME ,from0);
            String newTableName = TableManager.TMP_DATABASE +"."+TableManager.generateTableName();
            tmpTableManager.packetTable(newTableName,from0);
            from0 = newTableName;
        }
    }
    @Override
    public boolean process() {
        initialFromColumn();
        return true;
    }

    @Override
    public String finish() {
        String newTableName = Utils.packetTable(TableManager.TMP_DATABASE, TableManager.generateTableName());
        String sql=String.format("create table %s select * from %s where `%s` > %s",newTableName,from0,column0,column1);

        try {
            tmpTableManager.execSQL(sql);
            tmpTableManager.deleteTmpTable(from0);
        }catch (SQLException e){
            LOG.info(e.getMessage());
            e.printStackTrace();
        }

        sqlTreeNode.setTableName(newTableName);
        return newTableName;
    }
}
