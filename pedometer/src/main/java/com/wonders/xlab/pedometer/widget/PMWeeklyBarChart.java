package com.wonders.xlab.pedometer.widget;

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

/**
 * TODO: document your custom view class.
 */
public class PMWeeklyBarChart extends View {

    private Paint mDotLinePaint;
    private float mDotLineWithInPx;

    private Paint mBaseLinePaint;
    private float mBaseLineWithInPx;

    private Paint mBarPaint;

    private TextPaint mTextPaint;

    private int mBaseLineWeekTextHeight;
    private int mMaxStepValue;
    private float mBaseLineTimeHeight;

    public PMWeeklyBarChart(Context context) {
        super(context);
        init(context, null);
    }

    public PMWeeklyBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PMWeeklyBarChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PMWeeklyBarChart(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private Rect mTempTextBoundRect = new Rect();

    private void init(Context context, AttributeSet attrs) {

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mDotLineWithInPx = DensityUtil.dp2px(context, 1);
        mDotLinePaint = new Paint();
        mDotLinePaint.setStyle(Paint.Style.STROKE);
        mDotLinePaint.setColor(Color.GRAY);
        mDotLinePaint.setStrokeWidth(mDotLineWithInPx);
        mDotLinePaint.setPathEffect(new DashPathEffect(new float[]{10, 5, 10, 5}, 0));

        mBaseLineWithInPx = DensityUtil.dp2px(context, 2);
        mBaseLinePaint = new Paint();
        mBaseLinePaint.setStyle(Paint.Style.STROKE);
        mBaseLinePaint.setColor(Color.parseColor("#328de8"));
        mBaseLinePaint.setStrokeWidth(mBaseLineWithInPx);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.GRAY);
        mTextPaint.setTextSize(DensityUtil.sp2px(context, 12));
        mTextPaint.getTextBounds("周", 0, 1, mTempTextBoundRect);
        mBaseLineWeekTextHeight = mTempTextBoundRect.height();

        mBarPaint = new Paint();
        mBarPaint.setColor(Color.parseColor("#328de8"));
        mBarPaint.setStrokeWidth(8);
        mBarPaint.setStyle(Paint.Style.STROKE);

    }

    private int mContentLeft;
    private int mContentRight;
    private int mContentWidth;
    private float mBarStrokeWidth;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mContentLeft = getPaddingLeft();
        mContentRight = getMeasuredWidth() - getPaddingRight();
        mContentWidth = mContentRight - mContentLeft;
        mBarStrokeWidth = mContentWidth / 14;
        mBarPaint.setStrokeWidth(mBarStrokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void drawLeftLegend(Canvas canvas, float firstDotLineY, float secondDotLineY) {
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(String.valueOf(mMaxStepValue), getPaddingLeft(), firstDotLineY + 3 * mBaseLineTimeHeight / 2, mTextPaint);
        canvas.drawText(String.valueOf(mMaxStepValue / 2), getPaddingLeft(), secondDotLineY + 3 * mBaseLineTimeHeight / 2, mTextPaint);
    }

    private void drawBaseLineTime(Canvas canvas) {
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        for (int i = 0; i < 7; i++) {
            int x = 0;
            String timeStr = "06:00";
            switch (i) {
                case 0:
                    x = mContentWidth / 4;
                    timeStr = "06:00";
                    break;
                case 1:
                    x = mContentWidth / 2;
                    timeStr = "12:00";
                    break;
                case 2:
                    x = 3 * mContentWidth / 4;
                    timeStr = "18:00";
                    break;
            }
            x += getPaddingLeft();
            canvas.drawText(timeStr, x, getMeasuredHeight() - getPaddingBottom(), mTextPaint);
        }
    }
}
