package com.huasu.bess4c48.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.huasu.bess4c48.utils.ToolUtil;

public class CircleProgressView extends View {
    // 画笔对象
    private Paint backgroundPaint;
    private Paint progressPaint;

    // 进度相关参数
    private float progress = 0F;  // 默认进度值
    private final float maxProgress = 100f;
    private float strokeWidth = 60f; // 圆环宽度

    // 颜色参数
    private int bgColor = Color.parseColor("#2600CE80");      // 背景圆环颜色
    private int progressColor = Color.parseColor("#00CE80");// 进度圆环颜色
    private int textColor = Color.parseColor("#333333");    // 文字颜色

    // 三个构造方法 必须全部实现，适配xml布局+代码创建
    public CircleProgressView(Context context) {
        super(context);
        initPaint();
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    /**
     * 初始化所有画笔和画笔属性
     */
    private void initPaint() {
        strokeWidth = ToolUtil.dp2px(15);
        // 背景圆环画笔
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 抗锯齿
        backgroundPaint.setStyle(Paint.Style.STROKE);       // 描边模式（空心）
        backgroundPaint.setStrokeWidth(strokeWidth);        // 圆环宽度
        backgroundPaint.setColor(bgColor);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);      // 圆环两端圆角
        // 进度圆环画笔
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setColor(progressColor);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
//        progressPaint.setShadowLayer(9f, 0f, 0f, progressColor);
    }

    /**
     * 对外提供的设置进度的方法
     * @param progress 进度值 自动限制在 0-100 之间
     */
    public void setProgress(float progress) {
        // 边界处理，防止进度值小于0 或 大于100
        this.progress = Math.max(0, Math.min(progress, maxProgress));
        invalidate(); // 刷新绘制，重新调用onDraw方法
    }

    /**
     * 核心绘制方法：绘制背景圆环、进度圆环、百分比文字
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 获取控件的中心点坐标
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        // 计算圆环半径 = 控件最小边的一半 - 圆环宽度的一半（防止圆环超出控件范围）
        float radius = (Math.min(getWidth(), getHeight()) / 2f) - strokeWidth / 2f;

        // ========== 1. 绘制背景灰色圆环 ==========
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint);

        // ========== 2. 绘制彩色进度圆环 ==========
        RectF rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        float sweepAngle = (progress / maxProgress) * 360f; // 计算进度对应的圆弧角度
        // -90f 表示从圆环正顶部开始绘制，false表示绘制空心圆弧
        canvas.drawArc(rectF, -90f, sweepAngle, false, progressPaint);
    }
}
