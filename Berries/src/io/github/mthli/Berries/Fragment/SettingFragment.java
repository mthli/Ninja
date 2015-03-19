package io.github.mthli.Berries.Fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.view.View;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Unit.PackageUnit;

public class SettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(android.R.id.list).setVerticalScrollBarEnabled(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Preference preference = getPreferenceManager().findPreference(getString(R.string.sp_setting_secondary_browser));
        String packageName = preference.getSharedPreferences().getString(getString(R.string.sp_setting_secondary_browser), null);
        if (packageName == null) {
            ActivityInfo info = PackageUnit.getDefaultBrowserInfo(getActivity());
            preference.getEditor().putString(getString(R.string.sp_setting_secondary_browser), info.packageName).commit();
            preference.setSummary(PackageUnit.getBrowserName(getActivity(), info.packageName));
        } else {
            preference.setSummary(PackageUnit.getBrowserName(getActivity(), packageName));
        }

        preference = getPreferenceManager().findPreference(getString(R.string.sp_setting_text_scaling));
        // TODO
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen screen, @NonNull Preference preference) {
        String key = preference.getKey();
        if (key.equals(getString(R.string.sp_setting_secondary_browser))) {
            // TODO
        } else if (key.equals(getString(R.string.sp_setting_double_taps))) {
            // TODO
        } else if (key.equals(getString(R.string.sp_setting_text_scaling))) {
            // TODO
        } else if (key.equals(getString(R.string.sp_setting_history))) {
            // TODO
        } else if (key.equals(getString(R.string.sp_setting_clear_defaults))) {
            // TODO
        }

        return super.onPreferenceTreeClick(screen, preference);
    }
}
