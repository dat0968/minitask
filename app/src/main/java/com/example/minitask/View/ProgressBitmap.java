package com.example.minitask.View;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.TypedValue;
import android.content.res.Resources;

public class ProgressBitmap {

    /**
     * Vẽ 1 bitmap duy nhất: phần progress + divider + phần empty
     *
     * @param width           tổng width bitmap
     * @param height          chiều cao bitmap
     * @param progressPercent 0..1 (ví dụ 0.7 = 70%)

     * @return bitmap
     */
    public static Bitmap createProgressWithDivider(int width, int height, float progressPercent, int dividerWidthDp, int dividerMarginDp) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // --- Chuyển dp sang px ---
        float dpToPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, Resources.getSystem().getDisplayMetrics());
        float dividerWidth = dividerWidthDp * dpToPx;
        float dividerMargin = dividerMarginDp * dpToPx;

        // Giới hạn progress từ 0 → 1
        progressPercent = Math.max(0f, Math.min(1f, progressPercent));

        // Tính chiều dài các phần
        float totalAvailableWidth = width - dividerWidth - 2 * dividerMargin;
        float progressWidth = totalAvailableWidth * progressPercent;
        float emptyWidth = totalAvailableWidth - progressWidth;

        // --- Góc bo ---
        float progressTopLeft = 8 * dpToPx;
        float progressTopRight = 16 * dpToPx;
        float progressBottomRight = 16 * dpToPx;
        float progressBottomLeft = 8 * dpToPx;

        float emptyTopLeft = 16 * dpToPx;
        float emptyTopRight = 8 * dpToPx;
        float emptyBottomRight = 8 * dpToPx;
        float emptyBottomLeft = 16 * dpToPx;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // --- Vẽ progress ---
        RectF progressRect = new RectF(0, 0, progressWidth, height);
        float[] progressRadii = new float[]{
                progressTopLeft, progressTopLeft,
                progressTopRight, progressTopRight,
                progressBottomRight, progressBottomRight,
                progressBottomLeft, progressBottomLeft
        };
        Path progressPath = new Path();
        progressPath.addRoundRect(progressRect, progressRadii, Path.Direction.CW);

        LinearGradient gradient = new LinearGradient(0, 0, progressWidth, 0,
                new int[]{0xFF40DFFF, 0xFFFF7BFF}, null, Shader.TileMode.CLAMP);
        paint.setShader(gradient);
        canvas.drawPath(progressPath, paint);

        // --- Vẽ divider ---
        float dividerStart = progressWidth + dividerMargin;
        float dividerEnd = dividerStart + dividerWidth;
        paint.setShader(null);
        paint.setColor(0xFFFF00FF); // màu magenta ví dụ
        canvas.drawRect(dividerStart, 0, dividerEnd, height, paint);

        // --- Vẽ phần empty ---
        float emptyStart = dividerEnd + dividerMargin;
        RectF emptyRect = new RectF(emptyStart, 0, width, height);
        float[] emptyRadii = new float[]{
                emptyTopLeft, emptyTopLeft,
                emptyTopRight, emptyTopRight,
                emptyBottomRight, emptyBottomRight,
                emptyBottomLeft, emptyBottomLeft
        };
        Path emptyPath = new Path();
        emptyPath.addRoundRect(emptyRect, emptyRadii, Path.Direction.CW);
        paint.setColor(0xFF1E1F36); // màu trống
        canvas.drawPath(emptyPath, paint);

        return bitmap;
    }
}
