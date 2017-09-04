package com.tsinghua.dbgroup.crowddb.crowdplat.http;

import com.google.gson.JsonObject;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by talus on 16/6/2.
 */
public class HttpRequestTest extends TestCase {

    private static String url = "http://www.chinacrowds.com:6789/account/login";

    private HttpRequest request = null;

    public void testGetRequest() {
        JsonObject json = request.sendGetRequest(url, null);

        assertNotNull(json);
        assertEquals(json.get("code").getAsInt(), 1100);
    }

    public void testPostRequest() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", "talus");
        params.put("password", "chinacrowds");
        params.put("type", "worker");

        JsonObject json = request.sendPostRequest(url, null, params);
        assertNotNull(json);
        assertEquals(json.get("code").getAsInt(), 0);
    }

    public void testPostRequestWithFile() {

    }

    @Override
    protected void setUp() throws Exception {
        request = new HttpRequest();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
