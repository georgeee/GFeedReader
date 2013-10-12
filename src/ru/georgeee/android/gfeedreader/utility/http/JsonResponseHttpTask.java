package ru.georgeee.android.gfeedreader.utility.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 29.09.13
 * Time: 15:51
 * To change this template use File | Settings | File Templates.
 */
public abstract class JsonResponseHttpTask<Result> extends HttpTask<Result> {

    @Override
    protected HttpRequestBase getHttpRequestBase() {
        HttpGet httpGet = new HttpGet(getUrl());
        return httpGet;
    }

    protected abstract String getUrl();

    protected void handleJSONException(JSONException ex, String context) {
        ex.printStackTrace();
        System.err.println("Context (JSONException): " + context);
    }

    protected abstract Result getResultByJson(JSONObject jsonObject);

    @Override
    protected Result getResult(HttpResponse httpResponse) throws IOException, CanceledException {
        // json is UTF-8 by default
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"), 8);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            checkCancell();
            sb.append(line + "\n");
        }
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(sb.toString());
        } catch (JSONException e) {
            handleJSONException(e, sb.toString());
            return null;
        }
        return getResultByJson(jObject);
    }
}
