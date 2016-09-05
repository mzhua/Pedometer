package com.wonders.xlab.pedometer.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.wonders.xlab.pedometer.util.DensityUtil;

import java.util.HashMap;
import java.util.Random;


/**
 * Created by hua on 16/9/5.
 */

public class WalkMinutesBarChart extends View {
    private HashMap<Long, Integer> mStepDatasMap;

    private Paint mDotLinePaint;
    private float mDotLineWithInPx;

    private Paint mBaseLinePaint;
    private float mBaseLineWithInPx;

    private Paint mBarPaint;
    private TextPaint mTextPaint;
    private int mBaseLineTimeHeight;

    public WalkMinutesBarChart(Context context) {
        super(context);
        init(context, null);
    }

    public WalkMinutesBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WalkMinutesBarChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WalkMinutesBarChart(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private Rect mTempTextBoundRect = new Rect();

    private void init(Context context, AttributeSet attrs) {
        // FIXME: 16/9/5
        /*if (attrs == null) {
            return;
        }*/

        setupDatas();

        mDotLineWithInPx = DensityUtil.dp2px(context, 1);
        mDotLinePaint = new Paint();
        mDotLinePaint.setStyle(Paint.Style.STROKE);
        mDotLinePaint.setColor(Color.GRAY);
        mDotLinePaint.setStrokeWidth(mDotLineWithInPx);
        mDotLinePaint.setPathEffect(new DashPathEffect(new float[]{10f, 5f}, 1));

        mBaseLineWithInPx = DensityUtil.dp2px(context, 2);
        mBaseLinePaint = new Paint();
        mBaseLinePaint.setStyle(Paint.Style.STROKE);
        mBaseLinePaint.setColor(Color.parseColor("#328de8"));
        mBaseLinePaint.setStrokeWidth(mBaseLineWithInPx);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.GRAY);
        mTextPaint.setTextSize(DensityUtil.sp2px(context, 12));
        mTextPaint.getTextBounds("0", 0, 1, mTempTextBoundRect);
        mBaseLineTimeHeight = mTempTextBoundRect.height();
    }

    private int mMaxStepValue = Integer.MIN_VALUE;

    @SuppressLint("UseSparseArrays")
    private void setupDatas() {
        mStepDatasMap = new HashMap<>();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 20; i++) {
            random.setSeed(System.currentTimeMillis() + i);
            int value = random.nextInt(30000);
            mStepDatasMap.put(System.currentTimeMillis() + i * 10000, value);
            //计算最大值
            if (value > mMaxStepValue) {
                mMaxStepValue = value;
            }
        }
        mMaxStepValue = (mMaxStepValue / 10 + 1) * 10;//去掉个位数
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int left = getPaddingLeft();
        int right = getMeasuredWidth() - getPaddingRight();

        int offsetBottom = mBaseLineTimeHeight + getPaddingBottom();//预留的底部时间的高度s
        float baseLineY = getMeasuredHeight() - mBaseLineWithInPx / 2 - offsetBottom;
        float firstDotLineY = mDotLineWithInPx / 2 + getPaddingTop();
        float secondDotLineY = (firstDotLineY + baseLineY) / 2;

        canvas.drawLine(left, firstDotLineY, right, firstDotLineY, mDotLinePaint);
        canvas.drawLine(left, secondDotLineY, right, secondDotLineY, mDotLinePaint);
        canvas.drawLine(left, baseLineY, right, baseLineY, mBaseLinePaint);

        drawLeftLegend(canvas, firstDotLineY, secondDotLineY);
        drawBaseLineTime(canvas);
    }

    private void drawLeftLegend(Canvas canvas, float firstDotLineY, float secondDotLineY) {
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(String.valueOf(mMaxStepValue), getPaddingLeft(), firstDotLineY + mBaseLineTimeHeight, mTextPaint);
        canvas.drawText(String.valueOf(mMaxStepValue / 2), getPaddingLeft(), secondDotLineY + mBaseLineTimeHeight, mTextPaint);
    }

    private void drawBaseLineTime(Canvas canvas) {
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        int contentWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();

        for (int i = 0; i < 4; i++) {
            int x = 0;
            String timeStr = "06:00";
            switch (i) {
                case 0:
                    x = contentWidth / 8;
                    timeStr = "06:00";
                    break;
                case 1:
                    x = 3 * contentWidth / 8;
                    timeStr = "12:00";
                    break;
                case 2:
                    x = 5 * contentWidth / 8;
                    timeStr = "18:00";
                    break;
                case 3:
                    x = 7 * contentWidth / 8;
                    timeStr = "00:00";
                    break;
            }
            x += getPaddingLeft();
            canvas.drawText(timeStr, x, getMeasuredHeight() - getPaddingBottom(), mTextPaint);
        }
    }
}
