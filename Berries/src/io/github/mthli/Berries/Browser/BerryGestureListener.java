package io.github.mthli.Berries.Browser;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class BerryGestureListener extends GestureDetector.SimpleOnGestureListener {
    private BrowserController controller;

    private boolean longPress = true;

    public BerryGestureListener(BrowserController controller) {
        super();

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
