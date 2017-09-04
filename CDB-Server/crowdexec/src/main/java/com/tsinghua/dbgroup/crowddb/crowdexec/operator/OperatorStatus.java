package com.tsinghua.dbgroup.crowddb.crowdexec.operator;

/**
 * Created by talus on 11/19/16.
 */
public enum OperatorStatus {
    /**
     * Have not sent task to crowd platform
     */
    INIT,

    /**
     * Querying questions on crowd platform
     */
    RUNNING,

    /**
     * Have received answers from platform, try to store into database
     */
    FINISHING,

    /**
     * Current operator has finished
     */
    FINISHED,
}
