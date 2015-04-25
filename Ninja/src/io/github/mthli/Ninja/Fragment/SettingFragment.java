package io.github.mthli.Ninja.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.webkit.CookieManager;
import android.widget.Toast;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.Unit.BrowserUnit;
import io.github.mthli.Ninja.Unit.IntentUnit;

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private ListPreference searchEngine;
    private ListPreference notificationPriority;

    private boolean sharedPreferenceChange = false;
    public boolean isSharedPreferenceChange() {
        return sharedPreferenceChange;
    }

    private boolean databaseChange = false;
    public boolean isDatabaseChange() {
        return databaseChange;
    }
    public void setDatabaseChange(boolean databaseChange) {
        this.databaseChange = databaseChange;
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
        notificationPriority = (ListPreference) findPreference(getString(R.string.sp_notification_priority));
        notificationPriority.setSummary(sp.getString(getString(R.string.sp_notification_priority), getString(R.string.setting_summary_notification_priority_default)));
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
                BrowserUnit.exportBookmarks(getActivity());
                break;
            case R.string.setting_title_import_bookmarks:
                Intent importBookmarks = new Intent(Intent.ACTION_GET_CONTENT);
                importBookmarks.setType(IntentUnit.INTENT_TYPE_TEXT_PLAIN);
                importBookmarks.addCategory(Intent.CATEGORY_OPENABLE);
                getActivity().startActivityForResult(importBookmarks, IntentUnit.REQUEST_FILE);
                break;
            case R.string.setting_title_clear_bookmarks:
                BrowserUnit.clearBookmarks(getActivity());
                databaseChange = true;
                break;
            case R.string.setting_title_clear_cache:
                BrowserUnit.clearCache(getActivity());
                break;
            case R.string.setting_title_clear_cookies:
                BrowserUnit.clearCookies(getActivity());
                break;
            case R.string.setting_title_clear_form_data:
                BrowserUnit.clearFromData(getActivity());
                break;
            case R.string.setting_title_clear_history:
                BrowserUnit.clearHistory(getActivity());
                databaseChange = true;
                break;
            case R.string.setting_title_clear_passwords:
                BrowserUnit.clearPasswords(getActivity());
                break;
            case R.string.setting_title_version:
                Toast.makeText(getActivity(), R.string.toast_judge, Toast.LENGTH_SHORT).show();
                break;
            case R.string.setting_title_github:
                Intent toGitHub = new Intent();
                toGitHub.putExtra(IntentUnit.DATABASE_CHANGE, databaseChange);
                toGitHub.putExtra(IntentUnit.SHARED_PREFERENCE_CHANGE, sharedPreferenceChange);
                toGitHub.putExtra(IntentUnit.GITHUB, getString(R.string.app_github));
                getActivity().setResult(IntentUnit.RESULT_SETTING, toGitHub);
                getActivity().finish();
                break;
            case R.string.setting_title_contact_us:
                Intent toGmail = new Intent(Intent.ACTION_SENDTO);
                toGmail.setData(Uri.parse(getString(R.string.app_gmail)));
                startActivity(toGmail);
                break;
            default:
                break;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        sharedPreferenceChange = true;
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
