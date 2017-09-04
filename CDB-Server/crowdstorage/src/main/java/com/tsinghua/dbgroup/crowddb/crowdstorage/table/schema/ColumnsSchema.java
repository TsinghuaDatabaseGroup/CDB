package com.tsinghua.dbgroup.crowddb.crowdstorage.table.schema;

import java.util.HashMap;

/**
 * Created by talus on 11/15/16.
 */
public class ColumnsSchema {

    public static final int SCHEMA_ID = 0x02;

    private String id;

    private HashMap<String, String> columns;

    public ColumnsSchema(String id, HashMap<String, String> columns) {
        this.id = id;
        this.columns = columns;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, String> getColumns() {
        return columns;
    }

    public void setColumns(HashMap<String, String> columns) {
        this.columns = columns;
    }
}
