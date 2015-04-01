package io.github.mthli.Berries.Browser;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Message;
import android.webkit.*;
import io.github.mthli.Berries.Database.Record;

public class BerryWebViewClient extends WebViewClient {
    private Context context;
    public Context getContext() {
        return context;
    }

    private Record record;
    public Record getRecord() {
        return record;
    }
    public void setRecord(Record record) {
        this.record = record;
    }

    private BrowserController controller;
    public BrowserController getController() {
        return controller;
    }
    public void setController(BrowserController controller) {
        this.controller = controller;
    }

    private boolean finish = false;
    public boolean isFinish() {
        return finish;
    }

    public BerryWebViewClient(Context context) {
        this.context = context;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        // TODO
        finish = false;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        // TODO
        finish = true;
        controller.updateNotification();
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        // TODO
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // TODO
        finish = false;
        return false;
    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        // TODO
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        // TODO
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        // TODO
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        // TODO
        return null;
    }
}
