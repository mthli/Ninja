package io.github.mthli.Ninja.Browser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.Service.HolderService;
import io.github.mthli.Ninja.Unit.BrowserUnit;
import io.github.mthli.Ninja.Unit.IntentUnit;
import io.github.mthli.Ninja.View.NinjaContextWrapper;

import java.net.URISyntaxException;

public class NinjaView extends WebView implements TabController {
    private Context context;
    private int flag = BrowserUnit.FLAG_BERRY;

    private boolean foreground;
    public boolean isForeground() {
        return foreground;
    }

    private NinjaTab ninjaTab;
    private NinjaWebViewClient webViewClient;
    private NinjaWebChromeClient webChromeClient;
    private NinjaDownloadListener downloadListener;
    private NinjaClickHandler clickHandler;
    private GestureDetector gestureDetector;

    private BrowserController controller  = null;
    public BrowserController getController() {
        return controller;
    }
    public void setController(BrowserController controller) {
        this.controller = controller;
    }

    public NinjaView(Context context) {
        super(new NinjaContextWrapper(context));

        this.context = new NinjaContextWrapper(context);
        this.foreground = false;

        this.ninjaTab = new NinjaTab(this);
        this.webViewClient = new NinjaWebViewClient(this);
        this.webChromeClient = new NinjaWebChromeClient(this);
        this.downloadListener = new NinjaDownloadListener(this.context);
        this.clickHandler = new NinjaClickHandler(this);
        this.gestureDetector = new GestureDetector(context, new NinjaGestureListener(this));

        this.initWebView();
        this.initWebSettings();
        this.initPreferences();
    }

    public NinjaView(Context context, AttributeSet attrs) {
        super(new NinjaContextWrapper(context), attrs);

        this.context = new NinjaContextWrapper(context);
        this.foreground = false;

        this.ninjaTab = new NinjaTab(this);
        this.webViewClient = new NinjaWebViewClient(this);
        this.webChromeClient = new NinjaWebChromeClient(this);
        this.downloadListener = new NinjaDownloadListener(this.context);
        this.clickHandler = new NinjaClickHandler(this);
        this.gestureDetector = new GestureDetector(context, new NinjaGestureListener(this));

        this.initWebView();
        this.initWebSettings();
        this.initPreferences();
    }

    public NinjaView(Context context, AttributeSet attrs, int defStyle) {
        super(new NinjaContextWrapper(context), attrs, defStyle);

        this.context = new NinjaContextWrapper(context);
        this.foreground = false;

        this.ninjaTab = new NinjaTab(this);
        this.webViewClient = new NinjaWebViewClient(this);
        this.webChromeClient = new NinjaWebChromeClient(this);
        this.downloadListener = new NinjaDownloadListener(this.context);
        this.clickHandler = new NinjaClickHandler(this);
        this.gestureDetector = new GestureDetector(context, new NinjaGestureListener(this));

        this.initWebView();
        this.initWebSettings();
        this.initPreferences();
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

    public synchronized void initPreferences() {
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
        if (controller != null && foreground) {
            controller.updateBookmarks();
        }
    }

    @Override
    public synchronized void activate() {
        setVisibility(View.VISIBLE);
        requestFocus();
        foreground = true;
        ninjaTab.activate();
    }

    @Override
    public synchronized void deactivate() {
        clearFocus();
        setVisibility(View.INVISIBLE);
        foreground = false;
        ninjaTab.deactivate();
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
    public View getTabView() {
        return ninjaTab.getView();
    }

    @Override
    public void setTabTitle(String title) {
        ninjaTab.setTitle(title);
    }

    public synchronized void update(String title, String url) {
        setTabTitle(title);
        if (foreground) {
            controller.updateBookmarks();
            controller.updateInputBox(url);
        }
    }

    public synchronized void update(int progress) {
        if (foreground || controller instanceof HolderService) {
            controller.updateProgress(progress);
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
        setVisibility(View.GONE);
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
