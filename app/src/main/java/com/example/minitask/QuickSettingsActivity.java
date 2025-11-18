package com.example.minitask;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.example.minitask.Helper.SettingsHelper;

public class QuickSettingsActivity extends AppCompatActivity {

    SettingsHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_settings);


        helper = new SettingsHelper(this);


        // Gán sự kiện click
        findViewById(R.id.btnWifi).setOnClickListener(v -> toggleWifi(v));
        findViewById(R.id.btnData).setOnClickListener(v -> toggleData(v));
        findViewById(R.id.btnBluetooth).setOnClickListener(v -> toggleBluetooth(v));
        findViewById(R.id.btnSound).setOnClickListener(v -> toggleSound(v));
        findViewById(R.id.btnGPS).setOnClickListener(v -> openGPSSettings());
        findViewById(R.id.btnSync).setOnClickListener(v -> toggleSync(v));
        findViewById(R.id.btnBrightness).setOnClickListener(v -> toggleDisplaySettings(v));
        findViewById(R.id.btnTimeout).setOnClickListener(v -> toggleTimeoutSettings(v));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupItem(findViewById(R.id.btnWifi), "Wi-Fi", helper.isWifiOn());
        setupItem(findViewById(R.id.btnData), "Data", helper.isMobileDataOn());
        setupItem(findViewById(R.id.btnBluetooth), "Bluetooth", helper.isBluetoothOn());
        setupItem(findViewById(R.id.btnSync), "Sync", helper.isSyncOn());
        setupItem(findViewById(R.id.btnSound), "Sound", helper.isSoundOn());
        setupItem(findViewById(R.id.btnBrightness), "Brightness", true);
        setupItem(findViewById(R.id.btnTimeout), "Timeout", true);
        setupItem(findViewById(R.id.btnGPS), "GPS", helper.isGPSEnabled());
    }

    private void setupItem(View includeLayout, String label, boolean isOn) {
        TextView txt = includeLayout.findViewById(R.id.txtLabel);
        FrameLayout circle = includeLayout.findViewById(R.id.iconContainer);

        txt.setText(label);
        circle.setAlpha(isOn ? 1f : 0.3f);
    }
    /** ------------------- ACTIONS ------------------- **/
    private void toggleTimeoutSettings(View view) {
        ContentResolver resolver = getContentResolver();

        // Các mức timeout (ms)
        int[] timeouts = {15000, 30000, 60000, 120000, 300000, 600000};
        String[] labels = {"15s", "30s", "1m", "2m", "5m", "10m"};

        try {
            int currentTimeout = Settings.System.getInt(resolver, Settings.System.SCREEN_OFF_TIMEOUT);

            // Tìm index hiện tại trong mảng
            int index = 0;
            for (int i = 0; i < timeouts.length; i++) {
                if (currentTimeout <= timeouts[i]) {
                    index = i;
                    break;
                }
            }

            // Lấy timeout kế tiếp theo vòng tròn
            int nextIndex = (index + 1) % timeouts.length;
            int newTimeout = timeouts[nextIndex];
            String label = labels[nextIndex];

            // Kiểm tra quyền WRITE_SETTINGS
            boolean canWrite = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                canWrite = Settings.System.canWrite(this);
            }

            if (canWrite) {
                Settings.System.putInt(resolver, Settings.System.SCREEN_OFF_TIMEOUT, newTimeout);
                Toast.makeText(this, "Screen Timeout: " + label, Toast.LENGTH_SHORT).show();

                // Update alpha icon: max 1f nếu >=1 phút, 0.5f nếu <1 phút
                float alpha = newTimeout >= 60000 ? 1f : 0.5f;
                ((FrameLayout)view.findViewById(R.id.iconContainer)).setAlpha(alpha);
            } else {
                Toast.makeText(this, "No permission to change timeout", Toast.LENGTH_SHORT).show();
                openDisplaySettings();
            }

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            openDisplaySettings();
        }
    }

    // Data
    private void toggleData(View view){
        try {
            Intent intent = new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
            view.getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Wi-Fi
    private void toggleWifi(View view) {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifi != null) {
            boolean newState = !wifi.isWifiEnabled();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                wifi.setWifiEnabled(newState);
                ((FrameLayout)view.findViewById(R.id.iconContainer)).setAlpha(newState ? 1f : 0.3f);
                Toast.makeText(this, "Wi-Fi " + (newState ? "ON" : "OFF"), Toast.LENGTH_SHORT).show();
            } else {
                // Android 10+ không cho bật/tắt trực tiếp, mở Setting
                openWifiSettings();
            }
        }
    }

    private void openWifiSettings() {
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    // Bluetooth
    private void toggleBluetooth(View view) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            return;
        }

        // Kiểm tra và yêu cầu quyền
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // Yêu cầu quyền thay vì chỉ return
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                        100);
                return;
            }
        }

        FrameLayout iconContainer = view.findViewById(R.id.iconContainer);

        if (adapter.isEnabled()) {
            adapter.disable();
            // Cập nhật ngay lập tức cho trải nghiệm người dùng
            iconContainer.setAlpha(0.3f);
        } else {
            adapter.enable();
            iconContainer.setAlpha(1f);
        }

        // Cập nhật lại sau 1 giây để đảm bảo chính xác
        new Handler().postDelayed(() -> {
            boolean isEnabled = adapter.isEnabled();
            float alpha = isEnabled ? 1f : 0.3f;
            iconContainer.setAlpha(alpha);
        }, 1000);
    }

    // Sound
    private void toggleSound(View view) {
        AudioManager audio = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audio != null) {
            // Kiểm tra và yêu cầu quyền truy cập chính sách thông báo (tự code)

            int currentMode = audio.getRingerMode();
            int newMode;
            String modeName;

            // Chuyển đổi chế độ âm thanh
            switch (currentMode) {
                case AudioManager.RINGER_MODE_NORMAL:
                    newMode = AudioManager.RINGER_MODE_VIBRATE;
                    modeName = "Chế độ rung";
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    newMode = AudioManager.RINGER_MODE_SILENT;
                    modeName = "Chế độ im lặng";
                    break;
                case AudioManager.RINGER_MODE_SILENT:
                    newMode = AudioManager.RINGER_MODE_NORMAL;
                    modeName = "Chế độ âm thanh";
                    break;
                default:
                    newMode = AudioManager.RINGER_MODE_NORMAL;
                    modeName = "Chế độ âm thanh";
            }

            // Thử đặt chế độ trực tiếp
            try {
                audio.setRingerMode(newMode);
                updateSoundUI(view, newMode);
                showSoundDialog(modeName);
            } catch (SecurityException e) {
                // Log chi tiết nguyên nhân lỗi
                Log.e("ToggleSound", "SecurityException: " + e.getMessage());
                Log.e("ToggleSound", "Lỗi chi tiết:", e);

                // Log thông tin thiết bị và quyền
                Log.d("ToggleSound", "Android API: " + Build.VERSION.SDK_INT);
                Log.d("ToggleSound", "Device: " + Build.MANUFACTURER + " " + Build.MODEL);

                // Kiểm tra quyền cụ thể
                int audioPermission = checkSelfPermission(android.Manifest.permission.MODIFY_AUDIO_SETTINGS);
                int vibratePermission = checkSelfPermission(android.Manifest.permission.VIBRATE);

                Log.d("ToggleSound", "MODIFY_AUDIO_SETTINGS permission: " +
                        (audioPermission == PackageManager.PERMISSION_GRANTED ? "GRANTED" : "DENIED"));
                Log.d("ToggleSound", "VIBRATE permission: " +
                        (vibratePermission == PackageManager.PERMISSION_GRANTED ? "GRANTED" : "DENIED"));

                // Hiển thị thông báo cho người dùng
                Toast.makeText(this, "Không có quyền thay đổi âm thanh: " + e.getMessage(), Toast.LENGTH_LONG).show();

                // Nếu không có quyền, mở cài đặt âm thanh
                openSoundSettings();
            }
        }
    }
    private void showSoundDialog(String modeName) {
        new AlertDialog.Builder(this)
                .setTitle("Chế độ âm thanh")
                .setMessage("Đã chuyển sang: " + modeName)
                .setPositiveButton("OK", null)
                .show();
    }
    private void openSoundSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_SOUND_SETTINGS);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Không thể mở cài đặt âm thanh", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateSoundUI(View view, int ringerMode) {
        FrameLayout iconContainer = view.findViewById(R.id.iconContainer);
        float alpha = (ringerMode == AudioManager.RINGER_MODE_NORMAL) ? 1f : 0.3f;
        iconContainer.setAlpha(alpha);
    }
    // GPS
    private void openGPSSettings() {
        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    // Sync
    private void toggleSync(View view) {
        if (checkSyncPermission()) {
            // Thử bật/tắt trực tiếp
            boolean isSyncEnabled = ContentResolver.getMasterSyncAutomatically();
            ContentResolver.setMasterSyncAutomatically(!isSyncEnabled);

            // Cập nhật giao diện
            updateSyncUI(view, !isSyncEnabled);
        } else {
            // Không có quyền, mở cài đặt
            openSyncSettings();
        }
    }
    private boolean checkSyncPermission() {
        // Kiểm tra quyền WRITE_SYNC_SETTINGS
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_SYNC_SETTINGS) == PackageManager.PERMISSION_GRANTED;
    }
    private void updateSyncUI(View view, boolean isEnabled) {
        FrameLayout iconContainer = view.findViewById(R.id.iconContainer);
        float alpha = isEnabled ? 1f : 0.3f;
        iconContainer.setAlpha(alpha);
    }

    private void openSyncSettings() {
        try {
            startActivity(new Intent(Settings.ACTION_SYNC_SETTINGS));
        } catch (Exception e) {
            Toast.makeText(this, "Không thể mở cài đặt đồng bộ", Toast.LENGTH_SHORT).show();
        }
    }
    // Brightness & Timeout
    private void toggleDisplaySettings(View view) {
        ContentResolver resolver = getContentResolver();

        try {
            int mode = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS_MODE);

            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Toast.makeText(this, "Brightness is Auto. Open Settings to change.", Toast.LENGTH_SHORT).show();
                openDisplaySettings();
                return;
            }

            // Manual mode
            int brightness = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
            int newBrightness;
            String label;

            // Chia 4 mức theo giá trị
            if (brightness <= 50) {
                newBrightness = 100;
                label = "Tối";
            } else if (brightness <= 100) {
                newBrightness = 180;
                label = "Sáng 50%";
            } else if (brightness <= 180) {
                newBrightness = 255;
                label = "Sáng cao";
            } else {
                newBrightness = 30;
                label = "Tối hơn nữa";
            }

            // Kiểm tra quyền WRITE_SETTINGS
            boolean canWrite = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                canWrite = Settings.System.canWrite(this);
            }

            if (canWrite) {
                Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, newBrightness);
                ((FrameLayout)view.findViewById(R.id.iconContainer)).setAlpha(newBrightness / 255f);
                Toast.makeText(this, "Brightness: " + label, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No permission to change brightness", Toast.LENGTH_SHORT).show();
                openDisplaySettings();
            }

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            openDisplaySettings();
        }

    }
    // Brightness & Timeout
    private void openDisplaySettings() {
        startActivity(new Intent(Settings.ACTION_DISPLAY_SETTINGS));
    }
}