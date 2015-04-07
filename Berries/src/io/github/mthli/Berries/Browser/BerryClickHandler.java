package io.github.mthli.Berries.Browser;

import android.os.Handler;
import android.os.Message;

public class BerryClickHandler extends Handler {
    private BerryView berryView;

    public BerryClickHandler(BerryView berryView) {
        super();
        this.berryView = berryView;
    }

    @Override
    public void handleMessage(Message message) {
        super.handleMessage(message);
        berryView.getController().onLongPress(message.getData().getString("url"));
    }
}
