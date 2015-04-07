package io.github.mthli.Berries.Browser;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class BerryGestureListener extends GestureDetector.SimpleOnGestureListener {
    private BerryView berryView;
    private boolean longPress = true;

    public BerryGestureListener(BerryView berryView) {
        super();
        this.berryView = berryView;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (longPress) {
            berryView.onLongPress();
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
