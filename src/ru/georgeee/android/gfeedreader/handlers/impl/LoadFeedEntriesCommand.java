package ru.georgeee.android.gfeedreader.handlers.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import ru.georgeee.android.gfeedreader.handlers.SFBaseCommand;
import ru.georgeee.android.gfeedreader.utility.db.EntryTable;
import ru.georgeee.android.gfeedreader.utility.model.Entry;
import ru.georgeee.android.gfeedreader.utility.model.Feed;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 24.10.13
 * Time: 8:56
 * To change this template use File | Settings | File Templates.
 */
public class LoadFeedEntriesCommand extends SFBaseCommand {
    Feed feed;

    @Override
    protected void doExecute(Intent intent, Context context, ResultReceiver callback) {
        EntryTable table = EntryTable.getInstance(context);
        Bundle bundle = new Bundle();
        try{
            Entry[] entries = table.loadEntries(feed);
            ArrayList<Entry> _entries = new ArrayList<Entry>();
            Collections.addAll(_entries, entries);
            bundle.putSerializable("entries", _entries);
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
        dest.writeSerializable(feed);
    }
    public static final Parcelable.Creator<LoadFeedEntriesCommand> CREATOR = new Parcelable.Creator<LoadFeedEntriesCommand>() {
        public LoadFeedEntriesCommand createFromParcel(Parcel in) {
            return new LoadFeedEntriesCommand(in);
        }

        public LoadFeedEntriesCommand[] newArray(int size) {
            return new LoadFeedEntriesCommand[size];
        }
    };

    private LoadFeedEntriesCommand(Parcel in) {
        feed = (Feed) in.readSerializable();
    }

    public LoadFeedEntriesCommand(Feed feed) {
        this.feed = feed;
    }

}
