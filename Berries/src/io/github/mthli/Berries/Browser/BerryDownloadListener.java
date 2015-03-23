package io.github.mthli.Berries.Browser;

import android.content.Context;
import android.webkit.DownloadListener;

public class BerryDownloadListener implements DownloadListener {
    private Context context;

    public BerryDownloadListener(Context context) {
        super();

        this.context = context;
    }

    public void onDownloadStart (String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
        // TODO
    }
}
