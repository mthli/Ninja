package io.github.mthli.Berries.Browser;

import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.*;
import io.github.mthli.Berries.Database.Record;

public class BerryWebViewClient extends WebViewClient {
    private Berry berry;
    private Context context;
    private Record record;
    private BrowserController controller;

    private boolean finish = false;
    public boolean isFinish() {
        return finish;
    }

    public BerryWebViewClient(Berry berry) {
        super();
        this.berry = berry;
        this.context = berry.getContext();
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        System.out.println("onPageStarted()");

        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        System.out.println("onPageFinished()");

        super.onPageFinished(view, url);
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        System.out.println("onScaleChanged()");

        super.onScaleChanged(view, oldScale, newScale);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        System.out.println("shouldOverrideUrlLoading()");

        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        System.out.println("shouldInterceptRequest()");

        return super.shouldInterceptRequest(view, request);
    }
}
