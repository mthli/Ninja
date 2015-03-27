package io.github.mthli.Berries.Service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;
import io.github.mthli.Berries.Browser.BerryContainer;
import io.github.mthli.Berries.Browser.BerryContextWrapper;
import io.github.mthli.Berries.Browser.BerryView;
import io.github.mthli.Berries.Browser.BrowserController;
import io.github.mthli.Berries.Database.Record;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Unit.*;

public class HolderService extends Service implements BrowserController {
    private BerryContextWrapper context;

    @Override
    public void onCreate() {
        super.onCreate();

        BerryContainer.clear();
        context = new BerryContextWrapper(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO: when BrowserActivity disappear.

        try {
            if (intent.getExtras().getBoolean(IntentUnit.LIST)) {
                // TODO
            } else if (intent.getExtras().getBoolean(IntentUnit.QUIT)) {
                this.stopSelf();
            }
        } catch (NullPointerException n) {}

        if (BerryContainer.size() < PreferenceUnit.LOAD_LIMIT_MAX_DEFAULT) {
            Record record = RecordUnit.get();
            BerryView view = new BerryView(context, record);
            view.setController(this);
            BerryContainer.add(view);
            updateNotification();
        } else {
            Toast.makeText(this, R.string.toast_load_limit_max, Toast.LENGTH_SHORT).show();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        BerryContainer.clear();
        stopForeground(true);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void updateRecord(Record record) {}

    public void updateProgress(int progress) {}

    public void showControlPanel() {}

    public void hideControlPanel() {}

    public void onLongPress() {}

    public boolean isToolbarShowing() {
        return false;
    }

    public boolean isIncognito() {
        SharedPreferences sp = getSharedPreferences(PreferenceUnit.NAME, MODE_PRIVATE);
        return sp.getBoolean(PreferenceUnit.INCOGNITO, PreferenceUnit.INCOGNITO_DEFAULT);
    }

    public void updateNotification() {
        Notification.Builder builder = NotificationUnit.getBuilder(this);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE;
        startForeground(NotificationUnit.ID, notification);
    }
}
