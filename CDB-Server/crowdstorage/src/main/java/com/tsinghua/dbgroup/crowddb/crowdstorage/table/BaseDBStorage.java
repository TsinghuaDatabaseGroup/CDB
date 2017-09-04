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
import com.tsinghua.dbgroup.crowddb.crowdcore.configs.GlobalConfigs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Created by talus on 16/6/8.
 */
public class BaseDBStorage {

    private static String LOG_FORMAT = "##BaseDBStorage##";

    private static Logger LOG = LoggerFactory.getLogger(BaseDBStorage.class);

    protected Connection getConnnection() {
        String host, port, user, password, connUrl;
        try {
            host = (String) GlobalConfigs.GlobalConfigs.getOrDefault("HOST", "127.0.0.1");
            port = (String) GlobalConfigs.GlobalConfigs.getOrDefault("MYSQL_PORT", "3306");
            user = (String) GlobalConfigs.GlobalConfigs.getOrDefault("USERNAME", "3306");
            password = (String) GlobalConfigs.GlobalConfigs.getOrDefault("PASSWORD", "3306");
//            host = "166.111.71.172";
//            port = "3306";
//            user = "crowddb";
//            password = "crowddb!password";
        } catch (Exception e) {
            LOG.error(LOG_FORMAT, " load config file failed. ", e);
            return null;
        }

        connUrl = String.format("jdbc:mysql://%s:%s/crowddb_temp?characterEncoding=UTF-8",
                host, port);

        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(connUrl, user, password);
        } catch (Exception e) {
            LOG.error(LOG_FORMAT+" connect the database failed.", e);
        }
        return conn;
    }

    protected Properties loadDBConfig() throws Exception {
        Properties prop = new Properties();
        InputStream input = null;
        String configName = "jdbc.properties.development";
        String connectionUrl = "";

        input = BaseDBStorage.class.getClassLoader().getResourceAsStream(configName);
        if (input == null) {
            throw new Exception("cann't find the config file");
        }
        prop.load(input);
        return prop;
    }
}
