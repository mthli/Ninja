package io.github.mthli.Ninja.Unit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

    public static Bitmap capture(View view, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.WHITE);

        Canvas canvas = new Canvas(bitmap);
        int left = view.getScrollX();
        int top = view.getScrollY();
        int status = canvas.save();
        canvas.translate(-left, -top);

        float scale = width / (float) view.getWidth();
        canvas.scale(scale, scale, left, top);

        view.draw(canvas);
        canvas.restoreToCount(status);

        Paint alphaPaint = new Paint();
        alphaPaint.setColor(Color.TRANSPARENT);

        canvas.drawRect(0, 0, 1, height, alphaPaint);
        canvas.drawRect(width - 1, 0, width, height, alphaPaint);
        canvas.drawRect(0, 0, width, 1, alphaPaint);
        canvas.drawRect(0, height - 1, width, height, alphaPaint);
        canvas.setBitmap(null);

        return bitmap;
    }
}
