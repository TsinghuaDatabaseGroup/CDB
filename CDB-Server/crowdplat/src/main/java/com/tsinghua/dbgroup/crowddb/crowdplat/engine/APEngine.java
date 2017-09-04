/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 10/8/16 8:26 PM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

package com.tsinghua.dbgroup.crowddb.crowdplat.engine;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.*;
import com.tsinghua.dbgroup.crowddb.crowdplat.http.HttpRequest;
import com.tsinghua.dbgroup.crowddb.crowdplat.result.BaseResult;
import com.tsinghua.dbgroup.crowddb.crowdplat.result.impl.CollectionResult;
import com.tsinghua.dbgroup.crowddb.crowdplat.result.impl.ColumnsResult;
import com.tsinghua.dbgroup.crowddb.crowdplat.result.impl.JudgementResult;
import com.tsinghua.dbgroup.crowddb.crowdplat.result.impl.OptionsResult;

import java.util.HashMap;
import java.util.List;

public class APEngine implements ICrowdEngine {

    @Override
    public boolean uploadTask(Task task) {
        HashMap<String, String> options = packetOptions(task);

        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.add("options", gson.toJsonTree(options));
        data.add("questions", gson.toJsonTree(task.getQuestions().values()));

        HashMap<String, String> postParams = new HashMap<>();
        HttpRequest request = new HttpRequest();
        postParams.put("data", data.toString());

        JsonObject response = request.sendPostRequest(TaskConfigure.UPLOAD_TASK_URL, null, postParams);
        if (response == null || response.get("code").getAsInt() != 0) {
            throw new RuntimeException("response is null or response code is not 0");
        }
        return true;
    }

    @Override
    public TaskStatus checkStatus(String taskId) {
        HashMap<String, String> getParams = new HashMap<>();
        HttpRequest request = new HttpRequest();
        getParams.put("task_id", taskId);

        JsonObject response = request.sendGetRequest(TaskConfigure.CHECK_STATUS_URL, getParams);
        if (response == null || response.get("code").getAsInt() != 0) {
            throw new RuntimeException("response is null or response code is not 0");
        }

        boolean status = response.get("status").getAsBoolean();
//        System.out.println(response.get("status"));
        if (status) return TaskStatus.FINISHED;
        return TaskStatus.FINISHING;
    }

    @Override
    public boolean appendData(String taskId, List<Question> questions) {
        return false;
    }

    @Override
    public HashMap<String, ? extends BaseResult> pullResults(Task task) {
        HashMap<String, String> getParams = new HashMap<>();
        HttpRequest request = new HttpRequest();
        getParams.put("task_id", task.getId());

        JsonObject response = request.sendGetRequest(TaskConfigure.DOWNLOAD_RESULTS_URL, getParams);
        if (response == null || response.get("code").getAsInt() != 0) {
            throw new RuntimeException("response is null or response code is not 0");
        }
        HashMap<String, ? extends BaseResult> res = buildResult(task, response.get("data").toString());
        return buildResult(task, response.get("data").toString());
    }

    private HashMap<String, String> packetOptions(Task task) {
        HashMap<String, String> options = new HashMap<>(TaskConfigure.OPTIONS);
        options.put("task_id", task.getId());
        options.put("task_category", task.getTaskCategory().toString());
        options.put("task_type", task.getTaskType().toString());

        TaskType taskType = task.getTaskType();
        if (taskType == TaskType.FILL) {
            options.put("q_type", "FREE");
        } else if (taskType == TaskType.COLLECT) {
            options.put("q_type", "COLLECT");
        } else if (taskType == TaskType.MULTI_LABEL) {
            options.put("q_type", "M_TO_M");
        } else if (taskType == TaskType.SINGLE_LABEL) {
            options.put("q_type", "M_TO_O");
        } else {
            options.put("q_type", "Y_N");
        }
        return options;
    }

    private HashMap<String, ? extends BaseResult> buildResult(Task task, String resultString) {
        Gson gson = new Gson();
        TaskType taskType = task.getTaskType();

        try {
            if (taskType == TaskType.FILL) {
                HashMap<String, ColumnsResult> results =
                        gson.fromJson(resultString, new TypeToken<HashMap<String, ColumnsResult>>() {
                        }.getType());
                return results;

            } else if (taskType == taskType.COLLECT) {
                HashMap<String, CollectionResult> result =
                        gson.fromJson(resultString, new TypeToken<HashMap<String, CollectionResult>>() {
                        }.getType());
                return buildColumnsResult(result);
            } else if (taskType == taskType.SINGLE_LABEL ||
                    taskType == TaskType.MULTI_LABEL) {

                HashMap<String, OptionsResult> results =
                        gson.fromJson(resultString, new TypeToken<HashMap<String, OptionsResult>>() {
                        }.getType());

                return results;
            } else {
                HashMap<String, JudgementResult> results =
                        gson.fromJson(resultString, new TypeToken<HashMap<String, JudgementResult>>() {
                        }.getType());

                return results;
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private HashMap<String, ColumnsResult> buildColumnsResult(HashMap<String, CollectionResult> result) {
        HashMap<String, ColumnsResult> results = new HashMap<>();
        List<HashMap<String, String>> answers = result.entrySet().iterator().next().getValue().getAnswer();
        int idx = 0;
        for (HashMap<String, String> answer : answers) {
            String key = Integer.toString(idx++);
            ColumnsResult columnsResult = new ColumnsResult(key, answer);
            results.put(key, columnsResult);
        }
        return results;
    }
}