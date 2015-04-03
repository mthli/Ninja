package io.github.mthli.Berries.Browser;

import android.content.Context;
import android.os.Message;
import android.webkit.*;
import io.github.mthli.Berries.Unit.BrowserUnit;

public class BerryWebChromeClient extends WebChromeClient {
    private Berry berry;
    private Context context;

    private int progress = BrowserUnit.PROGRESS_MIN;
    public boolean isLoadFinish() {
        return progress >= BrowserUnit.PROGRESS_MAX;
    }

    public BerryWebChromeClient(Berry berry) {
        super();
        this.berry = berry;
        this.context = berry.getContext();
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        System.out.println("onCreateWindow()");
        // TODO
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
    }

    @Override
    public void onCloseWindow(WebView view) {
        super.onCloseWindow(view);
    }

    @Override
    public void onProgressChanged(WebView view, int progress) {
        this.progress = progress;
        berry.update(progress);
        super.onProgressChanged(view, progress);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        berry.update(title, view.getUrl());
        super.onReceivedTitle(view, title);
    }
}
