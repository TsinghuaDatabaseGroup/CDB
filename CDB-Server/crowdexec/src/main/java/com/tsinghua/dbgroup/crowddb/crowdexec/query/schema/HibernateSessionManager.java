package com.tsinghua.dbgroup.crowddb.crowdexec.query.schema;

/**
 * Created by talus on 16/6/19.
 */

import com.tsinghua.dbgroup.crowddb.crowdcore.configs.GlobalConfigs;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

public class HibernateSessionManager {

    private static SessionFactory sessionFactory;

    static
    {
        String username = GlobalConfigs.GlobalConfigs.getProperty("USERNAME");
        String password = GlobalConfigs.GlobalConfigs.getProperty("PASSWORD");
        String host = GlobalConfigs.GlobalConfigs.getProperty("HOST");
        String port = GlobalConfigs.GlobalConfigs.getProperty("MYSQL_PORT");
        String connUrl = String.format("jdbc:mysql://%s:%s/crowddb_meta", host, port);
//        String username = "crowddb";
//        String password = "crowddb!password";
//        String host = "166.111.71.172";
//        String port = "3306";
//        String connUrl = String.format("jdbc:mysql://%s:%s/crowddb_meta", host, port);

//        System.out.println(connUrl);
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.url", connUrl);
        configuration.setProperty("hibernate.connection.username", username);
        configuration.setProperty("hibernate.connection.password", password);

        try
        {
            configuration.configure("hibernate/hibernate.cfg.xml");
            sessionFactory = configuration.buildSessionFactory();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static final ThreadLocal tl = new ThreadLocal();

    public static Session currentSession()
    {
//        Session s = (Session)tl.get();
//        if (s == null)
//        {
//            s = sessionFactory.openSession();
//            tl.set(s);
//        }
//
//        return s;
        return sessionFactory.getCurrentSession();
    }

    public static void closeSession()
    {
        Session s = (Session)tl.get();
        tl.set(null);
        if (s != null)
        {
            s.close();
        }
    }
}
