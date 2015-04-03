package io.github.mthli.Berries.Browser;

import java.util.LinkedList;
import java.util.List;

public class BerryContainer {
    private static List<Berry> list = new LinkedList<Berry>();

    public static Berry get(int index) {
        return list.get(index);
    }

    public synchronized static void set(Berry view, int index) {
        list.get(index).destroy();
        list.set(index, view);
    }

    public synchronized static void add(Berry view) {
        list.add(view);
    }

    public synchronized static void add(Berry view, int index) {
        list.add(index, view);
    }

    public synchronized static void remove(int index) {
        list.get(index).destroy();
        list.remove(index);
    }

    public synchronized static void remove(Berry berry) {
        berry.destroy();
        list.remove(berry);
    }

    public static int indexOf(Berry berry) {
        return list.indexOf(berry);
    }

    public static List<Berry> list() {
        return list;
    }

    public static int size() {
        return list.size();
    }

    public synchronized static void clear() {
        for (Berry view : list) {
            view.destroy();
        }

        list.clear();
    }
}
