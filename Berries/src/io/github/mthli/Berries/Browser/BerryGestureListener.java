package io.github.mthli.Berries.Browser;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class BerryGestureListener extends GestureDetector.SimpleOnGestureListener {
    private boolean longPress = true;

    private BrowserController controller;
    public BrowserController getController() {
        return controller;
    }
    public void setController(BrowserController controller) {
        this.controller = controller;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (longPress) {
            // TODO
        }
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        longPress = false;
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        longPress = true;
    }
}
