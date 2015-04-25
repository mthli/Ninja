package io.github.mthli.Ninja.Browser;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class NinjaGestureListener extends GestureDetector.SimpleOnGestureListener {
    private NinjaView ninjaView;
    private boolean longPress = true;

    public NinjaGestureListener(NinjaView ninjaView) {
        super();
        this.ninjaView = ninjaView;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (longPress) {
            ninjaView.onLongPress();
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
