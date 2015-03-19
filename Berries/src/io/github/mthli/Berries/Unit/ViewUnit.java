package io.github.mthli.Berries.Unit;

import android.app.Activity;
import android.os.Build;
import android.support.v7.internal.widget.TintImageView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import io.github.mthli.Berries.R;

import java.util.ArrayList;

public class ViewUnit {
    public static void setOverflowColor(final Activity activity, final int color) {
        final String overflowDescription = activity.getString(R.string.abc_action_menu_overflow_description);
        final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        final ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();

        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final ArrayList<View> outViews = new ArrayList<View>();
                decorView.findViewsWithText(
                        outViews,
                        overflowDescription,
                        View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION
                );

                if (outViews.isEmpty()) {
                    return;
                }

                ((TintImageView) outViews.get(0)).setColorFilter(color);
                removeOnGlobalLayoutListener(decorView, this);
            }
        });
    }

    private static void removeOnGlobalLayoutListener(View view, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }
}
