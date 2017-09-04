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
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoinOperator extends BaseOperator implements IOperator {
    private static Logger LOG = LoggerFactory.getLogger(JoinOperator.class);

    String from0,from1,column0,column1,DBNAME;

    TableManager tmpTableManager = new TableManager();


    public JoinOperator(SqlTreeNode sqlTreeNode,String dbName) {
        super(sqlTreeNode);
        this.taskCategory = TaskCategory.TEXT;
        this.taskType = TaskType.CROWD_EQUAL;
        this.DBNAME= dbName;
    }


    private void initialFromColumn(){

        String[] part = sqlTreeNode.getWhereClause().split(" ");

        from0= part[0].substring(0,part[0].indexOf("."));

        column0 = part[0].substring(part[0].indexOf('.')+1,part[0].length());

        from1 = part[2].substring(0,part[2].indexOf("."));

        column1 = part[2].substring(part[2].indexOf('.')+1,part[2].length());

        LOG.info(DBNAME);

        if (sqlTreeNode.getLeft()!=null) {

            column0 = Utils.packetColumn(from0, column0);
            from0 = sqlTreeNode.getLeft().getTableName();
        }
        else {
            column0 = Utils.packetColumn(from0, column0);

            from0 = Utils.packetTable(DBNAME,from0);
            String newTableName = TableManager.TMP_DATABASE +"."+TableManager.generateTableName();
            tmpTableManager.packetTable(newTableName,from0);
            from0 = newTableName;
        }
        column1 = Utils.packetColumn(from1,column1);

        if (sqlTreeNode.getRight()!=null) {
            from1 = sqlTreeNode.getRight().getTableName();
        }
        else {
            from1 = Utils.packetTable(DBNAME,from1);
            String newTableName = TableManager.TMP_DATABASE +"."+TableManager.generateTableName();
            tmpTableManager.packetTable(newTableName,from1);
            from1 = newTableName;
        }
    }
    @Override
    public boolean process() {
        initialFromColumn();
        return true;
    }

    @Override
    public String finish() {
        String newTableName = TableManager.TMP_DATABASE+"."+TableManager.generateTableName();
        tmpTableManager.normalJoinTables(newTableName,from0,from1,Pair.of(column0,column1), true);
        this.sqlTreeNode.setTableName(newTableName);
        return newTableName;
    }
}
