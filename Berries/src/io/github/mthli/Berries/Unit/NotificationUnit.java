package io.github.mthli.Berries.Unit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import io.github.mthli.Berries.Activity.BrowserActivity;
import io.github.mthli.Berries.Browser.BerryView;
import io.github.mthli.Berries.Browser.BrowserContainer;
import io.github.mthli.Berries.Browser.TabController;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Service.HolderService;

public class NotificationUnit {
    public static final int ID = 0x65536;

    public static Notification.Builder getBuilder(Context context) {
        Notification.Builder builder = new Notification.Builder(context);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String priority = sp.getString(context.getString(R.string.sp_notification_priority), context.getString(R.string.setting_summary_notification_priority_default));
        if (priority.equals(context.getString(R.string.setting_summary_notification_priority_default))) {
            builder.setPriority(Notification.PRIORITY_DEFAULT);
        } else if (priority.equals(context.getString(R.string.setting_summary_notification_priority_high))) {
            builder.setPriority(Notification.PRIORITY_HIGH);
        } else if (priority.equals(context.getString(R.string.setting_summary_notification_priority_low))) {
            builder.setPriority(Notification.PRIORITY_LOW);
        } else {
            builder.setPriority(Notification.PRIORITY_DEFAULT);
        }

        // TODO
        int done = 0;
        int total = 0;
        for (TabController controller : BrowserContainer.list()) {
            if (controller instanceof BerryView) {
                if (((BerryView) controller).isLoadFinish()) {
                    done++;
                }
                total++;
            }
        }
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setNumber(total);
        if (done < total) {
            builder.setSmallIcon(R.drawable.ic_notification_berries);
            builder.setContentText(context.getString(R.string.notification_content_loading) + done);
        } else {
            builder.setSmallIcon(R.drawable.ic_notification_done_all);
            builder.setContentText(context.getString(R.string.notification_content_done_all));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(context.getResources().getColor(R.color.blue_500));
        }

        Intent toActivity = new Intent(context, BrowserActivity.class);
        toActivity.putExtra(IntentUnit.PIN, true);
        PendingIntent pin = PendingIntent.getActivity(context, 0, toActivity, 0);
        builder.setContentIntent(pin);

        return builder;
    }

    public static void cancel(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(ID);
    }
}