package io.github.mthli.Ninja.Browser;

import android.os.Handler;
import android.os.Message;
import io.github.mthli.Ninja.View.NinjaWebView;

public class NinjaClickHandler extends Handler {
    private NinjaWebView ninjaWebView;

    public NinjaClickHandler(NinjaWebView ninjaWebView) {
        super();
        this.ninjaWebView = ninjaWebView;
    }

    @Override
    public void handleMessage(Message message) {
        super.handleMessage(message);
        ninjaWebView.getBrowserController().onLongPress(message.getData().getString("url"));
    }
}
