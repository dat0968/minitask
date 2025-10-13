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

import java.util.ArrayList;
import java.util.List;

public class ScreenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Intent.ACTION_SCREEN_ON.equals(action)) {
            Log.d("PowerReceiver", "🟢 Screen ON");
            int battery = getBatteryPercent(context);
            events.add(new ScreenEvent(true, System.currentTimeMillis(), battery, true));
        }
        else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            Log.d("PowerReceiver", "⚫ Screen OFF");
            int battery = getBatteryPercent(context);
            boolean isAwake = hasWakeLock(pm);
            events.add(new ScreenEvent(false, System.currentTimeMillis(), battery, isAwake));
        }
    }
    private boolean hasWakeLock(PowerManager pm) {
        try {
            // Nếu không ở chế độ Doze và màn hình tắt -> có thể đang có WakeLock
            return !pm.isDeviceIdleMode() && !pm.isInteractive();
        } catch (Exception e) {
            return false;
        }
    }
}
