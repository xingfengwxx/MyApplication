package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * author : 王星星
 * date : 2023/5/29 14:34
 * email : 1099420259@qq.com
 * description :
 */
public class CustomCircleProgressBar extends View {
    private int progress = 0;
    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint textPaint;

    public CustomCircleProgressBar(Context context) {
        super(context);
        init();
    }

    public CustomCircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomCircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.GRAY);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(10f);

        progressPaint = new Paint();
        progressPaint.setColor(Color.BLUE);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(10f);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(50f);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int viewWidth = getWidth();
        int viewHeight = getHeight();
        int centerX = viewWidth / 2;
        int centerY = viewHeight / 2;
        int radius = Math.min(viewWidth, viewHeight) / 2 - 20;

        // 绘制背景圆
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint);

        // 绘制进度条
        RectF progressRect = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        float sweepAngle = 360 * ((float) progress / 100);
        canvas.drawArc(progressRect, -90, sweepAngle, false, progressPaint);

        // 绘制进度文本
        String progressText = progress + "%";
        canvas.drawText(progressText, centerX, centerY, textPaint);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate(); // 重新绘制视图
    }
}
