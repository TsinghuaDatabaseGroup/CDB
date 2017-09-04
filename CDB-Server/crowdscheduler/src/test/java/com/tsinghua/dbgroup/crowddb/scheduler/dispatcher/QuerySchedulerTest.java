/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 10/13/16 12:36 AM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

package com.tsinghua.dbgroup.crowddb.scheduler.dispatcher;

import org.junit.Test;

import static org.junit.Assert.*;

public class QuerySchedulerTest {
    @Test
    public void TestNewQuery() throws Exception {
        int queryId = 1;
        QueryScheduler scheduler = new QueryScheduler();
        scheduler.startNewQuery(queryId);
    }

}