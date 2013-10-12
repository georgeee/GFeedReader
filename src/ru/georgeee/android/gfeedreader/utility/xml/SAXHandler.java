package ru.georgeee.android.gfeedreader.utility.xml;

import org.xml.sax.ContentHandler;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 12.10.13
 * Time: 4:30
 * To change this template use File | Settings | File Templates.
 */
public interface SAXHandler<Result> extends ContentHandler {
    public Result getResult();
}
