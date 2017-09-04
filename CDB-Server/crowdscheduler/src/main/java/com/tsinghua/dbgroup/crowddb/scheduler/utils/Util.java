/*
 * Copyright (C) 2016-2016 by The Department of Computer Science and
 * Technology, Tsinghua University
 *
 * Redistribution of this file is permitted under the terms of
 * the BSD license.
 *
 * Author   : XuepingWeng
 * Created  : 10/12/16 11:38 PM
 * Modified :
 * Contact  : wxping715@gmail.com
 */

package com.tsinghua.dbgroup.crowddb.scheduler.utils;

import java.io.BufferedInputStream;
import java.io.IOException;

public class Util {

    public static String readFromSocket(BufferedInputStream inputStream) {
        int red = -1;
        byte[] buffer = new byte[8 * 1024];
        byte[] redData;

        StringBuilder clientData = new StringBuilder();
        String redDataText;

        if (inputStream == null) return null;

        try {
            while ((red = inputStream.read(buffer)) > -1) {
                redData = new byte[red];
                System.arraycopy(buffer, 0, redData, 0, red);
                redDataText = new String(redData, "UTF-8");
                clientData.append(redDataText);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            return clientData.toString();
        }
    }
}
