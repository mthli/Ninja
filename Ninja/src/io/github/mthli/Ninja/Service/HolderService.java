package io.github.mthli.Ninja.Service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.webkit.WebView;
import android.widget.Toast;
import io.github.mthli.Ninja.Browser.BrowserContainer;
import io.github.mthli.Ninja.Browser.BerryView;
import io.github.mthli.Ninja.Browser.BrowserController;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.Unit.BrowserUnit;
import io.github.mthli.Ninja.Unit.IntentUnit;
import io.github.mthli.Ninja.Unit.NotificationUnit;
import io.github.mthli.Ninja.Unit.RecordUnit;
import io.github.mthli.Ninja.View.TabRelativeLayout;

public class HolderService extends Service implements BrowserController {
    @Override
    public void updateBookmarks() {}

    @Override
    public void updateInputBox(String query) {}

    @Override
    public void onCreateView(WebView view, Message resultMsg) {}

    @Override
    public void showTab(BerryView berryView) {}

    @Override
    public void showTab(TabRelativeLayout tabRelativeLayout) {}

    @Override
    public void deleteTab() {}

    @Override
    public void onLongPress(String url) {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!BrowserUnit.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.toast_network_error, Toast.LENGTH_SHORT).show();
            this.stopSelf();
        }

        BerryView berryView = new BerryView(this);
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
        if (IntentUnit.isClear()) {
            BrowserContainer.clear();
        }
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void updateProgress(int progress) {
        updateNotification();
    }

    private void updateNotification() {
        Notification notification = NotificationUnit.getBuilder(this).build();
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE;
        startForeground(NotificationUnit.ID, notification);
    }
}
