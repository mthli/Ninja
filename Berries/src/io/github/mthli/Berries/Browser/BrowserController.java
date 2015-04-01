package io.github.mthli.Berries.Browser;

import io.github.mthli.Berries.Database.Record;

public interface BrowserController {
    void updateRecord(Record record);

    void updateProgress(int progress);

    void updateNotification();

    void showControlPanel();

    void hideControlPanel();

    boolean isPanelShowing();

    void showSelectedTab(Berry berry);

    void deleteSelectedTab(Berry berry);

    void onLongPress();
}
