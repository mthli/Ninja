package io.github.mthli.Berries.Browser;

import java.util.LinkedList;
import java.util.List;

public class BerryContainer {
    private static List<BerryView> list = new LinkedList<BerryView>();

    public static BerryView get(int index) {
        return list.get(index);
    }

    public synchronized static void set(BerryView view, int index) {
        list.get(index).destroy();
        list.set(index, view);
    }

    public synchronized static void add(BerryView view) {
        list.add(view);
    }

    public synchronized static void add(BerryView view, int index) {
        list.add(index, view);
    }

    public synchronized static void remove(int index) {
        list.get(index).destroy();
        list.remove(index);
    }

    public synchronized static void remove(BerryView berryView) {
        berryView.destroy();
        list.remove(berryView);
    }

    public static int indexOf(BerryView berryView) {
        return list.indexOf(berryView);
    }

    public static List<BerryView> list() {
        return list;
    }

    public static int size() {
        return list.size();
    }

    public synchronized static void clear() {
        for (BerryView view : list) {
            view.destroy();
        }
        list.clear();
    }
}
