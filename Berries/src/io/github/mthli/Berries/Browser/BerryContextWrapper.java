package io.github.mthli.Berries.Browser;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import io.github.mthli.Berries.R;

public class BerryContextWrapper extends ContextWrapper {
    private Context context;

    public BerryContextWrapper(Context context) {
        super(context);
        this.context = context;
        this.context.setTheme(R.style.BrowserActivityTheme);
    }

    @Override
    public Resources.Theme getTheme() {
        return context.getTheme();
    }
}
