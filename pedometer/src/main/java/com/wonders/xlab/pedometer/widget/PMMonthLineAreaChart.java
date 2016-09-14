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
import android.view.View;

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
public class PMMonthLineAreaChart extends View {

    private int mSectionCounts = 4;//Y轴平分的数量
    private Paint mDotLinePaint;

    private float mDotLineWidthInPx;
    private Paint mBottomLinePaint;

    private float mBottomLineWidthInPx;
    private Paint mLinePaint;

    private TextPaint mTextPaint;

    private int mNumberTextHeightPx;//数字高度

    private final int DEFAULT_MAX_VALUE = 100;
    private int mMaxStepValue = DEFAULT_MAX_VALUE;

    /**
     * data source
     */
    private List<PMMonthLineAreaBean> mDataBeanList;
    private int[] mXLegendArray;
    private Path mPath;

    public PMMonthLineAreaChart(Context context) {
        super(context);
        init(context, null);
    }

    public PMMonthLineAreaChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PMMonthLineAreaChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PMMonthLineAreaChart(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private Rect mTempTextBoundRect = new Rect();

    private void init(Context context, AttributeSet attrs) {
        mPath = new Path();

        setupXLegendArray(System.currentTimeMillis());

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
        mBottomLinePaint.setColor(context.getResources().getColor(R.color.pmAppBlue));
        mBottomLinePaint.setStrokeWidth(mBottomLineWidthInPx);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.GRAY);
        mTextPaint.setTextSize(DensityUtil.sp2px(context, 12));
        mTextPaint.getTextBounds("9", 0, 1, mTempTextBoundRect);
        mNumberTextHeightPx = mTempTextBoundRect.height();

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(context.getResources().getColor(R.color.pmAppBlue));
        mLinePaint.setStyle(Paint.Style.FILL);

    }

    private float mBarHeightFraction;

    private ValueAnimator mBarAnimator;

    private float mYLegendLeft;
    private float mChartLeft;
    private float mChartRight;

    private float mBottomLineY;
    private float mTopLineY;


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mYLegendLeft = getPaddingLeft();
        mChartRight = getMeasuredWidth() - getPaddingRight();

        initParams();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSplitters(canvas);
        drawXLegend(canvas);
        drawLineArea(canvas);
        drawLeftLegend(canvas);
    }

    private void initParams() {
        mChartLeft = (int) (mYLegendLeft + 3 * getMaxYLegendWidth() / 2);
        int offsetBottom = 3 * mNumberTextHeightPx / 2 + getPaddingBottom();
        mBottomLineY = getMeasuredHeight() - mBottomLineWidthInPx / 2 - offsetBottom;
        mTopLineY = mDotLineWidthInPx / 2 + getPaddingTop() + 2 * mNumberTextHeightPx;//包括top padding,三角形,以及三角形上面的数字

        if (mDataBeanList != null && mDataBeanList.size() >= mXLegendArray.length) {
            if (mPath != null) {
                if (!mPath.isEmpty()) {
                    mPath.reset();
                }
            } else {
                mPath = new Path();
            }
            for (int i = 0; i < mXLegendArray.length; i++) {
                float x = getDateLegendX(i);
                float y = mBottomLineY - mDataBeanList.get(i).getValue() * 1.0f / mMaxStepValue * (mBottomLineY - mTopLineY);
                if (i == 0) {
                    mPath.moveTo(x, y);
                } else {
                    mPath.lineTo(x, y);
                }
            }
            mPath.lineTo(getDateLegendX(mXLegendArray.length - 1), mBottomLineY);
            mPath.lineTo(mChartLeft, mBottomLineY);
            mPath.close();
        }

    }

    /**
     * 补齐缺失的日期的数据为默认0,排序,求最大值
     * 这样方便后面draw操作
     *
     * @param dataBeanList
     * @param anyTimeOfThisMonthInMill
     */
    public void setDataBean(List<PMMonthLineAreaBean> dataBeanList, long anyTimeOfThisMonthInMill) {
        setupXLegendArray(anyTimeOfThisMonthInMill);

        if (mDataBeanList == null) {
            mDataBeanList = new ArrayList<>();
        } else {
            mDataBeanList.clear();
        }
        if (dataBeanList != null) {
            //补全缺失的日期数据为默认0
            for (int s : mXLegendArray) {
                boolean exists = false;
                for (PMMonthLineAreaBean bean : dataBeanList) {
                    if (bean.getDayOfMonth() == s) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    dataBeanList.add(new PMMonthLineAreaBean(s, 0));
                }
            }

            //排序
            Collections.sort(dataBeanList, new Comparator<PMMonthLineAreaBean>() {
                @Override
                public int compare(PMMonthLineAreaBean o1, PMMonthLineAreaBean o2) {
                    int o1DayOfMonth = o1.getDayOfMonth();
                    int o2DayOfMonth = o2.getDayOfMonth();
                    return o1DayOfMonth < o2DayOfMonth ? -1 : (o1DayOfMonth == o2DayOfMonth ? 0 : 1);
                }
            });
            mDataBeanList.addAll(dataBeanList);
            //找最大值
            PMMonthLineAreaBean maxTemp = Collections.max(mDataBeanList, new Comparator<PMMonthLineAreaBean>() {
                @Override
                public int compare(PMMonthLineAreaBean o1, PMMonthLineAreaBean o2) {
                    return o1.getValue() < o2.getValue() ? -1 : (o1.getValue() == o2.getValue() ? 0 : 1);
                }
            });
            mMaxStepValue = (maxTemp.getValue() / DEFAULT_MAX_VALUE + 1) * DEFAULT_MAX_VALUE;//去掉十位数
        }


        initParams();

        invalidate();
//        if (mBarAnimator != null && mBarAnimator.isRunning()) {
//            mBarAnimator.cancel();
//        }
//        mBarAnimator = ValueAnimator.ofInt(mMaxStepValue);
//        mBarAnimator.setDuration(800);
//        mBarAnimator.setInterpolator(new DecelerateInterpolator());
//        mBarAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                mBarHeightFraction = animation.getAnimatedFraction();
//                postInvalidate((int) mChartLeft, (int) mTopLineY, (int) mChartRight, (int) mBottomLineY);
//            }
//        });
//        mBarAnimator.start();

    }

    /**
     * 根据当前月份,设置X轴日期
     *
     * @param anyTimeOfThisMonthInMill
     */
    private void setupXLegendArray(long anyTimeOfThisMonthInMill) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(anyTimeOfThisMonthInMill);
        int daysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        mXLegendArray = new int[daysOfMonth];
        for (int i = 0; i < daysOfMonth; i++) {
            mXLegendArray[i] = i + 1;
        }
    }

    /**
     * 根据数据动态计算y轴数字中最大的长度
     *
     * @return
     */
    private float getMaxYLegendWidth() {
        return mTextPaint.measureText(String.valueOf(mMaxStepValue));
    }

    private void drawLineArea(Canvas canvas) {
        if (mPath == null || mPath.isEmpty()) {
            return;
        }
        canvas.drawPath(mPath, mLinePaint);

    }

    private void drawXLegend(Canvas canvas) {
        for (int i = 0; i < mXLegendArray.length; i += 2) {
            float x = getDateLegendX(i);
            String timeStr = String.valueOf(mXLegendArray[i]);
            canvas.drawText(timeStr, x, mBottomLineY + 3 * mNumberTextHeightPx / 2, mTextPaint);
        }
    }

    private float getDateLegendX(int position) {
        if (mXLegendArray == null || mXLegendArray.length == 0) {
            return 1;
        }
        return mChartLeft + position * (mChartRight - mChartLeft) / (mXLegendArray.length - 1);
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
                paint = mBottomLinePaint;
            }
            canvas.drawLine(mChartLeft, stopY, mChartRight, stopY, paint);
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
}
