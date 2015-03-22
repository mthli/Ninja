package io.github.mthli.Berries.Unit;

import android.content.Intent;
import io.github.mthli.Berries.Database.Record;

public class IntentUnit {
    public static Record getRecord(Intent intent) {
        Record record = new Record();
        record.setTitle(intent.getStringExtra(RecordUnit.TITLE));
        record.setURL(intent.getStringExtra(RecordUnit.URL));
        record.setTime(intent.getLongExtra(RecordUnit.TIME, 0l));

        return record;
    }


    public static void putRecord(Intent intent, Record record) {
        intent.putExtra(RecordUnit.TITLE, record.getTitle());
        intent.putExtra(RecordUnit.URL, record.getURL());
        intent.putExtra(RecordUnit.TIME, record.getTime());
    }
}