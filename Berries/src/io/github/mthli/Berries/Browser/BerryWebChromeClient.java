package io.github.mthli.Berries.Browser;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.view.View;
import android.webkit.*;

public class BerryWebChromeClient extends WebChromeClient {
    private Context context;

    public BerryWebChromeClient(Context context) {
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
    public void onRequestFocus (WebView view) {
        // TODO
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        // TODO
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        // TODO
        return true;
    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
        // TODO
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        // TODO
        return true;
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        // TODO
        return true;
    }
}
