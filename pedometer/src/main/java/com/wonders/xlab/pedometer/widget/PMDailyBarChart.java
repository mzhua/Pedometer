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
import com.wonders.xlab.pedometer.util.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by hua on 16/9/5.
 */

public class PMDailyBarChart extends View {

    /**
     * 每根柱子对应的分钟数
     */
    private final int MINUTES_PER_BAR = 14;
    private List<PMDataBean> mStepPMDataBeanList;

    private Paint mDotLinePaint;
    private float mDotLineWidthInPx;

    private Paint mBaseLinePaint;
    private float mBaseLineWidthInPx;

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

        mDotLineWidthInPx = DensityUtil.dp2px(context, 1);
        mDotLinePaint = new Paint();
        mDotLinePaint.setStyle(Paint.Style.STROKE);
        mDotLinePaint.setColor(Color.GRAY);
        mDotLinePaint.setStrokeWidth(mDotLineWidthInPx);
        mDotLinePaint.setPathEffect(new DashPathEffect(new float[]{10, 5, 10, 5}, 0));

        mBaseLineWidthInPx = DensityUtil.dp2px(context, 2);
        mBaseLinePaint = new Paint();
        mBaseLinePaint.setStyle(Paint.Style.STROKE);
        mBaseLinePaint.setColor(Color.parseColor("#328de8"));
        mBaseLinePaint.setStrokeWidth(mBaseLineWidthInPx);

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

    private int mMaxStepValue = 100;

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

    private int mContentLeft;
    private int mContentRight;
    private int mContentWidth;
    private float mPxPerMinutes;//每分钟对应的横坐标宽度
    private float mBarStrokeWidth;
    private int offsetBottom;//预留的底部时间的高度s

    private float baseLineY;
    private float firstDotLineY;
    private float secondDotLineY;

    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        offsetBottom = 3 * mBaseLineTimeHeight / 2 + getPaddingBottom();
        baseLineY = getMeasuredHeight() - mBaseLineWidthInPx / 2 - offsetBottom;
        firstDotLineY = mDotLineWidthInPx / 2 + getPaddingTop();
        secondDotLineY = (firstDotLineY + baseLineY) / 2;

        mContentLeft = getPaddingLeft();
        mContentRight = getMeasuredWidth() - getPaddingRight();
        mContentWidth = mContentRight - mContentLeft;
        mPxPerMinutes = mContentWidth * 1.0f / 24 / 60;
        mBarStrokeWidth = mPxPerMinutes * MINUTES_PER_BAR;
        mBarPaint.setStrokeWidth(mBarStrokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawSplitters(canvas);

        drawBaseLineTime(canvas);

        drawBar(canvas);

        drawLeftLegend(canvas, firstDotLineY, secondDotLineY);
    }

    /**
     * 分割线
     *
     * @param canvas
     */
    private void drawSplitters(Canvas canvas) {
        canvas.drawLine(mContentLeft, firstDotLineY, mContentRight, firstDotLineY, mDotLinePaint);
        canvas.drawLine(mContentLeft, secondDotLineY, mContentRight, secondDotLineY, mDotLinePaint);
        canvas.drawLine(mContentLeft, baseLineY, mContentRight, baseLineY, mBaseLinePaint);
    }

    /**
     * 柱
     *
     * @param canvas
     */
    private void drawBar(Canvas canvas) {
        if (mStepPMDataBeanList != null && mStepPMDataBeanList.size() > 0) {
            for (PMDataBean PMDataBean : mStepPMDataBeanList) {
                calendar.setTimeInMillis(PMDataBean.getTimeInMill());
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);
                minutes += hour * 60;
                float x = mPxPerMinutes * minutes;//转化为相对于0点0分的分钟数,然后x每分钟对应的宽度,得到改时间点对应的x坐标
                canvas.drawLine(mBarStrokeWidth / 2 + mContentLeft + x, baseLineY, mBarStrokeWidth / 2 + mContentLeft + x, baseLineY - PMDataBean.getStepCounts() * 1.0f / mMaxStepValue * (baseLineY - firstDotLineY), mBarPaint);
            }
        }
    }

    private void drawLeftLegend(Canvas canvas, float firstDotLineY, float secondDotLineY) {
        mTextPaint.setTextAlign(Paint.Align.LEFT);//drawBaseLineTime也会用到该paint,所以需要重新设置对齐方式
        canvas.drawText(String.valueOf(mMaxStepValue), getPaddingLeft(), firstDotLineY + 3 * mBaseLineTimeHeight / 2, mTextPaint);
        canvas.drawText(String.valueOf(mMaxStepValue / 2), getPaddingLeft(), secondDotLineY + 3 * mBaseLineTimeHeight / 2, mTextPaint);
    }

    private void drawBaseLineTime(Canvas canvas) {
        mTextPaint.setTextAlign(Paint.Align.CENTER);//drawLeftLegend也会用到该paint,所以需要重新设置对齐方式
        for (int i = 0; i < 3; i++) {
            int x = (i + 1) * mContentWidth / 4 + getPaddingLeft();
            canvas.drawText(StringUtil.autoPrefixStr((i + 1) * 6 + ":00", "0", 5), x, getMeasuredHeight() - getPaddingBottom(), mTextPaint);
        }
    }
}
