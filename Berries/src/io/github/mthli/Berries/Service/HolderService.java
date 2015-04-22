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
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Unit.BrowserUnit;
import io.github.mthli.Berries.Unit.NotificationUnit;
import io.github.mthli.Berries.Unit.RecordUnit;
import io.github.mthli.Berries.View.TabRelativeLayout;

public class HolderService extends Service implements BrowserController {
    @Override
    public void updateBookmarks() {}

    @Override
    public void updateInputBox(String query) {}

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
        if (!BrowserUnit.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.toast_network_error, Toast.LENGTH_SHORT).show();
            this.stopSelf();
        }

        BerryView berryView = new BerryView(this, RecordUnit.isIncognito());
        berryView.setController(this);
        berryView.setFlag(BrowserUnit.FLAG_BERRY);
        berryView.setTabTitle(getString(R.string.browser_tab_untitled));
        berryView.loadUrl(RecordUnit.getHolder().getURL());
        berryView.deactivate();

        BrowserContainer.add(berryView);
        updateNotification();

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
    public void updateProgress(int progress) {
        if (progress < BrowserUnit.PROGRESS_MAX) {
            return;
        }
        updateNotification();
    }

    private void updateNotification() {
        Notification notification = NotificationUnit.getBuilder(this).build();
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE;
        startForeground(NotificationUnit.ID, notification);
    }
}
