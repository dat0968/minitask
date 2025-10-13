package com.example.minitask.BroadCastReceiver;

import static android.content.ContentValues.TAG;
import static com.example.minitask.BroadCastReceiver.PowerReceiver.events;
import static com.example.minitask.BroadCastReceiver.PowerReceiver.getBatteryPercent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.example.minitask.Model.ScreenEvent;

public class WakeLockReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        // Chỉ xử lý khi màn hình đang TẮT
        if (!pm.isInteractive()) {
            int battery = getBatteryPercent(context);
            if (PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED.equals(action)) {
                boolean isIdle = pm.isDeviceIdleMode();
                if (isIdle) {
                    Log.d(TAG, "💤 Entering Deep Sleep (Doze Mode)");
                    // isAwake = false vì đang vào deep sleep
                    events.add(new ScreenEvent(false, System.currentTimeMillis(), battery, false));
                } else {
                    Log.d(TAG, "🔵 Exiting Deep Sleep (Awake)");
                    // isAwake = true vì thoát khỏi deep sleep
                    events.add(new ScreenEvent(false, System.currentTimeMillis(), battery, true));
                }
            }
        }
    }
}
