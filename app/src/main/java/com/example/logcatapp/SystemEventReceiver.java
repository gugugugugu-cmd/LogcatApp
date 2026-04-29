package com.example.logcatapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SystemEventReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        String action = intent.getAction();

        switch (action) {
            case Intent.ACTION_BATTERY_CHANGED:
                BatteryTracker.onBatteryChanged(context, intent);
                break;

            case Intent.ACTION_SCREEN_ON:
                LogStore.appendLog(context, "屏幕点亮");
                break;

            case Intent.ACTION_SCREEN_OFF:
                LogStore.appendLog(context, "屏幕关闭");
                break;

            case Intent.ACTION_POWER_CONNECTED:
                LogStore.appendLog(context, "已连接电源");
                break;

            case Intent.ACTION_POWER_DISCONNECTED:
                LogStore.appendLog(context, "已断开电源");
                break;
        }
    }
}