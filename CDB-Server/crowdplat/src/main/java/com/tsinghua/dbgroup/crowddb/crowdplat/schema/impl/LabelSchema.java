package com.tsinghua.dbgroup.crowddb.crowdplat.schema.impl;

import com.tsinghua.dbgroup.crowddb.crowdplat.schema.BaseSchema;
import com.tsinghua.dbgroup.crowddb.crowdplat.schema.SchemaType;

import java.util.List;

/**
 * Created by talus on 11/17/16.
 */
public class LabelSchema extends BaseSchema{

    private String url;

    private List<String> options;

    private String attribute;

    public LabelSchema(String id, String attribute, String url, List<String> options) {
        super(id);
        this.url = url;
        this.options = options;
        this.attribute = attribute;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    @Override
    public SchemaType getType() {
        return SchemaType.LABEL_SCHEMA;
    }
}
