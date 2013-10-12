package ru.georgeee.android.gfeedreader.utility.xml;

import org.xml.sax.SAXException;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 12.10.13
 * Time: 4:57
 * To change this template use File | Settings | File Templates.
 */
public class FeedReaderException extends SAXException {
    public FeedReaderException() {
    }

    public FeedReaderException(String message) {
        super(message);
    }

    public FeedReaderException(Exception e) {
        super(e);
    }

    public FeedReaderException(String message, Exception e) {
        super(message, e);
    }
}
