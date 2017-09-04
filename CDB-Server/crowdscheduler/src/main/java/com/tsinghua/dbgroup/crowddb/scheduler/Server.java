package com.tsinghua.dbgroup.crowddb.scheduler;
import com.tsinghua.dbgroup.crowddb.crowdcore.configs.GlobalConfigs;
import com.tsinghua.dbgroup.crowddb.scheduler.dispatcher.QueryScheduler;
import com.tsinghua.dbgroup.crowddb.scheduler.server.SocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by talus on 16/6/20.
 */
public class Server {

    private static String LOG_FORMAT = "##Server##";
    private static Logger LOG = LoggerFactory.getLogger(Server.class);

    public static void main(String args[]) {

        DateFormat df = new SimpleDateFormat("y-MM-dd HH:mm:ss");
        Date date = new Date();

        /*
        * Load parameter configs
        * */
        Properties configs = Configs.CONFIGS;
        if (configs == null) {
            LOG.error(String.format("%s load config files failed", LOG_FORMAT).toString());
            System.exit(1);
        } else {
            GlobalConfigs.GlobalConfigs = Configs.CONFIGS;
        }
        LOG.info(String.format("%s load configs files successfully.", LOG_FORMAT));

        /*
        * Start Query Scheduler
        * */
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        QueryScheduler scheduler = null;
        try {
            scheduler = new QueryScheduler();
            long delay = Long.parseLong(configs.getProperty("POLL_INTERVAL"));
            service.scheduleWithFixedDelay(scheduler, 0, delay, TimeUnit.SECONDS);

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(String.format("%s start query scheduler failed", LOG_FORMAT));
        }

        LOG.info(String.format("%s start query scheduler successfully，current timestamp: %s", LOG_FORMAT, df.format(date)));

        /*
        * Start Listening Thread
        * */
        SocketServer socketServer = null;
        int port = Integer.parseInt(configs.getProperty("PORT"));

        try {
            socketServer = new SocketServer(scheduler, port);
            socketServer.start();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(String.format("%s SocketServer run failed. ", LOG_FORMAT));
        }
        LOG.info(String.format("%s run socket server successfully, current timestamp: %s", LOG_FORMAT, df.format(date)));
    }
}
