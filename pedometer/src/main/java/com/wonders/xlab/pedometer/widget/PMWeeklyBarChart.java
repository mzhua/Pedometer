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
import android.util.Log;
import android.view.View;

import com.wonders.xlab.pedometer.R;
import com.wonders.xlab.pedometer.util.DensityUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * TODO: document your custom view class.
 */
public class PMWeeklyBarChart extends View {

    private int mSectionCounts = 4;//Y轴平分的数量
    private Paint mDotLinePaint;
    private float mDotLineWidthInPx;

    private Paint mBaseLinePaint;
    private float mBaseLineWidthInPx;

    private Paint mBarPaint;

    private TextPaint mTextPaint;

    private int mWeekTextHeightPx;
    private int mNumberTextHeightPx;
    private int mMaxStepValue = 100;

    private int mBarCounts = 7;
    private String[] mBarXLegendText;
    private List<Integer> mDataBeanList;

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

        mBarXLegendText = new String[]{context.getString(R.string.pm_monday),
                context.getString(R.string.pm_tuesday),
                context.getString(R.string.pm_wednesday),
                context.getString(R.string.pm_thursday),
                context.getString(R.string.pm_friday),
                context.getString(R.string.pm_saturday),
                context.getString(R.string.pm_sunday),};

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
        mTextPaint.getTextBounds("周", 0, 1, mTempTextBoundRect);
        mWeekTextHeightPx = mTempTextBoundRect.height();
        mTextPaint.getTextBounds("9", 0, 1, mTempTextBoundRect);
        mNumberTextHeightPx = mTempTextBoundRect.height();

        mBarPaint = new Paint();
        mBarPaint.setColor(Color.parseColor("#328de8"));
        mBarPaint.setStrokeWidth(8);
        mBarPaint.setStyle(Paint.Style.STROKE);

    }

    public void setDataBean(List<Integer> dataBeanList) {
        if (dataBeanList == null) {
            return;
        }
        if (mDataBeanList == null) {
            mDataBeanList = new ArrayList<>();
        } else {
            mDataBeanList.clear();
        }
        mDataBeanList.addAll(dataBeanList);
        mMaxStepValue = Collections.max(mDataBeanList, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 < o2 ? -1 : (o1.equals(o2) ? 0 : 1);
            }
        });

        initParams();

        invalidate();
    }

    private int mYLegendLeft;
    private int mChartLeft;
    private int mContentRight;
    private int mContentWidthPx;

    private float mBottomLineY;
    private float mTopLineY;
    private int mBarWidthPx;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initParams();
    }

    private void initParams() {
        int offsetBottom = 3 * mWeekTextHeightPx / 2 + getPaddingBottom();
        mBottomLineY = getMeasuredHeight() - mBaseLineWidthInPx / 2 - offsetBottom;
        mTopLineY = mDotLineWidthInPx / 2 + getPaddingTop();

        mYLegendLeft = getPaddingLeft();
        mChartLeft = (int) (mYLegendLeft + 3 * getMaxYLegendWidth() / 2);
        mContentRight = getMeasuredWidth() - getPaddingRight();
        mContentWidthPx = mContentRight - mChartLeft;

        mBarWidthPx = mContentWidthPx / (mBarCounts * 2);
        mBarPaint.setStrokeWidth(mBarWidthPx <= 0 ? 1 : mBarWidthPx);
    }

    /**
     * 根据数据动态计算y轴数字中最大的长度
     *
     * @return
     */
    private float getMaxYLegendWidth() {
        return mTextPaint.measureText(String.valueOf(mMaxStepValue));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSplitters(canvas);
        drawBaseLineTime(canvas);
        drawBar(canvas);
        drawLeftLegend(canvas);
    }

    private void drawBar(Canvas canvas) {
        if (mDataBeanList == null || mDataBeanList.size() == 0) {
            return;
        }
        float startX = mChartLeft + mBarWidthPx;
        for (int i = 0; i < mBarCounts; i++) {
            float left = startX + mBarWidthPx * 2 * i;
            canvas.drawLine(left, mBottomLineY, left, mBottomLineY - mDataBeanList.get(i) * 1.0f / mMaxStepValue * (mBottomLineY - mTopLineY), mBarPaint);
        }
    }

    /**
     * 分割线
     *
     * @param canvas
     */
    private void drawSplitters(Canvas canvas) {
        float pxPerDivide = (mBottomLineY - mTopLineY) / mSectionCounts;
        Paint paint;
        for (int i = 0; i < mSectionCounts + 1; i++) {
            float stopY = pxPerDivide * i + mTopLineY;
            if (i < 4) {
                paint = mDotLinePaint;
            } else {
                paint = mBaseLinePaint;
            }
            canvas.drawLine(mChartLeft, stopY, mContentRight, stopY, paint);
        }
    }

    private void drawLeftLegend(Canvas canvas) {
        float pxPerDivide = (mBottomLineY - mTopLineY) / mSectionCounts;
        for (int i = 0; i < mSectionCounts + 1; i++) {
            float stopY = pxPerDivide * i + mTopLineY + mNumberTextHeightPx / 2;
            int a = mMaxStepValue / mSectionCounts;
            canvas.drawText(String.valueOf(a * (mSectionCounts - i)), mYLegendLeft + getMaxYLegendWidth() / 2, stopY, mTextPaint);
        }
    }

    private void drawBaseLineTime(Canvas canvas) {
        for (int i = 0; i < mBarCounts; i++) {
            int x = mBarWidthPx + mChartLeft + i * 2 * mBarWidthPx;
            String timeStr = mBarXLegendText[i];
            canvas.drawText(timeStr, x, mBottomLineY + 3 * mWeekTextHeightPx / 2, mTextPaint);
        }
    }
}
