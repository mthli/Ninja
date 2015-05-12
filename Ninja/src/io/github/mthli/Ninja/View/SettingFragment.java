package io.github.mthli.Ninja.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.webkit.CookieManager;
import io.github.mthli.Ninja.Browser.*;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.Unit.BrowserUnit;
import io.github.mthli.Ninja.Unit.IntentUnit;

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
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
                NinjaToast.show(getActivity(), R.string.toast_judge);
                break;
            case R.string.setting_title_github:
                NinjaToast.show(getActivity(), R.string.app_github);
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
}
