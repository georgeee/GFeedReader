package ru.georgeee.android.gfeedreader.utility.http;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 29.09.13
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */
public class HttpUtility {
    private static HttpUtility ourInstance = new HttpUtility();
    private HttpClient defaultHttpClient = null;
    private Executor rssRetrieveExecutor = null;
    private Executor fileDownloadExecutor = null;

    private HttpUtility() {
    }

    public static HttpUtility getInstance() {
        return ourInstance;
    }

    public Executor getRssRetrieveExecutor() {
        if (rssRetrieveExecutor == null) {
            rssRetrieveExecutor = Executors.newCachedThreadPool();
        }
        return rssRetrieveExecutor;
    }

    public Executor getFileDownloadExecutor() {
        if (fileDownloadExecutor == null) {
            fileDownloadExecutor = Executors.newCachedThreadPool();
        }
        return fileDownloadExecutor;
    }

    public HttpClient getMultiThreadHttpClient() {
        HttpParams params = new BasicHttpParams();
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(params, registry);
        if (defaultHttpClient == null) defaultHttpClient = new DefaultHttpClient(connectionManager, params);
        return defaultHttpClient;
    }

    public static final SimpleDateFormat rfc822DateFormats[] = new SimpleDateFormat[] {
            new SimpleDateFormat("EEE, d MMM yy HH:mm:ss z"),
            new SimpleDateFormat("EEE, d MMM yy HH:mm z"),
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z"),
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm z"),
            new SimpleDateFormat("d MMM yy HH:mm z"),
            new SimpleDateFormat("d MMM yy HH:mm:ss z"),
            new SimpleDateFormat("d MMM yyyy HH:mm z"),
            new SimpleDateFormat("d MMM yyyy HH:mm:ss z")
    };

    /**
     * Parse an RFC 822 date string.
     *
     * @param dateString The date string to parse
     * @return The date, or null if it could not be parsed.
     */
    public static Date parseRfc822DateString(String dateString) {
        Date date = null;
        for (SimpleDateFormat sdf : rfc822DateFormats) {
            try {
                date = sdf.parse(dateString);
            } catch (ParseException e) {
                // Don't care, we'll just run through all
            }
            if (date != null) {
                return date;
            }
        }
        return null;
    }

}
