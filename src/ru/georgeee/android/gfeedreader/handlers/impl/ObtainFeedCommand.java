package ru.georgeee.android.gfeedreader.handlers.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;
import ru.georgeee.android.gfeedreader.handlers.SFBaseCommand;
import ru.georgeee.android.gfeedreader.utility.db.EntryTable;
import ru.georgeee.android.gfeedreader.utility.db.FeedTable;
import ru.georgeee.android.gfeedreader.utility.model.Feed;
import ru.georgeee.android.gfeedreader.utility.xml.FeedReaderTask;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 24.10.13
 * Time: 7:34
 * To change this template use File | Settings | File Templates.
 */
public class ObtainFeedCommand extends SFBaseCommand{
    String feedUrl;

    @Override
    public synchronized void cancel() {
        super.cancel();
    }

    @Override
    protected void doExecute(Intent intent, Context context, ResultReceiver callback) {
        Bundle bundle = new Bundle();
        FeedTable feedTable = FeedTable.getInstance(context);
        EntryTable entryTable = EntryTable.getInstance(context);
        try{
            Feed feed = feedTable.loadFeed(feedUrl);
            if(feed == null){
                feed = new FeedReaderTask(feedUrl, feedTable, entryTable).executeInCurrentThread();
            }
            Log.d(getClass().getCanonicalName(), "Feed: "+feed);
            if(feed == null){
                throw new NullPointerException();
            }
            bundle.putSerializable("feed", feed);
            notifySuccess(bundle);
        }catch (Exception ex){
            bundle.putSerializable("exceptionClass", ex.getClass());
            bundle.putString("exceptionMessage", ex.getMessage());
            notifyFailure(bundle);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(feedUrl);
    }
    public static final Parcelable.Creator<ObtainFeedCommand> CREATOR = new Parcelable.Creator<ObtainFeedCommand>() {
        public ObtainFeedCommand createFromParcel(Parcel in) {
            return new ObtainFeedCommand(in);
        }

        public ObtainFeedCommand[] newArray(int size) {
            return new ObtainFeedCommand[size];
        }
    };

    private ObtainFeedCommand(Parcel in) {
        feedUrl = in.readString();
    }

    public ObtainFeedCommand(String feedUrl) {
        this.feedUrl = feedUrl;
    }

}
