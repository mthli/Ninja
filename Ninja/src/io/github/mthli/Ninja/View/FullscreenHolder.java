package io.github.mthli.Ninja.View;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class FullscreenHolder extends FrameLayout {
    public FullscreenHolder(Context context) {
        super(context);
        this.setBackgroundColor(context.getResources().getColor(android.R.color.black));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
