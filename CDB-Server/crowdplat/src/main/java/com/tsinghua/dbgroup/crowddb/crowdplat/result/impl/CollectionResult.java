package com.tsinghua.dbgroup.crowddb.crowdplat.result.impl;

import com.tsinghua.dbgroup.crowddb.crowdplat.result.BaseResult;

import java.util.HashMap;
import java.util.List;

/**
 * Created by talus on 11/30/16.
 */
public class CollectionResult extends BaseResult {

    private List<HashMap<String, String>> answer;

    public CollectionResult(String id, List<HashMap<String, String>> answer) {
        super(id);
        this.setAnswer(answer);
    }

    public List<HashMap<String, String>> getAnswer() {
        return answer;
    }

    public void setAnswer(List<HashMap<String, String>> answer) {
        this.answer = answer;
    }
}
