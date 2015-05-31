package com.wangjie.shadowviewhelper;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 5/25/15.
 */
public class ShadowViewHelper {
    private ShadowProperty shadowProperty;
    private View view;
    private ShadowViewDrawable shadowViewDrawable;
    private int color;
    private float rx;
    private float ry;

    public static ShadowViewHelper bindShadowHelper(ShadowProperty shadowProperty, View view) {
        return new ShadowViewHelper(shadowProperty, view, Color.TRANSPARENT, 0, 0);
    }

    public static ShadowViewHelper bindShadowHelper(ShadowProperty shadowProperty, View view, int color) {
        return new ShadowViewHelper(shadowProperty, view, color, 0, 0);
    }

    public static ShadowViewHelper bindShadowHelper(ShadowProperty shadowProperty, View view, float rx, float ry) {
        return new ShadowViewHelper(shadowProperty, view, Color.TRANSPARENT, rx, ry);
    }

    public static ShadowViewHelper bindShadowHelper(ShadowProperty shadowProperty, View view, int color, float rx, float ry) {
        return new ShadowViewHelper(shadowProperty, view, color, rx, ry);
    }

    private ShadowViewHelper(ShadowProperty shadowProperty, View view, int color, float rx, float ry) {
        this.shadowProperty = shadowProperty;
        this.view = view;
        this.color = color;
        this.rx = rx;
        this.ry = ry;
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        int shadowOffset = shadowProperty.getShadowOffset();
        view.setPadding(view.getPaddingLeft() + shadowOffset, view.getPaddingTop() + shadowOffset, view.getPaddingRight() + shadowOffset, view.getPaddingBottom() + shadowOffset);

        shadowViewDrawable = new ShadowViewDrawable(shadowProperty, color, rx, ry);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                shadowViewDrawable.setBounds(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(shadowViewDrawable);
        } else {
            view.setBackground(shadowViewDrawable);
        }


    }

    public ShadowViewDrawable getShadowViewDrawable() {
        return shadowViewDrawable;
    }

    public View getView() {
        return view;
    }

    public ShadowProperty getShadowProperty() {
        return shadowProperty;
    }
}
