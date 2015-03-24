package io.github.mthli.Berries.Browser;

import java.util.ArrayList;
import java.util.List;

public class BerryContainer {
    private static List<BerryView> list = new ArrayList<BerryView>();

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

    public synchronized static void remove(int index) {
        list.get(index).destroy();
        list.remove(index);
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
