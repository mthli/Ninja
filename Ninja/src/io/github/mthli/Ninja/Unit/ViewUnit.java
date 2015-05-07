package io.github.mthli.Ninja.Unit;

import android.content.Context;

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
}
