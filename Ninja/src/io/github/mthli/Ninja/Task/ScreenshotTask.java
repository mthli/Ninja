package io.github.mthli.Ninja.Task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.Unit.BrowserUnit;
import io.github.mthli.Ninja.Unit.ViewUnit;
import io.github.mthli.Ninja.View.NinjaToast;
import io.github.mthli.Ninja.View.NinjaWebView;

public class ScreenshotTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private NinjaWebView webView;
    private String path = null;

    public ScreenshotTask(Context context, NinjaWebView webView) {
        this.context = context;
        this.webView = webView;
    }

    @Override
    protected void onPreExecute() {
        NinjaToast.show(context, R.string.toast_start_screenshot);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        int windowWidth = ViewUnit.getWindowWidth(context);
        float contentHeight = webView.getContentHeight() * ViewUnit.getDensity(context);
        Bitmap bitmap = ViewUnit.capture(webView, windowWidth, contentHeight, false, Bitmap.Config.ARGB_8888);
        path = BrowserUnit.screenshot(context, bitmap, webView.getTitle());
        return path != null && !path.isEmpty();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            NinjaToast.show(context, context.getString(R.string.toast_screenshot_successful) + " " + path);
        } else {
            NinjaToast.show(context, R.string.toast_screenshot_failed);
        }
    }
}
