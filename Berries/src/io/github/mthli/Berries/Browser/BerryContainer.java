package io.github.mthli.Berries.Browser;

import java.util.ArrayList;
import java.util.List;

public class BerryContainer {
    private static List<BerryView> list = new ArrayList<BerryView>();

    public static BerryView get(int index) {
        return list.get(index);
    }

    public static void set(BerryView view, int index) {
        list.set(index, view);
    }

    public static void add(BerryView view) {
        list.add(view);
    }

    public static void remove(int index) {
        list.remove(index);
    }

    public static void remove(BerryView view) {
        // TODO
        list.remove(view);
    }

    public static List<BerryView> list() {
        return list;
    }

    public static void clear() {
        // TODO
        list.clear();
    }
}
