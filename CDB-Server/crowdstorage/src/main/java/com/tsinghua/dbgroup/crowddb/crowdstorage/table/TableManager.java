/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 11/3/16 12:51 PM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

package com.tsinghua.dbgroup.crowddb.crowdstorage.table;

import com.tsinghua.dbgroup.crowddb.crowdstorage.table.schema.EqualSchema;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.schema.JoinSchema;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.schema.ColumnsSchema;
import com.tsinghua.dbgroup.crowddb.crowdstorage.utils.TableHelper;
import com.tsinghua.dbgroup.crowddb.crowdstorage.utils.Utils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * Created by talus on 16/6/2.
 */
public class TableManager extends BaseDBStorage implements ITableManager {

    public static final int JOIN_TABLE = 0x01;
    public static final int EQUAL_TABLE = 0x02;
    public static final String TMP_DATABASE = "crowddb_temp";

    private static String LOG_FORMAT = "##TableManager##";

    private static Logger LOG = LoggerFactory.getLogger(TableManager.class);

    public static String generateTableName() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return "table_"+uuid;
    }

    public static String generateTableName(int queryID, int nodeId) {
        return String.format("table_q_%d_n_%d)", queryID, nodeId);
    }

    public boolean createTmpTable(String tableName, int tableType) {
        Class cls = TableHelper.getSchemaClass(tableType);
        if (cls == null) {
            return false;
        }

        String formatStr = null;
        try {
            formatStr = (String)cls.getField("createSQL").get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        String createSQL = String.format(formatStr, tableName);
        try {
            execSQL(createSQL);
        } catch (SQLException e) {
            LOG.error(String.format("%s create tmp table %s failed.", LOG_FORMAT, tableName), e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteTmpTable(String tableName) {
        String sql = String.format("drop table %s", tableName);
        try {
            execSQL(sql);
        } catch (SQLException e) {
            LOG.error(String.format("%s delete table %s failed.", LOG_FORMAT, tableName), e);
            return false;
        }
        return true;
    }

    public List<Map<String, Object>> extractColumn(String column, String tableName, Object[] params) throws SQLException{
        QueryRunner runner = new QueryRunner();
        Connection conn = getConnnection();
        String sql = String.format("select `%s` from %s", column, tableName);
        ColumnListHandler<List<String>> handler = new ColumnListHandler<List<String>>(column);

        LOG.info(String.format("current sql: %s", sql));
        List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
        try {
            res = runner.query(conn, sql, new MapListHandler());
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.error(String.format("%s extract column failed. column = %s, table = %s", LOG_FORMAT, column, tableName));
        } finally {
            DbUtils.close(conn);
        }
        return res;
    }

    public void execSQL(String sql) throws SQLException{
        QueryRunner runner = new QueryRunner();
        Connection conn = getConnnection();
        if (conn == null) throw new SQLException("Connection is null.");

        LOG.info(String.format("executing sql: %s", sql));
        try {
            runner.update(conn, sql);
        } catch (SQLException e) {
            throw e;
        } finally {
            DbUtils.close(conn);
        }
    }

    public List<Pair<String, Integer>> getTableColumnPairs(String tableName) throws SQLException{
        List<Pair<String, Integer>> columns = null;

        Connection conn = getConnnection();
        if (conn == null) throw new SQLException("Connection is null.");
        try {
            Statement stmt = conn.createStatement();
            String sql = String.format("select * from %s limit 1;", tableName);
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();

            int columnCount = rsmd.getColumnCount();
            columns = new ArrayList<>();

            for (int i = 1; i <= columnCount; i++) {
                Pair<String, Integer> pair = new MutablePair<>(rsmd.getColumnName(i), rsmd.getColumnType(i));
                columns.add(pair);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return columns;
    }

    // return columns
    public List<String> getTableColumns(String tableName) throws SQLException {
        List<String> columns = null;

        Connection conn = getConnnection();
        if (conn == null) throw new SQLException("Connection is null.");
        try {
            Statement stmt = conn.createStatement();
            String sql = String.format("select * from %s limit 1;", tableName);
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();

            int columnCount = rsmd.getColumnCount();
            columns = new ArrayList<>();

            for (int i = 1; i <= columnCount; i++) {
                columns.add(rsmd.getColumnName(i));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return columns;
    }

    public void insertEqualRecords(String tableName, List<EqualSchema> records){
        List<String> tuples = new ArrayList<>();
        for (EqualSchema schema: records) {
            tuples.add(String.format("(\"%s\", %d)", schema.getEid(), schema.getRes()));
        }

        String values = String.join(",", tuples);
        String sql = String.format(EqualSchema.insertSQL, tableName, values);

        try {
            execSQL(sql);
        } catch (SQLException e) {
            LOG.error(String.format("%s insert tmp table %s failed.", LOG_FORMAT, tableName), e);
        }
    }

    public void insertJoinRecords(String tableName, List<JoinSchema> records){
        List<String> tuples = new ArrayList<>();
        for (JoinSchema schema: records) {
            tuples.add(String.format("(\"%s\", \"%s\", %d)", schema.getEid1(), schema.getEid2(), schema.getRes()));
        }

        String values = String.join(",", tuples);
        String sql = String.format(JoinSchema.insertSQL, tableName, values);

        try {
            execSQL(sql);
        } catch (SQLException e) {
            LOG.error(String.format("%s insert tmp table %s failed.", LOG_FORMAT, tableName), e);
        }
    }

    public boolean normalJoinTables(String newTable, String table1, String table2, Pair<String, String> colPair, boolean isEntityJoin) {
        String sql_schema = "create table %s ( eid int AUTO_INCREMENT PRIMARY KEY) select %s from %s where %s";
        String tables = String.format("%s, %s", table1, table2);
        // System.out.println(table1);
        // System.out.println(table2);
        String dbName1 = Utils.unpacketDBName(table1);
        String dbName2 = Utils.unpacketDBName(table2);

        String left = colPair.getLeft(), right = colPair.getRight();
        if (left.equals("eid")) left = Utils.packetColumn(table1, left);
        if (right.equals("eid")) right = Utils.packetColumn(table2, right);

        String wheres = String.format("%s = %s", left, right);

        String projects = "";
        projects = getProjectFields(table1, table2, isEntityJoin);
//        if (isEntityJoin) {
//            projects = "*";
//        } else {
//
//        }

        if (projects == null) return false;
        String sql = String.format(sql_schema, newTable, projects, tables, wheres);

        try {
            execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        boolean res = deleteTmpTable(table1) && deleteTmpTable(table2);
        if (!res) {
            LOG.warn("delete temp table failed.");
        }

        return true;
    }

    public boolean crowdJoinTables(String newTable, String table1, String table2, String result_table, Pair<String, String> columns) {
        String tmpTable = TableManager.generateTableName();
        tmpTable = Utils.packetTable(TableManager.TMP_DATABASE, tmpTable);
        Pair<String, String> pair = new MutablePair<String, String>(columns.getLeft(), "eid1");
        boolean res = normalJoinTables(tmpTable, table1, result_table, pair, true);
        if (!res) {
            return false;
        }

        pair = new MutablePair<String, String>(columns.getRight(), "eid2");
        res = normalJoinTables(newTable, table2, tmpTable, pair, false);
        return res;
    }

    public boolean copyTable(String dstTable, String srcTable) {
        if (srcTable == null) {
            LOG.warn("srcTable is null");
            return true;
        }

        String sql = String.format("create table %s select * from %s", dstTable, srcTable);
        try {
            execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        boolean res = deleteTmpTable(srcTable);
        if (!res) {
            LOG.warn("delete temp table failed");
        }

        return true;
    }

    public boolean packetTable(String dstTable, String srcTable) {
        List<String> columns = null;
        try {
            columns = getTableColumns(srcTable);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


        String sqlSchema = "create table %s ( eid int AUTO_INCREMENT PRIMARY KEY) select %s from %s";
        String tableNoDbName = Utils.unpacketTable(srcTable);

        List<String> fields = new ArrayList<>();

        for (String column : columns) {
            String newColumn = Utils.packetColumn(tableNoDbName, column);
            fields.add(String.format("%s as \"%s\"", column, newColumn));
        }
        String sql = String.format(sqlSchema, dstTable, String.join(", ", fields), srcTable);
        try {
            execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean restoreTable(String dstTable, String srcTable) {
        if (srcTable == null) {
            LOG.warn(String.format("srcTable is null"));
            return true;
        }

        List<String> columns = null;
        try {
            columns = getTableColumns(srcTable);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        HashMap<String, Integer> counter = new HashMap<>();
        for (String column: columns) {
            String oldColumn = Utils.unpacketColumn(column);
            if (!counter.containsKey(oldColumn))
                counter.put(oldColumn, 0);
            counter.put(oldColumn, counter.get(oldColumn) + 1);
        }

        String sqlSchema = "create table %s select %s from %s";
        List<String> fields = new ArrayList<>();
        String asFormatter = "`%s` as %s";
        for (String column : columns) {
            String oldColumn = Utils.unpacketColumn(column);
            if (oldColumn.equals("eid"))
                continue;
            fields.add(String.format(asFormatter, column, oldColumn));
        }

        String sql = String.format(sqlSchema, dstTable, ", ".join(", ", fields), srcTable);
        try {
            execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

//        boolean res = deleteTmpTable(srcTable);
//        if (!res) {
//            LOG.warn("delete temp table failed");
//        }
        return true;
    }

    private String getProjectFields(String table1, String table2, boolean isEntityJoin) {
        List<String> columns1 = null, columns2 = null;
        try {
            columns1 = getTableColumns(table1);
            columns2 = getTableColumns(table2);
        } catch (SQLException e) {
            LOG.error("Can not get table columns ");
            e.printStackTrace();
            return null;
        }

        columns1.addAll(columns2);
        List<String> selections = new ArrayList<>();
        for (String s: columns1) {
            String oldColumn = Utils.unpacketColumn(s);
            if (oldColumn.equals("eid")) continue;

            if (!isEntityJoin && (oldColumn.contains("res") || oldColumn.contains("eid")))
                continue;
            selections.add(String.format("`%s`", s));
        }
        String projects = String.join(", ", selections);
        return projects;
    }

//    public boolean update(String table, List<ColumnsSchema> schemas, List<String> columns) {
//        String sqlTemplate = "INSERT INTO %s %s VALUES %s" +
//                "ON DUPLICATE KEY UPDATE %s;";
//        List<String> cols = new ArrayList<>(columns);
//        cols.add(0, "eid");
//        String colsStr = String.format("(%s)", String.join(", ", cols));
//
//        List<String> updates = new ArrayList<>();
//        for (String col : columns)
//            updates.add(String.format("%s=VALUES(%s)", col, col));
//        String updateStr = String.join(", ", updates);
//
//        List<String> tuples = new ArrayList<>();
//        for (ColumnsSchema schema: schemas) {
//            List<String> vals = new ArrayList<>();
//            vals.add("\"" + schema.getId() + "\"");
//            for (String col: columns) {
//                vals.add("\"" + schema.getColumns().get(col) + "\"");
//            }
//            tuples.add(String.format("(%s)", String.join(",", vals)));
//        }
//        String valsStr = String.join(", ", tuples);
//
//        String sql = String.format(sqlTemplate, table, colsStr, valsStr, updateStr);
//
//        try {
//            execSQL(sql);
//        } catch (SQLException e) {
//            return false;
//        }
//        return true;
//    }

    public boolean update(String table, List<ColumnsSchema> schemas, List<String> columns) {
        String sqlTemplate = "UPDATE %s SET %s WHERE %s;";
        List<String> cols = new ArrayList<>(columns);

        List<String> sqls = new ArrayList<>();
        for (ColumnsSchema schema : schemas) {
            List<String> fields = new ArrayList<>();
            for (String column : columns)
                fields.add(String.format("`%s` = \"%s\"", column, schema.getColumns().get(column)));
            String wheres = String.format("`eid` = %s", schema.getId());
            String sql = String.format(sqlTemplate, table, String.join(", ", fields), wheres);
            sqls.add(sql);
        }

        try {
            for (String sql: sqls)
                execSQL(sql);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean insertColumns(String table, List<ColumnsSchema> schemas, List<String> columns) {
        HashMap<String, String> types = new HashMap<>();
        for (String column: columns)
            types.put(column, "VARCHAR(255)");

        boolean res = createTable(table, columns, types);
        if (!res) return false;

        String sqlTemplate = "insert into %s (%s) values %s";
        List<String> packetCols = new ArrayList<>();
        for (String column: columns)
            packetCols.add(String.format("`%s`", column));
        String colStr = String.join(", ", packetCols);


        List<String> tuples = new ArrayList<>();
        List<String> vals = new ArrayList<>();
        for (ColumnsSchema schema: schemas) {
            vals.clear();
            for (String column: columns) {
                String val = schema.getColumns().getOrDefault(column, "null");
                vals.add(String.format("\"%s\"", val));
            }
            tuples.add(String.format("(%s)", String.join(", ", vals)));
        }
        String valStr = String.join(", ", tuples);

        String sql = String.format(sqlTemplate, table, colStr, valStr);
        try {
            execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // extract columns
    public List<Map<String, Object>> extractColumns(String tableName, List<String> columns) throws SQLException{
        QueryRunner runner = new QueryRunner();
        Connection conn = getConnnection();
        String sql = String.format("SELECT %s from %s", String.join(", ", columns), tableName);

        LOG.info(String.format("current sql: %s", sql));
        List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
        try {
            res = runner.query(conn, sql, new MapListHandler());
        } catch (SQLException e) {
            e.printStackTrace();
            //LOG.error(String.format("%s extract column failed. column = %s, table = %s", LOG_FORMAT, column, tableName));
        } finally {
            DbUtils.close(conn);
        }
        return res;
    }

    private boolean createTable(String table, List<String> columns, HashMap<String, String> columnTypes) {
        String sqlTemplate = "CREATE TABLE %s (%s)";
        List<String> colStrs = new ArrayList<>();
        for (String column: columns) {
            String type = columnTypes.getOrDefault(column, "VARCHAR(255)");
            colStrs.add(String.format("`%s` %s", column, type));
        }

        String createSQL = String.format(sqlTemplate, table, String.join(", ", colStrs));
        try {
            execSQL(createSQL);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean addColumns(String table, List<String> columns, HashMap<String, String> columnTypes) {
        List<String> oldColumns = null;
        try {
            oldColumns = getTableColumns(table);
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.error(String.format("table %s does not exist", table));
            return false;
        }

        List<String> addingColumns = new ArrayList<>(columns);
        addingColumns.removeAll(oldColumns);

        if (addingColumns.isEmpty()) return true;

        String sqlTemplate = "ALTER TABLE %s %s";
        List<String> tuples = new ArrayList<>();
        for (String column: addingColumns) {
            String type = columnTypes.getOrDefault(column, "VARCHAR(255)");
            tuples.add(String.format("ADD COLUMN %s %s", column, type));
        }
        String colStr = String.join(", ", tuples);

        String sql = String.format(sqlTemplate, table, colStr);
        try {
            execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}