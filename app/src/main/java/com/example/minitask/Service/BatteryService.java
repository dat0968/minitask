package com.example.minitask.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.minitask.BroadCastReceiver.PowerReceiver;
import com.example.minitask.BroadCastReceiver.ScreenReceiver;

public class BatteryService extends Service {
    private static final String CHANNEL_ID = "battery_tracking_channel";
    private PowerReceiver powerReceiver;
    private ScreenReceiver screenReceiver;
    Handler handler = new Handler();
    Runnable runnable;
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Battery Tracking Service")
                    .setContentText("Tracking charging session...")
                    .setSmallIcon(android.R.drawable.ic_lock_idle_charging)
                    .build();
        }
        startForeground(1, notification);
        powerReceiver = new PowerReceiver();
        screenReceiver = new ScreenReceiver();

        registerReceiver(powerReceiver, new IntentFilter(Intent.ACTION_POWER_CONNECTED));
        registerReceiver(powerReceiver, new IntentFilter(Intent.ACTION_POWER_DISCONNECTED));
        registerReceiver(screenReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(screenReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        runnable = new Runnable() {
            @Override
            public void run() {
                BatteryManager bm = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
                int currentNow = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);

                handler.postDelayed(this, 1000); // đọc lại sau 1 giây
            }
        };
        handler.post(runnable);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Battery Tracking Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }
}
