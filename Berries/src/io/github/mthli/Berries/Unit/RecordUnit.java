package io.github.mthli.Berries.Unit;

import android.content.Context;
import io.github.mthli.Berries.Database.Record;
import io.github.mthli.Berries.R;

public class RecordUnit {
    public static final String BOOKMARKS = "BOOKMARKS";
    public static final String HISTORY = "HISTORY";

    public static final String TITLE = "TITLE";
    public static final String URL = "URL";
    public static final String TIME = "TIME";

    public static final String CREATE_HISTORY = "CREATE TABLE "
            + HISTORY
            + " ("
            + " " + TITLE + " text,"
            + " " + URL + " text,"
            + " " + TIME + " integer"
            + ")";

    public static final String CREATE_BOOKMARKS = "CREATE TABLE "
            + BOOKMARKS
            + " ("
            + " " + TITLE + " text,"
            + " " + URL + " text,"
            + " " + TIME + " integer"
            + ")";

    private static Record holder;
    public static Record getHolder() {
        return holder;
    }
    public synchronized static void setHolder(Record record) {
        holder = record;
    }

    public static Record getHome(Context context) {
        return new Record(context.getString(R.string.browser_tab_home), BrowserUnit.ABOUT_HOME, System.currentTimeMillis());
    }
}
