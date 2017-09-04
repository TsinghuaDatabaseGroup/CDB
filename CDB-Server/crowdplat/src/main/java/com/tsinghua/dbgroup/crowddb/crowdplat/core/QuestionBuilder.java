/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 11/8/16 4:23 PM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

package com.tsinghua.dbgroup.crowddb.crowdplat.core;

import com.tsinghua.dbgroup.crowddb.crowdplat.schema.BaseSchema;
import com.tsinghua.dbgroup.crowddb.crowdplat.schema.impl.*;

public class QuestionBuilder {

    public Question createQuestion(TaskCategory taskCategory, TaskType taskType, String questionId, BaseSchema schema) {
        Question question = new Question(questionId, "");
        question.setSchemaId(schema.getId());
        String template = null;
        String content = null;

        switch (schema.getType()) {
            case COLLECT_SCHEMA:

                CollectionSchema collectionSchema = (CollectionSchema) schema;
                question.setAttribute(collectionSchema.getAttribute());
                question.setLimit(collectionSchema.getLimit());
                question.setColumns(collectionSchema.getColumns());
                content = String.format(TaskConfigure.COLLECT_TEMPLATE, collectionSchema.getAttribute());

                question.setContent(content);

                break;

            case JOIN_SCHEMA:
                JudgementSchema judgementSchema = (JudgementSchema) schema;
                content = String.format(TaskConfigure.JUDGEMENT_TEMPLATE, judgementSchema.getLeftKey(),
                        TaskConfigure.COMPARISION_WORDS.get(taskType), judgementSchema.getRightKey());
                question.setContent(content);

                break;

            case OPTION_SCHEMA:
                OptionsSchema optionsSchema = (OptionsSchema) schema;

                question.setOptions(optionsSchema.getOptions());
                content = String.format(TaskConfigure.IN_TEMPLATE);
                question.setContent(content);

                break;

            case FILL_SCHEMA:
                FillSchema fillSchema = (FillSchema) schema;
                question.setColumns(fillSchema.getColumns());
                question.setAttribute(fillSchema.getValue());

                String columnStr = String.join(", ", fillSchema.getColumns());
                content = String.format(TaskConfigure.FILL_TEMPLATE, columnStr, fillSchema.getValue());
                question.setContent(content);

                break;

            case LABEL_SCHEMA:
                LabelSchema labelSchema = (LabelSchema) schema;
                question.setAttribute(labelSchema.getAttribute());
                question.setOptions(labelSchema.getOptions());
                question.setUrl(labelSchema.getUrl());

                template = taskType == TaskType.SINGLE_LABEL ? TaskConfigure.SINGLE_LABEL_TEMPLATE : TaskConfigure.MULT_LABEL_TEMPLATE;
                content = String.format(template, labelSchema.getAttribute());
                question.setContent(content);

                break;
        }
        return question;
    }
}
