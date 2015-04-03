package io.github.mthli.Berries.Browser;

import android.content.Context;
import android.os.Message;
import android.view.View;
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
        berry.getController().onCreateView(view, berry.isIncognito(), resultMsg);
        return isUserGesture;
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

    /**
     * TODO: support this method
     * @link http://developer.android.com/reference/android/webkit/WebChromeClient.html#onShowCustomView(android.view.View,%20int,%20android.webkit.WebChromeClient.CustomViewCallback)
     */
    @Deprecated
    @Override
    public void onShowCustomView(View view, int requestedOrientation, WebChromeClient.CustomViewCallback callback) {
        super.onShowCustomView(view, requestedOrientation, callback);
    }

    /**
     * TODO: support this method
     * @link http://developer.android.com/reference/android/webkit/WebChromeClient.html#onShowCustomView(android.view.View,%20android.webkit.WebChromeClient.CustomViewCallback)
     */
    @Override
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        super.onShowCustomView(view, callback);
    }

    /**
     * TODO: support this method
     * @link http://developer.android.com/reference/android/webkit/WebChromeClient.html#onHideCustomView()
     */
    @Override
    public void onHideCustomView() {
        super.onHideCustomView();
    }
}
