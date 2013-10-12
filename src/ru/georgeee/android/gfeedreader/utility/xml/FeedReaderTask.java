package ru.georgeee.android.gfeedreader.utility.xml;

import ru.georgeee.android.gfeedreader.utility.http.HttpUtility;
import ru.georgeee.android.gfeedreader.utility.http.XmlResponseHttpTask;
import ru.georgeee.android.gfeedreader.utility.model.Feed;

import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 12.10.13
 * Time: 6:08
 * To change this template use File | Settings | File Templates.
 */
public class FeedReaderTask extends XmlResponseHttpTask<Feed> {
    String rssUrl;

    public FeedReaderTask(String rssUrl) {
        this.rssUrl = rssUrl;
    }

    @Override
    protected SAXHandler<Feed> getSAXHandler() {
        try {
            return new FeedReader(getUrl());
        } catch (FeedReaderException e) {
            handleSAXException(e);
        }
        return null;
    }

    @Override
    protected String getUrl() {
        return rssUrl;
    }

    @Override
    protected Executor getExecutor() {
        return HttpUtility.getInstance().getRssRetrieveExecutor();
    }
}
