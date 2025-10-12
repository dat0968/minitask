package com.example.minitask.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.minitask.BroadCastReceiver.PowerReceiver;
import com.example.minitask.BroadCastReceiver.ScreenReceiver;

public class BatteryService extends Service {
    private static final String CHANNEL_ID = "battery_tracking_channel";
    private PowerReceiver powerReceiver;
    private ScreenReceiver screenReceiver;
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
