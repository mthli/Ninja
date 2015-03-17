package app.mosn.zdepthshadowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import io.github.mthli.Berries.R;


public class ZDepthShadowLayout extends FrameLayout {
    public static final String TAG = "ZDepthShadowLayout";

    protected static final int DEFAULT_ATTR_SHAPE = 0;
    protected static final int DEFAULT_ATTR_ZDEPTH = 1;
    protected static final int DEFAULT_ATTR_ZDEPTH_PADDING = 5;
    protected static final int DEFAULT_ATTR_ZDEPTH_ANIM_DURATION = 150;
    protected static final boolean DEFAULT_ATTR_ZDEPTH_DO_ANIMATION = true;

    protected static final int SHAPE_RECT = 0;
    protected static final int SHAPE_OVAL = 1;

    protected ShadowView mShadowView;

    protected int mAttrShape;
    protected int mAttrZDepth;
    protected int mAttrZDepthPaddingLeft;
    protected int mAttrZDepthPaddingTop;
    protected int mAttrZDepthPaddingRight;
    protected int mAttrZDepthPaddingBottom;
    protected long mAttrZDepthAnimDuration;
    protected boolean mAttrZDepthDoAnimation;

    public ZDepthShadowLayout(Context context) {
        this(context, null);
    }

    public ZDepthShadowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZDepthShadowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    protected void init(AttributeSet attrs, int defStyle) {
        setClipToPadding(false);

        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ZDepthShadowLayout, defStyle, 0);
        mAttrShape = typedArray.getInt(R.styleable.ZDepthShadowLayout_z_depth_shape, DEFAULT_ATTR_SHAPE);
        mAttrZDepth = typedArray.getInt(R.styleable.ZDepthShadowLayout_z_depth, DEFAULT_ATTR_ZDEPTH);
        mAttrZDepthAnimDuration = typedArray.getInt(R.styleable.ZDepthShadowLayout_z_depth_animDuration, DEFAULT_ATTR_ZDEPTH_ANIM_DURATION);
        mAttrZDepthDoAnimation = typedArray.getBoolean(R.styleable.ZDepthShadowLayout_z_depth_doAnim, DEFAULT_ATTR_ZDEPTH_DO_ANIMATION);

        int attrZDepthPadding = typedArray.getInt(R.styleable.ZDepthShadowLayout_z_depth_padding, -1);
        int attrZDepthPaddingLeft = typedArray.getInt(R.styleable.ZDepthShadowLayout_z_depth_paddingLeft, -1);
        int attrZDepthPaddingTop = typedArray.getInt(R.styleable.ZDepthShadowLayout_z_depth_paddingTop, -1);
        int attrZDepthPaddingRight = typedArray.getInt(R.styleable.ZDepthShadowLayout_z_depth_paddingRight, -1);
        int attrZDepthPaddingBottom = typedArray.getInt(R.styleable.ZDepthShadowLayout_z_depth_paddingBottom, -1);

        if (attrZDepthPadding > -1) {
            mAttrZDepthPaddingLeft   = attrZDepthPadding;
            mAttrZDepthPaddingTop    = attrZDepthPadding;
            mAttrZDepthPaddingRight  = attrZDepthPadding;
            mAttrZDepthPaddingBottom = attrZDepthPadding;
        } else {
            mAttrZDepthPaddingLeft   = attrZDepthPaddingLeft   > -1 ? attrZDepthPaddingLeft   : DEFAULT_ATTR_ZDEPTH_PADDING;
            mAttrZDepthPaddingTop    = attrZDepthPaddingTop    > -1 ? attrZDepthPaddingTop    : DEFAULT_ATTR_ZDEPTH_PADDING;
            mAttrZDepthPaddingRight  = attrZDepthPaddingRight  > -1 ? attrZDepthPaddingRight  : DEFAULT_ATTR_ZDEPTH_PADDING;
            mAttrZDepthPaddingBottom = attrZDepthPaddingBottom > -1 ? attrZDepthPaddingBottom : DEFAULT_ATTR_ZDEPTH_PADDING;
        }

        typedArray.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mShadowView = new ShadowView(getContext());
        mShadowView.setShape(mAttrShape);
        mShadowView.setZDepth(mAttrZDepth);
        mShadowView.setZDepthPaddingLeft(mAttrZDepthPaddingLeft);
        mShadowView.setZDepthPaddingTop(mAttrZDepthPaddingTop);
        mShadowView.setZDepthPaddingRight(mAttrZDepthPaddingRight);
        mShadowView.setZDepthPaddingBottom(mAttrZDepthPaddingBottom);
        mShadowView.setZDepthAnimDuration(mAttrZDepthAnimDuration);
        mShadowView.setZDepthDoAnimation(mAttrZDepthDoAnimation);
        addView(mShadowView, 0);

        int paddingLeft = mShadowView.getZDepthPaddingLeft();
        int paddingTop = mShadowView.getZDepthPaddingTop();
        int paddingRight = mShadowView.getZDepthPaddingRight();
        int paddingBottom = mShadowView.getZDepthPaddingBottom();
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int childCount = getChildCount();

        int maxChildViewWidth = 0;
        int maxChildViewHeight = 0;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (maxChildViewWidth  < child.getMeasuredWidth())  maxChildViewWidth  = child.getMeasuredWidth();
            if (maxChildViewHeight < child.getMeasuredHeight()) maxChildViewHeight = child.getMeasuredHeight();
        }

        // その他の View のうち最も大きいサイズに padding を足して measure を呼び出す
        int paddingLeft = mShadowView.getZDepthPaddingLeft();
        int paddingTop = mShadowView.getZDepthPaddingTop();
        int paddingRight = mShadowView.getZDepthPaddingRight();
        int paddingBottom = mShadowView.getZDepthPaddingBottom();
        maxChildViewWidth  += paddingLeft + paddingRight; // 左右の padding を加算する
        maxChildViewHeight += paddingTop + paddingBottom; // 上下の padding を加算する
        mShadowView.measure(
                MeasureSpec.makeMeasureSpec(maxChildViewWidth,  MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(maxChildViewHeight, MeasureSpec.EXACTLY)
        );
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int width  = right - left;
        int height = bottom - top;
        mShadowView.layout(0, 0, width, height);
    }

    public int getWidthExceptShadow() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    public int getHeightExceptShadow() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    public void changeZDepth(ZDepth zDepth) {
        mShadowView.changeZDepth(zDepth);
    }
}
