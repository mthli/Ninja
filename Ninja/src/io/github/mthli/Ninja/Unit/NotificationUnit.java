package io.github.mthli.Ninja.Unit;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import io.github.mthli.Ninja.Activity.BrowserActivity;
import io.github.mthli.Ninja.Browser.AlbumController;
import io.github.mthli.Ninja.Browser.BrowserContainer;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.View.NinjaWebView;

public class NotificationUnit {
    public static final int HOLDER_ID = 0x65536;

    public static Notification.Builder getHBuilder(Context context) {
        Notification.Builder builder = new Notification.Builder(context);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        int priority = Integer.valueOf(sp.getString(context.getString(R.string.sp_notification_priority), "0"));
        if (priority == 0) {
            builder.setPriority(Notification.PRIORITY_DEFAULT);
        } else if (priority == 1) {
            builder.setPriority(Notification.PRIORITY_HIGH);
        } else if (priority == 2) {
            builder.setPriority(Notification.PRIORITY_LOW);
        } else {
            builder.setPriority(Notification.PRIORITY_DEFAULT);
        }

        int total = 0;
        for (AlbumController controller : BrowserContainer.list()) {
            if (controller instanceof NinjaWebView) {
                total++;
            }
        }
        builder.setNumber(total);

        builder.setSmallIcon(R.drawable.ic_notification_ninja);
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setContentText(context.getString(R.string.notification_content_holder));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(context.getResources().getColor(R.color.blue_500));
        }

        Intent toActivity = new Intent(context, BrowserActivity.class);
        PendingIntent pin = PendingIntent.getActivity(context, 0, toActivity, 0);
        builder.setContentIntent(pin);

        return builder;
    }
}
