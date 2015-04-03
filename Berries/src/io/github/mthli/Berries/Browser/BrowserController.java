package io.github.mthli.Berries.Browser;

public interface BrowserController {
    void updateInputBox(String query);

    void updateProgress(int progress);

    void updateNotification();

    void showControlPanel();

    void hideControlPanel();

    boolean isControlPanelShowing();

    void showSelectedTab(Berry berry);

    void deleteSelectedTab();

    void onLongPress();
}
