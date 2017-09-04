package com.tsinghua.dbgroup.crowddb.crowdsql.tree;

import java.util.LinkedList;
import java.util.Queue;

public class SqlTree {

    private SqlTreeNode root;

    public SqlTree(SqlTreeNode root) {
        this.root = root;
    }

    public SqlTreeNode getRoot() {
        return root;
    }

    public void setRoot(SqlTreeNode root) {
        this.root = root;
    }

    @Override
    public String toString() {
        Queue<SqlTreeNode> nodeQueue = new LinkedList<>();
        nodeQueue.add(root);
        StringBuilder objectStr = new StringBuilder();
        objectStr.append(System.lineSeparator());
        while (!nodeQueue.isEmpty()) {
            SqlTreeNode node = nodeQueue.poll();
            objectStr.append(render(node)).append(System.lineSeparator());
            if (node.left != null) {
                nodeQueue.add(node.left);
            }
            if (node.right != null) {
                nodeQueue.add(node.right);
            }
        }
        return objectStr.toString();
    }

    String render(SqlTreeNode node) {
        return node + " SQL: id " +node.getNodeID()+" "+ node.query + " nodeType: " + node.nodeType + " left: " + node.left + " right: " + node.right;
    }
}
