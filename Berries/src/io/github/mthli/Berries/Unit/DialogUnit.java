package io.github.mthli.Berries.Unit;

import android.app.AlertDialog;
import android.content.Context;
import android.net.http.SslError;
import android.os.Message;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.View.PreferenceDialogListAdapter;
import io.github.mthli.Berries.View.PreferenceDialogListItem;

import java.util.List;

public class DialogUnit {
    public static void show(final Context context, final List<PreferenceDialogListItem> list, final Preference preference) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.preference_dialog_list, null, false);
        builder.setView(view);
        builder.setCancelable(true);

        ListView listView = (ListView) view.findViewById(R.id.preference_dialog_list_listview);

        PreferenceDialogListAdapter adapter = new PreferenceDialogListAdapter(context, R.layout.preference_dialog_list_item, list);
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

    public static void show(final Context context, final Preference preference) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.preference_dialog_web, null, false);
        builder.setView(view);
        builder.setCancelable(true);

        int textSize = preference.getSharedPreferences().getInt(
                context.getString(R.string.sp_setting_text_size),
                context.getResources().getInteger(R.integer.text_size_default)
        );
        final int min = context.getResources().getInteger(R.integer.text_size_min);

        final WebView webView = (WebView) view.findViewById(R.id.preference_dialog_web_webview);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setBackgroundColor(context.getResources().getColor(R.color.dividers));
        webView.getSettings().setDefaultFontSize(textSize);
        webView.loadDataWithBaseURL(
                null,
                context.getString(R.string.text_size_hint) + " " + textSize,
                null,
                null,
                null
        );

        SeekBar seekBar = (SeekBar) view.findViewById(R.id.preference_dialog_web_seekbar);
        seekBar.setProgress(textSize - min);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                webView.getSettings().setDefaultFontSize(min + progress);
                webView.loadDataWithBaseURL(
                        null,
                        context.getString(R.string.text_size_hint) + " " + (min + progress),
                        null,
                        null,
                        null
                );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                /* Do nothing */
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                webView.getSettings().setDefaultFontSize(min + seekBar.getProgress());
                webView.loadDataWithBaseURL(
                        null,
                        context.getString(R.string.text_size_hint) + " " + (min + seekBar.getProgress()),
                        null,
                        null,
                        null
                );

                preference.getEditor().putInt(
                        context.getString(R.string.sp_setting_text_size),
                        min + seekBar.getProgress()
                ).commit();
                preference.setSummary(String.valueOf(min + seekBar.getProgress()));
            }
        });

        builder.create().show();
    }


    public static void show(Context context, String origin, GeolocationPermissions.Callback callback) {
        // TODO
    }

    public static void show(Context context, WebView webView, String url, String message, JsResult result) {
        // TODO
    }

    public static void show(Context context, WebView webView, String url, String message, String defaultValue, JsPromptResult result) {
        // TODO
    }

    public static void show(Context context, WebView webView, Message dontResend, Message resend) {
        // TODO
    }

    public static void show(Context context, WebView webView, HttpAuthHandler handler, String host, String realm) {
        // TODO
    }

    public static void show(Context context, WebView webView, SslErrorHandler handler, SslError error) {
        // TODO
    }
}
