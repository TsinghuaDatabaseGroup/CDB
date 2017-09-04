package com.tsinghua.dbgroup.crowddb.scheduler.threads;

import com.tsinghua.dbgroup.crowddb.crowdexec.query.QueryManager;
import com.tsinghua.dbgroup.crowddb.crowdexec.query.schema.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by talus on 7/3/16.
 */
public class NewQueryThread extends Thread {

    private QueryManager queryManager;

    private Query query;

    private static Logger LOG = LoggerFactory.getLogger(FinishQueryThread.class);

    public NewQueryThread(QueryManager queryManager, Query query) {
        setQueryManager(queryManager);
        setQuery(query);
    }

    public void setQueryManager(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    @Override
    public void run() {
        //TODO: start new Query
        try {
            queryManager.startNewQuery(query);
        } catch (Exception e) {
            LOG.error("query scheduler start new query failed.", e);
        }
    }
}
