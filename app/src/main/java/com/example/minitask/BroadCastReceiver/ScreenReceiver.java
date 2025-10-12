package com.example.minitask.BroadCastReceiver;

import static android.content.ContentValues.TAG;

import static com.example.minitask.BroadCastReceiver.PowerReceiver.events;
import static com.example.minitask.BroadCastReceiver.PowerReceiver.getBatteryPercent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.minitask.Model.ScreenEvent;

import java.util.ArrayList;
import java.util.List;

public class ScreenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Chỉ xử lý nếu đang trong phiên sạc
        if (!PowerReceiver.sessionActive) {
            Log.d("ScreenReceiver", "Ignored screen event (no active charge session)");
            return;
        }
        String action = intent.getAction();
        if (Intent.ACTION_SCREEN_ON.equals(action)) {
            Log.d("PowerReceiver", "🟢 Screen ON");
            int battery = getBatteryPercent(context);
            events.add(new ScreenEvent(true, System.currentTimeMillis(), battery));
        }
        else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            Log.d("PowerReceiver", "⚫ Screen OFF");
            int battery = getBatteryPercent(context);
            events.add(new ScreenEvent(false, System.currentTimeMillis(), battery));
        }
    }
}
