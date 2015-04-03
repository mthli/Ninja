package io.github.mthli.Berries.Browser;

import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.*;
import io.github.mthli.Berries.R;

public class BerryWebViewClient extends WebViewClient {
    private Berry berry;
    private Context context;

    public BerryWebViewClient(Berry berry) {
        super();
        this.berry = berry;
        this.context = berry.getContext();
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (view.getTitle() == null || view.getTitle().isEmpty()) {
            berry.update(context.getString(R.string.browser_tab_untitled), url);
        } else {
            berry.update(view.getTitle(), url);
        }
        berry.showControlPanel();

        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (view.getTitle() == null || view.getTitle().isEmpty()) {
            berry.update(context.getString(R.string.browser_tab_untitled), url);
        } else {
            berry.update(view.getTitle(), url);
        }

        if (berry.isForeground()) {
            berry.invalidate();
        } else {
            berry.postInvalidate();
        }

        super.onPageFinished(view, url);
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
