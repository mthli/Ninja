package io.github.mthli.Ninja.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.View.NinjaToast;

public class TokenActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.token);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText tokenEdit = (EditText) findViewById(R.id.token_edit);
        Button tokenAdd = (Button) findViewById(R.id.token_add);

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String token = sp.getString(getString(R.string.sp_readability_token), "");
        tokenEdit.setText(token);
        tokenEdit.setSelection(token.length());
        showSoftInput(tokenEdit);

        tokenAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tokenEdit.getText().toString().trim().isEmpty()) {
                    NinjaToast.show(TokenActivity.this, R.string.toast_input_empty);
                } else {
                    sp.edit().putString(
                            getString(R.string.sp_readability_token),
                            tokenEdit.getText().toString().trim()
                    ).commit();
                    NinjaToast.show(TokenActivity.this, R.string.toast_add_token_successful);
                }
            }
        });
    }

    @Override
    public void onPause() {
        hideSoftInput(findViewById(R.id.token_edit));
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.token_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }

        return true;
    }

    private void hideSoftInput(View view) {
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showSoftInput(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
