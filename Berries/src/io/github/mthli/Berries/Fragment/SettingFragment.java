package io.github.mthli.Berries.Fragment;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;
import io.github.mthli.Berries.Activity.HistoryActivity;
import io.github.mthli.Berries.Dialog.DialogUnit;
import io.github.mthli.Berries.Dialog.PreferenceDialogListItem;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Unit.PackageUnit;

import java.util.ArrayList;
import java.util.List;

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
            packageName = PackageUnit.getDefaultBrowserPackageName(getActivity());
            preference.getEditor().putString(getString(R.string.sp_setting_secondary_browser), packageName).commit();
            if (packageName != null) {
                preference.setSummary(PackageUnit.getBrowserName(getActivity(), packageName));
            }
        } else {
            preference.setSummary(PackageUnit.getBrowserName(getActivity(), packageName));
        }

        preference = getPreferenceManager().findPreference(getString(R.string.sp_setting_text_size));
        int textSize = preference.getSharedPreferences().getInt(
                getString(R.string.sp_setting_text_size),
                getResources().getInteger(R.integer.text_size_default)
        );
        preference.setSummary(String.valueOf(textSize));
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen screen, @NonNull Preference preference) {
        String key = preference.getKey();
        if (key.equals(getString(R.string.sp_setting_secondary_browser))) {
            List<PreferenceDialogListItem> list = new ArrayList<PreferenceDialogListItem>();
            for (ResolveInfo info : PackageUnit.getBrowserList(getActivity())) {
                PreferenceDialogListItem item = new PreferenceDialogListItem();
                item.setTitle(PackageUnit.getBrowserName(getActivity(), info.activityInfo.packageName));
                item.setContent(info.activityInfo.packageName);
                list.add(item);
            }
            DialogUnit.show(getActivity(), list, preference);
        } else if (key.equals(getString(R.string.sp_setting_text_size))) {
            DialogUnit.show(getActivity(), preference);
        } else if (key.equals(getString(R.string.sp_setting_history))) {
            Intent history = new Intent(getActivity(), HistoryActivity.class);
            startActivity(history);
        } else if (key.equals(getString(R.string.sp_setting_clear_defaults))) {
            PackageUnit.clearPackagePreference(getActivity());
            Toast.makeText(getActivity(), R.string.setting_toast_clear_defaults, Toast.LENGTH_SHORT).show();
        }

        return super.onPreferenceTreeClick(screen, preference);
    }
}
