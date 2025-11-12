package com.example.minitask.Provider;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

import com.example.minitask.Helper.WidgetHelper;

public class CpuWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "CpuWidgetProvider";
    private static final String ACTION_UPDATE_CPU = "com.yourpackage.ACTION_UPDATE_CPU";
    private static final int UPDATE_INTERVAL_MS = 2000; // Cập nhật mỗi 2 giây

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // Cập nhật ngay lập tức khi widget được thêm hoặc hệ thống yêu cầu
        WidgetHelper.updateCpuWidget(context);
    }
}