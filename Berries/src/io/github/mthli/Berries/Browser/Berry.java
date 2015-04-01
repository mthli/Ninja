package io.github.mthli.Berries.Browser;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import io.github.mthli.Berries.Database.Record;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Unit.BrowserUnit;

public class Berry {
    private Context context;
    public Context getContext() {
        return context;
    }

    private Record record;
    public Record getRecord() {
        return record;
    }

    private boolean foreground;
    public boolean isForeground() {
        return foreground;
    }

    // TODO
    private boolean incognito;
    public boolean isIncognito() {
        return incognito;
    }

    private Tab tab;
    public View getTabView() {
        return tab.getView();
    }

    private WebView webView;
    public WebView getWebView() {
        return webView;
    }

    private WebSettings webSettings;
    private BerryWebViewClient webViewClient;
    private BerryWebChromeClient webChromeClient;
    private BerryDownloadListener downloadListener;
    private BerryGestureListener gestureListener;
    private GestureDetector gestureDetector;

    private BrowserController controller;
    public BrowserController getController() {
        return controller;
    }
    public void setController(BrowserController controller) {
        this.controller = controller;
        this.tab.setController(controller);
        this.webViewClient.setController(controller);
        this.webChromeClient.setController(controller);
        this.gestureListener.setController(controller);
    }

    public Berry(Context context, Record record, boolean incognito) {
        this.context = context;
        this.record = record;
        this.foreground = false;
        this.incognito = incognito;

        this.tab = new Tab(this);
        this.webView = new WebView(context);
        this.webSettings = webView.getSettings();
        this.webViewClient = new BerryWebViewClient(context);
        this.webViewClient.setRecord(record);
        this.webChromeClient = new BerryWebChromeClient(context);
        this.webChromeClient.setRecord(record);
        this.downloadListener = new BerryDownloadListener(context);
        this.gestureListener = new BerryGestureListener();
        this.gestureDetector = new GestureDetector(context, gestureListener);

        this.initWebView();
        this.initWebSettings();
        this.initPreferences();
        this.loadUrl(record.getURL());
    }

    private synchronized void initWebView() {
        if (webView == null) {
            return;
        }

        webView.setAlwaysDrawnWithCacheEnabled(true);
        webView.setAnimationCacheEnabled(true);

        webView.setBackground(null);
        webView.getRootView().setBackground(null);
        webView.setBackgroundColor(context.getResources().getColor(R.color.white));

        webView.setDrawingCacheBackgroundColor(0x00000000);
        webView.setDrawingCacheEnabled(true);

        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);

        webView.setSaveEnabled(true);
        webView.setScrollbarFadingEnabled(true);

        webView.setWillNotCacheDrawing(false);

        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(webChromeClient);
        webView.setDownloadListener(new BerryDownloadListener(context));
        webView.setOnTouchListener(new View.OnTouchListener() {
            private int action;
            private float y;
            private float location;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (view != null && !view.hasFocus()) {
                    view.requestFocus();
                }

                action = motionEvent.getAction();
                y = motionEvent.getY();
                if (action == MotionEvent.ACTION_DOWN) {
                    location = y;
                } else if (action == MotionEvent.ACTION_UP) {
                    if ((y - location) > 10) {
                        controller.showControlPanel();
                    } else if ((y - location) < -10) {
                        controller.hideControlPanel();
                    }

                    location = 0;
                }

                gestureDetector.onTouchEvent(motionEvent);

                return false;
            }
        });
    }

    private synchronized void initWebSettings() {
        if (webSettings == null) {
            return;
        }

        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(context.getCacheDir().toString());
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webSettings.setDefaultTextEncodingName(BrowserUnit.ENCODING);

        webSettings.setGeolocationDatabasePath(context.getFilesDir().toString());

        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(true);
    }

    private synchronized void initPreferences() {
        if (webView == null || webSettings == null) {
            return;
        }

        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_name), Context.MODE_PRIVATE);
        webSettings.setBlockNetworkImage(sp.getBoolean(context.getString(R.string.sp_images), true));
        webSettings.setGeolocationEnabled(sp.getBoolean(context.getString(R.string.sp_location), true));
        webSettings.setSaveFormData(sp.getBoolean(context.getString(R.string.sp_passwords), true));

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setTextZoom(100);
        webSettings.setUseWideViewPort(true);
    }

    public synchronized void loadUrl(String url) {
        url = url.trim();
        if (webView != null && !url.isEmpty()) {
            if (url.equals(BrowserUnit.Home)) {
                // TODO
                return;
            }

            webView.loadUrl(url);
        }
    }

    public synchronized void invalidate() {
        if (webView != null) {
            webView.invalidate();
        }
    }

    public synchronized void onPause() {
        if (webView != null) {
            webView.onPause();
        }
    }

    public synchronized void pauseTimers() {
        if (webView != null) {
            webView.pauseTimers();
        }
    }

    public synchronized void onResume() {
        if (webView != null) {
            webView.onResume();
        }
    }

    public synchronized void resumeTimers() {
        if (webView != null) {
            webView.resumeTimers();
        }
    }

    public void requestFocus() {
        if (webView != null && !webView.hasFocus()) {
            webView.requestFocus();
        }
    }

    public void setVisibility(int visibility) {
        if (webView != null) {
            webView.setVisibility(visibility);
        }
    }

    public synchronized void destroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.onPause();
            webView.clearHistory();
            webView.setVisibility(View.GONE);
            webView.removeAllViews();
            webView.destroyDrawingCache();
        }
    }

    public void activate() {
        onResume();
        setVisibility(View.VISIBLE);
        requestFocus();
        foreground = true;

        if (tab != null) {
            tab.activate();
        }
    }

    public void deactivate() {
        onPause();
        setVisibility(View.INVISIBLE);
        foreground = false;

        if (tab != null) {
            tab.deactivate();
        }
    }

    public boolean isShown() {
        return webView != null && webView.isShown();
    }

    public synchronized void stopLoading() {
        if (webView != null) {
            webView.stopLoading();
        }
    }

    public synchronized void reload() {
        if (webView != null) {
            webView.reload();
        }
    }

    public int getProgress() {
        if (webView != null) {
            return webView.getProgress();
        } else {
            return BrowserUnit.PROGRESS_MAX;
        }
    }

    public boolean isFinish() {
        return webViewClient != null && webViewClient.isFinish();
    }

    public synchronized void pageUp(boolean top) {
        if (webView != null) {
            webView.pageUp(top);
        }
    }

    public synchronized void pageDown(boolean bottom) {
        if (webView != null) {
            webView.pageDown(bottom);
        }
    }

    public synchronized void goBack() {
        if (webView != null) {
            webView.goBack();
        }
    }

    public synchronized void goForward() {
        if (webView != null) {
            webView.goForward();
        }
    }

    public boolean canGoBack() {
        return webView != null && webView.canGoBack();
    }

    public boolean canGoForward() {
        return webView != null && webView.canGoForward();
    }

    public synchronized void clearCache(boolean clear) {
        if (webView != null) {
            webView.clearCache(clear);
        }
    }

    public synchronized void clearFormData() {
        if (webView != null) {
            webView.clearFormData();
        }
    }

    public synchronized void clearHistory() {
        if (webView != null) {
            webView.clearHistory();
        }
    }

    public synchronized void clearMatches() {
        if (webView != null) {
            webView.clearMatches();
        }
    }

    public synchronized void clearSslPreferences() {
        if (webView != null) {
            webView.clearSslPreferences();
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        if (!(object instanceof Berry)) {
            return false;
        }

        return this.record.getTime() == ((Berry) object).getRecord().getTime();
    }

    @Override
    public int hashCode() {
        return (int) (this.record.getTime() * 31);
    }
}
