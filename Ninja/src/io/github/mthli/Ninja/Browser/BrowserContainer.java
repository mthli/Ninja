package io.github.mthli.Ninja.Browser;

import java.util.LinkedList;
import java.util.List;

public class BrowserContainer {
    private static List<TabController> list = new LinkedList<TabController>();

    public static TabController get(int index) {
        return list.get(index);
    }

    public synchronized static void set(TabController controller, int index) {
        if (list.get(index) instanceof NinjaView) {
            ((NinjaView) list.get(index)).destroy();
        }
        list.set(index, controller);
    }

    public synchronized static void add(TabController controller) {
        list.add(controller);
    }

    public synchronized static void add(TabController controller, int index) {
        list.add(index, controller);
    }

    public synchronized static void remove(int index) {
        if (list.get(index) instanceof NinjaView) {
            ((NinjaView) list.get(index)).destroy();
        }
        list.remove(index);
    }

    public synchronized static void remove(TabController controller) {
        if (controller instanceof NinjaView) {
            ((NinjaView) controller).destroy();
        }
        list.remove(controller);
    }

    public static int indexOf(TabController controller) {
        return list.indexOf(controller);
    }

    public static List<TabController> list() {
        return list;
    }

    public static int size() {
        return list.size();
    }

    public synchronized static void clear() {
        for (TabController controller : list) {
            if (controller instanceof NinjaView) {
                ((NinjaView) controller).destroy();
            }
        }
        list.clear();
    }
}
