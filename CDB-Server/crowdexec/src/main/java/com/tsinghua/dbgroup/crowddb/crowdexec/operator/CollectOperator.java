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
import com.tsinghua.dbgroup.crowddb.crowdplat.schema.impl.CollectionSchema;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.TableManager;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.schema.ColumnsSchema;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.schema.EqualSchema;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskCategory;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskType;
import com.tsinghua.dbgroup.crowddb.crowdsql.tree.SqlTreeNode;
import com.tsinghua.dbgroup.crowddb.crowdstorage.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

public class CollectOperator extends BaseOperator implements IOperator {
    private static Logger LOG = LoggerFactory.getLogger(CollectOperator.class);

    String from0,from1,column0,column1,DBNAME;

    TableManager tmpTableManager = new TableManager();


    public CollectOperator(SqlTreeNode sqlTreeNode,String dbName) {
        super(sqlTreeNode);
        this.taskCategory = TaskCategory.TEXT;
        if (sqlTreeNode.getSqlContext().getTagOptions()==null)
            this.taskType = TaskType.COLLECT;
        else
            this.taskType = TaskType.COLLECT;
        this.DBNAME= dbName;
        setCrowd(true);
    }
    private void initialFromColumn(){
        from0 = sqlTreeNode.getSqlContext().getQuestion();

        column0 = String.join(",",sqlTreeNode.getSqlContext().getCollects());

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

        LOG.info("CrowdCollect : "+from0+"."+column0);

        List<CollectionSchema> crowdQuerys = new ArrayList<>();

//        crowdQuerys.add(new CollectionSchema("0",sqlTreeNode.getSqlContext().getLimit(),sqlTreeNode.getSqlContext().getCollect()));
        crowdQuerys.add(new CollectionSchema("0", sqlTreeNode.getSqlContext().getQuestion(), sqlTreeNode.getSqlContext().getLimit(), removeTableName(sqlTreeNode.getSqlContext().getCollects())));

        return createAndUploadTask(crowdQuerys);

    }

    @Override
    public String finish() {
        HashMap<String,? extends BaseResult> res = getTask().getResults();

        String newTableName = TableManager.generateTableName();
        newTableName = Utils.packetTable(TableManager.TMP_DATABASE, newTableName);
        List<ColumnsSchema> list = new ArrayList<>();

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
        tmpTableManager.insertColumns(newTableName,list,new ArrayList<String>(list.get(0).getColumns().keySet()));

        this.sqlTreeNode.setTableName(newTableName);
        return newTableName;
    }
}
