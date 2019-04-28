package com.mywings.appschedulling.stats;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class Utils {

    public static void hideAppIcon(Context context, String packageName) {
        PackageManager p = context.getPackageManager();
        Intent launchIntent = p.getLaunchIntentForPackage(packageName);
        String className = launchIntent.getComponent().getClassName();
        ComponentName componentName = new ComponentName(context, className);
        if ("com.mywings.appschedulling".equalsIgnoreCase(packageName))
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    public static void showAppIcon(Context context, String packageName) {
        PackageManager p = context.getPackageManager();
        Intent launchIntent = p.getLaunchIntentForPackage(packageName);
        String className = launchIntent.getComponent().getClassName();
        ComponentName componentName = new ComponentName(context, className);
        if ("com.mywings.appschedulling".equalsIgnoreCase(packageName))
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

}
