package app.mosn.zdepthshadowlayout.shadow;

import android.graphics.Canvas;

import app.mosn.zdepthshadowlayout.ZDepthParam;


public interface Shadow {
    public void setParameter(ZDepthParam parameter, int left, int top, int right, int bottom);
    public void onDraw(Canvas canvas);
}
