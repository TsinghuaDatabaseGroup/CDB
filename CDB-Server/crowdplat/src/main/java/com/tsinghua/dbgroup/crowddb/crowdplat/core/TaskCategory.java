/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 10/12/16 2:50 PM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

package com.tsinghua.dbgroup.crowddb.crowdplat.core;

public enum TaskCategory {
    TEXT("text"), IMAGE("image"), AUDIO("audio");

    private final String category;

    private TaskCategory(final String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return this.category;
    }
}