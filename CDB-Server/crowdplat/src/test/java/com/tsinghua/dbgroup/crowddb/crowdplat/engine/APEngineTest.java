/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 11/9/16 7:58 PM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

package com.tsinghua.dbgroup.crowddb.crowdplat.engine;

import com.tsinghua.dbgroup.crowddb.crowdplat.core.Task;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskCategory;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskType;
import com.tsinghua.dbgroup.crowddb.crowdplat.result.BaseResult;
import com.tsinghua.dbgroup.crowddb.crowdplat.result.impl.JudgementResult;
import com.tsinghua.dbgroup.crowddb.crowdplat.result.impl.OptionsResult;
import com.tsinghua.dbgroup.crowddb.crowdplat.schema.impl.JudgementSchema;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class APEngineTest extends TestCase{

    @Test
    public void testConvertResult() throws Exception {
//        APEngine engine = new APEngine();
//
//        List<JudgementSchema> schemas = new ArrayList<JudgementSchema>();
//
//        Task task = new Task(schemas, TaskCategory.TEXT, TaskType.CROWD_EQUAL);
//        String jsonString = "{\"q1\": {\"answer\": 1, \"id\": \"q1\"}}";
//
//        HashMap<String, ? extends BaseResult> map = new HashMap<>();
//        map = engine.(task, jsonString);
//
//        for (HashMap.Entry<String, ? extends BaseResult> entry: map.entrySet()) {
//            System.out.println(entry.getValue().toString());
//            assertEquals(((JudgementResult)entry.getValue()).getAnswer(), new Integer(1));
//        }
//
//        jsonString = "{\"q1\": {\"answer\": [\"red\", \"green\", \"blue\"], \"id\": \"q1\"}}";
//        task = new Task(schemas, TaskCategory.TEXT, TaskType.CROWD_IN);
//        map = engine.convertResult(task, jsonString);
//        List<String> list = new ArrayList<>();
//        list.add("red");
//        list.add("green");
//        list.add("blue");
//        for (HashMap.Entry<String, ? extends BaseResult> entry: map.entrySet()) {
//            OptionsResult optionsResult = (OptionsResult) entry.getValue();
//            System.out.println(optionsResult.toString());
//            assertEquals(optionsResult.getAnswer(), list);
//        }
    }
}