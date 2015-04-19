package io.github.mthli.Berries.Browser;

import android.os.Message;
import android.webkit.WebView;
import io.github.mthli.Berries.View.TabRelativeLayout;

public interface BrowserController {
    void updateBookmarks();

    void updateInputBox(String query);

    void updateProgress(int progress);

    void updateNotification();

    void showTab(BerryView berryView);

    void showTab(TabRelativeLayout tabRelativeLayout);

    void deleteTab();

    void onCreateView(WebView view, boolean incognito, Message resultMsg);

    void onLongPress(String url);
}
