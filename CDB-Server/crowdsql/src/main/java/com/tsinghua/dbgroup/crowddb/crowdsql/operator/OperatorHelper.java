package com.tsinghua.dbgroup.crowddb.crowdsql.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class OperatorHelper {

    static private Set<String> crowdOperatorSet = new HashSet<String>();

    static private Set<String> operatorSet = new HashSet<>();

    static private Logger logger = LoggerFactory.getLogger(OperatorHelper.class);

    static public Set<String> getCrowdOperatorSet() {
        return crowdOperatorSet;
    }

    static public Set<String> getOperatorSet() {
        return operatorSet;
    }

    static {
        generateCrowdOperatorSet();
        generateKeywordSet();
    }

    private static void generateCrowdOperatorSet() {
        Field[] fields = Operators.class.getDeclaredFields();
        Operators operators = new Operators();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.getName().startsWith("CROWD")) {
                    String fieldValue = field.get(operators).toString();
                    crowdOperatorSet.add(fieldValue);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        crowdOperatorSet.add(Operators.FILL);
        crowdOperatorSet.add(Operators.COLLECT);
    }

    private static void generateKeywordSet() {
        Field[] fields = Operators.class.getDeclaredFields();
        Operators operators = new Operators();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                String fieldValue = field.get(operators).toString();
                operatorSet.add(fieldValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
