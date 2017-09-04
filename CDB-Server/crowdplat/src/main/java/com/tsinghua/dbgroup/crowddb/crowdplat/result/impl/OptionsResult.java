/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 11/8/16 4:57 PM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

package com.tsinghua.dbgroup.crowddb.crowdplat.result.impl;

import com.tsinghua.dbgroup.crowddb.crowdplat.result.BaseResult;

import java.util.List;

public class OptionsResult extends BaseResult {

    private List<String> answer;

    public OptionsResult(String id, List<String> options) {
        super(id);
        this.setAnswer(options);
    }

    public List<String> getAnswer() {
        return answer;
    }

    public void setAnswer(List<String> answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "OptionsResult{" +
                "id=" + getId() +
                ", answer=" + answer +
                '}';
    }
}
