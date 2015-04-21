package io.github.mthli.Berries.Unit;

import android.content.Context;
import android.content.Intent;
import android.net.MailTo;

public class IntentUnit {
    public static final String LIST = "LIST";
    public static final String QUIT = "QUIT";
    public static final String CHANGE = "CHANGE";
    public static final String GITHUB = "GITHUB";

    public static final int REQUEST_SETTING = 0x100;
    public static final int RESULT_SETTING = 0x101;

    public static Intent getEmailIntent(MailTo mailTo) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { mailTo.getTo() });
        intent.putExtra(Intent.EXTRA_TEXT, mailTo.getBody());
        intent.putExtra(Intent.EXTRA_SUBJECT, mailTo.getSubject());
        intent.putExtra(Intent.EXTRA_CC, mailTo.getCc());
        intent.setType("message/rfc822");
        return intent;
    }

    public static void share(Context context, String title, String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, title + "\n" + url);
        context.startActivity(intent);
    }
}