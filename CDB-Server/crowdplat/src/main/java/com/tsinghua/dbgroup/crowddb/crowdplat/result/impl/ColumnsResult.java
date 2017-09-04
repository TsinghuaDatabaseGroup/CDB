/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 11/8/16 4:58 PM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

package com.tsinghua.dbgroup.crowddb.crowdplat.result.impl;

import com.tsinghua.dbgroup.crowddb.crowdplat.result.BaseResult;

import java.util.HashMap;
import java.util.List;

public class ColumnsResult extends BaseResult {

    private HashMap<String, String> answer;

    public ColumnsResult(String id, HashMap<String, String> answer) {
        super(id);
        setAnswer(answer);
    }

    public HashMap<String, String> getAnswer() {
        return answer;
    }

    public void setAnswer(HashMap<String, String> answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "ColumnsResult{" +
                "id=" + getId() +
                ", answer=" + answer +
                '}';
    }
}
