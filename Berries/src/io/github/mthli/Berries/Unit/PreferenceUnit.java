package io.github.mthli.Berries.Unit;

import android.app.Notification;

public class PreferenceUnit {
    public static final String NAME = "Berries";

    public static final String DOUBLE_TAPS_INTERVAL = "DOUBLE_TAPS_INTERVAL";
    public static final int DOUBLE_TAPS_INTERVAL_DEFAULT = 300;

    public static final String ENABLE_DOUBLE_TAPS = "ENABLE_DOUBLE_TAPS";
    public static final boolean ENABLE_DOUBLE_TAPS_DEFAULT = true;

    public static final String FONT_SIZE = "FONT_SIZE";
    public static final int FONT_SIZE_DEFAULT = 16;

    public static final String INCOGNITO = "INCOGNITO";
    public static final boolean INCOGNITO_DEFAULT = false;

    public static final String LOAD_LIMIT_MAX = "PAGES_NUMBER_MUX";
    public static final int LOAD_LIMIT_MAX_DEFAULT = 16;

    public static final String NOTIFICATION_PRIORITY = "NOTIFICATION_PRIORITY";
    public static final int NOTIFICATION_PRIORITY_DEFAULT = Notification.PRIORITY_DEFAULT;

    public static final String NOTIFICATION_SOUND = "NOTIFICATION_SOUND";
    public static final boolean NOTIFICATION_SOUND_DEFAULT = false;

    public static final String SECONDARY_BROWSER = "SECONDARY_BROWSER";
    public static final String SECONDARY_BROWSER_DEFAULT = null;
}
