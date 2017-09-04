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
import com.tsinghua.dbgroup.crowddb.crowdplat.result.impl.ColumnsResult;
import com.tsinghua.dbgroup.crowddb.crowdplat.schema.impl.FillSchema;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.TableManager;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskCategory;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskType;
import com.tsinghua.dbgroup.crowddb.crowdsql.tree.SqlTreeNode;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.schema.ColumnsSchema;
import com.tsinghua.dbgroup.crowddb.crowdstorage.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

public class FillOperator extends BaseOperator implements IOperator {
    private static Logger LOG = LoggerFactory.getLogger(FillOperator.class);

    String from0,from1,column0,column1,DBNAME;

    TableManager tmpTableManager = new TableManager();


    public FillOperator(SqlTreeNode sqlTreeNode,String dbName) {
        super(sqlTreeNode);
        this.taskCategory = TaskCategory.TEXT;
        if (sqlTreeNode.getSqlContext().getTagOptions()==null)
            this.taskType = TaskType.FILL;
        else
            this.taskType = TaskType.FILL;
        this.DBNAME= dbName;
        setCrowd(true);
    }
    private void initialFromColumn() {
        column0 = sqlTreeNode.getSqlContext().getQuestion();
        if (sqlTreeNode.getLeft() != null) {
            from0 = sqlTreeNode.getLeft().getTableName();
        } else {
            from0 = Utils.packetTable(DBNAME, sqlTreeNode.getFroms().get(0));
            String newTableName = TableManager.TMP_DATABASE + "." + TableManager.generateTableName();
            tmpTableManager.packetTable(newTableName, from0);
            from0 = newTableName;
        }
    }

    @Override
    public boolean process() {


        initialFromColumn();

        LOG.info("CrowdFill : "+from0+"."+column0);

        List<FillSchema> crowdQuerys = new ArrayList<>();


        Object a[] =new Object[3];
        List<Map<String, Object>> entrys0 = new ArrayList<Map<String, Object>>();
        try {
            entrys0 = tmpTableManager.extractColumn(column0, from0, a);
        }catch (SQLException e){
            LOG.error(e.getMessage());
        }
        List<Map<String, Object>> entrys0id = new ArrayList<Map<String, Object>>();
        try {
            entrys0id = tmpTableManager.extractColumn(getIdName(), from0, a);
        }catch (SQLException e){
            LOG.error(e.getMessage());
        }
        int idCounter=0;
        for (Map<String, Object> entry0:entrys0){
            crowdQuerys.add(new FillSchema(entrys0id.get(idCounter).get(getIdName()).toString(), entry0.get(column0).toString(), removeTableName(sqlTreeNode.getSqlContext().getFills())));
            ++idCounter;
        }

        return createAndUploadTask(crowdQuerys);
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
            HashMap<String,String> fillresult = ((ColumnsResult)row.getValue()).getAnswer();
            HashMap<String,String> fillReal = new HashMap<>();
            for (Map.Entry<String,String> entry:fillresult.entrySet()){
                fillReal.put(tableColumn.get(entry.getKey()),entry.getValue());
            }
            list.add(new ColumnsSchema(id,fillReal));
        }
        List<String> columnName = null;
        try {
            columnName=tmpTableManager.getTableColumns(from0);
        }catch (SQLException e){

        }
        tmpTableManager.copyTable(newTableName,from0);
        tmpTableManager.update(newTableName,list,new ArrayList<String>(list.get(0).getColumns().keySet()));

        this.sqlTreeNode.setTableName(newTableName);
        return newTableName;
    }
}
