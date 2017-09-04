package com.tsinghua.dbgroup.crowddb.crowdexec.table;

import com.tsinghua.dbgroup.crowddb.crowdstorage.table.TableManager;
import com.tsinghua.dbgroup.crowddb.crowdstorage.table.schema.ColumnsSchema;
import junit.framework.TestCase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by talus on 16/6/11.
 */
public class TableManagerTest extends TestCase {

    private TableManager tm = null;

//    public void testCreateTmpTable() {
//        tm.createTmpTable(11, 2, JudgementSchema.SCHEMA_ID);
//    }
//
//    public void testDeleteTmpTable() {
//        String name = "q11_t2_b7bb30";
//        tm.deleteTmpTable(name);
//    }
//
//    public void testPostRequestWithFile() {
//
//    }
//
//    public void testGetTableColumns() {
//        try {
//            List<String> columns = tm.getTableColumns("crowddb_dblp.author");
//            for (String column: columns) {
//                System.out.println(column);
//            }
//        } catch (SQLException e) {
//
//        }
//    }
//
//    public void testInsertRecords() {
//        String tableName = tm.createTmpTable(11, 2, EqualSchema.SCHEMA_ID);
//
//        List<EqualSchema> list = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            list.add(new EqualSchema(Integer.toString(i), 0));
//        }
//
//        tm.insertEqualRecords(tableName, list);
//
//        String joinTableName = tm.createTmpTable(11, 2, JudgementSchema.SCHEMA_ID);
//
//        List<JudgementSchema> list2 = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            list2.add(new JudgementSchema(Integer.toString(i), Integer.toString(i+1), 1));
//        }
//
//        tm.insertJoinRecords(joinTableName, list2);
//    }
//
//    public void testNormalJoinTables() {
//        String table1 = TableManager.TMP_DATABASE + "." + TableManager.generateTableName();
//        tm.packetTable(table1, "crowddb_dblp.author");
//        String table2 = TableManager.TMP_DATABASE + "." + TableManager.generateTableName();
//        tm.packetTable(table2, "crowddb_dblp.paper");
//        Pair<String, String> pair = new MutablePair<>("author.name", "paper.name");
//
//        String table3 = TableManager.TMP_DATABASE + "." + TableManager.generateTableName();
//
//        tm.normalJoinTables(table3, table1, table2, pair, true);
//    }
//    public void testCrowdJoinTables() {
//        String table1 = TableManager.TMP_DATABASE + "." + TableManager.generateTableName();
//        tm.packetTable(table1, "crowddb_dblp.author");
//        String table2 = TableManager.TMP_DATABASE + "." + TableManager.generateTableName();
//        tm.packetTable(table2, "crowddb_dblp.paper");
//
//        String table3 = TableManager.TMP_DATABASE + "." + TableManager.generateTableName();
//        Pair<String, String> pair = new MutablePair<>("author.name", "paper.name");
//
//
//        tm.crowdJoinTables(table3, table1, table2, "crowddb_temp.q11_t2_4255178d", pair);
//
//    }
//
//    public void testPacketTable() {
//        boolean res = tm.packetTable("crowddb_temp.temp_123", "crowddb_dblp.author");
//        assertEquals(res, true);
//    }
//
//    public void testRestoreTable() {
//        boolean res = tm.restoreTable("crowddb_temp.temp_1234", "crowddb_temp.temp_123");
//        assertEquals(res, true);
//    }

    public void testUpdate() {

    }

    public void testInsertColumns() {
        List<String> columns = Arrays.asList("name", "school", "rank");
        List<ColumnsSchema> schemas = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            HashMap<String, String> hash = new HashMap<>();
            for (String column: columns) {
                hash.put(column, Integer.toString(i+1));
            }
            ColumnsSchema schema = new ColumnsSchema(Integer.toString(i), hash);
            schemas.add(schema);
        }

        String tableName = "crowddb_temp.test_table";
        assertEquals(true, tm.insertColumns(tableName, schemas, columns));

        tm.deleteTmpTable(tableName);
    }

    public void testAddColumns() {
        List<String> columns = Arrays.asList("name", "school", "rank");
        List<ColumnsSchema> schemas = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            HashMap<String, String> hash = new HashMap<>();
            for (String column: columns) {
                hash.put(column, Integer.toString(i+1));
            }
            ColumnsSchema schema = new ColumnsSchema(Integer.toString(i), hash);
            schemas.add(schema);
        }
        String tableName = "crowddb_temp.test_table";
        assertEquals(true, tm.insertColumns(tableName, schemas, columns));

        List<String> addColumns = Arrays.asList("name", "people");
        HashMap<String, String> types = new HashMap<>();
        assertEquals(tm.addColumns(tableName, addColumns, types), true);

        tm.deleteTmpTable(tableName);
    }

    public void testPacketTable() {
        String oldTableName = "crowddb_dblp.author";
        String newTableName = "crowddb_temp.test_table";
        assertEquals(true, tm.packetTable(newTableName, oldTableName));
    }

    @Override
    protected void setUp() throws Exception {
        tm = new TableManager();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
