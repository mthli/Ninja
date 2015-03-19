package io.github.mthli.Berries.Dialog;

public class PreferenceDialogListItem {
    private String title;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    private String content;
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public PreferenceDialogListItem() {
        this.title = null;
        this.content = null;
    }
}
