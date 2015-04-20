package io.github.mthli.Berries.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.webkit.CookieManager;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Unit.BrowserUnit;

import java.io.File;

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private ListPreference searchEngine;
    private ListPreference notificationPriority;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        searchEngine = (ListPreference) findPreference(getString(R.string.sp_search_engine));
        notificationPriority = (ListPreference) findPreference(getString(R.string.sp_notification_priority));
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
                // TODO
                break;
            case R.string.setting_title_import_bookmarks:
                // TODO
                break;
            case R.string.setting_title_clear_bookmarks:
                BrowserUnit.clearBookmarks(getActivity());
                break;
            case R.string.setting_title_clear_cookies:
                BrowserUnit.clearCookies(getActivity());
                break;
            case R.string.setting_title_clear_history:
                BrowserUnit.clearHistory(getActivity());
                break;
            case R.string.setting_title_clear_passwords:
                BrowserUnit.clearPasswords(getActivity());
                break;
            case R.string.setting_title_usage:
                // TODO
                break;
            case R.string.setting_title_github:
                // TODO
                break;
            case R.string.setting_title_license:
                // TODO
                break;
            default:
                break;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.sp_search_engine))) {
            String summary = sharedPreferences.getString(key, getString(R.string.setting_summary_search_engine_google));
            searchEngine.setSummary(summary);
        } else if (key.equals(getString(R.string.sp_notification_priority))) {
            String summary = sharedPreferences.getString(key, getString(R.string.setting_summary_notification_priority_default));
            notificationPriority.setSummary(summary);
        } else if (key.equals(getString(R.string.sp_cookies))) {
            CookieManager manager = CookieManager.getInstance();
            manager.setAcceptCookie(sharedPreferences.getBoolean(getString(R.string.sp_cookies), true));
        }
    }
}
