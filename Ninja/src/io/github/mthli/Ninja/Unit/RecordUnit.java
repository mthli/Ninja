package io.github.mthli.Ninja.Unit;

import io.github.mthli.Ninja.Database.Record;

public class RecordUnit {
    public static final String TABLE_BOOKMARKS = "BOOKMARKS";
    public static final String TABLE_HISTORY = "HISTORY";
    public static final String TABLE_ADBLOCK = "ADBLOCK";

    public static final String COLUMN_TITLE = "TITLE";
    public static final String COLUMN_URL = "URL";
    public static final String COLUMN_TIME = "TIME";
    public static final String COLUMN_DOMAIN = "DOMAIN";

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

    public static final String CREATE_ADBLOCK = "CREATE TABLE "
            + TABLE_ADBLOCK
            + " ("
            + " " + COLUMN_DOMAIN
            + ")";

    private static Record holder;
    public static Record getHolder() {
        return holder;
    }
    public synchronized static void setHolder(Record record) {
        holder = record;
    }
}
