package io.github.mthli.Berries.Network;

import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BerryWebViewClient extends WebViewClient {
    private Context context;

    public BerryWebViewClient(Context context) {
        this.context = context;
    }

    @Deprecated
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        // TODO
        return null;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        // TODO
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        // TODO
    }

    // TODO
}
