package com.example.minitask;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minitask.Service.BatteryService;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalTime, tvTotalPercent, tvScreenOnTime, tvScreenOnPercent, tvScreenOffTime, tvScreenOffPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // Mở màn hình Settings để user cấp quyền
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        // Bật sáng màn hình nếu đang tắt
        /*Kiểm tra màn hình có tắt không, nếu tắt thì bật sáng trong 5 giây*/
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm != null && !pm.isInteractive()) { // nếu màn hình đang tắt
            PowerManager.WakeLock wakeLock = pm.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK
                            | PowerManager.ACQUIRE_CAUSES_WAKEUP
                            | PowerManager.ON_AFTER_RELEASE,
                    "BatteryApp:WakeLock");
            wakeLock.acquire(5000); // giữ sáng 5 giây
        }

        // Cho phép hiển thị khi đang khóa và bật sáng màn hình khi khởi động
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true); // cho phép hiển thị khi màn hình khóa
            setTurnScreenOn(true); // khi activity mở thì bật sáng màn hình
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON // Giữ màn hình bật sáng liên tục
            );
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, BatteryService.class));
        }
        tvTotalTime = findViewById(R.id.tvTotalTime);
        tvTotalPercent = findViewById(R.id.tvTotalPercent);
        tvScreenOnTime = findViewById(R.id.tvScreenOnTime);
        tvScreenOnPercent = findViewById(R.id.tvScreenOnPercent);
        tvScreenOffTime = findViewById(R.id.tvScreenOffTime);
        tvScreenOffPercent = findViewById(R.id.tvScreenOffPercent);
        Button btnViewDetail = findViewById(R.id.btnViewDetail);

        // Giả lập dữ liệu demo (sau này sẽ lấy từ SessionManager)
        int totalPercent = 10;
        int onPercent = 3;
        int offPercent = 7;
        long totalTime = 2 * 60 * 60 * 1000; // 2 tiếng
        long onTime = 25 * 60 * 1000;        // 25 phút
        long offTime = totalTime - onTime;

        // Hiển thị
        tvTotalTime.setText("Tổng thời gian sạc: " + formatDuration(totalTime));
        tvTotalPercent.setText("Tổng % sạc được: " + totalPercent + "%");
        tvScreenOnTime.setText("Thời gian Screen ON: " + formatDuration(onTime));
        tvScreenOnPercent.setText("% sạc khi Screen ON: " + onPercent + "%");
        tvScreenOffTime.setText("Thời gian Screen OFF: " + formatDuration(offTime));
        tvScreenOffPercent.setText("% sạc khi Screen OFF: " + offPercent + "%");

        btnViewDetail.setOnClickListener(v -> {
            // sau này có thể mở 1 Activity khác hiển thị lịch sử event chi tiết
        });
    }

    private String formatDuration(long millis) {
        long mins = (millis / 1000) / 60;
        long hours = mins / 60;
        mins = mins % 60;
        if (hours > 0) return hours + "h " + mins + "m";
        else return mins + " phút";
    }
}