package io.github.mthli.Berries.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import io.github.mthli.Berries.R;

import java.util.List;

public class PreferenceDialog {
    public static void show(final Context context, final List<PreferenceDialogItem> list, final Preference preference) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.preference_dialog, null, false);
        builder.setView(view);
        builder.setCancelable(true);

        ListView listView = (ListView) view.findViewById(R.id.preference_dialog_listview);

        PreferenceDialogAdapter adapter = new PreferenceDialogAdapter(context, R.layout.preference_dialog_item, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        final AlertDialog dialog = builder.create();
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                preference.getEditor().putString(
                        context.getString(R.string.sp_setting_secondary_browser),
                        list.get(position).getContent()
                ).commit();
                preference.setSummary(list.get(position).getTitle());

                dialog.hide();
                dialog.dismiss();
            }
        });
    }
}
