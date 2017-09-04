package com.tsinghua.dbgroup.crowddb.crowdplat.http;

import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by talus on 16/6/1.
 */
public class HttpRequest implements IHttpRequest {

    private static Logger LOG = LoggerFactory.getLogger(HttpRequest.class);

    private static final String LOG_FORMAT = "[HttpRequest]";

    private DefaultHttpClient client;

    public HttpRequest() {
        client = new DefaultHttpClient();
    }

    public JsonObject sendGetRequest(String url, Map<String, String> params) {
        String nurl = contactURL(url, params);
        HttpGet request = new HttpGet(nurl);

        LOG.info(LOG_FORMAT + "send get request: " + nurl);

        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (Exception e) {
            LOG.error(LOG_FORMAT + " request failed!", e);
            return null;
        }

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            LOG.error(LOG_FORMAT + " status code = " + statusCode);
            return null;
        }
        JsonObject json = null;
        try {
            json = readFromResponse(response);
            if (json == null)
                throw new Exception();
        } catch (Exception e) {
            LOG.error(LOG_FORMAT + " parser response json failed");
        }
        return json;
    }

    public JsonObject sendPostRequest(String url, Map<String, String> getParams, Map<String, String> postParams) {
        String nurl = contactURL(url, getParams);
        HttpPost request = new HttpPost(nurl);

        // add post params
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for (Map.Entry<String, String> entry: postParams.entrySet()) {
            builder.addTextBody(entry.getKey(), entry.getValue());
        }
        request.setEntity(builder.build());

        LOG.info(LOG_FORMAT + " send post request: " + nurl);
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (Exception e) {
            LOG.error(LOG_FORMAT + " request failed!", e);
            return null;
        }

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            LOG.error(LOG_FORMAT + " status code = " + statusCode);
        }

        JsonObject json = null;
        try {
            json = readFromResponse(response);
            if (json == null)
                throw new RuntimeException();
            LOG.info("received results: " + json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(LOG_FORMAT + " parser response json failed");
        }
        return json;
    }

    public JsonObject sendPostRequest(String url, Map<String, String> getParams, Map<String, String> postParams, Map<String, String> files) {
        String nurl = contactURL(url, getParams);
        HttpPost request = new HttpPost(nurl);

        // add post params
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for (Map.Entry<String, String> entry: postParams.entrySet()) {
            builder.addTextBody(entry.getKey(), entry.getValue());
        }

        // add file params
        for (Map.Entry<String, String> entry: files.entrySet()) {
            try {
                File file = new File(entry.getValue());
                builder.addBinaryBody(entry.getKey(), file, ContentType.DEFAULT_BINARY, entry.getValue());
            } catch (Exception e) {
                LOG.error(LOG_FORMAT + " can not open file:" + entry.getValue(), e);
            }
        }
        request.setEntity(builder.build());

        LOG.info(LOG_FORMAT + " send post request with files: " + nurl);
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (Exception e) {
            LOG.error(LOG_FORMAT + " request failed!", e);
            return null;
        }

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            LOG.error(LOG_FORMAT + " status code = " + statusCode);
        } else {
            LOG.info(LOG_FORMAT + " receive the response successfully");
        }

        JsonObject json = null;
        try {
            json = readFromResponse(response);
            if (json == null)
                throw new Exception();
        } catch (Exception e) {
            LOG.error(LOG_FORMAT + " parser response json failed");
        }
        return json;
    }

    private String contactURL(String url, Map<String, String> params) {
        if (params == null) return url;

        URIBuilder uri = null;
        try {
            uri = new URIBuilder(url);
            for (Map.Entry<String, String> entry: params.entrySet()) {
                uri.addParameter(entry.getKey(), entry.getValue());
            }
        } catch (URISyntaxException e) {
            LOG.error(LOG_FORMAT + " contact uri failed", e);
        }
        return uri.toString();
    }

    private JsonObject readFromResponse(HttpResponse response) throws Exception{
        if (response == null) return null;

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        return new JsonParser().parse(result.toString()).getAsJsonObject();
    }
}

