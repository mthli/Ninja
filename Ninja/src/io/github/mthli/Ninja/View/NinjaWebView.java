package io.github.mthli.Ninja.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import io.github.mthli.Ninja.Browser.*;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.Unit.BrowserUnit;
import io.github.mthli.Ninja.Unit.IntentUnit;

import java.net.URISyntaxException;

public class NinjaWebView extends WebView implements AlbumController {
    private Context context;
    private int flag = BrowserUnit.FLAG_NINJA;

    private Album album;
    private NinjaWebViewClient webViewClient;
    private NinjaWebChromeClient webChromeClient;
    private NinjaDownloadListener downloadListener;
    private NinjaClickHandler clickHandler;
    private GestureDetector gestureDetector;

    private boolean foreground;
    public boolean isForeground() {
        return foreground;
    }

    private BrowserController browserController = null;
    public BrowserController getBrowserController() {
        return browserController;
    }
    public void setBrowserController(BrowserController browserController) {
        this.browserController = browserController;
        this.album.setBrowserController(browserController);
    }

    public NinjaWebView(Context context) {
        this(context, null);
    }

    public NinjaWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NinjaWebView(Context context, AttributeSet attrs, int defStyle) {
        super(new NinjaContextWrapper(context), attrs, defStyle);

        this.context = new NinjaContextWrapper(context);
        this.foreground = false;

        this.album = new Album(this.context, this, this.browserController);
        this.webViewClient = new NinjaWebViewClient(this);
        this.webChromeClient = new NinjaWebChromeClient(this);
        this.downloadListener = new NinjaDownloadListener(this.context);
        this.clickHandler = new NinjaClickHandler(this);
        this.gestureDetector = new GestureDetector(context, new NinjaGestureListener(this));

        initWebView();
        initWebSettings();
        initPreferences();
        initAlbum();
    }

    private synchronized void initWebView() {
        setAlwaysDrawnWithCacheEnabled(true);
        setAnimationCacheEnabled(true);

        setBackground(null);
        getRootView().setBackground(null);
        setBackgroundColor(context.getResources().getColor(R.color.white));

        setDrawingCacheBackgroundColor(0x00000000);
        setDrawingCacheEnabled(true);

        setFocusable(true);
        setFocusableInTouchMode(true);

        setSaveEnabled(true);
        setScrollbarFadingEnabled(true);

        setWillNotCacheDrawing(false);

        setWebViewClient(webViewClient);
        setWebChromeClient(webChromeClient);
        setDownloadListener(downloadListener);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return false;
            }
        });
    }

    private synchronized void initWebSettings() {
        WebSettings webSettings = getSettings();

        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(context.getCacheDir().toString());
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webSettings.setDefaultTextEncodingName(BrowserUnit.URL_ENCODING);

        webSettings.setGeolocationDatabasePath(context.getFilesDir().toString());

        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
    }

    private synchronized void initPreferences() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        WebSettings webSettings = getSettings();

        webSettings.setBlockNetworkImage(!sp.getBoolean(context.getString(R.string.sp_images), true));
        webSettings.setJavaScriptEnabled(sp.getBoolean(context.getString(R.string.sp_javascript), true));
        webSettings.setJavaScriptCanOpenWindowsAutomatically(sp.getBoolean(context.getString(R.string.sp_javascript), true));
        webSettings.setGeolocationEnabled(sp.getBoolean(context.getString(R.string.sp_location), true));
        webSettings.setSupportMultipleWindows(sp.getBoolean(context.getString(R.string.sp_multiple_windows), true));
        webSettings.setSaveFormData(sp.getBoolean(context.getString(R.string.sp_passwords), true));

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setTextZoom(100);
        webSettings.setUseWideViewPort(true);
    }

    private synchronized void initAlbum() {
        album.setAlbumCover(null);
        album.setAlbumTitle(context.getString(R.string.album_untitled));
        album.setBrowserController(browserController);
    }

    @Override
    public synchronized void loadUrl(String url) {
        if (url == null || url.isEmpty()) {
            Toast.makeText(context, R.string.toast_load_error, Toast.LENGTH_SHORT).show();
            return;
        }
        url = BrowserUnit.queryWrapper(context, url);

        if (url.startsWith(BrowserUnit.URL_SCHEME_MAIL_TO)) {
            Intent intent = IntentUnit.getEmailIntent(MailTo.parse(url));
            context.startActivity(intent);
            reload();
            return;
        } else if (url.startsWith(BrowserUnit.URL_SCHEME_INTENT)) {
            Intent intent;
            try {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                context.startActivity(intent);
            } catch (URISyntaxException u) {}
            return;
        }

        super.loadUrl(url);
        if (browserController != null && foreground) {
            browserController.updateBookmarks();
        }
    }

    @Override
    public int getFlag() {
        return flag;
    }

    @Override
    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public View getAlbumView() {
        return album.getAlbumView();
    }

    @Override
    public void setAlbumCover(Bitmap bitmap) {
        album.setAlbumCover(bitmap);
    }

    @Override
    public void setAlbumTitle(String title) {
        album.setAlbumTitle(title);
    }

    @Override
    public synchronized void activate() {
        requestFocus();
        foreground = true;
        album.activate();
    }

    @Override
    public synchronized void deactivate() {
        clearFocus();
        foreground = false;
        album.deactivate();
    }

    public synchronized void update(int progress) {
        if (foreground) { // TODO: || browserController instanceof HolderService
            browserController.updateProgress(progress);
        }

        // TODO: update albumCover
    }

    public synchronized void update(String title, String url) {
        album.setAlbumTitle(title);
        if (foreground) {
            browserController.updateBookmarks();
            browserController.updateInputBox(url);
        }
    }

    public synchronized void pause() {
        onPause();
        pauseTimers();
    }

    public synchronized void resume() {
        onResume();
        resumeTimers();
    }

    public synchronized void destroy() {
        stopLoading();
        onPause();
        clearHistory();
        setVisibility(GONE);
        removeAllViews();
        destroyDrawingCache();
    }

    public boolean isLoadFinish() {
        return getProgress() >= BrowserUnit.PROGRESS_MAX;
    }

    public void onLongPress() {
        Message click = clickHandler.obtainMessage();
        if (click != null) {
            click.setTarget(clickHandler);
        }
        requestFocusNodeHref(click);
    }
}
