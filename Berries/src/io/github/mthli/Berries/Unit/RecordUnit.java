package io.github.mthli.Berries.Unit;

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
}
