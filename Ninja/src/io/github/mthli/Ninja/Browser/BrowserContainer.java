package io.github.mthli.Ninja.Browser;

import io.github.mthli.Ninja.View.NinjaWebView;

import java.util.ArrayList;
import java.util.List;

public class BrowserContainer {
    private static List<AlbumController> list = new ArrayList<>();

    public static AlbumController get(int index) {
        return list.get(index);
    }

    public synchronized static void set(AlbumController albumController, int index) {
        if (list.get(index) instanceof NinjaWebView) {
            ((NinjaWebView) list.get(index)).destroy();
        }
        list.set(index, albumController);
    }

    public synchronized static void add(AlbumController albumController) {
        list.add(albumController);
    }

    public synchronized static void add(AlbumController albumController, int index) {
        list.add(index, albumController);
    }

    public synchronized static void remove(int index) {
        if (list.get(index) instanceof NinjaWebView) {
            ((NinjaWebView) list.get(index)).destroy();
        }
        list.remove(index);
    }

    public synchronized static void remove(AlbumController albumController) {
        if (albumController instanceof NinjaWebView) {
            ((NinjaWebView) albumController).destroy();
        }
        list.remove(albumController);
    }

    public static int indexOf(AlbumController albumController) {
        return list.indexOf(albumController);
    }

    public static List<AlbumController> list() {
        return list;
    }

    public static int size() {
        return list.size();
    }

    public synchronized static void clear() {
        for (AlbumController albumController : list) {
            if (albumController instanceof NinjaWebView) {
                ((NinjaWebView) albumController).destroy();
            }
        }
        list.clear();
    }
}
