package com.tsinghua.dbgroup.crowddb.scheduler.threads;

import com.tsinghua.dbgroup.crowddb.crowdexec.query.QueryManager;
import com.tsinghua.dbgroup.crowddb.crowdexec.query.schema.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by talus on 7/3/16.
 */
public class FinishQueryThread extends Thread {

    private static Logger LOG = LoggerFactory.getLogger(FinishQueryThread.class);

    private QueryManager queryManager;

    private Query query;

    public FinishQueryThread(QueryManager queryManager, Query query) {
        setQuery(query);
        setQueryManager(queryManager);
    }

    public void setQueryManager(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    @Override
    public void run() {
        try {
            queryManager.finishQuery(query);
        } catch (Exception e) {
            LOG.error("finish query failed", e);
        }
    }
}
