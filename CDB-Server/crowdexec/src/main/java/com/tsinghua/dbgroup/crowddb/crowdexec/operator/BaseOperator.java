/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 10/13/16 11:10 AM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

package com.tsinghua.dbgroup.crowddb.crowdexec.operator;

import com.tsinghua.dbgroup.crowddb.crowdcore.exceptions.CrowdDBException;
import com.tsinghua.dbgroup.crowddb.crowdplat.TaskManager;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.Task;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskCategory;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskStatus;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskType;
import com.tsinghua.dbgroup.crowddb.crowdplat.schema.BaseSchema;
import com.tsinghua.dbgroup.crowddb.crowdplat.schema.impl.JudgementSchema;
import com.tsinghua.dbgroup.crowddb.crowdsql.tree.SqlTreeNode;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.TableManager;
import com.tsinghua.dbgroup.crowddb.crowdstorage.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BaseOperator {

    private static Logger LOG = LoggerFactory.getLogger(BaseOperator.class);

    public TaskCategory taskCategory = null;

    public TaskType taskType = null;

    private Task task = null;

    public SqlTreeNode sqlTreeNode = null;

    private OperatorStatus status = OperatorStatus.INIT;

    private boolean isCrowd = false;

    public BaseOperator(SqlTreeNode sqlTreeNode) {
        this.sqlTreeNode = sqlTreeNode;
    }

    private  String idName = "eid";


    protected boolean createAndUploadTask(List<? extends BaseSchema> schemas) {
        this.task = TaskManager.createTask(this.taskCategory, this.taskType, schemas);

        boolean res;

        try {
            res = TaskManager.uploadTask(task);
        } catch (Exception e) {
            throw new CrowdDBException(0x300);
        }

        /**
         * set status as running
         */
        if (res) {
            setStatus(OperatorStatus.RUNNING);
        }
        return res;
    }

    public boolean getResult() {
        boolean success;

        if (this.task == null)
            success = true;
        else {
            try {
                success = TaskManager.pullRequests(this.task);
            } catch (Exception e) {
                e.printStackTrace();
                throw new CrowdDBException(0x300);
            }
        }

        /**
         * set operator status as finishing
         */
        if (success) {
            setStatus(OperatorStatus.FINISHING);
        }

        return success;
    }

    public boolean hasTaskFinished() {
        if (this.task == null) return true;
        try {
            return TaskManager.checkStatus(this.task) == TaskStatus.FINISHED;
        } catch (Exception e) {
            throw new CrowdDBException(0x300);
        }
    }

    protected boolean execSQL(String sql) {
        TableManager tableManager = new TableManager();
        try {
            tableManager.execSQL(sql);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean process() {
        return true;
    }

    public boolean JudgeProcess(String from0,String column0,String column1){

        TableManager tmpTableManager = new TableManager();


        List<JudgementSchema> crowdQuerys = new ArrayList<>();


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

        int idCounter = 0;
        for (Map<String, Object> entry0:entrys0){
            crowdQuerys.add(new JudgementSchema(entrys0id.get(idCounter).get(getIdName()).toString(),entry0.get(column0).toString(), column1));
            ++ idCounter;
        }
        return createAndUploadTask(crowdQuerys);
    }

    public HashMap<String,String> tableColumn = new HashMap<>();
    public List<String> addTableName(List<String> src){
        List<String> dst = new ArrayList<>();
        for (String column:src){
            dst.add(tableColumn.get(column));
        }
        return dst;
    }
    public List<String> removeTableName(List<String> src){
        tableColumn.clear();
        List<String> dst = new ArrayList<>();
        for (String column:src){
            tableColumn.put(Utils.unpacketColumn(column),column);
            dst.add(Utils.unpacketColumn(column));
        }
        return dst;
    }

    public String finish() {
        return null;
    }

    public  String getIdName() {
        return idName;
    }

    public Task getTask() {
        return task;
    }

    public OperatorStatus getStatus() {
        return status;
    }

    public void setStatus(OperatorStatus status) {
        this.status = status;
    }

    public boolean isCrowd() {
        return isCrowd;
    }

    public void setCrowd(boolean crowd) {
        isCrowd = crowd;
    }
}
