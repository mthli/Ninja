package io.github.mthli.Ninja.Browser;

import android.view.GestureDetector;
import android.view.MotionEvent;
import io.github.mthli.Ninja.View.NinjaWebView;

public class NinjaGestureListener extends GestureDetector.SimpleOnGestureListener {
    private NinjaWebView webView;
    private boolean longPress = true;

    public NinjaGestureListener(NinjaWebView webView) {
        super();
        this.webView = webView;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (longPress) {
            webView.onLongPress();
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
