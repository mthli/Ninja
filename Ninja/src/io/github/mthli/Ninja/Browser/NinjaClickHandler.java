package io.github.mthli.Ninja.Browser;

import android.os.Handler;
import android.os.Message;

public class NinjaClickHandler extends Handler {
    private NinjaView ninjaView;

    public NinjaClickHandler(NinjaView ninjaView) {
        super();
        this.ninjaView = ninjaView;
    }

    @Override
    public void handleMessage(Message message) {
        super.handleMessage(message);
        ninjaView.getController().onLongPress(message.getData().getString("url"));
    }
}
