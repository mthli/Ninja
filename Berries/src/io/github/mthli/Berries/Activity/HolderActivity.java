package io.github.mthli.Berries.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import io.github.mthli.Berries.Database.Record;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Service.HolderService;
import io.github.mthli.Berries.Unit.PreferenceUnit;
import io.github.mthli.Berries.Unit.RecordUnit;

import java.util.Timer;
import java.util.TimerTask;

public class HolderActivity extends Activity {
    private Record first = null;
    private Record second = null;

    private Timer timer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        first = new Record();
        first.setTitle(getString(R.string.record_title_default));
        first.setURL(getIntent().getData().toString());
        first.setTime(System.currentTimeMillis());

        SharedPreferences sharedPreferences = getSharedPreferences(PreferenceUnit.NAME, MODE_PRIVATE);
        int interval = sharedPreferences.getInt(PreferenceUnit.DOUBLE_TAPS_INTERVAL, PreferenceUnit.DOUBLE_TAPS_INTERVAL_DEFAULT);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (first != null && second == null) {
                    Intent toService = new Intent(HolderActivity.this, HolderService.class);
                    RecordUnit.hold(first);
                    startService(toService);
                }

                timer.cancel();
                HolderActivity.this.finish();
            }
        };
        timer = new Timer();
        timer.schedule(task, interval);
    }

    @Override
    public void onNewIntent(Intent intent) {
        second = new Record();
        second.setTitle(getString(R.string.record_title_default));
        second.setURL(intent.getData().toString());
        second.setTime(System.currentTimeMillis());

        if (first.getURL().equals(second.getURL())) {
            Intent toBrowser = new Intent();
            // TODO: secondary browser
        } else {
            Intent toService = new Intent(HolderActivity.this, HolderService.class);
            RecordUnit.hold(second);
            startService(toService);
        }

        timer.cancel();
        finish();
    }
}
