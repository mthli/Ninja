package io.github.mthli.Berries.Browser;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.webkit.*;

public class BerryWebChromeClient extends WebChromeClient {
    private BerryView berryView;
    private Context context;

    public BerryWebChromeClient(BerryView berryView) {
        super();
        this.berryView = berryView;
        this.context = berryView.getContext();
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        berryView.getController().onCreateView(view, berryView.isIncognito(), resultMsg);
        return isUserGesture;
    }

    @Override
    public void onCloseWindow(WebView view) {
        super.onCloseWindow(view);
    }

    @Override
    public void onProgressChanged(WebView view, int progress) {
        berryView.update(progress); // TODO
        super.onProgressChanged(view, progress);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        berryView.update(title, view.getUrl());
        super.onReceivedTitle(view, title);
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
