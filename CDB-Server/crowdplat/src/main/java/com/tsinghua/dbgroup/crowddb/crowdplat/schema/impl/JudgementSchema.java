/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 11/8/16 4:55 PM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

package com.tsinghua.dbgroup.crowddb.crowdplat.schema.impl;

import com.tsinghua.dbgroup.crowddb.crowdplat.schema.BaseSchema;
import com.tsinghua.dbgroup.crowddb.crowdplat.schema.ISchema;
import com.tsinghua.dbgroup.crowddb.crowdplat.schema.SchemaType;

public class JudgementSchema extends BaseSchema implements ISchema {

    public static final String TYPE = "join_schema";

    private String leftKey;

    private String rightKey;

    public JudgementSchema(String id, String leftKey, String rightKey) {
        super(id);
        this.leftKey = leftKey;
        this.rightKey = rightKey;
    }

    public String getLeftKey() {
        return leftKey;
    }

    public void setLeftKey(String leftKey) {
        this.leftKey = leftKey;
    }

    public String getRightKey() {
        return rightKey;
    }

    public void setRightKey(String rightKey) {
        this.rightKey = rightKey;
    }

    @Override
    public SchemaType getType() {
        return SchemaType.JOIN_SCHEMA;
    }

    @Override
    public String toString() {
        return "JudgementSchema{" +
                "leftKey='" + leftKey + '\'' +
                ", rightKey='" + rightKey + '\'' +
                '}';
    }
}
