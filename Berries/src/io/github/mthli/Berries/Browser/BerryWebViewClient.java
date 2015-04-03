package io.github.mthli.Berries.Browser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.webkit.*;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Unit.BrowserUnit;
import io.github.mthli.Berries.Unit.IntentUnit;

import java.io.ByteArrayInputStream;
import java.net.URISyntaxException;

public class BerryWebViewClient extends WebViewClient {
    private Berry berry;
    private Context context;
    private AdBlock adBlock;

    public BerryWebViewClient(Berry berry) {
        super();
        this.berry = berry;
        this.context = berry.getContext();
        this.adBlock = new AdBlock(berry.getContext());
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
        if (berry.isIncognito()) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        if (url.startsWith(BrowserUnit.URL_SCHEME_ABOUT)) {
            return super.shouldOverrideUrlLoading(view, url);
        } else if (url.startsWith(BrowserUnit.URL_SCHEME_MAIL_TO)) {
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
                    BrowserUnit.URL_TYPE_TEXT_PLAIN,
                    BrowserUnit.URL_ENCODING,
                    new ByteArrayInputStream("".getBytes())
            );
        }

        return super.shouldInterceptRequest(view, url);
    }
}
