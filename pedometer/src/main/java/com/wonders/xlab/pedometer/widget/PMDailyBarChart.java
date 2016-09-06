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

import com.wonders.xlab.pedometer.data.PMDataBean;
import com.wonders.xlab.pedometer.util.DensityUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by hua on 16/9/5.
 */

public class PMDailyBarChart extends View {

    private List<PMDataBean> mStepPMDataBeanList;

    private Paint mDotLinePaint;
    private float mDotLineWithInPx;

    private Paint mBaseLinePaint;
    private float mBaseLineWithInPx;

    private Paint mBarPaint;
    private TextPaint mTextPaint;
    private int mBaseLineTimeHeight;

    public PMDailyBarChart(Context context) {
        super(context);
        init(context, null);
    }

    public PMDailyBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PMDailyBarChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PMDailyBarChart(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private Rect mTempTextBoundRect = new Rect();

    private void init(Context context, AttributeSet attrs) {
        // FIXME: 16/9/5
        /*if (attrs == null) {
            return;
        }*/

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
        mTextPaint.getTextBounds("0", 0, 1, mTempTextBoundRect);
        mBaseLineTimeHeight = mTempTextBoundRect.height();

        mBarPaint = new Paint();
        mBarPaint.setColor(Color.parseColor("#328de8"));
        mBarPaint.setStrokeWidth(8);
        mBarPaint.setStyle(Paint.Style.STROKE);
    }

    private int mMaxStepValue = Integer.MIN_VALUE;

    @SuppressLint("UseSparseArrays")
    public void setDataBeanList(List<PMDataBean> PMDataBeanList) {
        if (null == PMDataBeanList || PMDataBeanList.size() == 0) {
            return;
        }
        if (mStepPMDataBeanList == null) {
            mStepPMDataBeanList = new ArrayList<>();
        } else {
            mStepPMDataBeanList.clear();
        }
        mStepPMDataBeanList.addAll(PMDataBeanList);

        Collections.sort(mStepPMDataBeanList, new Comparator<PMDataBean>() {
            @Override
            public int compare(PMDataBean o1, PMDataBean o2) {
                return o1.getStepCounts() > o2.getStepCounts() ? -1 : (o1.getStepCounts() == o2.getStepCounts() ? 0 : 1);
            }
        });

        mMaxStepValue = (mStepPMDataBeanList.get(0).getStepCounts() / 10 + 1) * 10;//去掉十位数
        if (mMaxStepValue < 100) {
            mMaxStepValue = 100;
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int left = getPaddingLeft();
        int right = getMeasuredWidth() - getPaddingRight();

        int offsetBottom = 3 * mBaseLineTimeHeight / 2 + getPaddingBottom();//预留的底部时间的高度s
        float baseLineY = getMeasuredHeight() - mBaseLineWithInPx / 2 - offsetBottom;
        float firstDotLineY = mDotLineWithInPx / 2 + getPaddingTop();
        float secondDotLineY = (firstDotLineY + baseLineY) / 2;

        canvas.drawLine(left, firstDotLineY, right, firstDotLineY, mDotLinePaint);
        canvas.drawLine(left, secondDotLineY, right, secondDotLineY, mDotLinePaint);
        canvas.drawLine(left, baseLineY, right, baseLineY, mBaseLinePaint);

        drawBaseLineTime(canvas);

        Calendar calendar = Calendar.getInstance();
        int contentWidth = right - left;
        float pxPerMinutes = contentWidth * 1.0f / 24 / 60;//每分钟对应的横坐标宽度
        mBarPaint.setStrokeWidth(pxPerMinutes * 14);
        if (mStepPMDataBeanList != null && mStepPMDataBeanList.size() > 0) {
            for (PMDataBean PMDataBean : mStepPMDataBeanList) {
                calendar.setTimeInMillis(PMDataBean.getTimeInMill());
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);
                minutes += hour * 60;
                float x = pxPerMinutes * minutes;
                canvas.drawLine(left + x, baseLineY, left + x, baseLineY - PMDataBean.getStepCounts() * 1.0f / mMaxStepValue * (baseLineY - firstDotLineY), mBarPaint);
            }
        }

        drawLeftLegend(canvas, firstDotLineY, secondDotLineY);
    }

    private void drawLeftLegend(Canvas canvas, float firstDotLineY, float secondDotLineY) {
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(String.valueOf(mMaxStepValue), getPaddingLeft(), firstDotLineY + 3 * mBaseLineTimeHeight / 2, mTextPaint);
        canvas.drawText(String.valueOf(mMaxStepValue / 2), getPaddingLeft(), secondDotLineY + 3 * mBaseLineTimeHeight / 2, mTextPaint);
    }

    private void drawBaseLineTime(Canvas canvas) {
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        int contentWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();

        for (int i = 0; i < 3; i++) {
            int x = 0;
            String timeStr = "06:00";
            switch (i) {
                case 0:
                    x = contentWidth / 4;
                    timeStr = "06:00";
                    break;
                case 1:
                    x = contentWidth / 2;
                    timeStr = "12:00";
                    break;
                case 2:
                    x = 3 * contentWidth / 4;
                    timeStr = "18:00";
                    break;
            }
            x += getPaddingLeft();
            canvas.drawText(timeStr, x, getMeasuredHeight() - getPaddingBottom(), mTextPaint);
        }
    }
}
