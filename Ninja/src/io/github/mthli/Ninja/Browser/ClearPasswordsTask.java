package io.github.mthli.Ninja.Browser;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.Unit.BrowserUnit;
import io.github.mthli.Ninja.View.NinjaToast;

public class ClearPasswordsTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private ProgressDialog dialog;

    public ClearPasswordsTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage(context.getString(R.string.toast_wait_a_minute));
        dialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        BrowserUnit.clearPasswords(context);

        if (isCancelled()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        dialog.hide();
        dialog.dismiss();
        NinjaToast.show(context, R.string.toast_clear_passwords_successful);
    }
}
