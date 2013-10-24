package ru.georgeee.android.gfeedreader.utility.http;

import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import ru.georgeee.android.gfeedreader.utility.TaskHadler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 29.09.13
 * Time: 15:07
 * To change this template use File | Settings | File Templates.
 */
public abstract class HttpTask<Result>  extends TaskHadler<Result>{
    protected String[] bodyParams;

    public String[] getBodyParams() {
        return bodyParams;
    }

    public void setBodyParams(String[] bodyParams) {
        this.bodyParams = bodyParams;
    }

    public HttpClient getHttpClient() {
        return HttpUtility.getInstance().getMultiThreadHttpClient();
    }

    protected abstract HttpRequestBase getHttpRequestBase();


    protected void handleHttpIOException(IOException ex) {
        ex.printStackTrace();
    }

    protected void handleException(Exception ex){
        ex.printStackTrace();
    }

    protected abstract Result getResult(HttpResponse httpResponse) throws IOException, CanceledException;

    @Override
    protected Result doInBackground() {
        try {
            HttpRequestBase base = getHttpRequestBase();
            HttpResponse httpResponse;
            httpResponse = getHttpClient().execute(base);
            try {
                return getResult(httpResponse);
            } catch (CanceledException e) {
                return null;
            }
        } catch (IOException ex) {
            handleHttpIOException(ex);
            return null;
        }  catch (Exception ex){
            handleException(ex);
            return null;
        }
    }


    protected String composeUrl(String urlBase, Map<String, String> getParams) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder(urlBase);
        boolean isFirst = true;
        for (String key : getParams.keySet()) {
            String value = getParams.get(key);
            if (isFirst) {
                sb.append('?');
                isFirst = false;
            } else sb.append('&');
            sb.append(key).append('=').append(URLEncoder.encode(value, "UTF-8"));
        }
        return sb.toString();
    }

    protected void checkCancell() throws CanceledException {
        if (isCancelled()) throw new CanceledException();
    }

    protected static class CanceledException extends Exception {

    }
}
