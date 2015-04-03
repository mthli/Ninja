package io.github.mthli.Berries.Browser;

import android.content.Context;
import android.content.SharedPreferences;
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

    private BrowserController controller;
    public BrowserController getController() {
        return controller;
    }
    public void setController(BrowserController controller) {
        this.controller = controller;
    }

    public Berry(Context context, Record record, boolean incognito) {
        this.context = new BerryContextWrapper(context);
        this.record = record;
        this.foreground = false;
        this.incognito = incognito;

        this.tab = new Tab(this);
        this.webView = new WebView(this.context);
        this.webSettings = webView.getSettings();
        this.webViewClient = new BerryWebViewClient(this);
        this.webChromeClient = new BerryWebChromeClient(this);

        this.initWebView();
        this.initWebSettings();
        this.initPreferences();
    }

    private synchronized void initWebView() {
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

        // TODO
        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(webChromeClient);
    }

    private synchronized void initWebSettings() {
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
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_name), Context.MODE_PRIVATE);
        webSettings.setBlockNetworkImage(sp.getBoolean(context.getString(R.string.sp_images), false));
        webSettings.setJavaScriptEnabled(sp.getBoolean(context.getString(R.string.sp_javascript), true));
        webSettings.setGeolocationEnabled(sp.getBoolean(context.getString(R.string.sp_location), true));
        webSettings.setSaveFormData(sp.getBoolean(context.getString(R.string.sp_passwords), true));
        webSettings.setSupportMultipleWindows(sp.getBoolean(context.getString(R.string.sp_multiple_window), true));

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setTextZoom(100);
        webSettings.setUseWideViewPort(true);
    }

    protected synchronized void onPause() {
        webView.onPause();
    }

    protected synchronized void pauseTimers() {
        webView.pauseTimers();
    }

    protected synchronized void onResume() {
        webView.onResume();
    }

    protected synchronized void resumeTimers() {
        webView.resumeTimers();
    }

    protected void requestFocus() {
        if (!webView.hasFocus()) {
            webView.requestFocus();
        }
    }

    protected void clearFocus() {
        if (webView.hasFocus()) {
            webView.clearFocus();
        }
    }

    protected void setVisibility(int visibility) {
        webView.setVisibility(visibility);
    }

    public synchronized void load(Record record) {
        this.record = record;
        webView.loadUrl(record.getURL());
    }

    public synchronized void invalidate() {
        webView.invalidate();
    }

    public synchronized void postInvalidate () {
        webView.postInvalidate();
    }

    public void activate() {
        onResume();
        setVisibility(View.VISIBLE);
        requestFocus();
        foreground = true;
        tab.activate();
    }

    public void deactivate() {
        onPause();
        setVisibility(View.INVISIBLE);
        clearFocus();
        foreground = false;
        tab.deactivate();
    }

    public void update(String title, String url) {
        this.record.setTitle(title);
        this.record.setURL(url);
        tab.update(title, url);
        if (foreground) {
            controller.updateInputBox(url);
        }

        // TODO: History
    }

    public void update(int progress) {
        if (isForeground()) {
            controller.updateProgress(progress);
        }
    }

    public void pause() {
        onPause();
        pauseTimers();
    }

    public void resume() {
        onResume();
        resumeTimers();
    }

    public synchronized void destroy() {
        webView.stopLoading();
        webView.onPause();
        webView.clearHistory();
        webView.setVisibility(View.GONE);
        webView.removeAllViews();
        webView.destroyDrawingCache();
    }

    public void showControlPanel() {
        if (foreground) {
            controller.showControlPanel();
        }
    }

    public void hideControlPanel() {
        if (foreground) {
            controller.hideControlPanel();
        }
    }

    public boolean isControlPanelShowing() {
        return controller.isControlPanelShowing();
    }

    public synchronized void stopLoading() {
        webView.stopLoading();
    }

    public synchronized void reload() {
        webView.reload();
    }

    public boolean isLoadFinish() {
        return webChromeClient.isLoadFinish();
    }

    public synchronized void pageUp(boolean top) {
        webView.pageUp(top);
    }

    public synchronized void pageDown(boolean bottom) {
        webView.pageDown(bottom);
    }

    public synchronized void goBack() {
        webView.goBack();
    }

    public synchronized void goForward() {
        webView.goForward();
    }

    public boolean canGoBack() {
        return webView.canGoBack();
    }

    public boolean canGoForward() {
        return webView.canGoForward();
    }

    public synchronized void clearCache(boolean clear) {
        webView.clearCache(clear);
    }

    public synchronized void clearFormData() {
        webView.clearFormData();
    }

    public synchronized void clearHistory() {
        webView.clearHistory();
    }

    public synchronized void clearMatches() {
        webView.clearMatches();
    }

    public synchronized void clearSslPreferences() {
        webView.clearSslPreferences();
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
