package io.github.mthli.Berries.Browser;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.view.View;
import android.webkit.*;
import io.github.mthli.Berries.Database.Record;

public class BerryWebChromeClient extends WebChromeClient {
    private Berry berry;
    private Context context;
    private Record record;
    private BrowserController controller;

    public BerryWebChromeClient(Berry berry) {
        super();
        this.berry = berry;
        this.context = berry.getContext();
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        System.out.println("onCreateWindow()");

        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
    }

    @Override
    public void onCloseWindow(WebView view) {
        System.out.println("onCloseWindow()");

        super.onCloseWindow(view);
    }

    @Override
    public void onProgressChanged(WebView view, int progress) {
        System.out.println("onProgressChanged()");

        super.onProgressChanged(view, progress);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        berry.update(title, view.getUrl());
    }

    @Override
    public void onRequestFocus(WebView view) {
        System.out.println("onRequestFocus()");

        super.onRequestFocus(view);
    }
}
