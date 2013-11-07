package ru.georgeee.android.gfeedreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import ru.georgeee.android.gfeedreader.utility.db.EntryTable;
import ru.georgeee.android.gfeedreader.utility.db.FeedTable;
import ru.georgeee.android.gfeedreader.utility.model.Feed;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 24.10.13
 * Time: 22:36
 * To change this template use File | Settings | File Templates.
 */
public class GAlarmBroadcastReciever extends BroadcastReceiver {
    SFServiceHelper helper;
    public GAlarmBroadcastReciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(helper == null) helper = SFServiceHelper.getInstance();
        FeedTable feedTable = FeedTable.getInstance(context);
        EntryTable entryTable = EntryTable.getInstance(context);
        try {
            Feed [] feeds = feedTable.loadFeeds();
            for(Feed feed : feeds) {
                int requestId = helper.updateFeedAction(feed);
                Log.d(getClass().getCanonicalName(), feed.getFeedUrl() + " -> " + requestId);
            }
        } catch (IOException e) {
            Log.e(getClass().getCanonicalName(), e.toString());
        } catch (ClassNotFoundException e) {
            Log.e(getClass().getCanonicalName(), e.toString());
        }
    }
}