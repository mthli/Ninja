package io.github.mthli.Ninja.Task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.Unit.BrowserUnit;
import io.github.mthli.Ninja.View.NinjaToast;

public class ClearCacheTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private ProgressDialog dialog;

    public ClearCacheTask(Context context) {
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
        if (isCancelled()) {
            return false;
        }
        return BrowserUnit.clearCache(context);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        dialog.hide();
        dialog.dismiss();

        if (result) {
            NinjaToast.show(context, R.string.toast_clear_cache_successful);
        } else {
            NinjaToast.show(context, R.string.toast_clear_cache_failed);
        }
    }
}
