package ru.georgeee.android.gfeedreader.handlers.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;
import ru.georgeee.android.gfeedreader.handlers.SFBaseCommand;
import ru.georgeee.android.gfeedreader.utility.Storage;
import ru.georgeee.android.gfeedreader.utility.model.Feed;
import ru.georgeee.android.gfeedreader.utility.xml.FeedReaderTask;

import java.util.concurrent.ExecutionException;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 24.10.13
 * Time: 7:34
 * To change this template use File | Settings | File Templates.
 */
public class LoadFeedMetaCommand extends SFBaseCommand{
    String feedUrl;

    @Override
    public synchronized void cancel() {
        super.cancel();
    }

    @Override
    protected void doExecute(Intent intent, Context context, ResultReceiver callback) {
        Log.d(getClass().getCanonicalName(), "Command started");
        Storage storage = Storage.getInstance();
        Bundle bundle = new Bundle();
        try{
            Feed feed = storage.getFeedMeta(feedUrl);
            if(feed == null){
                feed = new FeedReaderTask(feedUrl).executeInCurrentThread();
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
    public static final Parcelable.Creator<LoadFeedMetaCommand> CREATOR = new Parcelable.Creator<LoadFeedMetaCommand>() {
        public LoadFeedMetaCommand createFromParcel(Parcel in) {
            return new LoadFeedMetaCommand(in);
        }

        public LoadFeedMetaCommand[] newArray(int size) {
            return new LoadFeedMetaCommand[size];
        }
    };

    private LoadFeedMetaCommand(Parcel in) {
        feedUrl = in.readString();
    }

    public LoadFeedMetaCommand(String feedUrl) {
        this.feedUrl = feedUrl;
    }

}
