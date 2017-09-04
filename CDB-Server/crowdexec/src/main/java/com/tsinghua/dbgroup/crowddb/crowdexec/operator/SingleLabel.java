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

import com.tsinghua.dbgroup.crowddb.crowdplat.result.BaseResult;
import com.tsinghua.dbgroup.crowddb.crowdplat.result.impl.OptionsResult;
import com.tsinghua.dbgroup.crowddb.crowdplat.result.impl.JudgementResult;
import com.tsinghua.dbgroup.crowddb.crowdplat.schema.impl.LabelSchema;
import com.tsinghua.dbgroup.crowddb.crowdplat.schema.impl.OptionsSchema;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.TableManager;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.schema.ColumnsSchema;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.schema.EqualSchema;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskCategory;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskType;
import com.tsinghua.dbgroup.crowddb.crowdsql.tree.SqlTreeNode;
import com.tsinghua.dbgroup.crowddb.crowdstorage.utils.Utils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

public class SingleLabel extends BaseOperator implements IOperator {
    private static Logger LOG = LoggerFactory.getLogger(SingleLabel.class);

    String from0,from1,column0,column1,DBNAME;

    TableManager tmpTableManager = new TableManager();


    public SingleLabel(SqlTreeNode sqlTreeNode, String dbName) {
        super(sqlTreeNode);
        this.taskCategory = TaskCategory.TEXT;
        this.taskType = TaskType.SINGLE_LABEL;
        this.DBNAME= dbName;
        setCrowd(true);
    }
    private void initialFromColumn(){
        from0 = sqlTreeNode.getFroms().get(0);
        column0 = sqlTreeNode.getSqlContext().getQuestion();
        if (sqlTreeNode.getLeft()!=null) {
            from0 = sqlTreeNode.getLeft().getTableName();
        }
        else {
            from0 = Utils.packetTable(DBNAME, sqlTreeNode.getFroms().get(0));
            String newTableName = TableManager.TMP_DATABASE +"."+TableManager.generateTableName();
            tmpTableManager.packetTable(newTableName,from0);
            from0 = newTableName;
        }
    }
    @Override
    public boolean process() {

        initialFromColumn();

        LOG.info("Label : "+from0+" "+column0);


        List<LabelSchema> crowdQuerys = new ArrayList<>();


        Object a[] =new Object[3];
        List<Map<String, Object>> entrys0 = new ArrayList<Map<String, Object>>();
        try {
            entrys0 = tmpTableManager.extractColumn(column0, from0, a);
        }catch (SQLException e){
            LOG.error(e.getMessage());
        }

        int idCounter = 0;

        List<String> options  = sqlTreeNode.getSqlContext().getTagOptions();
        for (Map<String, Object> entry0:entrys0){
            ++ idCounter;
            crowdQuerys.add(new LabelSchema(Integer.toString(idCounter),sqlTreeNode.getSqlContext().getCollects().get(0),entry0.get(column0).toString(), options));
        }
        return createAndUploadTask(crowdQuerys);
    }

    private Boolean testFinish(List<Map<String, Object>> entrys0){
        HashMap<Pair<String,String>,Boolean> res = new HashMap<>();

        return true;
    }

    @Override
    public String finish() {
        HashMap<String,? extends BaseResult> res = getTask().getResults();

        String newTableName = TableManager.generateTableName();
        newTableName = Utils.packetTable(TableManager.TMP_DATABASE, newTableName);
        List<ColumnsSchema> list = new ArrayList<>();
        Object a[] =new Object[3];

        List<String> fills = sqlTreeNode.getSqlContext().getFills();

        for (Map.Entry<String,?extends BaseResult> row:res.entrySet()){
            String  id= row.getKey();
            List<String> labelresult = ((OptionsResult)row.getValue()).getAnswer();
            HashMap<String,String> LabelReal = new HashMap<>();
            String result = String.join(",",labelresult);
            LabelReal.put(sqlTreeNode.getSqlContext().getCollects().get(0),result);
            list.add(new ColumnsSchema(id,LabelReal));
        }

        tmpTableManager.copyTable(newTableName,from0);
        tmpTableManager.update(newTableName,list,new ArrayList<String>(list.get(0).getColumns().keySet()));

        this.sqlTreeNode.setTableName(newTableName);
        return newTableName;
    }
}
