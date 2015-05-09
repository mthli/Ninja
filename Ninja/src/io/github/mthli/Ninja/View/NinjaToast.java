package io.github.mthli.Ninja.View;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class NinjaToast {
    private static Toast toast;
    private static Handler handler = new Handler();
    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            toast.cancel();
        }
    };

    public static void show(Context context, int stringResId) {
        show(context, context.getString(stringResId));
    }

    public static void show(Context context, String text) {
        handler.removeCallbacks(runnable);
        if (toast != null) {
            toast.setText(text);
        } else {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }
        handler.postDelayed(runnable, 2000);
        toast.show();
    }
}
