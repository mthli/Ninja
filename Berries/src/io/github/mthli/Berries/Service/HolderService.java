package io.github.mthli.Berries.Service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
import io.github.mthli.Berries.Browser.BerryContainer;
import io.github.mthli.Berries.Browser.Berry;
import io.github.mthli.Berries.Browser.BrowserController;
import io.github.mthli.Berries.Database.Record;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Unit.*;

public class HolderService extends Service implements BrowserController {
    public void updateRecord(Record record) {}

    public void updateProgress(int progress) {}

    public void showControlPanel() {}

    public void hideControlPanel() {}

    public boolean isPanelShowing() {
        return false;
    }

    public void onLongPress() {}

    public void showSelectedTab(Berry berry) {}

    public void deleteSelectedTab() {}

    @Override
    public void onCreate() {
        super.onCreate();
        BerryContainer.clear();
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

        if (BerryContainer.size() < BrowserUnit.LOAD_LIMIT) {
            Record record = RecordUnit.getHolder();
            // TODO: SP_INCOGNITO
            Berry view = new Berry(this, record, false);
            view.setController(this);
            BerryContainer.add(view);
            updateNotification();
        } else {
            Toast.makeText(this, R.string.browser_toast_load_limit, Toast.LENGTH_SHORT).show();
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

    public void updateNotification() {
        Notification.Builder builder = NotificationUnit.getBuilder(this);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE;
        startForeground(NotificationUnit.ID, notification);
    }
}
