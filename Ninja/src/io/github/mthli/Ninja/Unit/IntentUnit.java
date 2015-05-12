package io.github.mthli.Ninja.Unit;

import android.content.Context;
import android.content.Intent;
import android.net.MailTo;

public class IntentUnit {
    public static final String PIN = "PIN";
    public static final String OPEN = "OPEN";

    public static final int REQUEST_FILE = 0x102;
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