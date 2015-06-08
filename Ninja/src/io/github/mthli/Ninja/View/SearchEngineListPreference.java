package io.github.mthli.Ninja.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.Unit.BrowserUnit;

public class SearchEngineListPreference extends ListPreference {
    public SearchEngineListPreference(Context context) {
        super(context);
    }

    public SearchEngineListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressWarnings("New API")
    public SearchEngineListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("New API")
    public SearchEngineListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);

        builder.setNeutralButton(R.string.dialog_button_custom, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showEditDialog();
            }
        });
    }

    private void showEditDialog() {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);

        FrameLayout layout = (FrameLayout) LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit, null, false);
        builder.setView(layout);

        final AlertDialog dialog = builder.create();
        dialog.show();

        final EditText editText = (EditText) layout.findViewById(R.id.dialog_edit);
        editText.setHint(R.string.dialog_se_hint);
        String custom = sp.getString(getContext().getString(R.string.sp_search_engine_custom), "");
        editText.setText(custom);
        editText.setSelection(custom.length());
        showSoftInput(editText);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != EditorInfo.IME_ACTION_DONE) {
                    return false;
                }

                String domain = editText.getText().toString().trim();
                if (domain.isEmpty()) {
                    NinjaToast.show(getContext(), R.string.toast_input_empty);
                    return true;
                } else if (!BrowserUnit.isURL(domain)) {
                    NinjaToast.show(getContext(), R.string.toast_invalid_domain);
                    return true;
                } else {
                    sp.edit().putString(getContext().getString(R.string.sp_search_engine), "5").commit();
                    sp.edit().putString(getContext().getString(R.string.sp_search_engine_custom), domain).commit();

                    hideSoftInput(editText);
                    dialog.hide();
                    dialog.dismiss();
                    return false;
                }
            }
        });
    }

    private void hideSoftInput(View view) {
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showSoftInput(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
