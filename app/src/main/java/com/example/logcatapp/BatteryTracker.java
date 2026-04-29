package com.example.logcatapp;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import java.util.Locale;

public class BatteryTracker {

    private static int lastLevel = -1;
    private static int lastTemp = -1;
    private static int lastStatus = -1;

    public static void onBatteryChanged(Context context, Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);

        int percent = scale > 0 ? (level * 100 / scale) : level;
        float tempC = temp / 10f;
        float voltageV = voltage / 1000f;

        boolean changed = percent != lastLevel || temp != lastTemp || status != lastStatus;
        if (!changed) return;

        lastLevel = percent;
        lastTemp = temp;
        lastStatus = status;

        String statusText;
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                statusText = "充电中";
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                statusText = "已充满";
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                statusText = "放电中";
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                statusText = "未充电";
                break;
            default:
                statusText = "状态未知";
                break;
        }

        String msg = String.format(Locale.getDefault(),
                "电量 %d%% 温度 %.1f°C 电压 %.2fV %s",
                percent, tempC, voltageV, statusText);

        LogStore.appendLog(context, msg);
    }
}