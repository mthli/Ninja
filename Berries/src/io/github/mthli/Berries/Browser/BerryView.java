package io.github.mthli.Berries.Browser;

import android.content.Context;
import android.webkit.WebSettings;
import android.webkit.WebView;
import io.github.mthli.Berries.Database.Record;

public class BerryView {
    private Context context;
    public Context getContext() {
        return context;
    }

    private Record record;
    public Record getRecord() {
        return record;
    }

    private boolean incognito;
    public boolean isIncognito() {
        return incognito;
    }

    private boolean foreground;
    public boolean isForeground() {
        return foreground;
    }
    public void setForeground(boolean foreground) {
        this.foreground = foreground;
    }

    private WebView webView;
    private WebSettings webSettings;
    private BerryWebViewClient webViewClient;
    private BerryWebChromeClient webChromeClient;

    public BerryView(Context context, Record record) {
        this.context = context;
        this.record = record;

        this.initWebView();
    }

    private void initWebView() {
        // TODO
    }

    public synchronized void onPause() {
        if (webView != null) {
            webView.onPause();
        }
    }

    public synchronized void onResume() {
        if (webView != null) {
            webView.onResume();
        }
    }

    public synchronized void finish() {
        if (webView != null) {
            // TODO
        }
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
            return 100;
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
