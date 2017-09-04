package com.tsinghua.dbgroup.crowddb.crowdsql.tree;

import com.tsinghua.dbgroup.crowddb.crowdsql.operator.Operators;
import com.tsinghua.dbgroup.crowddb.crowdsql.parser.SqlParser;
import com.tsinghua.dbgroup.crowddb.crowdsql.query.SqlContext;
import com.tsinghua.dbgroup.crowddb.crowdsql.util.Utils;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.TableManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;


public class SqlTreeBuilder {
    int genId;
    static private Logger logger = LoggerFactory.getLogger(SqlTreeBuilder.class);

    private Map<String, Integer> priorityMap = new HashMap<>();
    void zeroId(){
        genId = 0;
    }
    int getId(){
        genId =  genId + 1;
        return genId;
    }
    public SqlTreeBuilder() {
        priorityMap.put(Operators.AND, 2);
        priorityMap.put(Operators.OR, 1);
    }

    public SqlTree build(SqlContext sqlContext,String dbName) {
        sqlContext.initialColumn(dbName);
        zeroId();
        SqlTreeNode node = null;
        if (sqlContext.getCollects()==null&&sqlContext.getFills()==null)
            node = new SqlTreeNode(NodeType.PROJECT, sqlContext,getId());
        else
        if (sqlContext.getFills()==null)
            node = new SqlTreeNode(NodeType.COLLECT, sqlContext,getId());
        else
            node = new SqlTreeNode(NodeType.FILL, sqlContext,getId());
        if (sqlContext.getMultiLabel()!= null)
            node = new SqlTreeNode(NodeType.FILL, sqlContext,getId());

        if (sqlContext.getSingleLabel()!= null)
            node = new SqlTreeNode(NodeType.SINGLELABEL, sqlContext,getId());
        if (sqlContext.getMultiLabel()!= null)
            node = new SqlTreeNode(NodeType.MULTILABEL, sqlContext,getId());

        if (sqlContext.getWheres() == null) {
            node.query = sqlContext.getQuery();
            node.froms = sqlContext.getFroms();
            return new SqlTree(node);
        } else {
            node.projects = sqlContext.getProjects();
            node.froms = sqlContext.getFroms();
            node.left = buildWheres(sqlContext,dbName);
            node.query = buildSqlQuery(node);
            return new SqlTree(node);
        }
    }
    private SqlTreeNode buildSingleWheres(List<String> SingleWSet,String from,SqlContext sqlContext){
        Stack<SqlTreeNode> nodeStack = new Stack<>();
        SqlTreeNode root = null;
        for (String token : SingleWSet) {
            SqlTreeNode leaf = new SqlTreeNode(getType(token), sqlContext,getId());
            leaf.whereClause = token;
            leaf.froms.add(from);
            leaf.projects.add("*");
            leaf.query = buildSqlQuery(leaf);
            nodeStack.push(leaf);
            leaf.left = root;
            root = leaf;
        }
        return root;
    }
    private String getWholeWhere(String swhere, HashSet<String > projectSet, SqlContext sqlContext){
        String ans = null;
        for (String from:sqlContext.getFroms()){
            if (projectSet.contains(from+"."+swhere))
                ans = from+"."+swhere;
        }
        return ans;
    }
    private SqlTreeNode buildWheres(SqlContext sqlContext,String dbName) {
        List<String> wheres = sqlContext.getWheres();

        List<String> joinWheres = new ArrayList<>();
        List<String> filterWheres = new ArrayList<>();
        Map<String, SqlTreeNode> RTNode = new HashMap<>();
        Map<String, String> unionSet = new HashMap<>();

        HashSet<String> projectSet = new HashSet<>();
        HashSet<String> columnsSet = new HashSet<>();

        TableManager tmpTableManager= new TableManager();

        for ( String from: sqlContext.getFroms()){
            List<String> columns = new ArrayList<>();
            try {
                //System.out.println(from);
                columns = tmpTableManager.getTableColumns(dbName+"."+from);
            } catch (SQLException e) {
                //// TODO: 11/3/16
            }
            
            for (String column:columns) {
                projectSet.add(from + "." + column);
                columnsSet.add(column);
            }
        }

        for (String tokent : wheres){
            switch (tokent) {
                case "(":
                case ")":
                case Operators.AND:
                case Operators.OR:
                    break;
                default:
                    String token  = tokent.trim();
                    String part[] = token.trim().split(" ");
                    if (columnsSet.contains(part[0])){
                        if (part[0].indexOf(".")<0){
                            part[0]=getWholeWhere(part[0],projectSet,sqlContext);
                        }
                    }
                    if (columnsSet.contains(part[2])){
                        if (part[2].indexOf(".")<0){
                            part[2]=getWholeWhere(part[2],projectSet,sqlContext);
                        }
                    }
                    token = part[0]+" "+part[1]+" "+part[2];
                    int pos = token.indexOf(".");
                    if (pos>0) {
                        pos = token.substring(pos + 1, token.length() - 1).indexOf(".");
                        if (pos > 0)
                            joinWheres.add(token.trim());
                        else
                            filterWheres.add(token.trim());
                    }
                    break;
            }
        }

        for (String from : sqlContext.getFroms()){
            unionSet.put(from,from);
            List<String> where4From = new ArrayList<>();
            for (String token : filterWheres) {
                switch (token) {
                    case "(":
                    case ")":
                    case Operators.AND:
                    case Operators.OR:
                        break;
                    default:
                        int pos = token.indexOf(".");
                        String fromName = from;
                        if (pos > 0)
                            fromName = token.substring(0,pos);
                        //System.out.println("xx:"+fromName+" "+from+" "+fromName.equals(from));
                        if (fromName.equals(from)){
                            where4From.add(token);
                        }
                        break;
                }
            }
            SqlTreeNode tmp = buildSingleWheres(where4From,from,sqlContext);
            RTNode.put(from,tmp);
        }

        for (String joinWhere : joinWheres){
            String part[] = joinWhere.split(" ");

            String from0 = part[0].substring(0,part[0].indexOf("."));
            String from1 = part[2].substring(0,part[2].indexOf("."));
            while (! unionSet.get(from0).equals(from0))
                from0 = unionSet.get(from0);
            while (! unionSet.get(from1).equals(from1))
                from1 = unionSet.get(from1);
            unionSet.put(from0,from1);

            SqlTreeNode root;
            if (isCrowd(joinWhere))
                root = new SqlTreeNode(NodeType.CROWD_JOIN,sqlContext,getId());
            else
                root = new SqlTreeNode(NodeType.JOIN,sqlContext,getId());

            root.left = RTNode.get(from0);
            root.right = RTNode.get(from1);
            root.whereClause = joinWhere;
            root.projects.add("*");
            root.froms.add(from0);
            root.froms.add(from1);

            root.query = buildSqlQuery(root);
            RTNode.put(from1,root);
        }
        return RTNode.get(unionSet.get(sqlContext.getFroms().get(0)));
    }
    // Retrieve relative A place holder function to obtain primary keys
    // TODO: Implement
    private NodeType getType(String whereclause){
        String part[] = whereclause.split(" ");
        NodeType tmpType;

        switch (part[1]){
            case ">":
                tmpType = NodeType.GT;
                break;
            case "<":
                tmpType = NodeType.LT;
                break;
            case "=":
                tmpType = NodeType.EQ;
                break;
            case "CROWD_EQ":
                tmpType = NodeType.CROWD_EQ;
                break;
            case "CROWD_GT":
                tmpType = NodeType.CROWD_GT;
                break;
            case "CROWD_LT":
                tmpType = NodeType.CROWD_LT;
                break;
            case "CROWD_IN":
                tmpType = NodeType.CROWD_IN;
                break;
            default:
                tmpType = NodeType.IN;
                break;
        }
        return tmpType;
    }
    private Boolean isCrowd(String whereclause){
        String part[] = whereclause.split(" ");
        NodeType tmpType;
        switch (part[1]){
            case "CROWD_EQ":
            case "CROWD_GT":
            case "CROWD_LT":
            case "CROWD_IN":
                return true;
            default:
                return false;
        }
    }
    private List<String> getPrimaryKeys() {
        List<String> primaryKeys = new ArrayList<>();
        primaryKeys.add("*");
        return primaryKeys;
    }

    private void createNode(Stack<String> operatorStack, Stack<SqlTreeNode> nodeStack, SqlContext sqlContext) {
        SqlTreeNode newNode = new SqlTreeNode();
        newNode.sqlContext = sqlContext;
        newNode.nodeType = operatorStack.peek().equals(Operators.AND)? NodeType.AND : NodeType.OR;
        newNode.left = nodeStack.pop();
        newNode.right = nodeStack.pop();
        nodeStack.push(newNode);
        operatorStack.pop();
    }

    private String buildSqlQuery(SqlTreeNode node) {
        String selectClause = StringUtils.join(node.projects, ",");
        String fromClause = StringUtils.join(node.froms, ",");
        String commonPart = Operators.SELECT + " " + selectClause + " " + Operators.FROM + " " + fromClause;
        if (node.whereClause == null) {
            return commonPart + ";";
        } else {
            return commonPart + " " +
                    Operators.WHERE + " " + node.whereClause + ";";
        }
    }
}

