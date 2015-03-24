package io.github.mthli.Berries.Browser;

import io.github.mthli.Berries.Database.Record;

public interface BrowserController {
    public void updateRecord(Record record);

    public void updateProgress(int progress);

    public void updateNotification();

    // TODO
    public void showToolbar();

    // TODO
    public void hideToolbar();

    public boolean isToolbarShowing();

    public boolean isIncognito();

    public void onLongPress();
}
