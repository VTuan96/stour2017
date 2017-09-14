package com.bkstek.stour.util;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by acebk on 8/1/2017.
 */

public class JSONParser {
    static InputStream is = null;

    static JSONObject jObj = null;

    static String json = "";

    private int timeout = 12;

    HttpResponse httpResponse;

    DefaultHttpClient httpClient;

    HttpPost httpPost;

    HttpGet httpGet;

    // constructor
    public JSONParser() {
    }

    public String getJSONFromUrl(String url, String method,
                                 List<NameValuePair> nameValuePairs) {

        // Making HTTP request
        try {
            // defaultHttpClient
            httpClient = new DefaultHttpClient();
            HttpParams httpParams = httpClient.getParams();
            httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                    timeout * 1000);
            httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT,
                    timeout * 1000);
            if (method.equals("post")) {
                httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
                        HTTP.UTF_8));
                httpResponse = httpClient.execute(httpPost);

            } else {
                httpGet = new HttpGet(url);
                httpResponse = httpClient.execute(httpGet);
            }

            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        httpClient.getConnectionManager().shutdown();
        httpClient.clearRequestInterceptors();
        httpClient.clearResponseInterceptors();
        // if (method.equals(CommonDefine.METHOD_POST)) {
        // httpPost.abort();
        // } else {
        // httpGet.abort();
        // }
        return json;
    }

}
