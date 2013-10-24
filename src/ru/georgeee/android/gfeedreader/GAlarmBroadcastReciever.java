package ru.georgeee.android.gfeedreader;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import ru.georgeee.android.gfeedreader.service.SFCommandExecutorService;
import ru.georgeee.android.gfeedreader.utility.Storage;
import ru.georgeee.android.gfeedreader.utility.model.Feed;
import ru.georgeee.android.gfeedreader.utility.xml.FeedReaderTask;

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
        Feed [] feeds = Storage.getInstance().getFeeds();
        for(Feed feed : feeds) {
            String feedUrl = feed.getFeedUrl();
            int requestId = helper.updateFeedAction(feedUrl);
            Log.d(getClass().getCanonicalName(), feedUrl + " -> " + requestId);
        }
    }
}