package com.tsinghua.dbgroup.crowddb.scheduler.server;

import com.tsinghua.dbgroup.crowddb.scheduler.dispatcher.QueryScheduler;
import com.tsinghua.dbgroup.crowddb.scheduler.utils.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by talus on 7/3/16.
 */
public class SocketServer extends Thread{

    private static String LOG_FORMAT = "##Server##";
    private static Logger LOG = LoggerFactory.getLogger(SocketServer.class);

    private ServerSocket serverSocket;

    private QueryScheduler queryScheduler;

    public SocketServer(QueryScheduler queryScheduler, int port) throws IOException{
        this.serverSocket = new ServerSocket(port);
        this.queryScheduler = queryScheduler;
    }

    @Override
    public void run() {
        while (true) {

            try {
                Socket socket = serverSocket.accept();
                BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());

                String redData = Util.readFromSocket(inputStream);
                int queryId = -1;
                try {
                    queryId = Integer.valueOf(redData);
                    LOG.info(String.format("receive queryId = %d from socket stream", queryId));

                    this.queryScheduler.startNewQuery(queryId);
                } catch (NumberFormatException e) {
                    LOG.error(String.format("get queryId failed, red data is @@@%s@@@", redData));
                } catch (Exception e) {
                    e.printStackTrace();
                    LOG.error(String.format("start new query failed, queryId = %d", queryId));
                }
            } catch (Exception e) {
                LOG.error(LOG_FORMAT + "Cannot accept the connection or read from inputstream", e);
            }
        }
    }
}
