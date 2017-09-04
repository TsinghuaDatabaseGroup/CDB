package com.tsinghua.dbgroup.crowddb.crowdsql.parser;

import com.tsinghua.dbgroup.crowddb.crowdsql.query.SqlContext;

public interface ISqlParser {
    SqlContext parse(String query,String dbName);
}
