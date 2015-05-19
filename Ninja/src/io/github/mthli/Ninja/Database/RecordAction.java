package io.github.mthli.Ninja.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import io.github.mthli.Ninja.Unit.RecordUnit;

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
        if (record == null || record.getTitle() == null || record.getTitle().trim().isEmpty() || record.getURL() == null || record.getURL().trim().isEmpty()) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(RecordUnit.COLUMN_TITLE, record.getTitle().trim());
        values.put(RecordUnit.COLUMN_URL, record.getURL().trim());
        values.put(RecordUnit.COLUMN_TIME, record.getTime());
        database.insert(RecordUnit.TABLE_BOOKMARKS, null, values);
        return true;
    }

    public boolean addHistory(Record record) {
        if (record == null || record.getTitle() == null || record.getTitle().trim().isEmpty() || record.getURL() == null || record.getURL().trim().isEmpty()) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(RecordUnit.COLUMN_TITLE, record.getTitle().trim());
        values.put(RecordUnit.COLUMN_URL, record.getURL().trim());
        values.put(RecordUnit.COLUMN_TIME, record.getTime());
        database.insert(RecordUnit.TABLE_HISTORY, null, values);
        return true;
    }

    public boolean addDomain(String domain) {
        if (domain == null || domain.trim().isEmpty()) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(RecordUnit.COLUMN_DOMAIN, domain.trim());
        database.insert(RecordUnit.TABLE_ADBLOCK, null, values);
        return true;
    }

    public void updateBookmark(Record record) {
        if (record == null || record.getTitle() == null || record.getTitle().trim().isEmpty() || record.getURL() == null || record.getURL().trim().isEmpty()) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(RecordUnit.COLUMN_TITLE, record.getTitle().trim());
        values.put(RecordUnit.COLUMN_URL, record.getURL().trim());
        values.put(RecordUnit.COLUMN_TIME, record.getTime());
        database.update(RecordUnit.TABLE_BOOKMARKS, values, RecordUnit.COLUMN_TIME + "=?", new String[] {String.valueOf(record.getTime())});
    }

    public boolean checkBookmark(Record record) {
        if (record == null || record.getTitle() == null || record.getTitle().trim().isEmpty() ||record.getURL() == null || record.getURL().trim().isEmpty()) {
            return false;
        }

        Cursor cursor = database.query(
                RecordUnit.TABLE_BOOKMARKS,
                new String[] {RecordUnit.COLUMN_URL},
                RecordUnit.COLUMN_URL + "=?",
                new String[] {record.getURL().trim()},
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

    public boolean checkBookmark(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        Cursor cursor = database.query(
                RecordUnit.TABLE_BOOKMARKS,
                new String[] {RecordUnit.COLUMN_URL},
                RecordUnit.COLUMN_URL + "=?",
                new String[] {url.trim()},
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

    public boolean checkDomain(String domain) {
        if (domain == null || domain.trim().isEmpty()) {
            return false;
        }

        Cursor cursor = database.query(
                RecordUnit.TABLE_ADBLOCK,
                new String[] {RecordUnit.COLUMN_DOMAIN},
                RecordUnit.COLUMN_URL + "=?",
                new String[] {domain.trim()},
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
        if (record == null || record.getURL() == null || record.getURL().trim().isEmpty()) {
            return false;
        }

        database.execSQL("DELETE FROM " + RecordUnit.TABLE_BOOKMARKS + " WHERE " + RecordUnit.COLUMN_URL + " = " + "\"" + record.getURL().trim() + "\"");
        return true;
    }

    public boolean deleteBookmark(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        database.execSQL("DELETE FROM "+ RecordUnit.TABLE_BOOKMARKS + " WHERE " + RecordUnit.COLUMN_URL + " = " + "\"" + url.trim() + "\"");
        return true;
    }

    public boolean deleteHistory(Record record) {
        if (record == null || record.getTime() <= 0) {
            return false;
        }

        database.execSQL("DELETE FROM "+ RecordUnit.TABLE_HISTORY + " WHERE " + RecordUnit.COLUMN_TIME + " = " + record.getTime());
        return true;
    }

    public boolean deleteDomain(String domain) {
        if (domain == null || domain.trim().isEmpty()) {
            return false;
        }

        database.execSQL("DELETE FROM "+ RecordUnit.TABLE_ADBLOCK + " WHERE " + RecordUnit.COLUMN_DOMAIN + " = " + "\"" + domain.trim() + "\"");
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

    // TODO: Use database sort but not Collections.sort()
    public List<Record> listBookmarks() {
        List<Record> list = new ArrayList<>();

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

    // TODO: Use database sort but not Collections.sort()
    public List<Record> listHistory() {
        List<Record> list = new ArrayList<>();

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
