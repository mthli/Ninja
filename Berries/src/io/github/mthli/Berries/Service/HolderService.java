package io.github.mthli.Berries.Service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;
import io.github.mthli.Berries.Browser.BerryContainer;
import io.github.mthli.Berries.Browser.BerryContextWrapper;
import io.github.mthli.Berries.Browser.BerryView;
import io.github.mthli.Berries.Database.Record;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Unit.IntentUnit;
import io.github.mthli.Berries.Unit.NotificationUnit;
import io.github.mthli.Berries.Unit.PreferenceUnit;
import io.github.mthli.Berries.Unit.RecordUnit;

public class HolderService extends Service {
    private BerryContextWrapper context;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        BerryContainer.clear();
        context = new BerryContextWrapper(this);
        sharedPreferences = getSharedPreferences(PreferenceUnit.NAME, MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (intent.getExtras().getBoolean(IntentUnit.LIST)) {
                // TODO
            }
            if (intent.getExtras().getBoolean(IntentUnit.QUIT)) {
                this.stopSelf();
            }
        } catch (NullPointerException n) {}

        if (BerryContainer.size() < PreferenceUnit.LOAD_LIMIT_MAX_DEFAULT) {
            Record record = RecordUnit.get();
            BerryView view = new BerryView(context, record);
            BerryContainer.add(view);
        } else {
            Toast.makeText(this, R.string.toast_load_limit_max, Toast.LENGTH_SHORT).show();
        }

        int priority = sharedPreferences.getInt(PreferenceUnit.NOTIFICATION_PRIORITY, PreferenceUnit.NOTIFICATION_PRIORITY_DEFAULT);
        boolean sound = sharedPreferences.getBoolean(PreferenceUnit.NOTIFICATION_SOUND, PreferenceUnit.NOTIFICATION_SOUND_DEFAULT);
        Notification.Builder builder = NotificationUnit.getBuilder(this, BerryContainer.size(), 0, priority, sound); // TODO: 0
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE;
        startForeground(NotificationUnit.ID, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        BerryContainer.clear();
        stopForeground(true);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
