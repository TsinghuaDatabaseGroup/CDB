package com.tsinghua.dbgroup.crowddb.crowdexec.query.schema;

import com.tsinghua.dbgroup.crowddb.crowdexec.operator.BaseOperator;
import com.tsinghua.dbgroup.crowddb.crowdsql.parser.SqlParser;
import com.tsinghua.dbgroup.crowddb.crowdsql.query.SqlContext;
import com.tsinghua.dbgroup.crowddb.crowdsql.tree.SqlTreeNode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.Stack;

/**
 * Created by talus on 16/6/19.
 * Created by talus on 16/6/19.
 */
public class Query implements Comparable<Query>{

    /*
    * Query status
    * */
    public static final String INIT = "init";
    public static final String QUERYING = "querying";
    public static final String FINISHED = "finished";
    public static final String ERROR = "error";

    public Query() {

    }

    public Query(int id, String sql, String timestamp, int user, String status, int currentSQLNodeId, String resultTable, String dbName, Boolean gmodel) {
        this.id = id;
        this.sql = sql;
        this.timestamp = timestamp;
        this.user = user;
        this.status = status;
        this.currentSQLNodeId = currentSQLNodeId;
        this.resultTable = resultTable;
        this.dbName = dbName;
        this.gmodel = gmodel;
    }


    /**
    * Query Database Field
    * */
    public int id;

    public String sql;

    public String timestamp;

    public int user;

    public String status;

    public int currentSQLNodeId;

    public String resultTable;

    public String dbName;

    public String errorMsg;

    public BaseOperator curOperator = null;

    private boolean gmodel = false;


    /**
    * Runtime Fields
    * */

    private SqlContext sqlContext;

    private SqlTreeNode curNode;

    private String curTmpTable;

    private BaseOperator operator;

    private Stack<SqlTreeNode> nextStack;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCurrentSQLNodeId() {
        return currentSQLNodeId;
    }

    public void setCurrentSQLNodeId(int currentSQLNodeId) {
        this.currentSQLNodeId = currentSQLNodeId;
    }

    public String getResultTable() {
        return resultTable;
    }

    public void setResultTable(String resultTable) {
        this.resultTable = resultTable;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
    *   Runtime fields getter and setter
    * */

    public BaseOperator getCurOperator() {
        return curOperator;
    }

    public void setCurOperator(BaseOperator curOperator) {
        this.curOperator = curOperator;
    }

    public SqlContext getSqlContext() {
        return sqlContext;
    }

    public void setSqlContext(SqlContext sqlContext) {
        this.sqlContext = sqlContext;
        this.setNextStack();
    }

    public SqlTreeNode getCurNode() {
        return curNode;
    }

    public void setCurNode(SqlTreeNode curNode) {
        this.curNode = curNode;
    }

    public String getCurTmpTable() {
        return curTmpTable;
    }

    public void setCurTmpTable(String curTmpTable) {
        this.curTmpTable = curTmpTable;
    }

    public BaseOperator getOperator() {
        return operator;
    }

    public void setOperator(BaseOperator operator) {
        this.operator = operator;
    }

    public boolean isGmodel() {
        return gmodel;
    }

    public void setGmodel(boolean gmodel) {
        this.gmodel = gmodel;
    }

    /**
    * public method settings
    * */

    public void save() {
        this.setCurrentSQLNodeId(1);
        Session session = HibernateSessionManager.currentSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(this);
            session.flush();
            transaction.commit();
        } catch (Exception e) {
            throw e;
        } finally {
            HibernateSessionManager.closeSession();
        }
    }

    @Override
    public String toString() {
        StringBuilder objectStr = new StringBuilder();
        objectStr.append("sqlContext: ").append(sqlContext.toString())
                .append("curNode: ").append(curNode.toString())
                .append("curTepTable: ").append(curTmpTable);
        return objectStr.toString();
    }

    /*
    * return the next sqlTreeNode
    * */
    public SqlTreeNode next() {
        if (nextStack.empty()) return null;
        return nextStack.pop();
    }

    /*
    * whether the stack has next node
    * */
    public boolean hasNext() {
        return !nextStack.empty();
    }

    /*
    * 1. Using stack to store the execution of sqlTreeNodes
    * */
    private void setNextStack() {
        nextStack = new Stack<SqlTreeNode>();
        dfs(sqlContext.getSqlTree().getRoot());
    }

    private void dfs(SqlTreeNode node) {
        nextStack.push(node);

        if (node.getRight() != null) dfs(node.getRight());
        if (node.getLeft() != null) dfs(node.getLeft());
    }

    public void parseSql() {
        SqlContext sqlContext = null;
        if (this.isGmodel())
            sqlContext = new SqlParser().parse2Single(getSql(), getDbName());
        else
            sqlContext = new SqlParser().parse(getSql(), getDbName());

        setSqlContext(sqlContext);
    }

    @Override
    public int compareTo(Query o) {
        return Integer.compare(this.getId(), o.getId());
    }
}
