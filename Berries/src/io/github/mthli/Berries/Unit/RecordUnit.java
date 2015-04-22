package io.github.mthli.Berries.Unit;

import io.github.mthli.Berries.Database.Record;

public class RecordUnit {
    public static final String TABLE_BOOKMARKS = "BOOKMARKS";
    public static final String TABLE_HISTORY = "HISTORY";

    public static final String COLUMN_TITLE = "TITLE";
    public static final String COLUMN_URL = "URL";
    public static final String COLUMN_TIME = "TIME";

    public static final String CREATE_HISTORY = "CREATE TABLE "
            + TABLE_HISTORY
            + " ("
            + " " + COLUMN_TITLE + " text,"
            + " " + COLUMN_URL + " text,"
            + " " + COLUMN_TIME + " integer"
            + ")";

    public static final String CREATE_BOOKMARKS = "CREATE TABLE "
            + TABLE_BOOKMARKS
            + " ("
            + " " + COLUMN_TITLE + " text,"
            + " " + COLUMN_URL + " text,"
            + " " + COLUMN_TIME + " integer"
            + ")";

    private static Record holder;
    public static Record getHolder() {
        return holder;
    }
    public synchronized static void setHolder(Record record) {
        holder = record;
    }

    private static boolean incognito;
    public static boolean isIncognito() {
        return incognito;
    }
    public synchronized static void setIncognito(boolean b) {
        incognito = b;
    }
}
