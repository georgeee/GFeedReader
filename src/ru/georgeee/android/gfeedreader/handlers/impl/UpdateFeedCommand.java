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
 * Time: 9:03
 * To change this template use File | Settings | File Templates.
 */
public class UpdateFeedCommand extends SFBaseCommand {
    Feed feed;

    @Override
    protected void doExecute(Intent intent, Context context, ResultReceiver callback) {
        Bundle bundle = new Bundle();
        FeedTable feedTable = FeedTable.getInstance(context);
        EntryTable entryTable = EntryTable.getInstance(context);
        try{
            Feed feed = new FeedReaderTask(this.feed.getFeedUrl(), feedTable, entryTable).executeInCurrentThread();
            if(feed == null){
                throw new NullPointerException();
            }
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
        dest.writeSerializable(feed);
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
        feed = (Feed) in.readSerializable();
    }

    public UpdateFeedCommand(Feed feed) {
        this.feed = feed;
    }
}
