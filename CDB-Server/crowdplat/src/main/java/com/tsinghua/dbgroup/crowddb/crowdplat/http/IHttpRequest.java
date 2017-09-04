package com.tsinghua.dbgroup.crowddb.crowdplat.http;

import com.google.gson.JsonObject;

import java.util.Map;

/**
 * Created by talus on 16/6/1.
 */
public interface IHttpRequest{

    public JsonObject sendGetRequest(String url, Map<String, String> params);

    public JsonObject sendPostRequest(String url, Map<String, String> getParams, Map<String, String> postParams);

    public JsonObject sendPostRequest(String url, Map<String, String> getParams, Map<String, String> postParams, Map<String, String> files);
}
