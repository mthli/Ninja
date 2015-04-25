package io.github.mthli.Ninja.Browser;

import android.content.Context;
import android.webkit.DownloadListener;
import io.github.mthli.Ninja.Unit.BrowserUnit;

public class NinjaDownloadListener implements DownloadListener {
    private Context context;

    public NinjaDownloadListener(Context context) {
        super();
        this.context = context;
    }

    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
        BrowserUnit.download(context, url, contentDisposition, mimeType);
    }
}
