package com.tsinghua.dbgroup.crowddb.crowdexec.query.schema;

import junit.framework.TestCase;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Created by talus on 16/6/19.
 */
public class QueryControllerTest extends TestCase {

    public void testQuery() {
        Session session = HibernateSessionManager.currentSession();
        Query query = (Query)session.get(Query.class, new Integer(4));
        assertNotNull(query);

        System.out.println("sql = " + query.sql);
        HibernateSessionManager.closeSession();
    }

    public void testUpdate() {
        Session session = HibernateSessionManager.currentSession();
        Transaction transaction = session.beginTransaction();

        Query query = (Query)session.get(Query.class, new Integer(4));
        assertNotNull(query);

        query.setSql("select * from user");
        session.save(query);
        session.flush();
        transaction.commit();

        query = (Query)session.get(Query.class, new Integer(4));
        System.out.println("sql = " + query.getSql());

        HibernateSessionManager.closeSession();
    }

    public void testInsert() {
        // Session session = HibernateSessionManager.currentSession();
        // Transaction transaction = session.beginTransaction();
        // Query query = new Query();
        // query.setSql("select username from user");
        // query.setTimestamp("2016-06-02 01:19:49");
        // query.setUser(5);
        // query.setStatus("init");
        // query.setCurrentSQLNodeId(1);
        // query.setResultTable("123");
        //
        // session.save(query);
        // session.flush();
        // transaction.commit();
        //
        // query = (Query)session.get(Query.class, new Integer(4));
        // System.out.println("sql = " + query.getSql());
        // HibernateSessionManager.closeSession();
    }

    public void testDelete() {

    }
}