package io.github.mthli.Berries.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import io.github.mthli.Berries.Browser.BerryContainer;
import io.github.mthli.Berries.Browser.BerryContextWrapper;
import io.github.mthli.Berries.Browser.BerryView;
import io.github.mthli.Berries.Database.Record;
import io.github.mthli.Berries.Unit.IntentUnit;

public class HolderService extends Service {
    private BerryContextWrapper context;

    @Override
    public void onCreate() {
        super.onCreate();

        BerryContainer.clear();
        context = new BerryContextWrapper(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Record record = IntentUnit.getRecord(intent);
        BerryView view = new BerryView(context, record);
        BerryContainer.add(view);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        BerryContainer.clear();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
