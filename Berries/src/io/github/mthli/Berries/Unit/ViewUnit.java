package io.github.mthli.Berries.Unit;

import android.content.Context;
import android.os.Build;
import android.view.View;

public class ViewUnit {
    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static float dp2px(Context context, int dp) {
        return context.getResources().getDisplayMetrics().density * dp;
    }

    public static float dp2px(Context context, float dp) {
        return context.getResources().getDisplayMetrics().density * dp;
    }

    public static void setElevation(Context context, View view, int dp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setElevation(dp2px(context, dp));
        }
    }

    public static void setElevation(Context context, View view, float dp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setElevation(dp2px(context, dp));
        }
    }
}