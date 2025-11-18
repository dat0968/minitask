package com.example.minitask.Helper;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

public class SettingsHelper {

    private final Context context;

    public SettingsHelper(Context context) {
        this.context = context;
    }

    public boolean isWifiOn() {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifi != null && wifi.isWifiEnabled();
    }
    public boolean isBluetoothOn() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter != null && adapter.isEnabled();
    }

    public boolean isGPSEnabled() {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm != null && lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isMobileDataOn() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                // Kiểm tra nếu đang kết nối qua Mobile Data
                return activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE
                        && activeNetwork.isConnected();
            }
        }
        return false;
    }

    public boolean isSyncOn() {
        return ContentResolver.getMasterSyncAutomatically();
    }

    public boolean isSoundOn() {
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audio != null && audio.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }
}

