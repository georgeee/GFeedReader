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
    public static final SimpleDateFormat rfc822DateFormats[] = new SimpleDateFormat[]{
            new SimpleDateFormat("EEE, d MMM yy HH:mm:ss z"),
            new SimpleDateFormat("EEE, d MMM yy HH:mm z"),
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z"),
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm z"),
            new SimpleDateFormat("d MMM yy HH:mm z"),
            new SimpleDateFormat("d MMM yy HH:mm:ss z"),
            new SimpleDateFormat("d MMM yyyy HH:mm z"),
            new SimpleDateFormat("d MMM yyyy HH:mm:ss z")
    };
    private static HttpUtility ourInstance = new HttpUtility();
    private HttpClient defaultHttpClient = null;
    private Executor rssRetrieveExecutor = null;
    private Executor fileDownloadExecutor = null;

    private HttpUtility() {
    }

    public static HttpUtility getInstance() {
        return ourInstance;
    }

    public static java.util.Date parseRfc3339DateString(String datestring) {
        Date d = new Date();
        try {
            //if there is no time zone, we don't need to do any special parsing.
            if (datestring.endsWith("Z")) {
                try {
                    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");//spec for RFC3339
                    d = s.parse(datestring);
                } catch (java.text.ParseException pe) {//try again with optional decimals
                    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");//spec for RFC3339 (with fractional seconds)
                    s.setLenient(true);
                    d = s.parse(datestring);
                }
                return d;
            }

            //step one, split off the timezone.
            String firstpart = datestring.substring(0, datestring.lastIndexOf('-'));
            String secondpart = datestring.substring(datestring.lastIndexOf('-'));

            //step two, remove the colon from the timezone offset
            secondpart = secondpart.substring(0, secondpart.indexOf(':')) + secondpart.substring(secondpart.indexOf(':') + 1);
            datestring = firstpart + secondpart;
            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");//spec for RFC3339
            try {
                d = s.parse(datestring);
            } catch (java.text.ParseException pe) {//try again with optional decimals
                s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");//spec for RFC3339 (with fractional seconds)
                s.setLenient(true);
                d = s.parse(datestring);
            }
        } catch (Exception ex) {
            d = null;
        }
        return d;
    }

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

}
