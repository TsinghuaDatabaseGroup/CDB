package com.tsinghua.dbgroup.crowddb.crowdsql.parser;

import com.tsinghua.dbgroup.crowddb.crowdsql.query.SqlContext;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SqlParserTest {

    static private Logger logger = LoggerFactory.getLogger(SqlParserTest.class);

    private SqlParser sqlParser;

    @Before
    public void SetUp() {
        sqlParser = new SqlParser();
    }

    @Test
    public void TestParse() {
//        SqlContext sqlContext = sqlParser.parse("SELECT * from t1, t2 WHERE t1.a CROWD_EQ t2.a AND t1.b > 10;");
        SqlContext sqlContext = sqlParser.parse("select author.name from author, citation, paper where author.name CROWD_EQ paper.name and citation.title CROWD_LT paper.title and num IN (10, 20);","crowddb_dblp");
        //SqlContext sqlContext = sqlParser.parse("collect ","crowddb_dblp");

        //System.out.println("!:" + sqlContext.toString());
        //return;

        //sqlContext = sqlParser.parse("fill capital, population from country on name;","crowddb_dblp");

//        System.out.println("!:" + sqlContext.toString());
//
//        sqlContext = sqlParser.parse("singlelabel weather from image on url TAGIN [\"sunny\", \"raining\", \"snow\", \"cloud\"];","crowddb_dblp");

//        System.out.println("!:" + sqlContext.toString());

        sqlContext = sqlParser.parse("singlelabel weather from image on url tagin [\"sunny\", \"raining\", \"snow\", \"cloud\"];","crowddb_dblp");

        System.out.println("!:" + sqlContext.toString());

//
//        sqlContext = sqlParser.parse("select author.name from author, citation, paper where author.name CROWD_EQ paper.name and citation.title CROWD_LT paper.title and num IN (10, 20) TAG url ;","crowddb_dblp");
//
//        System.out.println("!:" + sqlContext.toString());
//
//        sqlContext = sqlParser.parse("select author.name from author, citation, paper where author.name CROWD_EQ paper.name and citation.title CROWD_LT paper.title and num IN (10, 20) TAG url ;","crowddb_dblp");
//
//        System.out.println("!:" + sqlContext.toString());


        //sqlContext = sqlParser.parse("SELECT *\n" +
        //        "FROM products\n" +
        //        "WHERE Name CROWD_EQ “iPhone” AND\n" +
        //        "      Price CROWD_GT 5000 AND\n" +
        //        "      Description CROWD_LIKE '%s’ AND\n" +
        //        "      OriginCity CROWD_IN ‘Beijing’ AND\n" +
        //        "      CreateDate CROWD_BETWEEN 10;"
        //        ,"crowddb_dblp");
        //logger.info(sqlContext.toString());
    }

    @Test
    public void TestPreProcess() {
        String newQuery = sqlParser.preProcess("select *   from \"where\" where id = 1;");
        assertEquals(newQuery, "SELECT * FROM \"where\" WHERE id = 1;");
    }

    @Test
    public void TestParseWheres() {
        List<String> tokens = sqlParser.parseWheres("(id > 1) AND name = 'james' OR ((time > 10 AND salary < 10))");
        String[] correctTokens = {"(", "id > 1", ")", "AND", " name = 'james' ", "OR", "(", "(",
                "time > 10 ", "AND", " salary < 10", ")", ")"};
        for (int i = 0; i < tokens.size(); ++i) {
            assertEquals(tokens.get(i), correctTokens[i]);
        }

        tokens = sqlParser.parseWheres("Name CROWD_EQ “iPhone” AND\n" +
                "      Price CROWD_GT 5000 AND\n" +
                "      Description CROWD_LIKE '%s’ AND\n" +
                "      OriginCity CROWD_IN (‘Beijing’, ‘London’) AND\n" +
                "      CreateDate CROWD_BETWEEN (10, 20)\n" +
                "      CROWD_ORDER by price ASC LIMIT 1");

        for (String token : tokens) {
            System.out.println("!:" + token);
        }
    }
}
