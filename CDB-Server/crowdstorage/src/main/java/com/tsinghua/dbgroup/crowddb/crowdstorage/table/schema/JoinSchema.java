/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 11/3/16 12:51 PM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

package com.tsinghua.dbgroup.crowddb.crowdstorage.table.schema;

/**
 * Created by talus on 16/6/8.
 */
public class JoinSchema {

    public static String createSQL = new StringBuilder()
            .append("CREATE TABLE %s (")
            .append("eid1 int NOT NULL,")
            .append("eid2 int NOT NULL,")
            .append("res INT NOT NULL")
            .append(")").toString();

    public static String insertSQL = new StringBuilder()
            .append("INSERT INTO %s ")
            .append("(`eid1`, `eid2`, `res`) values %s").toString();

    public static final int SCHEMA_ID = 0x01;

    private int id;

    private String eid1;

    private String eid2;

    private int res;

    public JoinSchema() {

    }

    public JoinSchema(String eid1, String eid2, int res) {
        this.eid1 = eid1;
        this.eid2 = eid2;
        this.res = res;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEid1() {
        return eid1;
    }

    public void setEid1(String eid1) {
        this.eid1 = eid1;
    }

    public String getEid2() {
        return eid2;
    }

    public void setEid2(String eid2) {
        this.eid2 = eid2;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }
}
