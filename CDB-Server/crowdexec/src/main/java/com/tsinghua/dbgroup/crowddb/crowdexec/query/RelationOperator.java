package com.tsinghua.dbgroup.crowddb.crowdexec.query;

import com.tsinghua.dbgroup.crowddb.crowdstorage.table.TableManager;
import com.tsinghua.dbgroup.crowddb.crowdstorage.utils.TableHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by talus on 16/6/16.
 */
public class RelationOperator {

    private TableManager tm;

    private static String LOG_FORMAT = "##RelationOperator##";

    private static Logger LOG = LoggerFactory.getLogger(RelationOperator.class);

    public RelationOperator() {
        tm = new TableManager();
    }

    public void execEquals() {

    }

    public void execGreater() {

    }

    public void execLess() {

    }

    private List<Map<String, Object>> extractColumns(String column) {
        Pair<String, String> pair = TableHelper.unpackTableColumn(column);
        if (pair == null) {
            return null;
        }

        try {
            return tm.extractColumn(pair.getLeft(), pair.getRight(), null);
        } catch (SQLException e) {
            return null;
        }
    }
}
