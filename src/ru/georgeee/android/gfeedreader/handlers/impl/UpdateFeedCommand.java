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
import ru.georgeee.android.gfeedreader.utility.model.Entry;
import ru.georgeee.android.gfeedreader.utility.model.Feed;
import ru.georgeee.android.gfeedreader.utility.xml.FeedReaderTask;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 24.10.13
 * Time: 9:03
 * To change this template use File | Settings | File Templates.
 */
public class UpdateFeedCommand extends SFBaseCommand {
    String feedUrl;

    @Override
    protected void doExecute(Intent intent, Context context, ResultReceiver callback) {
        Bundle bundle = new Bundle();
        try{
            Feed feed = new FeedReaderTask(feedUrl).executeInCurrentThread();
            if(feed == null){
                throw new NullPointerException();
            }
            bundle.putSerializable("feed", feed);
            ArrayList<Entry> _entries = new ArrayList<Entry>();
            Collections.addAll(_entries, Storage.getInstance().getFeedEntries(feed.getFeedUrl()));
            bundle.putSerializable("entries", _entries);
            Log.d(getClass().getCanonicalName(), "Updated feed: " + feed);
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
    public static final Parcelable.Creator<UpdateFeedCommand> CREATOR = new Parcelable.Creator<UpdateFeedCommand>() {
        public UpdateFeedCommand createFromParcel(Parcel in) {
            return new UpdateFeedCommand(in);
        }

        public UpdateFeedCommand[] newArray(int size) {
            return new UpdateFeedCommand[size];
        }
    };

    private UpdateFeedCommand(Parcel in) {
        feedUrl = in.readString();
    }

    public UpdateFeedCommand(String feedUrl) {
        this.feedUrl = feedUrl;
    }

}
