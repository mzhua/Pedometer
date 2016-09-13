package com.wonders.xlab.pedometer.widget;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.wonders.xlab.pedometer.R;
import com.wonders.xlab.pedometer.util.DensityUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * TODO: document your custom view class.
 */
public class PMWeeklyBarChart extends View {

    private Paint mDotLinePaint;
    private float mDotLineWidthInPx;

    private Paint mBottomLinePaint;
    private float mBottomLineWidthInPx;

    private Paint mBarPaint;
    private Paint mTrianglePaint;

    private TextPaint mTextPaint;
    private Path mTrianglePath;

    private int mWeekTextHeightPx;//"周"的高度
    private int mNumberTextHeightPx;//数字高度

    /**
     * Y轴虚线的数量
     */
    private int mVerticalSplittersCounts = 4;

    private final int DEFAULT_MAX_VALUE = 100;
    /**
     * Y轴的最大值
     */
    private int mMaxValue = DEFAULT_MAX_VALUE;

    /**
     * X轴文字
     */
    private String[] mBarXLegendText;
    /**
     * X轴星期的第一天
     */
    private int mFirstDayOfWeek = Calendar.MONDAY;
    /**
     * 数据源
     */
    private List<PMWeeklyBarChartBean> mDataList;

    /**
     * 顶部倒三角指示器动画
     */
    private ValueAnimator mTriangleIndicatorAnimator;

    /**
     * 用于测绘文字大小的临时变量
     */
    private Rect mTempTextBoundRect = new Rect();

    /**
     * 柱子的动画参数
     */
    private float mBarHeightFraction;
    private ValueAnimator mBarAnimator;

    /**
     * 边界位置参数
     */
    private float mYLegendLeft;
    private float mChartLeft;//中间柱图区域(不包括左边的数值区域)的左边界,即虚线的左侧x
    private float mChartRight;//中间柱图区域的右边界
    private float mBottomLineY;//最底下一条实线的y
    private float mTopLineY;//最顶部一条虚线的y

    /**
     * 柱子的宽度
     */
    private int mBarWidthPx;

    /**
     * 倒三角指示器
     */
    private int mTriangleHeight;
    private int mTriangleEdgeLength;
    private int mIndicatorBarPosition = -1;

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

    private void init(Context context, AttributeSet attrs) {

        if (mFirstDayOfWeek == Calendar.SUNDAY) {
            mBarXLegendText = getResources().getStringArray(R.array.pm_week_sunday);
        } else {
            mBarXLegendText = getResources().getStringArray(R.array.pm_week);
        }

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mDotLineWidthInPx = DensityUtil.dp2px(context, 1);
        mDotLinePaint = new Paint();
        mDotLinePaint.setStyle(Paint.Style.STROKE);
        mDotLinePaint.setColor(Color.GRAY);
        mDotLinePaint.setStrokeWidth(mDotLineWidthInPx);
        mDotLinePaint.setPathEffect(new DashPathEffect(new float[]{10, 5, 10, 5}, 0));

        mBottomLineWidthInPx = DensityUtil.dp2px(context, 2);
        mBottomLinePaint = new Paint();
        mBottomLinePaint.setStyle(Paint.Style.STROKE);
        mBottomLinePaint.setColor(getResources().getColor(R.color.pmAppBlue));
        mBottomLinePaint.setStrokeWidth(mBottomLineWidthInPx);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.GRAY);
        mTextPaint.setTextSize(DensityUtil.sp2px(context, 12));
        mTextPaint.getTextBounds("周", 0, 1, mTempTextBoundRect);
        mWeekTextHeightPx = mTempTextBoundRect.height();
        mTextPaint.getTextBounds("9", 0, 1, mTempTextBoundRect);
        mNumberTextHeightPx = mTempTextBoundRect.height();

        mBarPaint = new Paint();
        mBarPaint.setColor(getResources().getColor(R.color.pmAppBlue));
        mBarPaint.setStrokeWidth(8);
        mBarPaint.setStyle(Paint.Style.STROKE);

        mTrianglePaint = new Paint();
        mTrianglePaint.setColor(getResources().getColor(R.color.pmAppBlue));
        mTrianglePaint.setStyle(Paint.Style.FILL);
        mTrianglePath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSplitters(canvas);
        drawBaseLineTime(canvas);
        drawBar(canvas);
        drawLeftLegend(canvas);
        drawTriangle(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mYLegendLeft = getPaddingLeft();
        mChartRight = getMeasuredWidth() - getPaddingRight();

        initParams();
    }

    private void initParams() {
        mChartLeft = (int) (mYLegendLeft + 3 * mTextPaint.measureText(String.valueOf(mMaxValue)) / 2);

        mBarWidthPx = (int) ((mChartRight - mChartLeft) / (mBarXLegendText.length * 2));
        mBarPaint.setStrokeWidth(mBarWidthPx <= 0 ? 1 : mBarWidthPx);

        mTriangleEdgeLength = (int) ((mChartRight - mChartLeft) / (mBarXLegendText.length * 2) / 2);
        mTriangleHeight = (int) (Math.sqrt(3) / 2 * mTriangleEdgeLength);

        int offsetBottom = 3 * mWeekTextHeightPx / 2 + getPaddingBottom();
        mBottomLineY = getMeasuredHeight() - mBottomLineWidthInPx / 2 - offsetBottom;
        mTopLineY = mDotLineWidthInPx / 2 + getPaddingTop() + mTriangleHeight + 2 * mNumberTextHeightPx;//包括top padding,三角形,以及三角形上面的数字

    }

    public void setDataBeanList(List<PMWeeklyBarChartBean> dataList) {

        if (mDataList == null) {
            mDataList = new ArrayList<>();
        } else {
            mDataList.clear();
        }

        if (dataList != null) {
            int[] daysOfWeek = new int[]{Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};
            for (int i : daysOfWeek) {
                boolean exists = false;
                for (PMWeeklyBarChartBean bean : dataList) {
                    if (i == bean.getDayOfWeek()) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    PMWeeklyBarChartBean bean = new PMWeeklyBarChartBean(i, 0);
                    dataList.add(bean);
                }
            }
            mDataList.addAll(dataList);
            mMaxValue = getMaxValueOfDataList(dataList);
            Collections.sort(mDataList, new Comparator<PMWeeklyBarChartBean>() {
                @Override
                public int compare(PMWeeklyBarChartBean o1, PMWeeklyBarChartBean o2) {
                    int o1DayOfWeek = o1.getDayOfWeek();
                    int o2DayOfWeek = o2.getDayOfWeek();
                    if (mFirstDayOfWeek == Calendar.SUNDAY) {
                        o1DayOfWeek -= 1;
                        o2DayOfWeek -= 1;
                    } else {
                        o1DayOfWeek -= 2;
                        o2DayOfWeek -= 2;
                    }
                    if (o1DayOfWeek < 0) {
                        o1DayOfWeek += 7;
                    }
                    if (o2DayOfWeek < 0) {
                        o2DayOfWeek += 7;
                    }
                    return (o1DayOfWeek < o2DayOfWeek) ? -1 : (o1DayOfWeek == o2DayOfWeek ? 0 : 1);
                }
            });
        }

        mMaxValue = (mMaxValue / DEFAULT_MAX_VALUE + 1) * DEFAULT_MAX_VALUE;

        initParams();

        startBarAnimator();
    }

    /**
     * 获取数据中最大的value的值
     *
     * @param dataList
     * @return
     */
    private int getMaxValueOfDataList(List<PMWeeklyBarChartBean> dataList) {
        int maxValue = DEFAULT_MAX_VALUE;
        if (dataList.size() > 0) {
            PMWeeklyBarChartBean tmpMax = Collections.max(mDataList, new Comparator<PMWeeklyBarChartBean>() {
                @Override
                public int compare(PMWeeklyBarChartBean o1, PMWeeklyBarChartBean o2) {
                    return (o1.getValue() < o2.getValue()) ? -1 : (o1.getValue() == o2.getValue() ? 0 : 1);
                }
            });
            maxValue = tmpMax.getValue();
        }
        return maxValue;
    }

    private void startBarAnimator() {
        if (mBarAnimator != null && mBarAnimator.isRunning()) {
            mBarAnimator.cancel();
        }
        mBarAnimator = ValueAnimator.ofInt(mMaxValue);
        mBarAnimator.setDuration(800);
        mBarAnimator.setInterpolator(new DecelerateInterpolator());
        mBarAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBarHeightFraction = animation.getAnimatedFraction();
                postInvalidate((int) mChartLeft, (int) mTopLineY, (int) mChartRight, (int) mBottomLineY);
            }
        });
        mBarAnimator.start();
    }

    /**
     * 获取第position根柱子的x坐标
     *
     * @param position
     * @return
     */
    private float getBarXOfPosition(int position) {
        return mChartLeft + mBarWidthPx + mBarWidthPx * 2 * position;
    }

    private float mTriangleTextX;

    private void drawTriangle(Canvas canvas) {
        if (null == mTrianglePath || mDataList == null || mDataList.size() == 0 || mIndicatorBarPosition == -1) {
            return;
        }
        canvas.drawPath(mTrianglePath, mTrianglePaint);
        canvas.drawText(String.valueOf(mDataList.get(mIndicatorBarPosition).getValue()), mTriangleTextX, mTopLineY - 2 * mTriangleHeight, mTextPaint);
    }

    private void drawBar(Canvas canvas) {
        if (mDataList == null || mDataList.size() == 0) {
            return;
        }
        for (int i = 0; i < mDataList.size(); i++) {
            PMWeeklyBarChartBean entity = mDataList.get(i);
            float left = getBarXOfPosition(i);
            canvas.drawLine(left, mBottomLineY, left, mBottomLineY - entity.getValue() * mBarHeightFraction * 1.0f / mMaxValue * (mBottomLineY - mTopLineY), mBarPaint);
        }
    }

    private void drawBaseLineTime(Canvas canvas) {
        for (int i = 0; i < mBarXLegendText.length; i++) {
            float x = getBarXOfPosition(i);
            String timeStr = mBarXLegendText[i];
            canvas.drawText(timeStr, x, mBottomLineY + 3 * mWeekTextHeightPx / 2, mTextPaint);
        }
    }

    /**
     * 分割线
     *
     * @param canvas
     */
    private void drawSplitters(Canvas canvas) {
        float pxPerDivide = (mBottomLineY - mTopLineY) / mVerticalSplittersCounts;
        Paint paint;
        for (int i = 0; i < mVerticalSplittersCounts + 1; i++) {
            float stopY = pxPerDivide * i + mTopLineY;
            if (i < 4) {
                paint = mDotLinePaint;
            } else {
                paint = mBottomLinePaint;
            }
            canvas.drawLine(mChartLeft, stopY, mChartRight, stopY, paint);
        }
    }

    private void drawLeftLegend(Canvas canvas) {
        float pxPerDivide = (mBottomLineY - mTopLineY) / mVerticalSplittersCounts;
        for (int i = 0; i < mVerticalSplittersCounts + 1; i++) {
            float stopY = pxPerDivide * i + mTopLineY + mNumberTextHeightPx / 2;
            int a = mMaxValue / mVerticalSplittersCounts;
            canvas.drawText(String.valueOf(a * (mVerticalSplittersCounts - i)), mYLegendLeft + mTextPaint.measureText(String.valueOf(mMaxValue)) / 2, stopY, mTextPaint);
        }
    }

    public void setIndicatorBarPosition(int indicatorBarPosition) {
        this.mIndicatorBarPosition = indicatorBarPosition;
        invalidateSelectedIndicator();
    }

    private void invalidateSelectedIndicator() {
        postInvalidate(getPaddingLeft(), getPaddingTop(), getMeasuredWidth() - getPaddingRight(), (int) mTopLineY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                float pointX = event.getX();

                int oldSelectedPosition = mIndicatorBarPosition == -1 ? 0 : mIndicatorBarPosition;
                mIndicatorBarPosition = getBarIndexUnderTheTouchPointX(pointX);

                startTriangleIndicatorAnimation(oldSelectedPosition);
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 倒三角指示器动画
     *
     * @param oldSelectedPosition
     */
    private void startTriangleIndicatorAnimation(int oldSelectedPosition) {
        if (oldSelectedPosition == mIndicatorBarPosition && oldSelectedPosition != 0) {
            return;
        }
        if (mTriangleIndicatorAnimator != null && mTriangleIndicatorAnimator.isRunning()) {
            mTriangleIndicatorAnimator.cancel();
        }
        mTriangleIndicatorAnimator = ValueAnimator.ofFloat(getBarXOfPosition(oldSelectedPosition), getBarXOfPosition(mIndicatorBarPosition));
        mTriangleIndicatorAnimator.setDuration(800);
        mTriangleIndicatorAnimator.setInterpolator(new OvershootInterpolator(1));
        mTriangleIndicatorAnimator.start();
        mTriangleIndicatorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (float) animation.getAnimatedValue();
                if (mTrianglePath != null) {
                    mTrianglePath.reset();
                } else {
                    mTrianglePath = new Path();
                }
                float y = mTopLineY;
                mTrianglePath.moveTo(x, y);
                mTrianglePath.lineTo(x - mTriangleEdgeLength / 2, (float) (y - Math.sqrt(3) / 2 * mTriangleEdgeLength));
                mTrianglePath.lineTo(x + mTriangleEdgeLength / 2, (float) (y - Math.sqrt(3) / 2 * mTriangleEdgeLength));
                mTrianglePath.close();

                mTriangleTextX = x;
                invalidateSelectedIndicator();
            }
        });
        mTriangleIndicatorAnimator.start();
    }

    private int getBarIndexUnderTheTouchPointX(float pointX) {
        int touchPosition = 0;
        for (int i = 0; i < mBarXLegendText.length; i++) {
            if (Math.abs(pointX - getBarXOfPosition(i)) <= mBarWidthPx) {
                touchPosition = i;
                break;
            }
        }
        return touchPosition;
    }
}
