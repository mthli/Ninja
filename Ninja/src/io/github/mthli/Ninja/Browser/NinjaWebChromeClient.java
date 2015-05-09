package io.github.mthli.Ninja.Browser;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.webkit.*;
import io.github.mthli.Ninja.View.NinjaWebView;

public class NinjaWebChromeClient extends WebChromeClient {
    private NinjaWebView ninjaWebView;
    private Context context;

    public NinjaWebChromeClient(NinjaWebView ninjaWebView) {
        super();
        this.ninjaWebView = ninjaWebView;
        this.context = ninjaWebView.getContext();
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        ninjaWebView.getBrowserController().onCreateView(view, resultMsg);
        return isUserGesture;
    }

    @Override
    public void onCloseWindow(WebView view) {
        super.onCloseWindow(view);
    }

    @Override
    public void onProgressChanged(WebView view, int progress) {
        super.onProgressChanged(view, progress);
        ninjaWebView.update(progress);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        ninjaWebView.update(title, view.getUrl());
    }

    /**
     * TODO: ?support this method
     * @link http://developer.android.com/reference/android/webkit/WebChromeClient.html#onGeolocationPermissionsShowPrompt%28java.lang.String,%20android.webkit.GeolocationPermissions.Callback%29
     * @param origin
     * @param callback
     */
    @Override
    public void onGeolocationPermissionsShowPrompt (String origin, GeolocationPermissions.Callback callback) {
        super.onGeolocationPermissionsShowPrompt(origin, callback);
    }

    /**
     * TODO: ?support this method
     * @link http://developer.android.com/reference/android/webkit/WebChromeClient.html#onShowCustomView(android.view.View,%20int,%20android.webkit.WebChromeClient.CustomViewCallback)
     */
    @Deprecated
    @Override
    public void onShowCustomView(View view, int requestedOrientation, WebChromeClient.CustomViewCallback callback) {
        super.onShowCustomView(view, requestedOrientation, callback);
    }

    /**
     * TODO: ?support this method
     * @link http://developer.android.com/reference/android/webkit/WebChromeClient.html#onShowCustomView(android.view.View,%20android.webkit.WebChromeClient.CustomViewCallback)
     */
    @Override
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        super.onShowCustomView(view, callback);
    }

    /**
     * TODO: ?support this method
     * @link http://developer.android.com/reference/android/webkit/WebChromeClient.html#onHideCustomView()
     */
    @Override
    public void onHideCustomView() {
        super.onHideCustomView();
    }
}
