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
import java.util.ArrayList;
import java.util.List;

public class ProjectOperator extends BaseOperator implements IOperator {
    private static Logger LOG = LoggerFactory.getLogger(ProjectOperator.class);

    String from0,from1,column0,column1,DBNAME;

    TableManager tmpTableManager = new TableManager();


    public ProjectOperator(SqlTreeNode sqlTreeNode,String dbName) {
        super(sqlTreeNode);
        this.taskCategory = TaskCategory.TEXT;
        this.taskType = TaskType.CROWD_EQUAL;
        this.DBNAME= dbName;
    }


    private void initialFromColumn(){
//        LOG.info(sqlTreeNode.getWhereClause());
        from0= DBNAME+sqlTreeNode.getFroms().get(0);

        if (sqlTreeNode.getLeft()!=null) {
            from0 = sqlTreeNode.getLeft().getTableName();
        }else{
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

    public String finish() {
        String newTableName = TableManager.generateTableName();
        newTableName = Utils.packetTable(TableManager.TMP_DATABASE, newTableName);

        List<String> fields = new ArrayList<>();
        for (String col: sqlTreeNode.getProjects()) {
            fields.add(String.format("`%s`", col));
        }
        String projects = String.join(", ", fields);

        String SQL=String.format("create table %s select %s from %s", newTableName, projects, from0);
        try {
            tmpTableManager.execSQL(SQL);
            tmpTableManager.deleteTmpTable(from0);
        }catch (SQLException e){
            LOG.info(e.getMessage());
            e.printStackTrace();
        }

        sqlTreeNode.setTableName(newTableName);
        return newTableName;
    }
}
