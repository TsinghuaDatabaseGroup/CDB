/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 10/8/16 8:27 PM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

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

public interface ICrowdEngine {

    public boolean uploadTask(Task task);

    public TaskStatus checkStatus(String taskId);

    public boolean appendData(String taskId, List<Question> questions);

    public HashMap<String, ? extends BaseResult> pullResults(Task task);
}
