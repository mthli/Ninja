package io.github.mthli.Berries.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HolderService extends Service {
    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return -1;
    }

    @Override
    public void onDestroy() {}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
