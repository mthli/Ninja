package io.github.mthli.Berries.Activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import io.github.mthli.Berries.Database.Record;
import io.github.mthli.Berries.Database.RecordAction;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Unit.ViewUnit;
import io.github.mthli.Berries.View.ListAdapter;

import java.util.List;

public class HistoryActivity extends Activity {
    private ListView listView;
    private ListAdapter adapter;
    private List<Record> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager.TaskDescription description = new ActivityManager.TaskDescription(
                    getResources().getString(R.string.app_name),
                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher),
                    getResources().getColor(R.color.blue_500)
            );
            setTaskDescription(description);
            getActionBar().setElevation(ViewUnit.dp2px(this, 2));
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);

        RecordAction action = new RecordAction(this);
        action.open(false);
        list = action.listHistory();
        action.close();

        listView = (ListView) findViewById(R.id.list);
        listView.setEmptyView(findViewById(R.id.list_empty));

        adapter = new ListAdapter(this, R.layout.list_item, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // TODO
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                // TODO
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        RecordAction action = new RecordAction(this);
        action.open(true);

        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.history_menu_search:
                break;
            case R.id.history_menu_clear:
                action.clearHistory();
                list.clear();
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }

        action.close();
        return super.onOptionsItemSelected(menuItem);
    }
}
