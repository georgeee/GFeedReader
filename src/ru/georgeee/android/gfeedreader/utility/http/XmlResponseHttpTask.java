package ru.georgeee.android.gfeedreader.utility.http;

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import ru.georgeee.android.gfeedreader.utility.xml.SAXHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private String getEncondingFromEntity(HttpEntity entity) {
        if (entity.getContentType() != null) {
            //Content-Type: text/xml; charset=ISO-8859-1
            //Content-Type: text/xml; charset=UTF-8
            for (String str : entity.getContentType().getValue().split(";")) {
                if (str.toLowerCase().contains("charset")) {
                    return str.toLowerCase().replace("charset=", "").replace(";", "").replace(" ", "");
                }
            }
        }
        return null;
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
            InputStreamWrapper isw = new InputStreamWrapper(httpResponse.getEntity().getContent());
            String encoding = isw.getEncoding();
            if (encoding == null) {
                encoding = getEncondingFromEntity(httpResponse.getEntity());
            }
            Reader isr = encoding == null ? new InputStreamReader(isw) : new InputStreamReader(isw, Charset.forName(encoding));
            InputSource is = new InputSource();
            is.setCharacterStream(isr);
            xmlReader.parse(is);
            return handler.getResult();
        } catch (SAXException e) {
            handleSAXException(e);
        } catch (ParserConfigurationException e) {
            handleParserConfigurationException(e);
        }
        return null;
    }

    /**
     * Class for extracting encoding from xml declaration
     * SAX Parser fails to do it
     */
    static class InputStreamWrapper extends InputStream {
        InputStream is;
        String encoding = null;
        ArrayList<Byte> firstLineBytes = new ArrayList<Byte>();
        int counter = 0;

        InputStreamWrapper(InputStream is) throws IOException {
            this.is = is;
            int _byte;
            while ((_byte = is.read()) != -1) {
                if ((char) _byte == '\n') {
                    //<?xml version="1.0" encoding="utf-8"?>
                    byte[] bytes = new byte[firstLineBytes.size()];
                    for (int i = 0; i < bytes.length; ++i) {
                        bytes[i] = firstLineBytes.get(i);
                    }
                    String xmlHeader = new String(bytes);
                    String expr = "<\\?xml.*encoding\\=\"([^\"]+)\".*\\?>";
                    Pattern p = Pattern.compile(expr);
                    Matcher m = p.matcher(xmlHeader);
                    if (m.find()) {
                        encoding = m.group(1);
                        xmlHeader = xmlHeader.replaceFirst(expr, "");
                        firstLineBytes.clear();
                        for(byte sbyte : xmlHeader.getBytes()) firstLineBytes.add(sbyte);
                    }
                    break;
                } else firstLineBytes.add((byte) _byte);
            }
        }

        String getEncoding() {
            return encoding;
        }

        @Override
        public int read() throws IOException {
            if (counter < firstLineBytes.size()) return firstLineBytes.get(counter++);
            return is.read();
        }

    }

}
