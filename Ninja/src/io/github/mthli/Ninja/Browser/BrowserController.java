package io.github.mthli.Ninja.Browser;

import android.os.Message;
import android.webkit.WebView;
import io.github.mthli.Ninja.View.TabRelativeLayout;

public interface BrowserController {
    void updateBookmarks();

    void updateInputBox(String query);

    void updateProgress(int progress);

    void showTab(NinjaView ninjaView);

    void showTab(TabRelativeLayout tabRelativeLayout);

    void deleteTab();

    void onCreateView(WebView view, Message resultMsg);

    void onLongPress(String url);
}
