package io.github.mthli.Ninja.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import android.webkit.CookieManager;
import android.widget.*;
import io.github.mthli.Ninja.Activity.ClearActivity;
import io.github.mthli.Ninja.Activity.TokenActivity;
import io.github.mthli.Ninja.Activity.WhitelistActivity;
import io.github.mthli.Ninja.R;
import io.github.mthli.Ninja.Task.*;
import io.github.mthli.Ninja.Unit.IntentUnit;
import io.github.mthli.Ninja.View.NinjaToast;

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
    private ListPreference tabPosition;
    private ListPreference volumeControl;
    private ListPreference userAgent;
    private ListPreference rendering;

    private String[] seEntries;
    private String[] npEntries;
    private String[] tpEntries;
    private String[] vcEntries;
    private String[] ucEntries;
    private String[] rdEntries;

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
        addPreferencesFromResource(R.xml.preference_setting);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        sp.registerOnSharedPreferenceChangeListener(this);
        String summary;

        seEntries = getResources().getStringArray(R.array.setting_entries_search_engine);
        searchEngine = (ListPreference) findPreference(getString(R.string.sp_search_engine));
        int num = Integer.valueOf(sp.getString(getString(R.string.sp_search_engine), "0"));
        if (0 <= num && num <= 4) {
            summary = seEntries[num];
            searchEngine.setSummary(summary);
        } else {
            summary = getString(R.string.setting_summary_search_engine_custom);
            searchEngine.setSummary(summary);
        }

        npEntries = getResources().getStringArray(R.array.setting_entries_notification_priority);
        summary = npEntries[Integer.valueOf(sp.getString(getString(R.string.sp_notification_priority), "0"))];
        notiPriority = (ListPreference) findPreference(getString(R.string.sp_notification_priority));
        notiPriority.setSummary(summary);

        tpEntries = getResources().getStringArray(R.array.setting_entries_tab_position);
        summary = tpEntries[Integer.valueOf(sp.getString(getString(R.string.sp_anchor), "1"))];
        tabPosition = (ListPreference) findPreference(getString(R.string.sp_anchor));
        tabPosition.setSummary(summary);

        vcEntries = getResources().getStringArray(R.array.setting_entries_volume_control);
        summary = vcEntries[Integer.valueOf(sp.getString(getString(R.string.sp_volume), "1"))];
        volumeControl = (ListPreference) findPreference(getString(R.string.sp_volume));
        volumeControl.setSummary(summary);

        ucEntries = getResources().getStringArray(R.array.setting_entries_user_agent);
        userAgent = (ListPreference) findPreference(getString(R.string.sp_user_agent));
        num = Integer.valueOf(sp.getString(getString(R.string.sp_user_agent), "0"));
        if (0 <= num && num <= 1) {
            summary = ucEntries[num];
            userAgent.setSummary(summary);
        } else {
            summary = getString(R.string.setting_summary_user_agent_custom);
            userAgent.setSummary(summary);
        }

        rdEntries = getResources().getStringArray(R.array.setting_entries_rendering);
        summary = rdEntries[Integer.valueOf(sp.getString(getString(R.string.sp_rendering), "0"))];
        rendering = (ListPreference) findPreference(getString(R.string.sp_rendering));
        rendering.setSummary(summary);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preference.getTitleRes()) {
            case R.string.setting_title_whitelist:
                Intent toWhitelist = new Intent(getActivity(), WhitelistActivity.class);
                getActivity().startActivity(toWhitelist);
                break;
            case R.string.setting_title_export_whilelist:
                new ExportWhitelistTask(getActivity()).execute();
                break;
            case R.string.setting_title_import_whilelist:
                Intent importWhitelist = new Intent(Intent.ACTION_GET_CONTENT);
                importWhitelist.setType(IntentUnit.INTENT_TYPE_TEXT_PLAIN);
                importWhitelist.addCategory(Intent.CATEGORY_OPENABLE);
                getActivity().startActivityForResult(importWhitelist, IntentUnit.REQUEST_WHITELIST);
                break;
            case R.string.setting_title_token:
                Intent toToken = new Intent(getActivity(), TokenActivity.class);
                getActivity().startActivity(toToken);
                break;
            case R.string.setting_title_export_bookmarks:
                new ExportBookmarksTask(getActivity()).execute();
                break;
            case R.string.setting_title_import_bookmarks:
                Intent importBookmarks = new Intent(Intent.ACTION_GET_CONTENT);
                importBookmarks.setType(IntentUnit.INTENT_TYPE_TEXT_PLAIN);
                importBookmarks.addCategory(Intent.CATEGORY_OPENABLE);
                getActivity().startActivityForResult(importBookmarks, IntentUnit.REQUEST_BOOKMARKS);
                break;
            case R.string.setting_title_clear_control:
                Intent clearControl = new Intent(getActivity(), ClearActivity.class);
                getActivity().startActivityForResult(clearControl, IntentUnit.REQUEST_CLEAR);
                break;
            case R.string.setting_title_version:
                NinjaToast.show(getActivity(), R.string.toast_emoji);
                break;
            case R.string.setting_title_license:
                showLicenseDialog();
                break;
            case R.string.setting_title_donation:
                showDonationDialog();
                break;
            default:
                break;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        spChange = true;
        if (key.equals(getString(R.string.sp_search_engine))) {
            int num = Integer.valueOf(sp.getString(key, "0"));
            if (0 <= num && num <= 4) {
                searchEngine.setSummary(seEntries[num]);
            } else {
                searchEngine.setValue("5");
                searchEngine.setSummary(R.string.setting_summary_search_engine_custom);
            }
        } else if (key.equals(getString(R.string.sp_notification_priority))) {
            String summary = npEntries[Integer.valueOf(sp.getString(key, "0"))];
            notiPriority.setSummary(summary);
        } else if (key.equals(getString(R.string.sp_anchor))) {
            String summary = tpEntries[Integer.valueOf(sp.getString(key, "1"))];
            tabPosition.setSummary(summary);
            NinjaToast.show(getActivity(), R.string.toast_need_restart);
        } else if (key.equals(getString(R.string.sp_volume))) {
            String summary = vcEntries[Integer.valueOf(sp.getString(key, "1"))];
            volumeControl.setSummary(summary);
        } else if (key.equals(getString(R.string.sp_user_agent))) {
            int num = Integer.valueOf(sp.getString(key, "0"));
            if (0 <= num && num <= 1) {
                userAgent.setSummary(ucEntries[num]);
            } else {
                userAgent.setValue("2");
                userAgent.setSummary(R.string.setting_summary_user_agent_custom);
            }
        } else if (key.equals(getString(R.string.sp_rendering))) {
            String summary = rdEntries[Integer.valueOf(sp.getString(key, "0"))];
            rendering.setSummary(summary);
        } else if (key.equals(getString(R.string.sp_cookies))) {
            CookieManager manager = CookieManager.getInstance();
            manager.setAcceptCookie(sp.getBoolean(getString(R.string.sp_cookies), true));
        }
    }

    private void showLicenseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);

        FrameLayout layout = (FrameLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_list, null, false);
        builder.setView(layout);

        List<Map<String, String>> list = new ArrayList<>();
        String[] titles = getResources().getStringArray(R.array.license_titles);
        String[] contents = getResources().getStringArray(R.array.license_contents);
        String[] authors = getResources().getStringArray(R.array.license_authors);
        String[] urls = getResources().getStringArray(R.array.license_urls);
        for (int i = 0; i < 7; i++) {
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

    private void showDonationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);

        FrameLayout layout = (FrameLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_desc, null, false);
        TextView textView = (TextView) layout.findViewById(R.id.dialog_desc);
        textView.setText(R.string.dialog_content_donation);

        builder.setView(layout);
        builder.create().show();
    }
}
