/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 10/8/16 9:14 PM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

package com.tsinghua.dbgroup.crowddb.crowdplat;

import com.tsinghua.dbgroup.crowddb.crowdcore.configs.GlobalConfigs;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.Task;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskCategory;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskStatus;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskType;
import com.tsinghua.dbgroup.crowddb.crowdplat.engine.APEngine;
import com.tsinghua.dbgroup.crowddb.crowdplat.result.BaseResult;
import com.tsinghua.dbgroup.crowddb.crowdplat.schema.BaseSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

public class TaskManager {

    private static String LOG_FORMAT = "##TaskManager##";

    private static Logger LOG = LoggerFactory.getLogger(TaskManager.class);

    private static boolean simulation = Boolean.valueOf(GlobalConfigs.GlobalConfigs.getProperty("SIMULATION"));

    public static Task createTask(TaskCategory taskCategory, TaskType taskType, List<? extends BaseSchema> schemas) {
        Task task = new Task(schemas, taskCategory, taskType);
        return task;
    }

    public static boolean uploadTask(Task task) {
       if (simulation) return true;

       APEngine engine = new APEngine();
       boolean result = engine.uploadTask(task);
       if (result) {
           LOG.info(String.format("upload task successfully, task_id = %s", task.getId()));
       } else {
           LOG.error(String.format("upload task failed, task_id = %s", task.getId()));
       }
       return result;
       // return true;
    }

    public static TaskStatus checkStatus(Task task) {
       if (simulation) return TaskStatus.FINISHED;

       APEngine engine = new APEngine();
       TaskStatus taskStatus = engine.checkStatus(task.getId());
       if (taskStatus == TaskStatus.FINISHED) {
           LOG.info(String.format("task = %s finished", task.getId()));
       }
       return taskStatus;
    }

    public static boolean pullRequests(Task task) {
       APEngine engine = new APEngine();

        HashMap<String, ? extends BaseResult> res = null;
       if (!simulation) {
           res = engine.pullResults(task);
       } else {
           res = task.mockResult();
       }

       if (res != null) {
            // HashMap<String, String> stringRes = task.packetResults(res);
            // task.extractResults(stringRes);
            task.extractResults(res);
            LOG.info(String.format("extract results successfully, task_id = %s", task.getId()));
            return true;
        }
        return false;
    }
}
