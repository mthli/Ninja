package io.github.mthli.Berries.Browser;

import android.webkit.DownloadListener;

public class BerryDownloadListener implements DownloadListener {
    private BerryView berryView;

    public BerryDownloadListener(BerryView berryView) {
        super();
        this.berryView = berryView;
    }

    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
        // TODO
    }
}
