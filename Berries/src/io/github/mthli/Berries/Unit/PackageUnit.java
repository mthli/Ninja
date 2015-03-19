package io.github.mthli.Berries.Unit;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

public class PackageUnit {

    public static ActivityInfo getDefaultBrowserInfo(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setDataAndType(Uri.parse("https://"), null);

        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(
                intent,
                PackageManager.GET_INTENT_FILTERS
        );
        if (list.size() > 0) {
            return list.get(0).activityInfo;
        } else {
            return null;
        }
    }

    public static String getBrowserName(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();

        try {
            ApplicationInfo info = manager.getApplicationInfo(packageName, 0);
            return info.loadLabel(manager).toString();
        } catch (PackageManager.NameNotFoundException n) {
            return null;
        }
    }
}
