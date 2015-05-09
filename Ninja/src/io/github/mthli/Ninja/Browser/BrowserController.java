package io.github.mthli.Ninja.Browser;

import android.os.Message;
import android.webkit.WebView;

public interface BrowserController {
    void updateBookmarks();

    void updateInputBox(String query);

    void updateProgress(int progress);

    void showAlbum(AlbumController albumController, boolean scroll);

    void onCreateView(WebView view, Message resultMsg);

    void onLongPress(String url);
}
