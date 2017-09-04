/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 11/3/16 12:52 PM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

package com.tsinghua.dbgroup.crowddb.crowdstorage.utils;

import com.tsinghua.dbgroup.crowddb.crowdstorage.table.TableManager;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.schema.EqualSchema;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.schema.JoinSchema;
import org.apache.commons.lang3.tuple.Pair;

import java.util.UUID;

/**
 * Created by talus on 16/6/11.
 */
public class TableHelper {

    public static String generateTmpTableName(int queryId, int taskId) {
        String randomStr = UUID.randomUUID().toString().substring(0,8);
        return String.format("q%d_t%d_%s", queryId, taskId, randomStr);
    }

    public static Class getSchemaClass(int tableType) {
        Object object = null;

        switch (tableType) {
            case JoinSchema.SCHEMA_ID:
                object = (JoinSchema)new JoinSchema();
                break;

            case EqualSchema.SCHEMA_ID:
                object = (EqualSchema)new EqualSchema();
                break;
        }
        return object.getClass();
    }

    public static String wrapTableName(String tableName) {
        return String.format("%s.%s", TableManager.TMP_DATABASE, tableName);
    }

    public static Pair<String, String> unpackTableColumn(String column) {
        int index = column.lastIndexOf('.');
        if (index == -1) return null;

        String col = column.substring(index+1,column.length()-1);
        String tableName = column.substring(0, index-1);
//        Pair<String, String> pair = new Pair<String, String>(tableName, col);
//        return pair;
        return null;
    }
}
