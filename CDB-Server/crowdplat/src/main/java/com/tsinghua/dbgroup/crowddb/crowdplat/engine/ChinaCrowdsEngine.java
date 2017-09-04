package com.tsinghua.dbgroup.crowddb.crowdplat.engine;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.Question;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.Task;
import com.tsinghua.dbgroup.crowddb.crowdplat.core.TaskStatus;
import com.tsinghua.dbgroup.crowddb.crowdplat.result.BaseResult;

import java.util.HashMap;
import java.util.List;

/**
 * Created by talus on 16/6/2.
 */
public class ChinaCrowdsEngine implements ICrowdEngine{

    @Override
    public boolean uploadTask(Task task) {
        return false;
    }

    @Override
    public TaskStatus checkStatus(String taskId) {
        return null;
    }

    @Override
    public boolean appendData(String taskId, List<Question> questions) {
        return false;
    }

    @Override
    public HashMap<String, ? extends BaseResult> pullResults(Task task) {
        return null;
    }
}
