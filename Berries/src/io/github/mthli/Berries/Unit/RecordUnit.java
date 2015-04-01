package io.github.mthli.Berries.Unit;

import android.content.Context;
import io.github.mthli.Berries.Database.Record;
import io.github.mthli.Berries.R;

public class RecordUnit {
    public static final String TABLE = "HISTORY";

    public static final String TITLE = "TITLE";
    public static final String URL = "URL";
    public static final String TIME = "TIME";

    public static final String CREATE_SQL = "CREATE TABLE "
            + TABLE
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
        Record record = new Record();
        record.setTitle(context.getString(R.string.browser_home));
        record.setURL(BrowserUnit.Home);
        record.setTime(System.currentTimeMillis());

        return record;
    }
}
