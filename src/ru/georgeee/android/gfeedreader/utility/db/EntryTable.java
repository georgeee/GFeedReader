package ru.georgeee.android.gfeedreader.utility.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ru.georgeee.android.gfeedreader.utility.model.Entry;
import ru.georgeee.android.gfeedreader.utility.model.Feed;
import ru.georgeee.android.gfeedreader.utility.model.WebString;

import java.io.*;
import java.util.Date;

public class EntryTable extends Table<Entry>{

    private static EntryTable instance;

    public static EntryTable getInstance(Context context){
        if(instance == null) instance = new EntryTable(context);
        return instance;
    }

    private EntryTable(Context context) {
        super(context);
    }

    protected static final String DB_TABLE = "entry_table";
    protected static final String COLUMN_ENTRY_ID = "entry_id";
    protected static final String COLUMN_FEED_ID = "feed_id";
    protected static final String COLUMN_TITLE = "title";
    protected static final String COLUMN_URL = "url";
    protected static final String COLUMN_CONTENT = "content";
    protected static final String COLUMN_SUMMARY = "summary";
    protected static final String COLUMN_PUB_DATE = "pub_date";
    protected static final String COLUMN_IMAGE_URL = "image_url";
    protected static final String COLUMN_FEED_URL = "feed_url";
    protected static final String COLUMN_UNIQUE_IDENTIFIER = "unique_id";
    protected static final String COLUMN_ID = "id";


    protected static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ENTRY_ID + " integer primary key autoincrement, " +
                    COLUMN_FEED_ID + " integer, " +
                    COLUMN_TITLE + " blob, " +
                    COLUMN_URL + " text, " +
                    COLUMN_CONTENT + " blob, " +
                    COLUMN_SUMMARY + " blob, " +
                    COLUMN_PUB_DATE + " integer, " +
                    COLUMN_FEED_URL + " text, " +
                    COLUMN_IMAGE_URL + " text, " +
                    COLUMN_UNIQUE_IDENTIFIER + " text, " +
                    COLUMN_ID + " text " +
                    ");";

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    protected Class<Entry> getModelClass() {
        return Entry.class;
    }

    @Override
    protected Entry readModelInstance(Cursor cursor) throws IOException, ClassNotFoundException {
        Entry entry = new Entry();
        entry.setEntryId(cursor.getLong(cursor.getColumnIndex(COLUMN_ENTRY_ID)));
        entry.setFeedId(cursor.getLong(cursor.getColumnIndex(COLUMN_FEED_ID)));
        entry.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));

        entry.setTitle((WebString) readObjectFromCursor(COLUMN_TITLE, cursor));
        entry.setContent((WebString) readObjectFromCursor(COLUMN_CONTENT, cursor));
        entry.setSummary((WebString) readObjectFromCursor(COLUMN_SUMMARY, cursor));

        entry.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_URL)));
        entry.setPubDate(new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_PUB_DATE))));
        entry.setImageUrl(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL)));
        entry.setFeedUrl(cursor.getString(cursor.getColumnIndex(COLUMN_FEED_URL)));
        return entry;
    }


    public void saveEntry(Entry entry) throws IOException {
        ContentValues cv = new ContentValues();
        long entryId = entry.getEntryId();

        cv.put(COLUMN_FEED_ID, entry.getFeedId());
        cv.put(COLUMN_ID, entry.getId());
        cv.put(COLUMN_TITLE, serializeObject(entry.getTitle()));
        cv.put(COLUMN_CONTENT, serializeObject(entry.getContent()));
        cv.put(COLUMN_SUMMARY, serializeObject(entry.getSummary()));
        cv.put(COLUMN_URL, entry.getUrl());
        cv.put(COLUMN_PUB_DATE, entry.getPubDate().getTime());
        cv.put(COLUMN_IMAGE_URL, entry.getImageUrl());
        cv.put(COLUMN_FEED_URL, entry.getFeedUrl());

        cv.put(COLUMN_UNIQUE_IDENTIFIER, entry.getUniqueIdentifier());

        if(entryId == 0){
            Cursor cursor = mDB.query(DB_TABLE,
                    new String[]{COLUMN_ENTRY_ID},
                    COLUMN_UNIQUE_IDENTIFIER+" = ?",
                    new String[]{entry.getUniqueIdentifier()},
                    null, null, null);
            cursor.moveToNext();
            entryId = cursor.isAfterLast()?0:cursor.getLong(0);
        }

        if(entryId != 0){
            mDB.update(DB_TABLE, cv, COLUMN_ENTRY_ID+" = "+entryId, null);
        }   else{
            entryId = mDB.insert(DB_TABLE, null, cv);
        }
        entry.setEntryId(entryId);
    }

    public void deleteAllFromFeed(long feedId) {
        mDB.delete(DB_TABLE, COLUMN_FEED_ID + " = " + feedId, null);
    }

    public void deleteAllFromFeed(Feed feed){
        deleteAllFromFeed(feed.getFeedId());
    }

    public Entry[] loadEntries(long feedId, int offset, int limit) throws IOException, ClassNotFoundException {
        return getModelInstancesByCursor(mDB.query(DB_TABLE, null, COLUMN_FEED_ID + " = " + feedId, null, null, null, COLUMN_FEED_ID + " DESC", offset + ", " + limit));
    }
    public Entry[] loadEntries(long feedId) throws IOException, ClassNotFoundException {
        return getModelInstancesByCursor(mDB.query(DB_TABLE, null, COLUMN_FEED_ID + " = " + feedId, null, null, null, COLUMN_ENTRY_ID + " DESC"));
    }


    public Entry[] loadEntries(Feed feed, int offset, int limit) throws IOException, ClassNotFoundException {
        return loadEntries(feed.getFeedId(), offset, limit);
    }
    public Entry[] loadEntries(Feed feed, int limit) throws IOException, ClassNotFoundException {
        return loadEntries(feed.getFeedId(), 0, limit);
    }
    public Entry[] loadEntries(int feedId, int limit) throws IOException, ClassNotFoundException {
        return loadEntries(feedId, 0, limit);
    }
    public Entry[] loadEntries(Feed feed) throws IOException, ClassNotFoundException {
        return loadEntries(feed.getFeedId());
    }


}