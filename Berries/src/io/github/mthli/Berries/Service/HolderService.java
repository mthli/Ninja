package io.github.mthli.Berries.Service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.webkit.WebView;
import android.widget.Toast;
import io.github.mthli.Berries.Browser.BrowserContainer;
import io.github.mthli.Berries.Browser.BerryView;
import io.github.mthli.Berries.Browser.BrowserController;
import io.github.mthli.Berries.Database.Record;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Unit.*;
import io.github.mthli.Berries.View.TabRelativeLayout;

public class HolderService extends Service implements BrowserController {
    @Override
    public void updateBookmarks() {}

    @Override
    public void updateInputBox(String query) {}

    @Override
    public void updateProgress(int progress) {}

    @Override
    public void onCreateView(WebView view, boolean incognito, Message resultMsg) {}

    @Override
    public void showTab(BerryView berryView) {}

    @Override
    public void showTab(TabRelativeLayout tabRelativeLayout) {}

    @Override
    public void deleteTab() {}

    @Override
    public void onLongPress(String url) {}

    @Override
    public void onCreate() {
        super.onCreate();
        BrowserContainer.clear();
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

        // TODO: Network available
        if (BrowserContainer.size() < BrowserUnit.LOAD_LIMIT) {
            Record record = RecordUnit.getHolder();
            // TODO: SP_INCOGNITO
            BerryView view = new BerryView(this, false);
            view.setController(this);
            BrowserContainer.add(view);
            updateNotification();
        } else {
            Toast.makeText(this, R.string.toast_load_limit, Toast.LENGTH_SHORT).show();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        BrowserContainer.clear();
        stopForeground(true);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void updateNotification() {
        Notification.Builder builder = NotificationUnit.getBuilder(this);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE;
        startForeground(NotificationUnit.ID, notification);
    }
}
