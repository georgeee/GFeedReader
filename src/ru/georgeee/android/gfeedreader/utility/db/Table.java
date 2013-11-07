package ru.georgeee.android.gfeedreader.utility.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.*;
import java.lang.reflect.Array;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 07.11.13
 * Time: 5:38
 * To change this template use File | Settings | File Templates.
 */
public abstract class Table<T> {

    protected static final String DB_NAME = "rss";
    protected static final int DB_VERSION = 1;
    protected final Context context;
    protected DBHelper mDBHelper;
    protected SQLiteDatabase mDB;

    public Table(Context context) {
        this.context = context;
        open();
    }

    protected T[] getModelInstancesByCursor(Cursor cursor) throws IOException, ClassNotFoundException {
        T[] result = (T[]) Array.newInstance(getModelClass(), cursor.getCount());
        cursor.moveToNext();
        while (!cursor.isAfterLast()) {
            result[cursor.getPosition()] = readModelInstance(cursor);
            cursor.moveToNext();
        }
        return result;
    }

    protected abstract Class<T> getModelClass();
    protected abstract T readModelInstance(Cursor cursor) throws IOException, ClassNotFoundException;

    @Override
    protected void finalize() throws Throwable {
        close();
    }

    public void open() {
        mDBHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    public void close() {
        if (mDBHelper != null)
            mDBHelper.close();
    }

    protected Object unserializeObject(byte [] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }


    protected Object readObjectFromCursor(String columnName, Cursor cursor) throws IOException, ClassNotFoundException {
        return unserializeObject(cursor.getBlob(cursor.getColumnIndex(columnName)));
    }

    protected byte[] serializeObject(Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream ous = new ObjectOutputStream(baos);
        ous.writeObject(object);
        return baos.toByteArray();
    }
}
