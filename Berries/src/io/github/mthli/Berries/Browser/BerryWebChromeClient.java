package io.github.mthli.Berries.Browser;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.view.View;
import android.webkit.*;
import io.github.mthli.Berries.Database.Record;

public class BerryWebChromeClient extends WebChromeClient {
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

    public BerryWebChromeClient(Context context) {
        super();
        this.context = context;
    }

    @Override
    public Bitmap getDefaultVideoPoster() {
        // TODO
        return null;
    }

    @Override
    public View getVideoLoadingProgressView() {
        // TODO
        return null;
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        // TODO
        return true;
    }

    @Override
    public void onCloseWindow(WebView view) {
        // TODO
    }

    @Override
    public void onProgressChanged(WebView view, int progress) {
        // TODO
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        // TODO
    }

    @Override
    public void onRequestFocus(WebView view) {
        // TODO
    }
}
