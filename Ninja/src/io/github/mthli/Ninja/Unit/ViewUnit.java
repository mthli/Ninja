package io.github.mthli.Ninja.Unit;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import io.github.mthli.Ninja.R;

public class ViewUnit {
    public static void bound(Context context, View view) {
        int windowWidth = getWindowWidth(context);
        int windowHeight = getWindowHeight(context);
        int statusBarHeight = getStatusBarHeight(context);
        int dimen48dp = context.getResources().getDimensionPixelOffset(R.dimen.layout_height_48dp);

        int widthSpec = View.MeasureSpec.makeMeasureSpec(windowWidth, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(windowHeight - statusBarHeight - dimen48dp, View.MeasureSpec.EXACTLY);

        view.measure(widthSpec, heightSpec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    public static Bitmap capture(View view, float width, float height, boolean scroll, Bitmap.Config config) {
        if (!view.isDrawingCacheEnabled()) {
            view.setDrawingCacheEnabled(true);
        }

        Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, config);
        bitmap.eraseColor(Color.WHITE);

        Canvas canvas = new Canvas(bitmap);
        int left = view.getLeft();
        int top = view.getTop();
        if (scroll) {
            left = view.getScrollX();
            top = view.getScrollY();
        }
        int status = canvas.save();
        canvas.translate(-left, -top);

        float scale = width / view.getWidth();
        canvas.scale(scale, scale, left, top);

        view.draw(canvas);
        canvas.restoreToCount(status);

        Paint alphaPaint = new Paint();
        alphaPaint.setColor(Color.TRANSPARENT);

        canvas.drawRect(0f, 0f, 1f, height, alphaPaint);
        canvas.drawRect(width - 1f, 0f, width, height, alphaPaint);
        canvas.drawRect(0f, 0f, width, 1f, alphaPaint);
        canvas.drawRect(0f, height - 1f, width, height, alphaPaint);
        canvas.setBitmap(null);

        return bitmap;
    }

    public static float dp2px(Context context, float dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    public static void dimBackground(Activity activity, boolean dim, boolean anim) {
        final Window window = activity.getWindow();
        float from;
        float to;

        if (anim) {
            if (dim) {
                from = 1f;
                to = 0.5f;
            } else {
                from = 0.5f;
                to = 1f;
            }

            ValueAnimator animator = ValueAnimator.ofFloat(from, to);
            animator.setDuration(activity.getResources().getInteger(android.R.integer.config_shortAnimTime));
            animator.setInterpolator(new DecelerateInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.alpha = (Float) animation.getAnimatedValue();
                    window.setAttributes(params);
                }
            });
            animator.start();
        } else {
            WindowManager.LayoutParams params = window.getAttributes();
            if (dim) {
                params.alpha = 0.5f;
                window.setAttributes(params);
            } else {
                params.alpha = 1f;
                window.setAttributes(params);
            }
        }
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int getWindowHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public static int getWindowWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }
}