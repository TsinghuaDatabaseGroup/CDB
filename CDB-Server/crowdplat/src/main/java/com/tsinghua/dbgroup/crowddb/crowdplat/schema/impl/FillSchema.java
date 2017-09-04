/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 11/15/16 1:33 PM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

package com.tsinghua.dbgroup.crowddb.crowdplat.schema.impl;

import com.tsinghua.dbgroup.crowddb.crowdplat.schema.BaseSchema;
import com.tsinghua.dbgroup.crowddb.crowdplat.schema.SchemaType;

import java.util.List;

public class FillSchema extends BaseSchema {

    private List<String> columns;

    private String value;

    public FillSchema(String id,  String value, List<String> columns) {
        super(id);
        setColumns(columns);
        setValue(value);
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public SchemaType getType() {
        return SchemaType.FILL_SCHEMA;
    }
}
