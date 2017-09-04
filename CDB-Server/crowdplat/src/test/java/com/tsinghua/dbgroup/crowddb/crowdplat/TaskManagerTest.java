/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 10/8/16 9:26 PM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

package com.tsinghua.dbgroup.crowddb.crowdplat;

// import com.tsinghua.dbgroup.crowddb.crowdplat.core.Task;
// import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskCategory;
// import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskStatus;
// import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskType;
// import com.tsinghua.dbgroup.crowddb.crowdplat.schema.BaseSchema;
// import com.tsinghua.dbgroup.crowddb.crowdplat.schema.impl.CollectionSchema;
// import com.tsinghua.dbgroup.crowddb.crowdplat.schema.impl.FillSchema;
// import com.tsinghua.dbgroup.crowddb.crowdplat.schema.impl.JudgementSchema;
// import com.tsinghua.dbgroup.crowddb.crowdplat.schema.impl.LabelSchema;

import com.tsinghua.dbgroup.crowddb.crowdplat.core.Task;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskCategory;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskStatus;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskType;
import com.tsinghua.dbgroup.crowddb.crowdplat.schema.BaseSchema;
import com.tsinghua.dbgroup.crowddb.crowdplat.schema.impl.CollectionSchema;
import com.tsinghua.dbgroup.crowddb.crowdplat.schema.impl.FillSchema;
import com.tsinghua.dbgroup.crowddb.crowdplat.schema.impl.JudgementSchema;
import com.tsinghua.dbgroup.crowddb.crowdplat.schema.impl.LabelSchema;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TaskManagerTest {

    @Test
    public void createTask() throws Exception {
        Task task = initTask(TaskType.COLLECT);
        boolean res = TaskManager.uploadTask(task);
        assertEquals(res, true);
    }

    @Test
    public void checkStatus() throws Exception {
        Task task = initTask(TaskType.COLLECT);
        boolean res = TaskManager.uploadTask(task);
        TaskStatus taskStatus = TaskManager.checkStatus(task);
        assertEquals(taskStatus, TaskStatus.FINISHED);
    }

    @Test
    public void pullRequests() throws Exception {
        Task task = initTask(TaskType.SINGLE_LABEL);
        TaskManager.uploadTask(task);
        boolean success = TaskManager.pullRequests(task);

        // for (Map.Entry<Pair<String, String>, Integer> entry : task.getResults().entrySet()) {
        //     System.out.println(entry.getKey().toString() + ", " + entry.getValue().toString() + " = " + entry.getValue().toString());
        // }
    }

    private Task initTask(TaskType taskType) {
        List<BaseSchema> schemas = new ArrayList<>();
        if (taskType == TaskType.SINGLE_LABEL || taskType == TaskType.MULTI_LABEL) {
            for (int i = 0; i < 5; i++) {
                List<String> tags = Arrays.asList("a", "b", "c");
                LabelSchema schema = new LabelSchema(Integer.toString(i), "name", "url", tags);
                schemas.add(schema);
            }
        } else if (taskType == TaskType.COLLECT) {
            for (int i = 0; i < 5; i++) {
                List<String> columns = Arrays.asList("name", "school", "rank");
                CollectionSchema schema = new CollectionSchema(Integer.toString(i), "name", 10, columns);
                schemas.add(schema);
            }
        } else if (taskType == TaskType.FILL) {
            for (int i = 0; i < 5; i++) {
                List<String> columns = Arrays.asList("name", "school", "rank");
                FillSchema schema = new FillSchema(Integer.toString(i), "name", columns);
                schemas.add(schema);
            }
        } else {
            for (int i = 0; i < 5; i++) {
                JudgementSchema schema = new JudgementSchema(Integer.toString(i), Integer.toString(i), Integer.toString(i));
                schemas.add(schema);
            }
        }

        return TaskManager.createTask(TaskCategory.TEXT, taskType, schemas);
    }
}