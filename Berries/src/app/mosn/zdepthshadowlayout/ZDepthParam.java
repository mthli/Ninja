package app.mosn.zdepthshadowlayout;

import android.content.Context;
import android.graphics.Color;


public class ZDepthParam {

    public int mAlphaTopShadow; // alpha to black
    public int mAlphaBottomShadow; // alpha to black

    public float mOffsetYTopShadowPx; // px
    public float mOffsetYBottomShadowPx; // px

    public float mBlurTopShadowPx; // px
    public float mBlurBottomShadowPx; // px

    public void initZDepth(Context context, ZDepth zDepth) {
        mAlphaTopShadow = zDepth.getAlphaTopShadow();
        mAlphaBottomShadow = zDepth.getAlphaBottomShadow();
        mOffsetYTopShadowPx = zDepth.getOffsetYTopShadowPx(context);
        mOffsetYBottomShadowPx = zDepth.getOffsetYBottomShadowPx(context);
        mBlurTopShadowPx = zDepth.getBlurTopShadowPx(context);
        mBlurBottomShadowPx = zDepth.getBlurBottomShadowPx(context);
    }

    public int getColorTopShadow() {
        return Color.argb(mAlphaTopShadow, 0, 0, 0);
    }

    public int getColorBottomShadow() {
        return Color.argb(mAlphaBottomShadow, 0, 0, 0);
    }
}
