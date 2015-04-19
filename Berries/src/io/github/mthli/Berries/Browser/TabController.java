package io.github.mthli.Berries.Browser;

import android.view.View;

public interface TabController {
    void activate();

    void deactivate();

    View getTabView();

    void setTabTitle(String title);

    int getFlag();

    void setFlag(int flag);
}
