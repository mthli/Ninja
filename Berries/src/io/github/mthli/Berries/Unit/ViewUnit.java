package io.github.mthli.Berries.Unit;

import android.content.Context;
import android.view.View;

public class ViewUnit {
    public static void expand(final View view) {
        // TODO
    }

    public static void collapse(final View view) {
        // TODO
    }

    public static float getElevation(Context context, int degree) {
        return context.getResources().getDisplayMetrics().density * degree;
    }
}