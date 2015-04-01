package io.github.mthli.Berries.Unit;

import android.content.Context;

public class ViewUnit {
    public static final int WIDTH_MAX = 128;
    public static final int WIDTH_MIN = 48;

    public static float dp2px(Context context, int dp) {
        return context.getResources().getDisplayMetrics().density * dp;
    }

    public static float dp2px(Context context, float dp) {
        return context.getResources().getDisplayMetrics().density * dp;
    }
}