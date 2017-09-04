package com.tsinghua.dbgroup.crowddb.scheduler;
import jdk.nashorn.internal.runtime.regexp.joni.Config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Created by talus on 16/6/20.
 */
public class Configs {

    public static Properties CONFIGS;

    static {
        try {
            CONFIGS = loadConfig();
        } catch (IOException e) {
            CONFIGS = null;
            e.printStackTrace();
        }
    }

    public static Properties loadConfig() throws IOException{
        Properties prop = new Properties();
        InputStream input = null;
        String configName = "resources/runtime.configs.properties";

        String configPath = String.format("%s/%s", System.getProperty("user.dir"), configName);
        input = new FileInputStream(configPath);
        if (input == null) {
            throw new IOException("cann't find the config file " + configName);
        }
        prop.load(input);
        return prop;
    }
}
