package io.github.mthli.Ninja.Unit;

import android.content.Context;
import android.content.Intent;
import android.net.MailTo;

public class IntentUnit {
    public static final String OPEN = "OPEN";
    public static final String URL = "URL";

    public static final int REQUEST_BOOKMARKS = 0x100;
    public static final int REQUEST_FILE_16 = 0x101;
    public static final int REQUEST_FILE_21 = 0x102;
    public static final int REQUEST_WHITELIST = 0x103;
    public static final int REQUEST_CLEAR = 0x104;
    public static final String INTENT_TYPE_TEXT_PLAIN = "text/plain";
    public static final String INTENT_TYPE_MESSAGE_RFC822 = "message/rfc822";

    public static Intent getEmailIntent(MailTo mailTo) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { mailTo.getTo() });
        intent.putExtra(Intent.EXTRA_TEXT, mailTo.getBody());
        intent.putExtra(Intent.EXTRA_SUBJECT, mailTo.getSubject());
        intent.putExtra(Intent.EXTRA_CC, mailTo.getCc());
        intent.setType(INTENT_TYPE_MESSAGE_RFC822);

        return intent;
    }

    public static void share(Context context, String title, String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(INTENT_TYPE_TEXT_PLAIN);
        intent.putExtra(Intent.EXTRA_TEXT, title + "\n" + url);
        context.startActivity(intent);
    }

    // Activity holder
    private static Context context = null;
    public static void setContext(Context holder) {
        context = holder;
    }
    public static Context getContext() {
        return context;
    }

    private static boolean clear = false;
    public static boolean isClear() {
        return clear;
    }
    public synchronized static void setClear(boolean b) {
        clear = b;
    }

    private static boolean dbChange = false;
    public static boolean isDBChange() {
        return dbChange;
    }
    public static void setDBChange(boolean b) {
        dbChange = b;
    }

    private static boolean spChange = false;
    public static boolean isSPChange() {
        return spChange;
    }
    public static void setSPChange(boolean b) {
        spChange = b;
    }
}