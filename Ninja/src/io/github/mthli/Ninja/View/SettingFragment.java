package io.github.mthli.Ninja.View;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.Task.ClearCacheTask;
import io.github.mthli.Ninja.Task.ClearFormDataTask;
import io.github.mthli.Ninja.Task.ClearPasswordsTask;
import io.github.mthli.Ninja.Task.ExportBookmarksTask;
import io.github.mthli.Ninja.Unit.BrowserUnit;
import io.github.mthli.Ninja.Unit.IntentUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String LICENSE_TITLE = "LICENSE_TITLE";
    private static final String LICENSE_CONTENT = "LICENSE_CONTENT";
    private static final String LICENSE_AUTHOR = "LICENSE_AUTHOR";
    private static final String LICENSE_URL = "LICENSE_URL";

    private ListPreference searchEngine;
    private ListPreference notiPriority;

    private boolean spChange = false;
    public boolean isSPChange() {
        return spChange;
    }

    private boolean dbChange = false;
    public boolean isDBChange() {
        return dbChange;
    }
    public void setDBChange(boolean dbChange) {
        this.dbChange = dbChange;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        sp.registerOnSharedPreferenceChangeListener(this);

        searchEngine = (ListPreference) findPreference(getString(R.string.sp_search_engine));
        searchEngine.setSummary(sp.getString(getString(R.string.sp_search_engine), getString(R.string.setting_summary_search_engine_google)));

        notiPriority = (ListPreference) findPreference(getString(R.string.sp_notification_priority));
        notiPriority.setSummary(sp.getString(getString(R.string.sp_notification_priority), getString(R.string.setting_summary_notification_priority_default)));
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preference.getTitleRes()) {
            case R.string.setting_title_export_bookmarks:
                new ExportBookmarksTask(this).execute();
                break;
            case R.string.setting_title_import_bookmarks:
                Intent importBookmarks = new Intent(Intent.ACTION_GET_CONTENT);
                importBookmarks.setType(IntentUnit.INTENT_TYPE_TEXT_PLAIN);
                importBookmarks.addCategory(Intent.CATEGORY_OPENABLE);
                getActivity().startActivityForResult(importBookmarks, IntentUnit.REQUEST_FILE);
                break;
            case R.string.setting_title_clear_bookmarks:
                BrowserUnit.clearBookmarks(getActivity());
                dbChange = true;
                break;
            case R.string.setting_title_clear_cache:
                new ClearCacheTask(getActivity()).execute();
                break;
            case R.string.setting_title_clear_cookie:
                ProgressDialog dialog = new ProgressDialog(getActivity());
                dialog.setCancelable(false);
                dialog.setMessage(getString(R.string.toast_wait_a_minute));
                dialog.show();
                BrowserUnit.clearCookies(getActivity());
                dialog.hide();
                dialog.dismiss();
                NinjaToast.show(getActivity(), R.string.toast_clear_cookie_successful);
                break;
            case R.string.setting_title_clear_form_data:
                new ClearFormDataTask(getActivity()).execute();
                break;
            case R.string.setting_title_clear_history:
                BrowserUnit.clearHistory(getActivity());
                dbChange = true;
                break;
            case R.string.setting_title_clear_passwords:
                new ClearPasswordsTask(getActivity()).execute();
                break;
            case R.string.setting_title_version:
                showIntroductionDialog();
                break;
            case R.string.setting_title_license:
                showLicenseDialog();
                break;
            case R.string.setting_title_contact_us:
                NinjaToast.show(getActivity(), R.string.app_gmail);
                break;
            default:
                break;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        spChange = true;
        if (key.equals(getString(R.string.sp_search_engine))) {
            String summary = sharedPreferences.getString(key, getString(R.string.setting_summary_search_engine_google));
            searchEngine.setSummary(summary);
        } else if (key.equals(getString(R.string.sp_notification_priority))) {
            String summary = sharedPreferences.getString(key, getString(R.string.setting_summary_notification_priority_default));
            notiPriority.setSummary(summary);
        } else if (key.equals(getString(R.string.sp_cookies))) {
            CookieManager manager = CookieManager.getInstance();
            manager.setAcceptCookie(sharedPreferences.getBoolean(getString(R.string.sp_cookies), true));
        }
    }

    private void showIntroductionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);

        LinearLayout layout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_introduction, null, false);
        builder.setView(layout);

        WebView webView = (WebView) layout.findViewById(R.id.dialog_introduction);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);

        WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultTextEncodingName(BrowserUnit.URL_ENCODING);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setTextZoom(100);
        webSettings.setUseWideViewPort(true);

        String lang;
        if (getResources().getConfiguration().locale.getLanguage().equals("zh")) {
            lang = BrowserUnit.NINJA_INTRODUCTION_ZH;
        } else {
            lang = BrowserUnit.NINJA_INTRODUCTION_EN;
        }
        webView.loadUrl(BrowserUnit.BASE_URL + lang);

        builder.create().show();
    }

    private void showLicenseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);

        LinearLayout layout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog, null, false);
        builder.setView(layout);

        List<Map<String, String>> list = new ArrayList<>();
        String[] titles = getResources().getStringArray(R.array.license_titles);
        String[] contents = getResources().getStringArray(R.array.license_contents);
        String[] authors = getResources().getStringArray(R.array.license_authors);
        String[] urls = getResources().getStringArray(R.array.license_urls);
        for (int i = 0; i < 5; i++) {
            Map<String, String> map = new HashMap<>();
            map.put(LICENSE_TITLE, titles[i]);
            map.put(LICENSE_CONTENT, contents[i]);
            map.put(LICENSE_AUTHOR, authors[i]);
            map.put(LICENSE_URL, urls[i]);
            list.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                getActivity(),
                list,
                R.layout.dialog_license_item,
                new String[] {LICENSE_TITLE, LICENSE_CONTENT, LICENSE_AUTHOR, LICENSE_URL},
                new int[] {R.id.dialog_license_item_title, R.id.dialog_license_item_content, R.id.dialog_license_item_author, R.id.dialog_license_item_url}
        );

        ListView listView = (ListView) layout.findViewById(R.id.dialog_list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        builder.create().show();
    }
}
