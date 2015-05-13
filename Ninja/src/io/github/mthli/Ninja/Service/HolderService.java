package io.github.mthli.Ninja.Service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import io.github.mthli.Ninja.Browser.AlbumController;
import io.github.mthli.Ninja.Browser.BrowserContainer;
import io.github.mthli.Ninja.Browser.BrowserController;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.Unit.*;
import io.github.mthli.Ninja.View.NinjaWebView;

public class HolderService extends Service implements BrowserController {
    @Override
    public void updateAutoComplete() {}

    @Override
    public void updateBookmarks() {}

    @Override
    public void updateInputBox(String query) {}

    @Override
    public void updateProgress(int progress) {}

    @Override
    public void showAlbum(AlbumController albumController, boolean expand, boolean capture) {}

    @Override
    public void removeAlbum(AlbumController albumController) {}

    @Override
    public void onCreateView(WebView view, Message resultMsg) {}

    @Override
    public void onLongPress(String url) {}

    private int windowWidth;
    private int windowHeight;
    private int statusBarHeight;
    private float dimen48dp;

    @Override
    public void onCreate() {
        windowWidth = ViewUnit.getWindowWidth(this);
        windowHeight = ViewUnit.getWindowHeight(this);
        statusBarHeight = ViewUnit.getStatusBarHeight(this);
        dimen48dp = getResources().getDimensionPixelOffset(R.dimen.layout_height_48dp);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int specWidth = View.MeasureSpec.makeMeasureSpec(windowWidth, View.MeasureSpec.EXACTLY);
        int specHeight = View.MeasureSpec.makeMeasureSpec((int) (windowHeight - statusBarHeight - dimen48dp), View.MeasureSpec.EXACTLY);

        NinjaWebView webView = new NinjaWebView(this);
        webView.setBrowserController(this);
        webView.setFlag(BrowserUnit.FLAG_NINJA);
        webView.setAlbumCover(null);
        webView.setAlbumTitle(getString(R.string.album_untitled));

        /* Very important for displaying webview's layout correctly */
        webView.measure(specWidth, specHeight);
        webView.layout(0, 0, webView.getMeasuredWidth(), webView.getMeasuredHeight());

        webView.loadUrl(RecordUnit.getHolder().getURL());
        webView.deactivate();

        BrowserContainer.add(webView);
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

    private void updateNotification() {
        Notification notification = NotificationUnit.getBuilder(this).build();
        startForeground(NotificationUnit.ID, notification);
    }
}
