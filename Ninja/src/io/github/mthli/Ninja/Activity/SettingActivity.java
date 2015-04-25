package io.github.mthli.Ninja.Activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import io.github.mthli.Ninja.Fragment.SettingFragment;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.Unit.BrowserUnit;
import io.github.mthli.Ninja.Unit.IntentUnit;

import java.io.File;

public class SettingActivity extends Activity {
    private SettingFragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager.TaskDescription description = new ActivityManager.TaskDescription(
                    getString(R.string.app_name),
                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher),
                    getResources().getColor(R.color.blue_500)
            );
            setTaskDescription(description);
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);

        fragment = new SettingFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra(IntentUnit.DATABASE_CHANGE, fragment.isDatabaseChange());
                intent.putExtra(IntentUnit.SHARED_PREFERENCE_CHANGE, fragment.isSharedPreferenceChange());
                intent.putExtra(IntentUnit.GITHUB, false);
                setResult(IntentUnit.RESULT_SETTING, intent);
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra(IntentUnit.DATABASE_CHANGE, fragment.isDatabaseChange());
            intent.putExtra(IntentUnit.SHARED_PREFERENCE_CHANGE, fragment.isSharedPreferenceChange());
            intent.putExtra(IntentUnit.GITHUB, false);
            setResult(IntentUnit.RESULT_SETTING, intent);
            finish();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != IntentUnit.REQUEST_FILE && resultCode != Activity.RESULT_OK || data == null || data.getData() == null) {
            Toast.makeText(this, R.string.toast_import_bookmarks_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(data.getData().getPath());
        BrowserUnit.importBookmarks(this, file);
        fragment.setDatabaseChange(true);
    }
}
