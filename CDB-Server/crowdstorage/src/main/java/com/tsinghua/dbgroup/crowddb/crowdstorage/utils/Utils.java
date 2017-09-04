/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 11/3/16 12:53 PM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

package com.tsinghua.dbgroup.crowddb.crowdstorage.utils;

public class Utils {

    static String Separator = "\\.";

    public static String packetColumn(String table, String column) {
        String[] items = column.split(Utils.Separator);
        if (items.length > 1) {
            return column;
        }
        return table + "." + column;
    }

    public static String unpacketColumn(String column) {
        String[] items = column.split(Utils.Separator);
        if (items.length > 1) {
            return items[1];
        }
        return column;
    }


    public static String packetTable(String dbName, String tableName) {
        String[] items = tableName.split(Utils.Separator);
        if (items.length > 1) {
            return tableName;
        }
        return dbName + "." + tableName;
    }

    public static String unpacketTable(String table) {
        String[] items = table.split(Utils.Separator);
        if (items.length > 1) {
            return items[1];
        }
        return table;
    }

    public static String unpacketDBName(String table) {
        String[] items = table.split(Utils.Separator);
        if (items.length > 1) {
            return items[0];
        }
        return null;
    }
}



