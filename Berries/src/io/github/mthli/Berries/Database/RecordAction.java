package io.github.mthli.Berries.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import io.github.mthli.Berries.Unit.RecordUnit;

import java.util.ArrayList;
import java.util.List;

public class RecordAction {
    private SQLiteDatabase database;
    private RecordHelper helper;

    public RecordAction(Context context) {
        this.helper = new RecordHelper(context);
    }

    public void open(boolean rw) {
        if (rw) {
            database = helper.getWritableDatabase();
        } else {
            database = helper.getReadableDatabase();
        }
    }

    public void close() {
        helper.close();
    }

    public boolean addBookmark(Record record) {
        if (record == null || record.getTitle() == null || record.getURL() == null) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(RecordUnit.COLUMN_TITLE, record.getTitle());
        values.put(RecordUnit.COLUMN_URL, record.getURL());
        values.put(RecordUnit.COLUMN_TIME, record.getTime());
        database.insert(RecordUnit.TABLE_BOOKMARKS, null, values);
        return true;
    }

    public boolean addHistory(Record record) {
        if (record == null || record.getTitle() == null || record.getURL() == null) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(RecordUnit.COLUMN_TITLE, record.getTitle());
        values.put(RecordUnit.COLUMN_URL, record.getURL());
        values.put(RecordUnit.COLUMN_TIME, record.getTime());
        database.insert(RecordUnit.TABLE_HISTORY, null, values);
        return true;
    }

    public boolean checkBookmark(Record record) {
        if (record == null || record.getTitle() == null || record.getURL() == null) {
            return false;
        }

        Cursor cursor = database.query(
                RecordUnit.TABLE_BOOKMARKS,
                new String[] {RecordUnit.COLUMN_URL},
                RecordUnit.COLUMN_URL + "=?",
                new String[] {record.getURL()},
                null,
                null,
                null
        );

        if (cursor != null) {
            boolean result = false;
            if (cursor.moveToFirst()) {
                result = true;
            }
            cursor.close();

            return result;
        }

        return false;
    }

    public boolean deleteBookmark(Record record) {
        if (record == null || record.getURL() == null) {
            return false;
        }

        database.execSQL("DELETE FROM " + RecordUnit.TABLE_BOOKMARKS + " WHERE " + RecordUnit.COLUMN_URL + " = " + "\"" + record.getURL() + "\"");
        return true;
    }

    public boolean checkBookmark(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }

        Cursor cursor = database.query(
                RecordUnit.TABLE_BOOKMARKS,
                new String[] {RecordUnit.COLUMN_URL},
                RecordUnit.COLUMN_URL + "=?",
                new String[] {url},
                null,
                null,
                null
        );

        if (cursor != null) {
            boolean result = false;
            if (cursor.moveToFirst()) {
                result = true;
            }
            cursor.close();

            return result;
        }

        return false;
    }

    public boolean deleteBookmark(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }

        database.execSQL("DELETE FROM "+ RecordUnit.TABLE_BOOKMARKS + " WHERE " + RecordUnit.COLUMN_URL + " = " + "\"" + url + "\"");
        return true;
    }

    public boolean deleteHistory(Record record) {
        if (record == null || record.getTime() <= 0) {
            return false;
        }

        database.execSQL("DELETE FROM "+ RecordUnit.TABLE_HISTORY + " WHERE " + RecordUnit.COLUMN_TIME + " = " + record.getTime());
        return true;
    }

    public void clearBookmarks() {
        database.execSQL("DELETE FROM " + RecordUnit.TABLE_BOOKMARKS);
    }

    public void clearHistory() {
        database.execSQL("DELETE FROM " + RecordUnit.TABLE_HISTORY);
    }

    private Record get(Cursor cursor) {
        Record record = new Record();
        record.setTitle(cursor.getString(0));
        record.setURL(cursor.getString(1));
        record.setTime(cursor.getLong(2));

        return record;
    }

    public List<Record> listBookmarks() {
        List<Record> list = new ArrayList<Record>();

        Cursor cursor = database.query(
                RecordUnit.TABLE_BOOKMARKS,
                new String[] {
                        RecordUnit.COLUMN_TITLE,
                        RecordUnit.COLUMN_URL,
                        RecordUnit.COLUMN_TIME
                },
                null,
                null,
                null,
                null,
                RecordUnit.COLUMN_TIME + " desc"
        );

        if (cursor == null) {
            return list;
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(get(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return list;
    }

    public List<Record> listHistory() {
        List<Record> list = new ArrayList<Record>();

        Cursor cursor = database.query(
                RecordUnit.TABLE_HISTORY,
                new String[] {
                        RecordUnit.COLUMN_TITLE,
                        RecordUnit.COLUMN_URL,
                        RecordUnit.COLUMN_TIME
                },
                null,
                null,
                null,
                null,
                RecordUnit.COLUMN_TIME + " desc"
        );

        if (cursor == null) {
            return list;
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(get(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return list;
    }
}
