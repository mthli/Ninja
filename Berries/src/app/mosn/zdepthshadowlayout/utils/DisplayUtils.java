package app.mosn.zdepthshadowlayout.utils;

import android.content.Context;

public class DisplayUtils {

    public static int convertDpToPx(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
