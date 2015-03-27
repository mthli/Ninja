package io.github.mthli.Berries.Unit;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;

public class ViewUnit {
    public static void expand(final View view) {
        view.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetWidth = view.getMeasuredWidth();

        view.getLayoutParams().width = 0;
        view.setVisibility(View.VISIBLE);
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                view.getLayoutParams().width = interpolatedTime == 1 ? ViewGroup.LayoutParams.WRAP_CONTENT : (int)(targetWidth * interpolatedTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration((int) (targetWidth / view.getContext().getResources().getDisplayMetrics().density));
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        view.startAnimation(animation);
    }

    public static void collapse(final View view) {
        final int initialWidth = view.getMeasuredWidth();

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    view.setVisibility(View.GONE);
                } else {
                    view.getLayoutParams().width = initialWidth - (int)(initialWidth * interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration((int) (initialWidth / view.getContext().getResources().getDisplayMetrics().density));
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        view.startAnimation(animation);
    }
}