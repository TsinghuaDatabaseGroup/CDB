package com.tsinghua.dbgroup.crowddb.scheduler.dispatcher;


import com.tsinghua.dbgroup.crowddb.crowdcore.exceptions.CrowdDBException;
import com.tsinghua.dbgroup.crowddb.crowdexec.query.QueryManager;
import com.tsinghua.dbgroup.crowddb.crowdexec.query.schema.Query;
import com.tsinghua.dbgroup.crowddb.scheduler.threads.ContinueQueryThread;
import com.tsinghua.dbgroup.crowddb.scheduler.threads.FinishQueryThread;
import com.tsinghua.dbgroup.crowddb.scheduler.threads.NewQueryThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by talus on 16/6/20.
 */
public class QueryScheduler extends Thread {

    private static String LOG_FORMAT = "##Query Scheduler##";

    private static Logger LOG = LoggerFactory.getLogger(QueryScheduler.class);

    private DateFormat df = new SimpleDateFormat("y-MM-dd HH:mm:ss");

    /*
    * Query Manager to manage query operation
    * */
    private QueryManager queryManager;

    /*
    * dispatcher Queue to store queries
    * */
    private ConcurrentSkipListSet<Query> dispatcherQueue;


    public QueryScheduler() throws Exception {
        queryManager = new QueryManager();
        dispatcherQueue = new ConcurrentSkipListSet<>();

        try {
            loadQueueFromDatabase();
        } catch (Exception e) {
            LOG.error(String.format("%s start query scheduler failed. ", LOG_FORMAT), e);
            throw e;
        }
    }

    @Override
    public void run() {
        LOG.info(String.format("start polling queries, current time: %s", df.format(new Date())));
        pollQueries();
    }

    public void startNewQuery(int queryId) throws Exception{

        Query query = queryManager.loadQuery(queryId);

        try {
            query.parseSql();
        } catch (CrowdDBException e) {
            LOG.error("parse sql error, queryId = %d", query.getId());
            queryManager.setErrorMessage(query, e.getMessage());
            return;
        }

        if (!query.getStatus().equals(Query.INIT)) {
            LOG.warn(String.format("wrong query status, now is %s, expect %s, queryId = %d", query.getStatus(),
                    query.INIT, query.getId()));
            return;
        }
        if (dispatcherQueue.contains(query)) {
            LOG.warn(String.format("query %d is already in queue, skip. ", query.getId()));
            return;
        }

        dispatcherQueue.add(query);

        try {
            (new NewQueryThread(queryManager, query)).start();
        } catch (CrowdDBException e) {
            dispatcherQueue.remove(query);
            queryManager.setErrorMessage(query, e.getMessage());
        }

    }

    private void pollQueries() {
        for (Query query: dispatcherQueue) {
            if (!hasCurrentTaskFinished(query)) continue;

            if (queryManager.hasNext(query)) {
                continueQuery(query);
            } else {
                try {
                    finishQuery(query);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }
    }

    private void continueQuery(Query query) {
        try {
            (new ContinueQueryThread(queryManager, query)).start();
        } catch (CrowdDBException e) {
            dispatcherQueue.remove(query);
            queryManager.setErrorMessage(query, e.getMessage());
        }
    }

    private void finishQuery(Query query) {
        try {
            (new FinishQueryThread(queryManager, query)).start();
        } catch (CrowdDBException e) {
            dispatcherQueue.remove(query);
            queryManager.setErrorMessage(query, e.getMessage());
        } finally {
            // if finishing query successfully, then we must remove query
            // if error, we need to remove too
            dispatcherQueue.remove(query);
        }
    }

    private boolean hasCurrentTaskFinished(Query query) {
        return queryManager.hasCurrentTaskFinished(query);
    }

    private void loadQueueFromDatabase() throws Exception{

        // List<Query> queryList = queryManager.loadQueries();
        // dispatcherQueue.clear();
        // for (Query query: queryList) {
        //
        //     query.parseSql();
        //     dispatcherQueue.add(query);
        // }
    }
}
