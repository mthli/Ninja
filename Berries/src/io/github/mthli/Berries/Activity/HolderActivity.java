package io.github.mthli.Berries.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import io.github.mthli.Berries.Database.Record;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Service.HolderService;
import io.github.mthli.Berries.Unit.ConstantUnit;
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
        first.setTitle(getString(R.string.record_untitled));
        first.setURL(getIntent().getData().toString());
        first.setTime(System.currentTimeMillis());

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (first != null && second == null) {
                    Intent toService = new Intent(HolderActivity.this, HolderService.class);
                    RecordUnit.setHolder(first);
                    startService(toService);
                }

                timer.cancel();
                HolderActivity.this.finish();
            }
        };
        timer = new Timer();
        timer.schedule(task, ConstantUnit.LOAD_LIMIT);
    }

    @Override
    public void onNewIntent(Intent intent) {
        second = new Record();
        second.setTitle(getString(R.string.record_untitled));
        second.setURL(intent.getData().toString());
        second.setTime(System.currentTimeMillis());

        if (first.getURL().equals(second.getURL())) {
            Intent toBrowser = new Intent();
            // TODO
        } else {
            Intent toService = new Intent(HolderActivity.this, HolderService.class);
            RecordUnit.setHolder(second);
            startService(toService);
        }

        timer.cancel();
        finish();
    }
}
