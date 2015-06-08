package io.github.mthli.Ninja.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import io.github.mthli.Ninja.Unit.RecordUnit;

public class RecordHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Ninja3.db";
    private static final int DATABASE_VERSION = 1;

    public RecordHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(RecordUnit.CREATE_BOOKMARKS);
        database.execSQL(RecordUnit.CREATE_HISTORY);
        database.execSQL(RecordUnit.CREATE_WHITELIST);
        database.execSQL(RecordUnit.CREATE_GRID);
    }

    // UPGRADE ATTENTION!!!
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {}

    // UPGRADE ATTENTION!!!
    private boolean isTableExist(@NonNull String tableName) {
        return false;
    }
}
