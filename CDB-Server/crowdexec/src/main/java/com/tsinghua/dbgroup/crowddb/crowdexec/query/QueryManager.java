package com.tsinghua.dbgroup.crowddb.crowdexec.query;

import com.tsinghua.dbgroup.crowddb.crowdcore.exceptions.CrowdDBException;
import com.tsinghua.dbgroup.crowddb.crowdexec.operator.BaseOperator;
import com.tsinghua.dbgroup.crowddb.crowdexec.operator.OperatorStatus;
import com.tsinghua.dbgroup.crowddb.crowdexec.query.schema.HibernateSessionManager;
import com.tsinghua.dbgroup.crowddb.crowdexec.query.schema.Query;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.TableManager;
import com.tsinghua.dbgroup.crowddb.crowdsql.tree.SqlTreeNode;
import com.tsinghua.dbgroup.crowddb.crowdstorage.utils.Utils;

import org.hibernate.HibernateError;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by talus on 16/6/2.
 */
public class QueryManager implements IQueryManager {

    private static String LOG_FORMAT = "##QueryManager##";

    private static Logger LOG = LoggerFactory.getLogger(QueryManager.class);

    public void startNewQuery(Query query) throws Exception {
        LOG.info(String.format("%s start new query, queryId = %d", LOG_FORMAT, query.getId()));
        processNext(query);
    }

    public void processNext(Query query) throws Exception {
        if (query == null) {
            LOG.warn(String.format("%s query is None", LOG_FORMAT));
            return;
        }

        /**
         * save the result of current node
         */
        if (!query.getStatus().equals("init")) {
                    if (!processCurOperator(query)) {
                        LOG.warn(String.format("can not operate the previous node, query = %d", query.getId()));
                        return;
            }
            /**
             * set operator status as finished
             */
            BaseOperator operator = query.getCurOperator();
            operator.setStatus(OperatorStatus.FINISHED);
        }

        /**
         * execute the next node
         */
        SqlTreeNode nextNode = query.next();
        if (nextNode == null) {
            finishQuery(query);
        } else {
            QueryExecutor executor = new QueryExecutor();
            BaseOperator operator = null;
            try {
                operator = executor.buildOperator(nextNode, query.getDbName());
                query.setCurOperator(operator);
                boolean res = executor.execute(operator);
                if (operator == null || !res) {
                    LOG.error(String.format("%s can not execute sqlnode %s", LOG_FORMAT, nextNode.getNodeID()));
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new CrowdDBException(0x400);
            }

            query.setStatus(Query.QUERYING);
            query.setCurNode(nextNode);
            setSnapshot(query, nextNode);

            LOG.info(String.format("%s continue to execute query %d", LOG_FORMAT, query.getId()));
        }
    }

    public boolean hasNext(Query query) {
        return query.hasNext();
    }

    public boolean hasCurrentTaskFinished(Query query) {
        BaseOperator operator = query.getCurOperator();
        if (operator == null) {
            LOG.error(String.format("operator is null, queryId = %d", query.getId()));
            return false;
        }

        /**
         * only operator status is running and current task has finished, it means finishing current
         * task and can restore result
         */
        return (!operator.isCrowd() ||
                (operator.hasTaskFinished() && operator.getStatus() == OperatorStatus.RUNNING));
    }

    public void finishQuery(Query query) throws Exception {
        if (!processCurOperator(query)) {
            LOG.warn(String.format("can not operate the previous node, query = %d", query.getId()));
            return;
        }

        LOG.info(String.format("%s query %d finished, start restore and save result",LOG_FORMAT, query.getId()));

        boolean res = restoreTable(query);
        if (!res) {
            LOG.error(String.format("Can not restore table %s", query.getCurTmpTable()));
            return;
        }

        res = saveResultTable(query);
        if (!res) {
            LOG.error(String.format("Can not save the result table, dsttable = %s, srctable = %s",
                    query.getResultTable(), query.getCurTmpTable()));
            return;
        }
        setFinishedStatus(query);

        LOG.info(String.format("query %d totally finished.", query.getId()));
    }

    public List<Query> loadQueries() throws Exception {

        Session session = HibernateSessionManager.currentSession();
        session.beginTransaction();
        List<Query> queries = null;
        try {
            String sql = String.format("from Query where status != '%s' and status != '%s'", Query.INIT, Query.ERROR);
            queries = session.createQuery(sql).list();
        } catch (Exception e) {
            LOG.error("Cann't load queries with querying status", e);
            e.printStackTrace();
            throw new CrowdDBException(0x200);
        } finally {
            HibernateSessionManager.closeSession();
        }

        return queries;
    }

    public Query loadQuery(int queryId) {
        Session session = HibernateSessionManager.currentSession();
        try {
            session.beginTransaction();
            Query query = (Query) session.get(Query.class, queryId);
            return query;
        } catch (HibernateError e) {
            e.printStackTrace();
            throw new CrowdDBException(0x200);
        } finally {
            session.close();
        }
    }

    private void setSnapshot(Query query, SqlTreeNode node) {
        //TODO: store current snapshot
        query.setCurrentSQLNodeId(1);
        try {
            saveQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CrowdDBException(0x200);
        }

    }

    private void setFinishedStatus(Query query) throws Exception{
        query.setStatus(Query.FINISHED);
        saveQuery(query);
    }

    public void setErrorMessage(Query query, String errorMsg) {
        query.setErrorMsg(errorMsg);
        query.setStatus(Query.ERROR);

        try {
            saveQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CrowdDBException(0x200);
        }
    }

    private boolean processCurOperator(Query query) {
        BaseOperator curOperator = query.getCurOperator();

        boolean success = curOperator.getResult();
        if (!success) {
            LOG.error(String.format("Can not get results, query_id = %s, operator = %s", query.getId(), curOperator.toString()));
            return false;
        }

        String curTempTable;
        try {
            curTempTable = curOperator.finish();
        } catch (Exception e) {
            e.printStackTrace();
            throw new CrowdDBException(0x200);
        }
        query.setCurTmpTable(curTempTable);
        return true;
    }

    private void saveQuery(Query query) throws Exception {
        query.save();
        try {
//            query.save();
        } catch (Exception e) {
            LOG.error(LOG_FORMAT + " save query failed, queryId = "+query.getId(), e);
            throw e;
        }
    }

    private boolean saveResultTable(Query query) {
        if (query.getCurTmpTable() == null) {
            LOG.warn("CurrentTempTable is null");
            return true;
        }

        String dstTable = String.format("%s.%s", query.getDbName(), query.getResultTable());
        String srcTable = query.getCurTmpTable();
        LOG.info(String.format("save result from %s to %s", srcTable, dstTable));

        TableManager tm = new TableManager();

        boolean res = false;
        try {
            res = tm.copyTable(dstTable, srcTable);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CrowdDBException(0x200);
        }

        if (res) query.setCurTmpTable(dstTable);
        return res;
    }

    private boolean restoreTable(Query query) {
        if (query.getCurTmpTable() == null) {
            LOG.warn("CurrentTempTable is null");
            return true;
        }

        String newTable = TableManager.generateTableName();
        newTable = Utils.packetTable(TableManager.TMP_DATABASE, newTable);

        boolean res = false;
        try {
            res = new TableManager().restoreTable(newTable, query.getCurTmpTable());
        } catch (Exception e) {
            e.printStackTrace();
            throw new CrowdDBException(0x200);
        }

        if (res) query.setCurTmpTable(newTable);
        return res;
    }
}
