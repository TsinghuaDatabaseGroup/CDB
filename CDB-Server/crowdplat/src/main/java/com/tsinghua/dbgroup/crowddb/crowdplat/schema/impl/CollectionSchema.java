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

import java.util.List;

public class CollectionSchema extends BaseSchema implements ISchema {

    private List<String> columns;

    private String attribute;

    private int limit;

    public CollectionSchema(String id, String attribute, int limit, List<String> columns) {
        super(id);
        setAttribute(attribute);
        setLimit(limit);
        setColumns(columns);
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    @Override
    public SchemaType getType() {
        return SchemaType.COLLECT_SCHEMA;
    }

    @Override
    public String toString() {
        return "CollectionSchema{" +
                "limit='" + limit + '\'' +
                ", attribute='" + attribute + '\'' +
                ", c='" + columns + '\'' +
                '}';
    }
}
