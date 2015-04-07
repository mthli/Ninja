package io.github.mthli.Berries.Browser;

import android.os.Message;
import android.webkit.WebView;

public interface BrowserController {
    void updateInputBox(String query);

    void updateProgress(int progress);

    void updateNotification();

    void showTab(BerryView berryView);

    void deleteTab();

    void onCreateView(WebView view, boolean incognito, Message resultMsg);

    void onLongPress();
}
