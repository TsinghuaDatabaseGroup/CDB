package com.tsinghua.dbgroup.crowddb.crowdcore.exceptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by talus on 11/19/16.
 */
public class ExceptionMap {

    public static Map<Integer, String> ErrorMap = new HashMap<>();

    static {
        initErrorMap();
    }

    private static void initErrorMap() {
        ErrorMap.put(0x100, "sql parse error");
        ErrorMap.put(0x200, "database error");
        ErrorMap.put(0x300, "crowdsourcing platform error");
        ErrorMap.put(0x400, "operator execution error");
    }
}
