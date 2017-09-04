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

import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskCategory;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskType;
import com.tsinghua.dbgroup.crowddb.crowdplat.result.BaseResult;
import com.tsinghua.dbgroup.crowddb.crowdplat.result.impl.JudgementResult;
import com.tsinghua.dbgroup.crowddb.crowdplat.schema.impl.JudgementSchema;
import com.tsinghua.dbgroup.crowddb.crowdsql.tree.SqlTreeNode;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.TableManager;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.schema.JoinSchema;
import com.tsinghua.dbgroup.crowddb.crowdstorage.utils.Utils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;


public class CrowdJoinOperator extends BaseOperator implements IOperator {
    private static Logger LOG = LoggerFactory.getLogger(CrowdJoinOperator.class);

    String from0,from1,column0,column1,DBNAME;
    TableManager tmpTableManager= new TableManager();

    public CrowdJoinOperator(SqlTreeNode sqlTreeNode,String dbName) {
        super(sqlTreeNode);
        this.taskCategory = TaskCategory.TEXT;
        this.taskType = TaskType.CROWD_JOIN;
        this.DBNAME = dbName;
        setCrowd(true);

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

        LOG.info("CrowdJoin : "+sqlTreeNode.getWhereClause());
        List<JudgementSchema> crowdQuerys = new ArrayList<>();

        Object a[] =new Object[3];
        List<Map<String, Object>> entrys0 = new ArrayList<Map<String, Object>>();
        try {
            entrys0 = tmpTableManager.extractColumn(column0, from0, a);
        }catch (SQLException e){
            e.printStackTrace();
            LOG.error(e.getMessage());
        }


        List<Map<String, Object>> entrys1 = new ArrayList<Map<String, Object>>();
        try {
            entrys1 = tmpTableManager.extractColumn(column1, from1, a);
        }catch (SQLException e){
            e.printStackTrace();
            LOG.error(e.getMessage());
        }

        List<Map<String, Object>> entrys0id = new ArrayList<Map<String, Object>>();
        try {
            entrys0id = tmpTableManager.extractColumn(getIdName(), from0, a);
        }catch (SQLException e){
            e.printStackTrace();
            LOG.error(e.getMessage());
        }


        List<Map<String, Object>> entrys1id = new ArrayList<Map<String, Object>>();
        try {
            entrys1id = tmpTableManager.extractColumn(getIdName(), from1, a);
        }catch (SQLException e){
            e.printStackTrace();
            LOG.error(e.getMessage());
        }


        //int idCounter = 0;
        int id0=0;
        for (Map<String, Object> entry0:entrys0) {
            int id1 = 0;
            for (Map<String, Object> entry1 : entrys1) {
                String key = entrys0id.get(id0).get("eid").toString()+","+entrys1id.get(id1).get("eid").toString();
                crowdQuerys.add(new JudgementSchema(key, entry0.get(column0).toString(), entry1.get(column1).toString()));
                id1 ++;
            }
            id0 ++;
        }
        return createAndUploadTask(crowdQuerys);
    }

    @Override
    public String finish() {
        HashMap<String, ? extends BaseResult> res = getTask().getResults();

        String tableName = TableManager.generateTableName();
        tableName = Utils.packetTable(TableManager.TMP_DATABASE, tableName);
        boolean result = tmpTableManager.createTmpTable(tableName, JoinSchema.SCHEMA_ID);

        List<JoinSchema> list = new ArrayList<>();

        HashSet h = new HashSet();
        Object a[] = new Object[3];
        for (Map.Entry<String, ? extends BaseResult> entry : res.entrySet()) {
            String value = entry.getKey();
            String[] ids = value.split(",");
            if (((JudgementResult) entry.getValue()).getAnswer().equals(1))
                    list.add(new JoinSchema(ids[0],ids[1],1));
        }
        tmpTableManager.insertJoinRecords(tableName, list);
        String newTableName = TableManager.TMP_DATABASE+"."+TableManager.generateTableName();
        tmpTableManager.crowdJoinTables(newTableName,from0,from1,tableName,Pair.of("eid","eid"));

        this.sqlTreeNode.setTableName(newTableName);
        return newTableName;
    }
}
