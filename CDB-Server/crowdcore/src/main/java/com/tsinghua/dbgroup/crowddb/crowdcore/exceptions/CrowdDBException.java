package com.tsinghua.dbgroup.crowddb.crowdcore.exceptions;

/**
 * Created by talus on 11/19/16.
 */
public class CrowdDBException extends RuntimeException {

    public CrowdDBException(int errorCode) {
        super(ExceptionMap.ErrorMap.getOrDefault(errorCode, "unknown error"));
    }
}
