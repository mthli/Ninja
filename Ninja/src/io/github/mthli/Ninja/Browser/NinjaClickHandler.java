package io.github.mthli.Ninja.Browser;

import android.os.Handler;
import android.os.Message;
import io.github.mthli.Ninja.View.NinjaView;

public class NinjaClickHandler extends Handler {
    private NinjaView ninjaView;

    public NinjaClickHandler(NinjaView ninjaView) {
        super();
        this.ninjaView = ninjaView;
    }

    @Override
    public void handleMessage(Message message) {
        super.handleMessage(message);
        ninjaView.getBrowserController().onLongPress(message.getData().getString("url"));
    }
}
