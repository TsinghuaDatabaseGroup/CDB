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
import com.tsinghua.dbgroup.crowddb.crowdsql.tree.SqlTreeNode;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.TableManager;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.schema.EqualSchema;
import com.tsinghua.dbgroup.crowddb.crowdstorage.utils.Utils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrowdEQOperator extends BaseOperator implements IOperator {
    private static Logger LOG = LoggerFactory.getLogger(CrowdEQOperator.class);

    String from0,from1,column0,column1,DBNAME;

    TableManager tmpTableManager = new TableManager();


    public CrowdEQOperator(SqlTreeNode sqlTreeNode,String dbName) {
        super(sqlTreeNode);
        this.taskCategory = TaskCategory.TEXT;
        this.taskType = TaskType.CROWD_EQUAL;
        this.DBNAME= dbName;
        setCrowd(true);
    }

    private void initialFromColumn(){
        String[] part = sqlTreeNode.getWhereClause().split(" ");
        from0= part[0].substring(0,part[0].indexOf("."));

        column0 = part[0].substring(part[0].indexOf('.')+1,part[0].length());

        column1 = part[2];

        column0 = Utils.packetColumn(from0, column0);

        if (sqlTreeNode.getLeft()!=null) {
            from0 = sqlTreeNode.getLeft().getTableName();
        }
        else {
            from0 = Utils.packetTable(DBNAME, from0);
            String newTableName = TableManager.TMP_DATABASE +"."+TableManager.generateTableName();
            tmpTableManager.packetTable(newTableName,from0);
            from0 = newTableName;
        }
    }

    @Override
    public boolean process() {

        LOG.info("CrowdEQ : "+sqlTreeNode.getWhereClause());

        initialFromColumn();

        return JudgeProcess(from0,column0,column1);
    }

    private Boolean testFinish(List<Map<String, Object>> entrys0){
        HashMap<Pair<String,String>,Boolean> res = new HashMap<>();

        return true;
    }

    @Override
    public String finish() {
        HashMap<String,? extends BaseResult> res = getTask().getResults();

        String tableName = TableManager.generateTableName();
        tableName = Utils.packetTable(TableManager.TMP_DATABASE, tableName);

        boolean result = tmpTableManager.createTmpTable(tableName,EqualSchema.SCHEMA_ID);

        List<EqualSchema> list = new ArrayList<>();


        for (Map.Entry<String, ? extends BaseResult> entry:res.entrySet()){
            String value = entry.getKey();
            JudgementResult judgeResult = (JudgementResult)res.get(value);

            if (judgeResult!=null && judgeResult.getAnswer().equals(1)){
                list.add(new EqualSchema(value, 1));
            }
            if (judgeResult==null)
                LOG.error("Miss Result of id : "+value);
        }

        tmpTableManager.insertEqualRecords(tableName, list);
        String newTableName = TableManager.TMP_DATABASE+"."+TableManager.generateTableName();
        tmpTableManager.normalJoinTables(newTableName,from0,tableName,Pair.of("eid","eid1"), false);

        this.sqlTreeNode.setTableName(newTableName);
        return newTableName;
//        HashSet h=new HashSet();
//
//        HashMap<String,? extends BaseResult> res = getTask().getResults();
//
//        String tableName = TableManager.generateTableName();
//        tableName = Utils.packetTable(TableManager.TMP_DATABASE, tableName);
//
//        boolean result = tmpTableManager.createTmpTable(tableName,EqualSchema.SCHEMA_ID);
//
//        List<EqualSchema> list = new ArrayList<>();
//
//        Object a[] =new Object[3];
//        List<Map<String, Object>> entrys0 = new ArrayList<Map<String, Object>>();
//        try {
//            entrys0 = tmpTableManager.extractColumn(column0, from0, a);
//        }catch (SQLException e){
//            LOG.error(e.getMessage());
//        }
//
//        List<Map<String, Object>> entrys0id = new ArrayList<Map<String, Object>>();
//        try {
//            entrys0id = tmpTableManager.extractColumn(getIdName(), from0, a);
//        }catch (SQLException e){
//            LOG.error(e.getMessage());
//        }
//
//        int idCounter = 0;
//
//        for (Map<String, Object> entry0:entrys0){
//            String value = entry0.get(column0).toString();
//            if (!h.contains(value)) {
//                h.add(value);
//                JudgementResult judgeResult = (JudgementResult)res.get(entrys0id.get(idCounter).toString());
//
//                if (judgeResult!=null && judgeResult.getAnswer().equals(1)){
//                    list.add(new EqualSchema(entrys0id.get(idCounter).toString(), 1));
//                }
//                if (judgeResult==null)
//                    LOG.error("Miss Result of id : "+Integer.toString(idCounter));
//            }
//            idCounter ++;
//        }
//
//        tmpTableManager.insertEqualRecords(tableName, list);
//        String newTableName = TableManager.TMP_DATABASE+"."+TableManager.generateTableName();
//        tmpTableManager.normalJoinTables(newTableName,from0,tableName,Pair.of(Utils.packetColumn(tableName,"eid"),Utils.packetColumn(from0,"eid")), false);
//
//        this.sqlTreeNode.setTableName(newTableName);
//        return newTableName;
    }
}
