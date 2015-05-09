package io.github.mthli.Ninja.Browser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.os.Build;
import android.webkit.*;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.Unit.BrowserUnit;
import io.github.mthli.Ninja.Unit.IntentUnit;
import io.github.mthli.Ninja.View.NinjaWebView;

import java.io.ByteArrayInputStream;
import java.net.URISyntaxException;

public class NinjaWebViewClient extends WebViewClient {
    private NinjaWebView ninjaWebView;
    private Context context;
    private AdBlock adBlock;

    public NinjaWebViewClient(NinjaWebView ninjaWebView) {
        super();

        this.ninjaWebView = ninjaWebView;
        this.context = ninjaWebView.getContext();
        this.adBlock = new AdBlock(ninjaWebView.getContext());
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

        if (view.getTitle() == null || view.getTitle().isEmpty()) {
            ninjaWebView.update(context.getString(R.string.album_untitled), url);
        } else {
            ninjaWebView.update(view.getTitle(), url);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        if (view.getTitle() == null || view.getTitle().isEmpty()) {
            ninjaWebView.update(context.getString(R.string.album_untitled), url);
        } else {
            ninjaWebView.update(view.getTitle(), url);
        }

        if (ninjaWebView.isForeground()) {
            ninjaWebView.invalidate();
        } else {
            ninjaWebView.postInvalidate();
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith(BrowserUnit.URL_SCHEME_MAIL_TO)) {
            Intent intent = IntentUnit.getEmailIntent(MailTo.parse(url));
            context.startActivity(intent);
            view.reload();
            return true;
        } else if (url.startsWith(BrowserUnit.URL_SCHEME_INTENT)) {
            Intent intent;
            try {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                context.startActivity(intent);
                return true;
            } catch (URISyntaxException u) {
                return false;
            }
        }

        return super.shouldOverrideUrlLoading(view, url);
    }

    @Deprecated
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (adBlock.isAd(url)) {
            return new WebResourceResponse(
                    BrowserUnit.MIME_TYPE_TEXT_PLAIN,
                    BrowserUnit.URL_ENCODING,
                    new ByteArrayInputStream("".getBytes())
            );
        }

        return super.shouldInterceptRequest(view, url);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (adBlock.isAd(request.getUrl().toString())) {
                return new WebResourceResponse(
                        BrowserUnit.MIME_TYPE_TEXT_PLAIN,
                        BrowserUnit.URL_ENCODING,
                        new ByteArrayInputStream("".getBytes())
                );
            }
        }

        return super.shouldInterceptRequest(view, request);
    }
}
