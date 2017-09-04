/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 11/8/16 4:14 PM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

package com.tsinghua.dbgroup.crowddb.crowdplat.schema;

public enum SchemaType {
    JOIN_SCHEMA("join_schema"), OPTION_SCHEMA("in_schema"), COLLECT_SCHEMA("collect_schema"),
    FILL_SCHEMA("fill_schema"), LABEL_SCHEMA("label_schema"), UNKNOWN("unknown");

    private String type;

    private SchemaType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}
