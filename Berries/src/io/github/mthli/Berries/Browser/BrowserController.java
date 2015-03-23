package io.github.mthli.Berries.Browser;

import io.github.mthli.Berries.Database.Record;

public interface BrowserController {
    public void updateTitle(Record record);

    public void updateURL(Record record);

    public void updateProgress(int progress);

    public void showToolbar(boolean show);

    public boolean isToolbarShowing();

    public boolean isIncognito();

    public void onLongPress();
}
