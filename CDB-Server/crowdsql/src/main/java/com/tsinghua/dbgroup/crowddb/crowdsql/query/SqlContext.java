package com.tsinghua.dbgroup.crowddb.crowdsql.query;

import com.tsinghua.dbgroup.crowddb.crowdsql.tree.SqlTree;
import com.tsinghua.dbgroup.crowddb.crowdsql.tree.SqlTreeBuilder;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.TableManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SqlContext {
    static private Logger logger = LoggerFactory.getLogger(SqlTreeBuilder.class);

    private SqlTree sqlTree;
    private List<String> projects;
    private List<String> froms;
    private List<String> wheres;
    private String query;
    private String question;


    private List<String> collects;
    private String singleLabel;

    private String multiLabel;
    private List<String> fills;
    private int limit;
    private List<String> tagOptions;

    public String getSingleLabel() {
        return singleLabel;
    }

    public void setSingleLabel(String singeLabel) {
        this.singleLabel = singeLabel;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
    private String getWholeWhere(String swhere, HashSet<String > projectSet){
        String ans = null;
        for (String from:getFroms()){
            if (projectSet.contains(from+"."+swhere))
                ans = from+"."+swhere;
        }
        return ans;
    }
    private List<String> putColumn(List<String> fills,HashSet<String> columnsSet, HashSet<String> projectSet){
        if (fills == null)
            return null;
        List<String> tempList = new ArrayList<>();
        tempList.clear();
        for (String fill:fills){
            String temp = fill.trim();
            if (columnsSet.contains(temp))
                temp = getWholeWhere(temp,projectSet);
            tempList.add(temp);
        }
        return tempList;
    }
    public void initialColumn(String dbName){
        HashSet<String> projectSet = new HashSet<>();
        HashSet<String> columnsSet = new HashSet<>();

        TableManager tmpTableManager= new TableManager();

        for ( String from: getFroms()){
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

        setFills(putColumn(fills,columnsSet,projectSet));

        setCollects(putColumn(collects,columnsSet,projectSet));

        setProjects(putColumn(projects,columnsSet,projectSet));

        if(getQuestion()!=null)
            setQuestion(getWholeWhere(getQuestion(),projectSet));

    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<String> getFills() {
        return fills;
    }

    public void setFills(List<String> fills) {
        this.fills = fills;
    }

    public List<String> getTagOptions() {
        return tagOptions;
    }

    public void setTagOptions(List<String> tagOptions) {
        this.tagOptions = tagOptions;
    }


    public String getMultiLabel() {
        return multiLabel;
    }

    public void setMultiLabel(String multiLabel) {
        this.multiLabel = multiLabel;
    }



    public List<String> getCollects() {

        return collects;
    }

    public void setCollects(List<String> collects) {
        this.collects = collects;
    }

    public List<String> getWheres() {
        return wheres;
    }

    public void setWheres(List<String> wheres) {
        this.wheres = wheres;
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

    public SqlContext() {

    }

    public SqlContext(SqlTree sqlTree) {
        this.sqlTree = sqlTree;
    }

    public SqlTree getSqlTree() {
        return sqlTree;
    }

    public void setSqlTree(SqlTree sqlTree) {
        this.sqlTree = sqlTree;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        StringBuilder objectStr = new StringBuilder();
        objectStr.append("tree:").append(System.lineSeparator()).append(sqlTree.toString());
        objectStr.append("projects: " + StringUtils.join(projects, ",")).append(System.lineSeparator());
        objectStr.append("froms: " + StringUtils.join(froms, ",")).append(System.lineSeparator());
        objectStr.append("wheres: " + StringUtils.join(wheres, ",")).append(System.lineSeparator());
        objectStr.append("fills: " + StringUtils.join(fills, ",")).append(System.lineSeparator());
        objectStr.append("collects: " + StringUtils.join(collects, ",")).append(System.lineSeparator());
        objectStr.append("tags: " + StringUtils.join(tagOptions, ",")).append(System.lineSeparator());
        objectStr.append("limits: " + limit).append(System.lineSeparator());
        objectStr.append("on: " + question);
        return objectStr.toString();
    }
}
