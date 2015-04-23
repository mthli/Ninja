package io.github.mthli.Berries.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import io.github.mthli.Berries.View.BerryContextWrapper;
import io.github.mthli.Berries.Database.Record;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Service.HolderService;
import io.github.mthli.Berries.Unit.BrowserUnit;
import io.github.mthli.Berries.Unit.IntentUnit;
import io.github.mthli.Berries.Unit.RecordUnit;
import io.github.mthli.Berries.View.DialogAdapter;

import java.util.*;

public class HolderActivity extends Activity {
    private Record first = null;
    private Record second = null;
    private Timer timer = null;
    private boolean background = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null || getIntent().getData() == null) {
            finish();
            return;
        }

        first = new Record();
        first.setTitle(getString(R.string.browser_tab_untitled));
        first.setURL(getIntent().getData().toString());
        first.setTime(System.currentTimeMillis());

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (first != null && second == null) {
                    Intent toService = new Intent(HolderActivity.this, HolderService.class);
                    RecordUnit.setHolder(first);
                    RecordUnit.setIncognito(false);
                    startService(toService);
                    background = true;
                }
                HolderActivity.this.finish();
            }
        };
        timer = new Timer();
        timer.schedule(task, 300);
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (intent == null || intent.getData() == null || first == null) {
            finish();
            return;
        }

        if (timer != null) {
            timer.cancel();
        }

        second = new Record();
        second.setTitle(getString(R.string.browser_tab_untitled));
        second.setURL(intent.getData().toString());
        second.setTime(System.currentTimeMillis());

        if (first.getURL().equals(second.getURL())) {
            showHolderDialog();
        } else {
            Intent toService = new Intent(HolderActivity.this, HolderService.class);
            RecordUnit.setHolder(second);
            RecordUnit.setIncognito(false);
            startService(toService);
            background = true;
            finish();
        }
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }

        if (background) {
            Toast.makeText(this, R.string.toast_load_in_background, Toast.LENGTH_SHORT).show();
        }

        first = null;
        second = null;
        timer = null;
        background = false;
        super.onDestroy();
    }

    private void showHolderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new BerryContextWrapper(this));
        builder.setCancelable(true);

        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog, null, false);
        builder.setView(linearLayout);

        String[] strings = getResources().getStringArray(R.array.holder_menu);
        List<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(strings));

        ListView listView = (ListView) linearLayout.findViewById(R.id.dialog_listview);
        DialogAdapter adapter = new DialogAdapter(this, R.layout.dialog_item, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        final AlertDialog dialog = builder.create();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                HolderActivity.this.finish();
            }
        });
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent toService = new Intent(HolderActivity.this, HolderService.class);
                        RecordUnit.setHolder(first);
                        RecordUnit.setIncognito(true);
                        startService(toService);
                        Toast.makeText(HolderActivity.this, R.string.toast_load_in_background_incognito, Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Intent toActivity = new Intent(HolderActivity.this, BrowserActivity.class);
                        toActivity.putExtra(IntentUnit.OPEN, first.getURL());
                        startActivity(toActivity);
                        break;
                    case 2:
                        BrowserUnit.copy(HolderActivity.this, first.getURL());
                        break;
                    case 3:
                        IntentUnit.share(HolderActivity.this, first.getTitle(), first.getURL());
                        break;
                    default:
                        break;
                }
                dialog.hide();
                dialog.dismiss();
                finish();
            }
        });
    }
}
