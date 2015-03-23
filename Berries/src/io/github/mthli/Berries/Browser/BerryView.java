package io.github.mthli.Berries.Browser;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import io.github.mthli.Berries.Database.Record;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Unit.BrowserUnit;
import io.github.mthli.Berries.Unit.PreferenceUnit;

public class BerryView {
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
    public void setForeground(boolean foreground) {
        this.foreground = foreground;
    }

    private AdBlock adBlock; // TODO
    private WebView webView;
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
        this.webViewClient.setController(controller);
        this.webChromeClient.setController(controller);
        this.gestureListener.setController(controller);
    }

    // TODO: Context must have getTheme() and some others.
    public BerryView(Context context, Record record) {
        this.context = context;
        this.record = record;
        this.foreground = false;

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
            private float loction;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (view != null && !view.hasFocus()) {
                    view.requestFocus();
                }

                action = motionEvent.getAction();
                y = motionEvent.getY();
                if (action == MotionEvent.ACTION_DOWN) {
                    loction = y;
                } else if (action == MotionEvent.ACTION_UP) {
                    if ((y - loction) > 10) {
                        controller.showToolbar();
                    } else if ((y - loction) < -10) {
                        controller.hideToolbar();
                    }

                    loction = 0;
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

        SharedPreferences sp = context.getSharedPreferences(PreferenceUnit.NAME, Context.MODE_PRIVATE);

        webSettings.setBlockNetworkImage(
                sp.getBoolean(PreferenceUnit.IMAGES, PreferenceUnit.IMAGES_DEFAULT)
        );

        webSettings.setGeolocationEnabled(
                sp.getBoolean(PreferenceUnit.LOCATION, PreferenceUnit.LOCATION_DEFAULT)
        );

        if (sp.getBoolean(PreferenceUnit.TEXT_REFLOW, PreferenceUnit.TEXT_REFLOW_DEFAULT)) {
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }

        webSettings.setLoadWithOverviewMode(
                sp.getBoolean(PreferenceUnit.OVERVIEW_MODE, PreferenceUnit.OVERVIEW_MODE_DEFAULT)
        );

        webSettings.setSaveFormData(
                sp.getBoolean(PreferenceUnit.SAVE_PASSWORDS, PreferenceUnit.SAVE_PASSWORDS_DEFAULT)
        );

        webSettings.setSupportMultipleWindows(
                sp.getBoolean(PreferenceUnit.MULTIPLE_WINDOWS, PreferenceUnit.MULTIPLE_WINDOWS_DEFAULT)
        );

        webSettings.setTextZoom(
                sp.getInt(PreferenceUnit.TEXT_ZOOM, PreferenceUnit.TEXT_ZOOM_DEFAULT)
        );

        webSettings.setUseWideViewPort(
                sp.getBoolean(PreferenceUnit.WIDE_VIEW_PORT, PreferenceUnit.WIDE_VIEW_PORT_DEFAULT)
        );
    }

    public synchronized void loadUrl(@NonNull String url) {
        if (webView != null && !url.trim().isEmpty()) {
            webView.loadUrl(url.trim());
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

    public synchronized void finish() {
        if (webView != null) {
            webView.stopLoading();
            webView.onPause();
            webView.clearHistory();
            webView.setVisibility(View.GONE);
            webView.removeAllViews();
            webView.destroyDrawingCache();
            webView = null;
        }
    }

    public void setVisibility(int visibility) {
        if (webView != null) {
            webView.setVisibility(visibility);
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
}
