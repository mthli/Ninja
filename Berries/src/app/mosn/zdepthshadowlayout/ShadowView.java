package app.mosn.zdepthshadowlayout;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;


import app.mosn.zdepthshadowlayout.shadow.Shadow;
import app.mosn.zdepthshadowlayout.shadow.ShadowOval;
import app.mosn.zdepthshadowlayout.shadow.ShadowRect;


public class ShadowView extends View {
    protected static final String TAG = "ShadowView";

    protected static final String ANIM_PROPERTY_ALPHA_TOP_SHADOW = "alphaTopShadow";
    protected static final String ANIM_PROPERTY_ALPHA_BOTTOM_SHADOW = "alphaBottomShadow";
    protected static final String ANIM_PROPERTY_OFFSET_TOP_SHADOW = "offsetTopShadow";
    protected static final String ANIM_PROPERTY_OFFSET_BOTTOM_SHADOW = "offsetBottomShadow";
    protected static final String ANIM_PROPERTY_BLUR_TOP_SHADOW = "blurTopShadow";
    protected static final String ANIM_PROPERTY_BLUR_BOTTOM_SHADOW = "blurBottomShadow";

    protected Shadow mShadow;
    protected ZDepthParam mZDepthParam;
    protected int mZDepthPaddingLeft;
    protected int mZDepthPaddingTop;
    protected int mZDepthPaddingRight;
    protected int mZDepthPaddingBottom;
    protected long mZDepthAnimDuration;
    protected boolean mZDepthDoAnimation;

    protected ShadowView(Context context) {
        this(context, null);
        init();
    }

    protected ShadowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    protected ShadowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected void init() {
        setWillNotDraw(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    protected void setZDepthDoAnimation(boolean doAnimation) {
        mZDepthDoAnimation = doAnimation;
    }

    protected void setZDepthAnimDuration(long duration) {
        mZDepthAnimDuration = duration;
    }

    protected void setZDepthPaddingLeft(int zDepthPaddingLeftValue) {
        ZDepth zDepth = getZDepthWithAttributeValue(zDepthPaddingLeftValue);
        mZDepthPaddingLeft = measureZDepthPadding(zDepth);
    }

    protected void setZDepthPaddingTop(int zDepthPaddingTopValue) {
        ZDepth zDepth = getZDepthWithAttributeValue(zDepthPaddingTopValue);
        mZDepthPaddingTop = measureZDepthPadding(zDepth);
    }

    protected void setZDepthPaddingRight(int zDepthPaddingRightValue) {
        ZDepth zDepth = getZDepthWithAttributeValue(zDepthPaddingRightValue);
        mZDepthPaddingRight = measureZDepthPadding(zDepth);
    }

    protected void setZDepthPaddingBottom(int zDepthPaddingBottomValue) {
        ZDepth zDepth = getZDepthWithAttributeValue(zDepthPaddingBottomValue);
        mZDepthPaddingBottom = measureZDepthPadding(zDepth);
    }

    protected int measureZDepthPadding(ZDepth zDepth) {
        float maxAboveBlurRadius = zDepth.getBlurTopShadowPx(getContext());
        float maxAboveOffset     = zDepth.getOffsetYTopShadowPx(getContext());
        float maxBelowBlurRadius = zDepth.getBlurBottomShadowPx(getContext());
        float maxBelowOffset     = zDepth.getOffsetYBottomShadowPx(getContext());

        float maxAboveSize = maxAboveBlurRadius + maxAboveOffset;
        float maxBelowSize = maxBelowBlurRadius + maxBelowOffset;

        return (int) Math.max(maxAboveSize, maxBelowSize);
    }

    protected int getZDepthPaddingLeft() {
        return mZDepthPaddingLeft;
    }

    protected int getZDepthPaddingTop() {
        return mZDepthPaddingTop;
    }

    protected int getZDepthPaddingRight() {
        return mZDepthPaddingRight;
    }

    protected int getZDepthPaddingBottom() {
        return mZDepthPaddingBottom;
    }

    protected void setShape(int shape) {
        switch (shape) {
            case ZDepthShadowLayout.SHAPE_RECT:
                mShadow = new ShadowRect();
                break;

            case ZDepthShadowLayout.SHAPE_OVAL:
                mShadow = new ShadowOval();
                break;

            default:
                throw new IllegalArgumentException("unknown shape value.");
        }
    }

    protected void setZDepth(int zDepthValue) {
        ZDepth zDepth = getZDepthWithAttributeValue(zDepthValue);
        setZDepth(zDepth);
    }

    protected void setZDepth(ZDepth zDepth) {
        mZDepthParam = new ZDepthParam();
        mZDepthParam.initZDepth(getContext(), zDepth);
    }

    private ZDepth getZDepthWithAttributeValue(int zDepthValue) {
        switch (zDepthValue) {
            case 0: return ZDepth.Depth0;
            case 1: return ZDepth.Depth1;
            case 2: return ZDepth.Depth2;
            case 3: return ZDepth.Depth3;
            case 4: return ZDepth.Depth4;
            case 5: return ZDepth.Depth5;
            default: throw new IllegalArgumentException("unknown zDepth value.");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);

        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        switch (wMode) {
            case MeasureSpec.EXACTLY:
                // NOP
                break;

            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                wSize = 0;
                break;
        }

        switch (hMode) {
            case MeasureSpec.EXACTLY:
                // NOP
                break;

            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                hSize = 0;
                break;
        }

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(wSize, wMode),
                MeasureSpec.makeMeasureSpec(hSize, hMode));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int parentWidth  = (right - left);
        int parentHeight = (bottom - top);

        mShadow.setParameter(mZDepthParam,
                mZDepthPaddingLeft,
                mZDepthPaddingTop,
                parentWidth  - mZDepthPaddingRight,
                parentHeight - mZDepthPaddingBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mShadow.onDraw(canvas);
    }

    protected void changeZDepth(ZDepth zDepth) {

        int   newAlphaTopShadow      = zDepth.getAlphaTopShadow();
        int   newAlphaBottomShadow   = zDepth.getAlphaBottomShadow();
        float newOffsetYTopShadow    = zDepth.getOffsetYTopShadowPx(getContext());
        float newOffsetYBottomShadow = zDepth.getOffsetYBottomShadowPx(getContext());
        float newBlurTopShadow       = zDepth.getBlurTopShadowPx(getContext());
        float newBlurBottomShadow    = zDepth.getBlurBottomShadowPx(getContext());

        if (!mZDepthDoAnimation) {
            mZDepthParam.mAlphaTopShadow        = newAlphaTopShadow;
            mZDepthParam.mAlphaBottomShadow     = newAlphaBottomShadow;
            mZDepthParam.mOffsetYTopShadowPx    = newOffsetYTopShadow;
            mZDepthParam.mOffsetYBottomShadowPx = newOffsetYBottomShadow;
            mZDepthParam.mBlurTopShadowPx       = newBlurTopShadow;
            mZDepthParam.mBlurBottomShadowPx    = newBlurBottomShadow;

            mShadow.setParameter(mZDepthParam,
                    mZDepthPaddingLeft,
                    mZDepthPaddingTop,
                    getWidth() - mZDepthPaddingRight,
                    getHeight() - mZDepthPaddingBottom);
            invalidate();
            return;
        }

        int   nowAlphaTopShadow      = mZDepthParam.mAlphaTopShadow;
        int   nowAlphaBottomShadow   = mZDepthParam.mAlphaBottomShadow;
        float nowOffsetYTopShadow    = mZDepthParam.mOffsetYTopShadowPx;
        float nowOffsetYBottomShadow = mZDepthParam.mOffsetYBottomShadowPx;
        float nowBlurTopShadow       = mZDepthParam.mBlurTopShadowPx;
        float nowBlurBottomShadow    = mZDepthParam.mBlurBottomShadowPx;

        PropertyValuesHolder alphaTopShadowHolder     = PropertyValuesHolder.ofInt(ANIM_PROPERTY_ALPHA_TOP_SHADOW,
                nowAlphaTopShadow,
                newAlphaTopShadow);
        PropertyValuesHolder alphaBottomShadowHolder  = PropertyValuesHolder.ofInt(ANIM_PROPERTY_ALPHA_BOTTOM_SHADOW,
                nowAlphaBottomShadow,
                newAlphaBottomShadow);
        PropertyValuesHolder offsetTopShadowHolder    = PropertyValuesHolder.ofFloat(ANIM_PROPERTY_OFFSET_TOP_SHADOW,
                nowOffsetYTopShadow,
                newOffsetYTopShadow);
        PropertyValuesHolder offsetBottomShadowHolder = PropertyValuesHolder.ofFloat(ANIM_PROPERTY_OFFSET_BOTTOM_SHADOW,
                nowOffsetYBottomShadow,
                newOffsetYBottomShadow);
        PropertyValuesHolder blurTopShadowHolder      = PropertyValuesHolder.ofFloat(ANIM_PROPERTY_BLUR_TOP_SHADOW,
                nowBlurTopShadow,
                newBlurTopShadow);
        PropertyValuesHolder blurBottomShadowHolder   = PropertyValuesHolder.ofFloat(ANIM_PROPERTY_BLUR_BOTTOM_SHADOW,
                nowBlurBottomShadow,
                newBlurBottomShadow);

        ValueAnimator anim = ValueAnimator
                .ofPropertyValuesHolder(
                        alphaTopShadowHolder,
                        alphaBottomShadowHolder,
                        offsetTopShadowHolder,
                        offsetBottomShadowHolder,
                        blurTopShadowHolder,
                        blurBottomShadowHolder);
        anim.setDuration(mZDepthAnimDuration);
        anim.setInterpolator(new LinearInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int   alphaTopShadow     = (Integer) animation.getAnimatedValue(ANIM_PROPERTY_ALPHA_TOP_SHADOW);
                int   alphaBottomShadow  = (Integer) animation.getAnimatedValue(ANIM_PROPERTY_ALPHA_BOTTOM_SHADOW);
                float offsetTopShadow    = (Float) animation.getAnimatedValue(ANIM_PROPERTY_OFFSET_TOP_SHADOW);
                float offsetBottomShadow = (Float) animation.getAnimatedValue(ANIM_PROPERTY_OFFSET_BOTTOM_SHADOW);
                float blurTopShadow      = (Float) animation.getAnimatedValue(ANIM_PROPERTY_BLUR_TOP_SHADOW);
                float blurBottomShadow   = (Float) animation.getAnimatedValue(ANIM_PROPERTY_BLUR_BOTTOM_SHADOW);

                mZDepthParam.mAlphaTopShadow = alphaTopShadow;
                mZDepthParam.mAlphaBottomShadow = alphaBottomShadow;
                mZDepthParam.mOffsetYTopShadowPx = offsetTopShadow;
                mZDepthParam.mOffsetYBottomShadowPx = offsetBottomShadow;
                mZDepthParam.mBlurTopShadowPx = blurTopShadow;
                mZDepthParam.mBlurBottomShadowPx = blurBottomShadow;

                mShadow.setParameter(mZDepthParam,
                        mZDepthPaddingLeft,
                        mZDepthPaddingTop,
                        getWidth() - mZDepthPaddingRight,
                        getHeight() - mZDepthPaddingBottom);

                invalidate();
             }
         });
        anim.start();
    }
}
