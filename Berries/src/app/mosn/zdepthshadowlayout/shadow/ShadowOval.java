package app.mosn.zdepthshadowlayout.shadow;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

import app.mosn.zdepthshadowlayout.ZDepthParam;

public class ShadowOval implements Shadow {

    private ShapeDrawable mTopShadow;
    private ShapeDrawable mBottomShadow;

    private RectF mRectTopShadow;
    private RectF mRectBottomShadow;

    public ShadowOval() {
        mRectTopShadow = new RectF();
        mRectBottomShadow = new RectF();
        mTopShadow = new ShapeDrawable(new OvalShape());
        mBottomShadow = new ShapeDrawable(new OvalShape());
    }

    @Override
    public void setParameter(ZDepthParam param, int left, int top, int right, int bottom) {
        mRectTopShadow.left   = left;
        mRectTopShadow.top    = top    + param.mOffsetYTopShadowPx;
        mRectTopShadow.right  = right;
        mRectTopShadow.bottom = bottom + param.mOffsetYTopShadowPx;

        mRectBottomShadow.left   = left;
        mRectBottomShadow.top    = top    + param.mOffsetYBottomShadowPx;
        mRectBottomShadow.right  = right;
        mRectBottomShadow.bottom = bottom + param.mOffsetYBottomShadowPx;

        mTopShadow.getPaint().setColor(Color.argb(param.mAlphaTopShadow, 0, 0, 0));
        if (0 < param.mBlurTopShadowPx) {
            mTopShadow.getPaint().setMaskFilter(new BlurMaskFilter(param.mBlurTopShadowPx, BlurMaskFilter.Blur.NORMAL));
        } else {
            mTopShadow.getPaint().setMaskFilter(null);
        }

        mBottomShadow.getPaint().setColor(Color.argb(param.mAlphaBottomShadow, 0, 0, 0));
        if (0 < param.mBlurBottomShadowPx) {
            mBottomShadow.getPaint().setMaskFilter(new BlurMaskFilter(param.mBlurBottomShadowPx, BlurMaskFilter.Blur.NORMAL));
        } else {
            mBottomShadow.getPaint().setMaskFilter(null);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawOval(mRectBottomShadow, mBottomShadow.getPaint());
        canvas.drawOval(mRectTopShadow, mTopShadow.getPaint());
    }
}
