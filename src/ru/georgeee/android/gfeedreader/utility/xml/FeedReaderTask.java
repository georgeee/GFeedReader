package ru.georgeee.android.gfeedreader.utility.xml;

import android.util.Log;
import ru.georgeee.android.gfeedreader.utility.db.EntryTable;
import ru.georgeee.android.gfeedreader.utility.db.FeedTable;
import ru.georgeee.android.gfeedreader.utility.http.HttpUtility;
import ru.georgeee.android.gfeedreader.utility.http.XmlResponseHttpTask;
import ru.georgeee.android.gfeedreader.utility.model.Entry;
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
    String feedUrl;
    FeedTable feedTable;
    EntryTable entryTable;

    public FeedReaderTask(String feedUrl, FeedTable feedTable, EntryTable entryTable) {
        if(!feedUrl.matches("^[a-z0-9A-Z]+://.*$")) feedUrl = "http://"+ feedUrl;
        this.feedUrl = feedUrl;
        this.feedTable = feedTable;
        this.entryTable = entryTable;
    }

    @Override
    protected SAXHandler<Feed> getSAXHandler() {
        try {
            return new FeedReader(getUrl(), feedTable, entryTable);
        } catch (FeedReaderException e) {
            handleSAXException(e);
        }
        return null;
    }

    @Override
    protected String getUrl() {
        return feedUrl;
    }

    @Override
    protected Executor getPreferedExecutor() {
        return HttpUtility.getInstance().getRssRetrieveExecutor();
    }
}
