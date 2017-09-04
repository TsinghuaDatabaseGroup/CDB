package com.tsinghua.dbgroup.crowddb.crowdplat.core;

/**
 * Created by x-yu13 on 16/10/28.
 */
public class TaskPair {
    String id1,id2,value1,value2;

    public TaskPair(String id1, String id2, String value1, String value2) {
        this.id1 = id1;
        this.id2 = id2;
        this.value1 = value1;
        this.value2 = value2;
    }

    public String getId1() {
        return id1;
    }

    public void setId1(String id1) {
        this.id1 = id1;
    }

    public String getId2() {
        return id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }
}
