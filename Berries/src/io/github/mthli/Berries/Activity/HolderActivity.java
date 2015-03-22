package io.github.mthli.Berries.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import io.github.mthli.Berries.Database.Record;
import io.github.mthli.Berries.R;

import java.util.Timer;
import java.util.TimerTask;

public class HolderActivity extends Activity {
    private Record first;
    private Record second;

    private Timer timer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        first = new Record();
        first.setTitle(getString(R.string.record_title_default));
        first.setURL(getIntent().getData().toString());
        first.setTime(System.currentTimeMillis());

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sp_name), MODE_PRIVATE);
        int interval = sharedPreferences.getInt(
                getString(R.string.sp_double_taps_interval),
                getResources().getInteger(R.integer.sp_double_taps_interval_default)
        );

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
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
            // TODO
        } else {
            // TODO
        }

        finish();
    }
}
