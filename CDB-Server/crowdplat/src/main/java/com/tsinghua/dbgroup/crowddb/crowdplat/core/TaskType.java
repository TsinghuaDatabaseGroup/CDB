/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 10/8/16 9:01 PM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

package com.tsinghua.dbgroup.crowddb.crowdplat.core;

public enum TaskType {

    /**
     * Judgement Questions
    * */
    CROWD_EQUAL("crowd_eq"), CROWD_NOT_EQUAL("crowd_not_eq"), CROWD_GT("crowd_gt"),
    CROWD_GE("crowd_ge"), CROWD_LT("crowd_lt"), CROWD_LE("crowd_le"),CROWD_JOIN("crowd_join"),

    /**
     * Multiple Options Questions
    * */
    CROWD_IN("crowd_in"),

    /**
     * Collection Questions
    * */
    COLLECT("collect"),

    /**
     * Label Questions
     */
    SINGLE_LABEL("single_label"), MULTI_LABEL("multi_label"),

    /**
     * FILL Questions
     */
    FILL("fill")

    ;

    private String type;

    private TaskType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}