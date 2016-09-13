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
import com.wonders.xlab.pedometer.data.PMStepCountEntity;
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

    private Paint mBottomLinePaint;
    private float mBottomLineWidthInPx;

    private Paint mBarPaint;
    private Paint mTrianglePaint;

    private TextPaint mTextPaint;
    private Path mTrianglePath;

    private int mWeekTextHeightPx;//"周"的高度
    private int mNumberTextHeightPx;//数字高度
    private final int DEFAULT_MAX_STEP_VALUE = 100;
    private int mMaxStepValue = DEFAULT_MAX_STEP_VALUE;

    private String[] mBarXLegendText;
    /**
     * data source
     */
    private List<Integer> mDataBeanList;
    private List<PMStepCountEntity> mDataList;

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

        mBarXLegendText = getResources().getStringArray(R.array.pm_week);

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

    private float mBarHeightFraction;

    private ValueAnimator mBarAnimator;

    public void setDataBeanList(List<PMStepCountEntity> dataList) {
        if (dataList == null) {
            return;
        }
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        } else {
            mDataList.clear();
        }
        mDataList.addAll(dataList);
        PMStepCountEntity tmpMax = Collections.max(mDataList, new Comparator<PMStepCountEntity>() {
            @Override
            public int compare(PMStepCountEntity o1, PMStepCountEntity o2) {
                return (o1.getStepCounts() < o2.getStepCounts()) ? -1 : (o1.getStepCounts() == o2.getStepCounts() ? 0 : 1);
            }
        });
        mMaxStepValue = tmpMax.getStepCounts();
        mMaxStepValue = (mMaxStepValue / DEFAULT_MAX_STEP_VALUE + 1) * DEFAULT_MAX_STEP_VALUE;
        initParams();

        if (mBarAnimator != null && mBarAnimator.isRunning()) {
            mBarAnimator.cancel();
        }
        mBarAnimator = ValueAnimator.ofInt(mMaxStepValue);
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

        mMaxStepValue = (mMaxStepValue / DEFAULT_MAX_STEP_VALUE + 1) * DEFAULT_MAX_STEP_VALUE;

        initParams();

        if (mBarAnimator != null && mBarAnimator.isRunning()) {
            mBarAnimator.cancel();
        }
        mBarAnimator = ValueAnimator.ofInt(mMaxStepValue);
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

    private float mYLegendLeft;
    private float mChartLeft;
    private float mChartRight;

    private float mBottomLineY;
    private float mTopLineY;
    private int mBarWidthPx;//柱子的宽度
    private int mTriangleHeight;
    private int mTriangleEdgeLength;


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mYLegendLeft = getPaddingLeft();
        mChartRight = getMeasuredWidth() - getPaddingRight();

        initParams();
    }

    private void initParams() {
        mChartLeft = (int) (mYLegendLeft + 3 * getMaxYLegendWidth() / 2);

        mBarWidthPx = (int) ((mChartRight - mChartLeft) / (mBarXLegendText.length * 2));
        mBarPaint.setStrokeWidth(mBarWidthPx <= 0 ? 1 : mBarWidthPx);

        mTriangleEdgeLength = (int) ((mChartRight - mChartLeft) / (mBarXLegendText.length * 2) / 2);
        mTriangleHeight = (int) (Math.sqrt(3) / 2 * mTriangleEdgeLength);

        int offsetBottom = 3 * mWeekTextHeightPx / 2 + getPaddingBottom();
        mBottomLineY = getMeasuredHeight() - mBottomLineWidthInPx / 2 - offsetBottom;
        mTopLineY = mDotLineWidthInPx / 2 + getPaddingTop() + mTriangleHeight + 2 * mNumberTextHeightPx;//包括top padding,三角形,以及三角形上面的数字

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

        drawTriangle(canvas);
    }

    /**
     * 获取第position根柱子的x坐标
     *
     * @param position
     * @return
     */
    private float getBarX(int position) {
        return mChartLeft + mBarWidthPx + mBarWidthPx * 2 * position;
    }

    private float mTriangleTextX;

    private void drawTriangle(Canvas canvas) {
        if (null == mTrianglePath || mDataBeanList == null || mDataBeanList.size() == 0 || mSelectedPosition == -1) {
            return;
        }
        canvas.drawPath(mTrianglePath, mTrianglePaint);
        canvas.drawText(String.valueOf(mDataBeanList.size() > mSelectedPosition ? mDataBeanList.get(mSelectedPosition) : 0), mTriangleTextX, mTopLineY - 2 * mTriangleHeight, mTextPaint);
    }

    private void drawBar(Canvas canvas) {
        if (mDataBeanList == null || mDataBeanList.size() == 0) {
            return;
        }
        for (int i = 0; i < mDataBeanList.size(); i++) {
            float left = getBarX(i);
            canvas.drawLine(left, mBottomLineY, left, mBottomLineY - mDataBeanList.get(i) * mBarHeightFraction * 1.0f / mMaxStepValue * (mBottomLineY - mTopLineY), mBarPaint);
        }
    }

    private void drawBaseLineTime(Canvas canvas) {
        for (int i = 0; i < mBarXLegendText.length; i++) {
            float x = getBarX(i);
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

    private int mSelectedPosition = -1;

    public void setSelectedPosition(int selectedPosition) {
        this.mSelectedPosition = selectedPosition;
        invalidateSelectedIndicator();
    }

    private void invalidateSelectedIndicator() {
        postInvalidate(getPaddingLeft(), getPaddingTop(), getMeasuredWidth() - getPaddingRight(), (int) mTopLineY);
    }

    private ValueAnimator mTriangleIndicatorAnimator;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                float pointX = event.getX();

                int oldSelectedPosition = mSelectedPosition == -1 ? 0 : mSelectedPosition;
                for (int i = 0; i < mBarXLegendText.length; i++) {
                    if (Math.abs(pointX - getBarX(i)) <= mBarWidthPx) {
                        mSelectedPosition = i;
                    }
                }
                if (oldSelectedPosition == mSelectedPosition && oldSelectedPosition != 0) {
                    break;
                }
                if (mTriangleIndicatorAnimator != null && mTriangleIndicatorAnimator.isRunning()) {
                    mTriangleIndicatorAnimator.cancel();
                }
                mTriangleIndicatorAnimator = ValueAnimator.ofFloat(getBarX(oldSelectedPosition), getBarX(mSelectedPosition));
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
                break;
        }

        return super.onTouchEvent(event);
    }
}
