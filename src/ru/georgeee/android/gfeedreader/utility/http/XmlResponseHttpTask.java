package ru.georgeee.android.gfeedreader.utility.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import ru.georgeee.android.gfeedreader.utility.xml.SAXHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
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
public abstract class XmlResponseHttpTask<Result> extends HttpTask<Result> {

    protected abstract SAXHandler<Result> getSAXHandler();

    @Override
    protected HttpRequestBase getHttpRequestBase() {
        HttpGet httpGet = new HttpGet(getUrl());
        return httpGet;
    }

    protected abstract String getUrl();

    protected void handleSAXException(SAXException ex) {
        ex.printStackTrace();
    }
    protected void handleParserConfigurationException(ParserConfigurationException ex) {
        ex.printStackTrace();
    }

    @Override
    protected Result getResult(HttpResponse httpResponse) throws IOException, CanceledException {
        SAXHandler<Result> handler = getSAXHandler();
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        try {
            SAXParser saxParser = spf.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(handler);
            xmlReader.parse(new InputSource(httpResponse.getEntity().getContent()));
            return handler.getResult();
        } catch (SAXException e) {
            handleSAXException(e);
        } catch (ParserConfigurationException e) {
            handleParserConfigurationException(e);
        }
        return null;
    }

}
