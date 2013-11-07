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

public class FeedTable extends Table<Feed> {
    private static FeedTable instance;

    public static FeedTable getInstance(Context context){
        if(instance == null) instance = new FeedTable(context);
        return instance;
    }

    private FeedTable(Context context) {
        super(context);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    protected static final String DB_TABLE = "feed_table";
    protected static final String COLUMN_FEED_ID = "feed_id";
    protected static final String COLUMN_TITLE = "title";
    protected static final String COLUMN_URL = "url";
    protected static final String COLUMN_ICON_URL = "icon_url";
    protected static final String COLUMN_LOGO_URL = "logo_url";
    protected static final String COLUMN_DESCRIPTION = "description";
    protected static final String COLUMN_FEED_URL = "feed_url";
    protected static final String COLUMN_LAST_UPDATED = "last_updated";
    protected static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_FEED_ID + " integer primary key autoincrement, " +
                    COLUMN_TITLE + " blob, " +
                    COLUMN_DESCRIPTION + " blob, " +
                    COLUMN_URL + " text, " +
                    COLUMN_ICON_URL + " text, " +
                    COLUMN_LOGO_URL + " text, " +
                    COLUMN_FEED_URL + " text, " +
                    COLUMN_LAST_UPDATED + " integer " +
                    ");";

    @Override
    protected Feed readModelInstance(Cursor cursor) throws IOException, ClassNotFoundException {
        Feed feed = new Feed();

        feed.setFeedId(cursor.getLong(cursor.getColumnIndex(COLUMN_FEED_ID)));

        feed.setTitle((WebString) readObjectFromCursor(COLUMN_TITLE, cursor));
        feed.setDescription((WebString) readObjectFromCursor(COLUMN_DESCRIPTION, cursor));

        feed.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_URL)));
        feed.setIconUrl(cursor.getString(cursor.getColumnIndex(COLUMN_ICON_URL)));
        feed.setLogoUrl(cursor.getString(cursor.getColumnIndex(COLUMN_LOGO_URL)));
        feed.setFeedUrl(cursor.getString(cursor.getColumnIndex(COLUMN_FEED_URL)));
        feed.setLastUpdated(new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_LAST_UPDATED))));
        return feed;
    }


    @Override
    protected Class<Feed> getModelClass() {
        return Feed.class;
    }
    public void saveFeed(Feed feed) throws IOException {
        saveFeed(feed, false);
    }
    public void saveFeed(Feed feed, boolean createOnly) throws IOException {
        ContentValues cv = new ContentValues();
        long feedId = feed.getFeedId();

        cv.put(COLUMN_TITLE, serializeObject(feed.getTitle()));
        cv.put(COLUMN_DESCRIPTION, serializeObject(feed.getDescription()));

        cv.put(COLUMN_URL, feed.getUrl());
        cv.put(COLUMN_ICON_URL, feed.getIconUrl());
        cv.put(COLUMN_LOGO_URL, feed.getLogoUrl());
        cv.put(COLUMN_FEED_URL, feed.getFeedUrl());
        cv.put(COLUMN_LAST_UPDATED, feed.getLastUpdated().getTime());

        if (feedId == 0) {
            Cursor cursor = mDB.query(DB_TABLE,
                    new String[]{COLUMN_FEED_ID},
                    COLUMN_FEED_URL + " = ?",
                    new String[]{feed.getFeedUrl()},
                    null, null, null);
            cursor.moveToNext();
            feedId = cursor.isAfterLast()?0:cursor.getLong(0);
        }

        if (feedId != 0) {
            if(!createOnly) mDB.update(DB_TABLE, cv, COLUMN_FEED_ID + " = " + feedId, null);
        } else {
            feedId = mDB.insert(DB_TABLE, null, cv);
        }

        feed.setFeedId(feedId);
    }

    public void deleteFeed(Feed feed) {
        mDB.delete(DB_TABLE, COLUMN_FEED_ID + " = " + feed.getFeedId(), null);
    }

    public Feed[] loadFeeds(int offset, int limit) throws IOException, ClassNotFoundException {
        return getModelInstancesByCursor(mDB.query(DB_TABLE, null, null, null, null, null, COLUMN_FEED_ID + " DESC", offset + ", " + limit));
    }

    public Feed[] loadFeeds() throws IOException, ClassNotFoundException {
        return getModelInstancesByCursor(mDB.query(DB_TABLE, null, null, null, null, null, COLUMN_FEED_ID + " DESC"));
    }

    public Feed loadFeed(long feedId) throws IOException, ClassNotFoundException {
        Feed[] feeds = getModelInstancesByCursor(mDB.query(DB_TABLE, null, COLUMN_FEED_ID + "=" + feedId, null, null, null, null));
        return feeds.length == 0 ? null : feeds[0];
    }

    public Feed loadFeed(String feedUrl) throws IOException, ClassNotFoundException {
        Feed[] feeds = getModelInstancesByCursor(mDB.query(DB_TABLE, null, COLUMN_FEED_URL + "=?", new String[]{feedUrl}, null, null, null));
        return feeds.length == 0 ? null : feeds[0];
    }
}