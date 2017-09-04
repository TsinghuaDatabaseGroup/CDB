package com.tsinghua.dbgroup.crowddb.crowdsql.parser;

import com.tsinghua.dbgroup.crowddb.crowdsql.operator.OperatorHelper;
import com.tsinghua.dbgroup.crowddb.crowdsql.operator.Operators;
import com.tsinghua.dbgroup.crowddb.crowdsql.query.SqlContext;
import com.tsinghua.dbgroup.crowddb.crowdsql.tree.NodeType;
import com.tsinghua.dbgroup.crowddb.crowdsql.tree.SqlTree;
import com.tsinghua.dbgroup.crowddb.crowdsql.tree.SqlTreeBuilder;
import com.tsinghua.dbgroup.crowddb.crowdsql.tree.SqlTreeNode;
import com.tsinghua.dbgroup.crowddb.crowdsql.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlParser implements ISqlParser {

    static private Logger logger = LoggerFactory.getLogger(SqlParser.class);

    public SqlContext parse2Single(String query, String dbName) {
        SqlTreeNode treeNode = new SqlTreeNode();
        treeNode.setQuery(query);
        treeNode.setNodeType(NodeType.GRAPH_MODEL);

        SqlTree sqlTree = new SqlTree(treeNode);
        SqlContext sqlContext = new SqlContext();
        sqlContext.setSqlTree(sqlTree);

        return sqlContext;
    }

    public SqlContext parse(String query, String dbName) {

        query = preProcess(query);
        if (!foundCrowdOperator(query)) {
            logger.info("No Crowd Operator");
            //return createNoCrowdContext(query);
        }

        SqlContext sqlContext = new SqlContext();
        int posTagIn = query.indexOf(Operators.TAGIN);
        int posFill = query.indexOf(Operators.FILL);
        int posTag = query.indexOf(" "+Operators.ON+" ");

        int posSelect = query.indexOf(Operators.SELECT);
        int posCollect = query.indexOf(Operators.COLLECT);
        int posLimit = query.indexOf(Operators.LIMIT);

        int posFrom = query.indexOf(Operators.FROM);
        int posWhere = query.indexOf(Operators.WHERE);

        int posMultiLabel = query.indexOf(Operators.MULTILABEL);
        int posSingleLabel = query.indexOf(Operators.SINGLELABEL);


        String Tag = null;
        String projects = null;
        String wheres = null;
        String froms = null;
        String collects = null;
        String fills  = null;
        int limit = 0;

        if (posLimit != -1){
            String sLimit = query.substring(posLimit + Operators.LIMIT.length(), query.length()-1).trim();
            limit = Integer.valueOf(sLimit);
        }
        
        if (posTagIn != -1){
            String tagOption = query.substring(posTagIn + Operators.TAGIN.length(), query.length()-1).trim();
            tagOption = tagOption.replace("\"","");
            String [] tags = tagOption.substring(1,tagOption.length()-1).split(",");
            List<String> tagOptions = new ArrayList<>();
            for (int i = 0; i < tags.length; ++i){
                tagOptions.add(tags[i]);
            }
            sqlContext.setTagOptions(tagOptions);
            query = query.substring(0, posTagIn)+";";
            //logger.info("Tagin:"+String.join(",",tagOptions));
        }


        if (posTag != -1 ){
            //logger.info("Tag:"+posTag+","+query.substring(posTag));
            Tag = query.substring(posTag + Operators.ON.length()+1, query.length()-1).trim();
            query = query.substring(0, posTag)+";";
            //logger.info("Tag:"+query);
            //logger.info("Tag:"+Tag);
        }

        int posFromEnd = posWhere == -1? query.length() - 1 : posWhere;
        if (posSelect != -1)
            projects = query.substring(posSelect + Operators.SELECT.length(), posFrom).replaceAll("\\s+", "");
        else
            if (posCollect != -1 )
                collects = query.substring(posCollect + Operators.COLLECT.length(),posFrom).replaceAll("\\s+", "");
                else if (posFill != -1)
                    fills = query.substring(posFill + Operators.FILL.length(),posFrom).replaceAll("\\s+", "");
                    else if (posSingleLabel !=-1 )
                        collects = query.substring(posSingleLabel + Operators.SINGLELABEL.length(),posFrom).replaceAll("\\s+", "");
                        else if (posMultiLabel!=-1)
                            collects = query.substring(posMultiLabel + Operators.MULTILABEL.length(),posFrom).replaceAll("\\s+", "");


        if (posFrom != -1)
            froms = query.substring(posFrom + Operators.FROM.length(), posFromEnd).replaceAll("\\s+", "");
        if (posWhere != -1)
            wheres = query.substring(posWhere + Operators.WHERE.length(), query.length()-1);

        logger.info(projects);
        if ( projects != null )
            sqlContext.setProjects(Arrays.asList(projects.split(",")));
        else
            sqlContext.setProjects(null);

        sqlContext.setQuestion(Tag);

        if (froms != null)
            sqlContext.setFroms(Arrays.asList(froms.split(",")));
        else
            sqlContext.setFroms(null);

        if (fills != null)
            sqlContext.setFills(Arrays.asList(fills.trim().split(",")));
        else
            sqlContext.setFills(null);

        if (wheres!=null)
            sqlContext.setWheres(preProcessWheres(parseWheres(wheres)));
        else
            sqlContext.setWheres(null);

        sqlContext.setLimit(limit);
        if (collects != null)
            sqlContext.setCollects(Arrays.asList(collects.trim().split(",")));
        else
            sqlContext.setCollects(null);

        if (posSingleLabel != -1)
            sqlContext.setSingleLabel(sqlContext.getCollects().get(0));
        else
            sqlContext.setSingleLabel(null);

        if (posMultiLabel != -1)
            sqlContext.setMultiLabel(sqlContext.getCollects().get(0));
        else
            sqlContext.setMultiLabel(null);


        SqlTreeBuilder sqlTreeBuilder = new SqlTreeBuilder();
        SqlTree sqlTree = sqlTreeBuilder.build(sqlContext,dbName);
        sqlContext.setSqlTree(sqlTree);

        return sqlContext;
    }

    public List<String> preProcessWheres(List<String> wheres){
        List<String> tokens = new ArrayList<>();
        for (String where:wheres){
            //logger.error(where.trim());
            if (where.indexOf("IN")!=-1){
                String [] parts = where.trim().split(" ");
                String newwhere = parts[0] + " " + parts[1] + " ";
                for (int i = 2 ; i < parts.length ; i++){
                    newwhere += parts[i];
                }
                tokens.add(newwhere);
            }else
                tokens.add(where.trim());
        }
        return tokens;
    }

    public List<String> parseWheres(String wheres) {
        logger.info("ParseWhere"+wheres);
        List<String> tokens = new ArrayList<>();
        StringBuilder lastStr = new StringBuilder();
        for (int i = 0; i < wheres.length(); ++i) {
            char ch = wheres.charAt(i);
            String matchToken= null;
            if (ch == '(' || ch == ')') {
                matchToken = String.valueOf(ch);
            } else if (Utils.matchLogicOperator(wheres, i, Operators.AND)) {
                matchToken = wheres.substring(i, i + Operators.AND.length());
                i += Operators.AND.length() - 1;
            } else if (Utils.matchLogicOperator(wheres, i, Operators.OR)) {
                matchToken = wheres.substring(i, i + Operators.OR.length());
                i += Operators.OR.length() - 1;
            } else {
                lastStr.append(ch);
            }

            if (matchToken != null) {
                if (!Utils.isAllWhiteSpace(lastStr.toString())) {
                    tokens.add(lastStr.toString());
                }
                lastStr.setLength(0);
                tokens.add(matchToken);
            }
        }
        if (!Utils.isAllWhiteSpace(lastStr.toString())) {
            tokens.add(lastStr.toString());
        }

        // 对于IN ('aa', 'b') 和 BETWEEN (....) 语句,括号要特别处理
        for (int i = 1; i < tokens.size(); ++i) {
           if (tokens.get(i).equals("(") &&
                (tokens.get(i-1).trim().endsWith(Operators.IN) || tokens.get(i-1).trim().endsWith(Operators.BETWEEN))) {
                StringBuilder parenthesisClause = new StringBuilder();
                parenthesisClause.append(tokens.get(i-1));
                while (!tokens.get(i).equals(")")) {
                    parenthesisClause.append(tokens.get(i));
                    tokens.remove(i);
                }
                if (tokens.get(i).equals(")")) {
                    parenthesisClause.append(tokens.get(i));
                    tokens.remove(i);
                }
                tokens.set(i-1, parenthesisClause.toString());
            }
        }
        return tokens;
    }

    /*
     * Pre process query by following steps:
     * 1. Replace multi-spaces to one space except quoted text
     * 2. Capitalize all operators except quoted text
     */
    public String preProcess(String query) {
        List<String> tokens = new ArrayList<>();
        String regex = "\"([^\"]*)\"|\'([^\']*)\'|(\\S+)";
        Matcher m = Pattern.compile(regex).matcher(query);
        while (m.find()) {
            if (m.group(1) != null) {
                tokens.add("\"" + m.group(1) + "\"");
            } else if (m.group(2) != null) {
                tokens.add("\'" + m.group(2) + "\'");
            } else {
                tokens.add(m.group(3));
            }
        }

        StringBuilder newQueryBuilder = new StringBuilder();
        for (String token : tokens) {
            for (String operator : OperatorHelper.getOperatorSet()) {
                if (token.equalsIgnoreCase(operator)) {
                    token = operator;
                    break;
                }
            }
            newQueryBuilder.append(token).append(" ");
        }
        newQueryBuilder.deleteCharAt(newQueryBuilder.length()-1);
        return newQueryBuilder.toString();
    }
    private boolean foundCrowdOperator(String query) {
        for (String crowdOperator : OperatorHelper.getCrowdOperatorSet()) {
            if (query.contains(crowdOperator)) {
                return true;
            }
        }
        return false;
    }

    private SqlContext createNoCrowdContext(String query) {
        SqlContext sqlContext = new SqlContext();
        SqlTreeNode node = new SqlTreeNode(NodeType.EXECUTE_SQL, sqlContext, query);
        SqlTree tree = new SqlTree(node);
        sqlContext.setSqlTree(tree);
        return sqlContext;
    }

}
