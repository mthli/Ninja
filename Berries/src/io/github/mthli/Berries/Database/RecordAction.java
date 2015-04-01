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

    public void add(Record record, String table) {
        ContentValues values = new ContentValues();

        values.put(RecordUnit.TITLE, record.getTitle());
        values.put(RecordUnit.URL, record.getURL());
        values.put(RecordUnit.TIME, record.getTime());

        database.insert(table, null, values);
    }

    public void delete(Record record, String table) {
        database.execSQL("DELETE FROM "+ table + " WHERE " + RecordUnit.TIME + " = " + record.getTime());
    }

    public void clear(String table) {
        database.execSQL("DELETE FROM " + table);
    }

    public Record get(Cursor cursor) {
        Record record = new Record();

        record.setTitle(cursor.getString(0));
        record.setURL(cursor.getString(1));
        record.setTime(cursor.getLong(2));

        return record;
    }

    public List<Record> list(String table) {
        List<Record> list = new ArrayList<Record>();

        Cursor cursor = database.query(
                table,
                new String[] {
                        RecordUnit.TITLE,
                        RecordUnit.URL,
                        RecordUnit.TIME
                },
                null,
                null,
                null,
                null,
                RecordUnit.TIME + " desc"
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
