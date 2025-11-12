package com.example.minitask.Helper;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.example.minitask.Provider.CpuWidgetProvider;
import com.example.minitask.R;
import com.example.minitask.View.ProgressBitmap;

public class WidgetHelper {
    public static void updateCpuWidget(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName widget = new ComponentName(context, CpuWidgetProvider.class);
        int[] appWidgetIds = manager.getAppWidgetIds(widget);

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_widget_card);

            // --- Lấy kích thước widget thực tế (launcher cung cấp) ---
            Bundle options = manager.getAppWidgetOptions(appWidgetId);
            int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 200);
            int maxWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, minWidthDp);

            // Chuyển dp -> px theo density thực của thiết bị
            float density = context.getResources().getDisplayMetrics().density;
            int totalWidth = (int) (maxWidthDp * density);

            // --- Padding, margin, divider ---
            int padding = dpToPx(context, 16); // padding của widget_layout
            int dividerWidth = dpToPx(context, 1);
            int height = dpToPx(context, 20);

            // Tổng chiều rộng khả dụng thực tế bên trong widget
            int availableWidth = totalWidth - 2 * padding;

            // Giả lập progress %
            float progressPercent = 20f / 100f;

            // Tạo bitmap progress
            Bitmap bitmap = ProgressBitmap.createProgressWithDivider(
                    availableWidth,
                    height,
                    progressPercent,
                    dividerWidth,
                    3
            );

            // Set bitmap vào imageView
            views.setImageViewBitmap(R.id.imageViewProgress, bitmap);

            // --- Cập nhật widget ---
            manager.updateAppWidget(appWidgetId, views);
        }
    }

    private static int dpToPx(Context context, float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }
}
