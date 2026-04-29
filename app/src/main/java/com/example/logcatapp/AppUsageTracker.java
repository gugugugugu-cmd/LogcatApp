package com.example.logcatapp;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

public class AppUsageTracker {

    private static String currentPackage = null;
    private static long currentStartTime = 0L;
    private static long lastCheckTime = 0L;

    public static void pollForegroundApp(Context context) {
        long now = System.currentTimeMillis();
        long begin = (lastCheckTime == 0L) ? (now - 10_000) : lastCheckTime;
        lastCheckTime = now;

        UsageStatsManager usm =
                (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if (usm == null) return;

        UsageEvents usageEvents = usm.queryEvents(begin, now);
        UsageEvents.Event event = new UsageEvents.Event();

        String latestForegroundPackage = currentPackage;
        long latestForegroundTime = currentStartTime;

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED) {
                    latestForegroundPackage = event.getPackageName();
                    latestForegroundTime = event.getTimeStamp();
                }
            } else {
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    latestForegroundPackage = event.getPackageName();
                    latestForegroundTime = event.getTimeStamp();
                }
            }
        }

        if (latestForegroundPackage == null) return;

        if (currentPackage == null) {
            currentPackage = latestForegroundPackage;
            currentStartTime = latestForegroundTime;
            logOpen(context, currentPackage);
            return;
        }

        if (!currentPackage.equals(latestForegroundPackage)) {
            long duration = Math.max(0, now - currentStartTime);
            logClose(context, currentPackage, duration);

            currentPackage = latestForegroundPackage;
            currentStartTime = latestForegroundTime;
            logOpen(context, currentPackage);
        }
    }

    private static void logOpen(Context context, String pkg) {
        LogStore.appendLog(context, "打开 " + getAppLabel(context, pkg) + " " + pkg);
    }

    private static void logClose(Context context, String pkg, long durationMs) {
        LogStore.appendLog(context,
                "关闭 " + getAppLabel(context, pkg) + " " + pkg + " 运行" + formatDuration(durationMs));
    }

    private static String getAppLabel(Context context, String pkg) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
            CharSequence label = pm.getApplicationLabel(ai);
            return label == null ? pkg : label.toString();
        } catch (Exception e) {
            return pkg;
        }
    }

    private static String formatDuration(long durationMs) {
        long totalSeconds = durationMs / 1000;
        long h = totalSeconds / 3600;
        long m = (totalSeconds % 3600) / 60;
        long s = totalSeconds % 60;

        if (h > 0) {
            return h + "小时" + m + "分钟" + s + "秒";
        } else if (m > 0) {
            return m + "分钟" + s + "秒";
        } else {
            return s + "秒";
        }
    }
}