package com.tsinghua.dbgroup.crowddb.crowdsql.tree;

import com.tsinghua.dbgroup.crowddb.crowdsql.query.SqlContext;

import java.util.ArrayList;
import java.util.List;

public class SqlTreeNode {
    String whereClause;
    String query;
    NodeType nodeType;
    SqlTreeNode left;
    SqlTreeNode right;
    SqlContext sqlContext;
    private String tableName = null;
    int nodeID;
    List<String> projects = new ArrayList<>();
    List<String> froms = new ArrayList<>();

    public SqlTreeNode() {

    }

    public SqlTreeNode(NodeType nodeType, SqlContext sqlContext,int id) {
        this.nodeType = nodeType;
        this.sqlContext = sqlContext;
        nodeID = id;
    }

    public SqlTreeNode(NodeType nodeType, SqlContext sqlContext, String query) {
        this.nodeType = nodeType;
        this.sqlContext = sqlContext;
        this.query = query;
    }

    public String getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public SqlTreeNode getLeft() {
        return left;
    }

    public void setLeft(SqlTreeNode left) {
        this.left = left;
    }

    public SqlTreeNode getRight() {
        return right;
    }

    public void setRight(SqlTreeNode right) {
        this.right = right;
    }

    public SqlContext getSqlContext() {
        return sqlContext;
    }

    public void setSqlContext(SqlContext sqlContext) {
        this.sqlContext = sqlContext;
    }

    public List<String> getProjects() {
        return projects;
    }

    public void setProjects(List<String> projects) {
        this.projects = projects;
    }

    public List<String> getFroms() {
        return froms;
    }

    public void setFroms(List<String> froms) {
        this.froms = froms;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getNodeID() {
        return nodeID;
    }

    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }

    @Override
    public String toString() {
        return "@" + Integer.toHexString(hashCode());
    }
}



