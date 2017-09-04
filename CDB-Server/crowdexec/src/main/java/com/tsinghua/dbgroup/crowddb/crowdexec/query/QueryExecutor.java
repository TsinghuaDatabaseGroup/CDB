package com.tsinghua.dbgroup.crowddb.crowdexec.query;

import com.tsinghua.dbgroup.crowddb.crowdexec.operator.*;
import com.tsinghua.dbgroup.crowddb.crowdsql.tree.NodeType;
import com.tsinghua.dbgroup.crowddb.crowdsql.tree.SqlTreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Created by talus on 16/6/2.
 */

class OperatorBuilder {

    private static HashMap<NodeType, Class<? extends BaseOperator>> operatorMap = new HashMap<>();

    static {
        initOperatorMap();
    }

    private static void initOperatorMap() {
        operatorMap.put(NodeType.CROWD_JOIN, CrowdJoinOperator.class);
        operatorMap.put(NodeType.CROWD_EQ, CrowdEQOperator.class);
        operatorMap.put(NodeType.CROWD_GT, CrowdGTOperator.class);
        operatorMap.put(NodeType.CROWD_LT, CrowdLTOperator.class);
        operatorMap.put(NodeType.CROWD_IN, CrowdInOperator.class);

        operatorMap.put(NodeType.JOIN, JoinOperator.class);
        operatorMap.put(NodeType.EQ, EQOperator.class);
        operatorMap.put(NodeType.GT, GTOperator.class);
        operatorMap.put(NodeType.LT, LTOperator.class);
        operatorMap.put(NodeType.PROJECT, ProjectOperator.class);

        operatorMap.put(NodeType.COLLECT, CollectOperator.class);
        operatorMap.put(NodeType.FILL, FillOperator.class);
        operatorMap.put(NodeType.SINGLELABEL, SingleLabel.class);
        operatorMap.put(NodeType.MULTILABEL, MultiLabel.class);

        operatorMap.put(NodeType.GRAPH_MODEL, CrowdGraphOperator.class);
    }

    public static Class<? extends BaseOperator> createOperator(NodeType nodeType) {
        if (!operatorMap.containsKey(nodeType)) {
            return null;
        }

        return operatorMap.get(nodeType);
    }
}

public class QueryExecutor implements IQueryExecutor {

    private static String LOG_FORMAT = "##Query Executor##";

    private static Logger LOG = LoggerFactory.getLogger(QueryExecutor.class);

    public BaseOperator buildOperator(SqlTreeNode node, String dbName) {
        Class<? extends BaseOperator> operatorClass = OperatorBuilder.createOperator(node.getNodeType());
        if (operatorClass == null) {
            LOG.error(String.format("no right operator, nodetype = %s", node.getNodeType()));
            return null;
        }
        try {
            Constructor<? extends BaseOperator> cons = operatorClass.getConstructor(SqlTreeNode.class, String.class);
            BaseOperator operator = cons.newInstance(node, dbName);
            return operator;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            LOG.error(String.format("can not create new instance for operator = %s", operatorClass.getName()));
            e.printStackTrace();
        }
        return null;
    }

    public boolean execute(BaseOperator operator) {
        if (operator == null) return false;
        return operator.process();
    }
}
