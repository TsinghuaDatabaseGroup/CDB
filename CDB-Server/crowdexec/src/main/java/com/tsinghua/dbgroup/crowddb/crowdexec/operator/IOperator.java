/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 10/13/16 11:10 AM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

package com.tsinghua.dbgroup.crowddb.crowdexec.operator;


public interface IOperator {

    public boolean process();

    public String finish();
}
