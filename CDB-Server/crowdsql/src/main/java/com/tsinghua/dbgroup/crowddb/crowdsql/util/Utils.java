package com.tsinghua.dbgroup.crowddb.crowdsql.util;

public class Utils {
    public static boolean isAllWhiteSpace(String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean matchLogicOperator(String s, int i, String subStr) {
        return i + subStr.length() <= s.length() && s.substring(i, i+subStr.length()).equals(subStr) &&
                (i == 0 || isSeprator(s.charAt(i-1))) &&
                (i+1 < s.length() || isSeprator(s.charAt(i+1)));
    }

    public static boolean isSeprator(char ch) {
        return ch == '(' || ch == ')' || Character.isWhitespace(ch);
    }

    public static String packString(String[] strings,int start,int end){
        String pack = null;
        for (int i = start; i < end; ++i){
            pack += strings[i];
        }
        return pack;
    }
}
