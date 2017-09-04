package com.tsinghua.dbgroup.crowddb.scheduler.server;
import com.tsinghua.dbgroup.crowddb.scheduler.server.SocketServer;
import junit.framework.TestCase;


import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by talus on 7/3/16.
 */
public class SocketServerTest extends TestCase {

    public void testSocketServer() {

        try {
            SocketServer server = new SocketServer(null, 1234);
            server.start();

            Socket socket= new Socket("127.0.0.1",1234);
            BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            dataOutputStream.writeInt(12);
            dataOutputStream.flush();
            dataOutputStream.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
