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

public class JudgementResult extends BaseResult {

    private Integer answer;

    public JudgementResult(String id, int value) {
        super(id);
        this.setAnswer(value);
    }

    public Integer getAnswer() {
        return answer;
    }

    public void setAnswer(Integer answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "JudgementResult{" +
                "id=" + getId() +
                ", answer=" + answer +
                '}';
    }
}
