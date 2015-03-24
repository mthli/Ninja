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

    private SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();

        BerryContainer.clear();
        context = new BerryContextWrapper(this);
        sp = getSharedPreferences(PreferenceUnit.NAME, MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
        } else {
            Toast.makeText(this, R.string.toast_load_limit_max, Toast.LENGTH_SHORT).show();
        }

        updateNotification();

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

    public void showToolbar() {}

    public void hideToolbar() {}

    public void onLongPress() {}

    public boolean isToolbarShowing() {
        return false;
    }

    public boolean isIncognito() {
        return sp.getBoolean(PreferenceUnit.INCOGNITO, PreferenceUnit.INCOGNITO_DEFAULT);
    }

    public void updateNotification() {
        int done = 0;

        for (BerryView view : BerryContainer.list()) {
            if (view.isFinish()) {
                done++;
            }
        }

        int priority = sp.getInt(PreferenceUnit.NOTIFICATION_PRIORITY, PreferenceUnit.NOTIFICATION_PRIORITY_DEFAULT);
        boolean sound = sp.getBoolean(PreferenceUnit.NOTIFICATION_SOUND, PreferenceUnit.NOTIFICATION_SOUND_DEFAULT);
        Notification.Builder builder = NotificationUnit.getBuilder(this, BerryContainer.size(), done, priority, sound);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE;
        startForeground(NotificationUnit.ID, notification);
    }
}
