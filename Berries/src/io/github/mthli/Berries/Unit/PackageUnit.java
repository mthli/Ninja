package io.github.mthli.Berries.Unit;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

public class PackageUnit {
    public static List<ResolveInfo> getBrowserList(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setDataAndType(Uri.parse("http://"), null);

        List<ResolveInfo> list =  context.getPackageManager().queryIntentActivities(
                intent,
                PackageManager.GET_INTENT_FILTERS
        );

        if (list != null && list.size() > 0) {
            int index = -1;
            for (int i = 0; i < list.size(); i++) {
                ResolveInfo info = list.get(i);
                if (info.activityInfo.packageName.equals(context.getPackageName())) {
                    index = i;
                    break;
                }
            }
            if (index > -1) {
                list.remove(index);
            }
        }

        return list;
    }

    public static String getDefaultBrowserPackageName(Context context) {
        List<ResolveInfo> list = getBrowserList(context);
        if (list != null && list.size() > 0) {
            return list.get(0).activityInfo.packageName;
        }

        return null;
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

    public static void clearPackagePreference(Context context) {
        context.getPackageManager().clearPackagePreferredActivities(context.getPackageName());
    }
}
