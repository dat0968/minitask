package com.example.minitask.BroadCastReceiver;

import static android.content.ContentValues.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.PowerManager;
import android.util.Log;

import com.example.minitask.Model.ScreenEvent;

import java.util.ArrayList;
import java.util.List;

public class PowerReceiver extends BroadcastReceiver {
    public static final List<ScreenEvent> events = new ArrayList<>();
    long startTime = 0L, endTime = 0L;
    int startPercent, endPercent;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
            // Bắt dầu tính toán phạm vi discharging
            float totalOnPercentDischarging = 0f;
            float totalOffPercentDischarging = 0f;
            long totalOnTimeDischarging = 0;
            long totalOffTimeDischarging = 0;

            //float totalAwakePercent = 0f;
            //float totalDeepSleepPercent = 0f;
            long totalAwakeTime = 0;
            long totalDeepSleepTime = 0;

            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            for (int i = 0; i < events.size() - 1; i++) {
                ScreenEvent e1 = events.get(i);
                ScreenEvent e2 = events.get(i + 1);

                float deltaPercent = e2.batteryPercent - e1.batteryPercent;
                long deltaTime = e2.timestamp - e1.timestamp;
                if (e1.isOn) {
                    totalOnPercentDischarging += deltaPercent;
                    totalOnTimeDischarging += deltaTime;
                } else {
                    totalOffPercentDischarging += deltaPercent;
                    totalOffTimeDischarging += deltaTime;

                    // Phân loại screen off thành Awake và DeepSleep
                    if (e1.isAwake) {
//                        totalAwakePercent += deltaPercent;
                        totalAwakeTime += deltaTime;
                    } else {
                        //totalDeepSleepPercent += deltaPercent;
                        totalDeepSleepTime += deltaTime;
                    }
                }
            }


            Log.d(TAG, "===============================");
            Log.d(TAG, "🔋 Total session discharge: " + (endPercent - startPercent) + "%");
            Log.d(TAG, "🔋 Total session time: " + ((endTime - startTime) / 60000f) + "%");
            Log.d(TAG, "🟢 Screen ON: " + totalOnPercentDischarging + "% in " + (totalOnTimeDischarging / 60000f) + " min");
            Log.d(TAG, "⚫ Screen OFF: " + totalOffPercentDischarging + "% in " + (totalOffTimeDischarging / 60000f) + " min");
            Log.d(TAG, "-------------------------------");
            Log.d(TAG, "  ↳ 🔵 Awake: "  + "in " + String.format("%.2f", totalAwakeTime / 60000f) + " min");
            Log.d(TAG, "  ↳ 💤 Deep Sleep: " + "in " + String.format("%.2f", totalDeepSleepTime / 60000f) + " min");
            Log.d(TAG, "===============================");


            events.clear();
            Log.d(TAG, "⚡ Power connected → Start session");
            boolean isScreenOn = pm.isInteractive();
            int battery = getBatteryPercent(context);
            boolean isAwake = pm.isInteractive() || hasWakeLock(pm);
            events.add(new ScreenEvent(isScreenOn, System.currentTimeMillis(), battery, isAwake));

            startTime = System.currentTimeMillis();
            startPercent = getBatteryPercent(context);
        }
        else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
            Log.d(TAG, "🔌 Power disconnected → End session");
            endPercent = getBatteryPercent(context);
            endTime = System.currentTimeMillis();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            boolean isOn = pm.isInteractive();
            boolean isAwake = pm.isInteractive() || hasWakeLock(pm);
            events.add(new ScreenEvent(isOn, endTime, endPercent, isAwake));

            float totalOnPercent = 0f;
            float totalOffPercent = 0f;
            long totalOnTime = 0;
            long totalOffTime = 0;

            //float totalAwakePercent = 0f;
            //float totalDeepSleepPercent = 0f;
            long totalAwakeTime = 0;
            long totalDeepSleepTime = 0;
            for (int i = 0; i < events.size() - 1; i++) {
                ScreenEvent e1 = events.get(i);
                ScreenEvent e2 = events.get(i + 1);

                float deltaPercent = e2.batteryPercent - e1.batteryPercent;
                long deltaTime = e2.timestamp - e1.timestamp;

                if (e1.isOn) {
                    totalOnPercent += deltaPercent;
                    totalOnTime += deltaTime;
                } else {
                    totalOffPercent += deltaPercent;
                    totalOffTime += deltaTime;

                    // Phân loại screen OFF thành Awake và Deep Sleep
                    if (e1.isAwake) {
                        //totalAwakePercent += deltaPercent;
                        totalAwakeTime += deltaTime;
                    } else {
                        //totalDeepSleepPercent += deltaPercent;
                        totalDeepSleepTime += deltaTime;
                    }
                }
            }
            Log.d(TAG, "===============================");
            Log.d(TAG, "🔋 Total session charge: " + (endPercent - startPercent) + "%");
            Log.d(TAG, "🔋 Total session time: " + ((endTime - startTime) / 60000f) + "%");
            Log.d(TAG, "🟢 Screen ON: " + totalOnPercent + "% in " + (totalOnTime / 60000f) + " min");
            Log.d(TAG, "⚫ Screen OFF: " + totalOffPercent + "% in " + (totalOffTime / 60000f) + " min");
            Log.d(TAG, "-------------------------------");

            events.clear();
            // Bắt đầu tính toán trong phạm vi discharging
            boolean isScreenOn = pm.isInteractive();
            int battery = getBatteryPercent(context);
            isAwake = pm.isInteractive() || hasWakeLock(pm);
            events.add(new ScreenEvent(isScreenOn, System.currentTimeMillis(), battery, isAwake));
        }
    }
    public static int getBatteryPercent(Context context) {
        Intent batteryStatus = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        return (level * 100) / scale;
    }
    private boolean hasWakeLock(PowerManager pm) {
        // Kiểm tra xem có WakeLock nào đang active không
        // Chú ý: Cần permission DEVICE_POWER trong Android 9+
        try {
            return pm.isDeviceIdleMode() == false && pm.isInteractive() == false;
        } catch (Exception e) {
            return false;
        }
    }
}
