package io.github.mthli.Berries.Activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Unit.Flag;
import io.github.mthli.Berries.View.SettingAdapter;
import io.github.mthli.Berries.View.SettingItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    private List<SettingItem> list = new ArrayList<SettingItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.main_menu_about:
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    private void initUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue_700));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        fillList();

        ListView listView = (ListView) findViewById(R.id.main_listview);

        SettingAdapter adapter = new SettingAdapter(this, R.layout.setting_item, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void fillList() {
        String[] titles = getResources().getStringArray(R.array.setting_titles);
        String[] contents = getResources().getStringArray(R.array.setting_contents);

        for (int i = 0; i < titles.length; i++) {
            SettingItem item = new SettingItem();
            item.setTitle(titles[i]);
            item.setContent(contents[i]);
            item.setStatus(Flag.SWITCHC_HIDE);
            item.setView(null);
            list.add(item);
        }

        list.get(1).setStatus(Flag.SWITCHC_OFF);
        list.get(2).setStatus(Flag.SWITCHC_OFF);
        list.get(5).setStatus(Flag.SWITCHC_OFF);

        SharedPreferences preferences = getSharedPreferences(getString(R.string.sp_name), MODE_PRIVATE);
        // TODO
    }
}
