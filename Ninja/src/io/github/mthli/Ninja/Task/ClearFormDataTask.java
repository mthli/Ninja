package io.github.mthli.Ninja.Task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.Unit.BrowserUnit;
import io.github.mthli.Ninja.View.NinjaToast;

public class ClearFormDataTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private ProgressDialog dialog;

    public ClearFormDataTask(Context context) {
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
        BrowserUnit.clearFormData(context);

        if (isCancelled()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        dialog.dismiss();
        dialog.hide();
        NinjaToast.show(context, R.string.toast_clear_form_data_successful);
    }
}
