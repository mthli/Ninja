package io.github.mthli.Ninja.Task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import io.github.mthli.Ninja.Database.RecordAction;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.View.GridItem;
import io.github.mthli.Ninja.View.NinjaToast;

import java.util.List;

public class SaveGridTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private List<GridItem> list;
    private ProgressDialog dialog;

    public SaveGridTask(Context context, List<GridItem> list) {
        this.context = context;
        this.list = list;
        this.dialog = null;
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
        RecordAction action = new RecordAction(context);
        action.open(true);
        action.clearGrid();
        for (GridItem item : list) {
            action.addGridItem(item);
        }
        action.close();

        if (isCancelled()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        dialog.hide();
        dialog.dismiss();

        if (result) {
            NinjaToast.show(context, R.string.toast_relayout_successful);
        } else {
            NinjaToast.show(context, R.string.toast_relayout_failed);
        }
    }
}
