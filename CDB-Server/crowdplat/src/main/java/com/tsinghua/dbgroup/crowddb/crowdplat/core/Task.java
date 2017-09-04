/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 10/8/16 4:25 PM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

package com.tsinghua.dbgroup.crowddb.crowdplat.core;

import com.tsinghua.dbgroup.crowddb.crowdplat.result.BaseResult;
import com.tsinghua.dbgroup.crowddb.crowdplat.result.impl.JudgementResult;
import com.tsinghua.dbgroup.crowddb.crowdplat.schema.BaseSchema;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Task {

    private String id;

    private List<? extends BaseSchema> schemas;

    private HashMap<String, Question> questions = new HashMap<>();

    private TaskCategory taskCategory;

    private TaskType taskType;

    private HashMap<String, BaseResult> results = new HashMap<>();

    public Task(List<? extends BaseSchema> schemas, TaskCategory taskCategory, TaskType taskType) {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        this.id = String.format("task-%s", uuid);
        this.setSchemas(schemas);
        this.setTaskCategory(taskCategory);
        this.setTaskType(taskType);

        this.generateQuestions();
    }

    public TaskCategory getTaskCategory() {
        return taskCategory;
    }

    public void setTaskCategory(TaskCategory taskCategory) {
        this.taskCategory = taskCategory;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<? extends BaseSchema> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<? extends BaseSchema> schemas) {
        this.schemas = schemas;
    }

    public HashMap<String, Question> getQuestions() {
        return questions;
    }

    public void setQuestions(HashMap<String, Question> questions) {
        this.questions = questions;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    private void generateQuestions() {
        String word = TaskConfigure.COMPARISION_WORDS.get(this.taskType);
        QuestionBuilder questionBuilder = new QuestionBuilder();

        for (BaseSchema schema: schemas) {
            String questionId = String.format("q-%d-op-%d-hit-%s", 01, 01, UUID.randomUUID().toString().substring(0, 8));
            Question question = questionBuilder.createQuestion(getTaskCategory(), getTaskType(), questionId, schema);
            this.questions.put(questionId, question);
        }
    }

    public HashMap<String, ? extends BaseResult> mockResult() {
        HashMap<String, JudgementResult> res = new HashMap<String, JudgementResult>();
        for (HashMap.Entry<String, Question> question : this.questions.entrySet()) {
            JudgementResult r = new JudgementResult(question.getKey(), 1);
            res.put(question.getKey(), r);
        }
        return res;
    }

    public void extractResults(HashMap<String, ? extends BaseResult> res) {
        for (HashMap.Entry<String, ? extends BaseResult> entry : res.entrySet()) {
            Question question = questions.getOrDefault(entry.getKey(), null);
            if (question == null)
                results.put(entry.getKey(), entry.getValue());
            else
                results.put(question.getSchemaId(), entry.getValue());
        }
    }
    //
    // public HashMap<String, String> packetResults(HashMap<String, ? extends BaseResult> resultHashMap) {
    //     HashMap<String, String> result = new HashMap<>();
    //     for (HashMap.Entry<String, ? extends BaseResult> entry: resultHashMap.entrySet()) {
    //         BaseResult resultObj = entry.getValue();
    //
    //         if (resultObj instanceof ColumnsResult) {
    //             result.put(entry.getKey(), String.join(";", ((ColumnsResult) resultObj).getAnswer()));
    //         } else if (resultObj instanceof OptionsResult) {
    //             result.put(entry.getKey(), String.join(";", ((OptionsResult) resultObj).getAnswer()));
    //         } else {
    //             result.put(entry.getKey(), ((JudgementResult) resultObj).getAnswer().toString());
    //         }
    //     }
    //     return result;
    // }

    public HashMap<String, ? extends BaseResult> getResults() {
        return results;
    }

//    public void setResults(HashMap<String, ? extends BaseResult> results) {
//        this.results = results;
//    }
}
