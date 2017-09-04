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

public class OptionsSchema extends BaseSchema implements ISchema {

    private String keywords;

    private List<String> options;

    public OptionsSchema(String id, String keywords, List<String> options) {
        super(id);
        this.keywords = keywords;
        this.options = options;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    @Override
    public SchemaType getType() {
        return SchemaType.OPTION_SCHEMA;
    }

    @Override
    public String toString() {
        return "OptionsSchema{" +
                "keywords='" + keywords + '\'' +
                ", options=" + options +
                '}';
    }
}
