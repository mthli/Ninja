package io.github.mthli.Ninja.View;

import android.graphics.Bitmap;

public class GridItem {
    private String title;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    private String url;
    public String getURL() {
        return url;
    }
    public void setURL(String url) {
        this.url = url;
    }

    private Bitmap cover;
    public Bitmap getCover() {
        return cover;
    }
    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    private int ordinal;
    public int getOrdinal() {
        return ordinal;
    }
    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public GridItem() {
        this.title = null;
        this.url = null;
        this.cover = null;
        this.ordinal = -1;
    }

    public GridItem(String title, String url, Bitmap cover, int ordinal) {
        this.title = title;
        this.url = url;
        this.cover = cover;
        this.ordinal = ordinal;
    }
}
