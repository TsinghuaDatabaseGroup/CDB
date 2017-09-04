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
public class EqualSchema {

    public static String createSQL = new StringBuilder()
            .append("CREATE TABLE %s (")
            .append("eid1 int NOT NULL,")
            .append("res INT NOT NULL")
            .append(")").toString();

    public static String insertSQL = new StringBuilder()
            .append("INSERT INTO %s ")
            .append("(`eid1`, `res`) values ")
            .append("%s").toString();


    public static final int SCHEMA_ID = 0x02;

    private int id;

    private String eid;

    private int res;

    public EqualSchema() {

    }

    public EqualSchema(String eid, int res) {
        this.eid = eid;
        this.res = res;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }
}
