package io.github.mthli.Berries.View;

import android.view.View;
import io.github.mthli.Berries.Unit.Flag;

public class SettingItem {
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

    private int status;
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    private View view;
    public View getView() {
        return view;
    }
    public void setView(View view) {
        this.view = view;
    }

    public SettingItem() {
        this.title = null;
        this.content = null;
        this.status = Flag.SWITCHC_HIDE;
        this.view = null;
    }
}
