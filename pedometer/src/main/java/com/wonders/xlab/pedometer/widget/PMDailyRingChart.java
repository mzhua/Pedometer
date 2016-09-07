package com.wonders.xlab.pedometer.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Parcelable;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.wonders.xlab.pedometer.R;
import com.wonders.xlab.pedometer.util.DensityUtil;

/**
 * Created by hua on 16/9/3.
 */

public class PMDailyRingChart extends View {
    private final int DEFAULT_INNER_CIRCLE_WIDTH_IN_DP = 18;
    /**
     * 底部默认空白的角度
     */
    private final float DEFAULT_EMPTY_ANGLE = 90;
    /**
     * 刻度数量
     */
    private final int DEFAULT_DIVIDER_COUNTS = 6;

    private float mOuterPadding;

    private float mEmptyAngle = DEFAULT_EMPTY_ANGLE;
    private float mCircleInterval;

    private float mSweepAngle;
    private Paint mOuterCirclePaint;
    private float mStartAngle = 0;

    /**
     * 外圈
     */
    private Paint mOuterReachedCirclePaint;
    private RectF mOuterCircleRect;

    /**
     * 内圈
     */
    private Paint mInnerCirclePaint;
    private RectF mInnerCircleRect;

    /**
     * 水滴指示
     */
    private Bitmap mDripBitmap;
    private Paint mDripBitmapPaint;
    private int mDripWidth;
    private int mDripHeight;
    private float mDripCurrentAngle;
    private Matrix mDripMatrix;

    /**
     * 刻度
     */
    private Paint mDividerPaint;
    private float mDividerLength;//长度
    private int mDividerValueAppend = 5000;
    private int mDividerValueMax = 10000;
    private float mDividerIntervalAngle;//每等分的角度
    private int mDividerIntervalStepCounts = mDividerValueMax / (DEFAULT_DIVIDER_COUNTS - 1);//每等分对应的步数

    private int mCurrentDripIndicatorValue;//当前指示位置的值

    private int mStepCounts = 0;

    private TextPaint mTextPaint;
    private Point mCenterPoint;

    private Rect mTempRectBounds = new Rect();

    private OnUpdateListener mOnUpdateListener;

    public void addUpdateListener(OnUpdateListener onUpdateListener) {
        mOnUpdateListener = onUpdateListener;
    }

    public interface OnUpdateListener {
        void onChange(int value, @FloatRange(from = 0.0f, to = 1.0f) float percent);
    }

    public PMDailyRingChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public PMDailyRingChart(Context context) {
        super(context);
        init(context, null);
    }

    public PMDailyRingChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    /**
     * 外圈的宽度
     */
    private float mStrokeWidthOuterCircle = 8;
    /**
     * 内圈的宽度
     */
    private float mStrokeWidthInnerCircle = 40;

    private void init(Context context, AttributeSet attrs) {
        initAttribute(context, attrs);

        mStrokeWidthOuterCircle = DensityUtil.dp2px(context, 3);
        mStrokeWidthInnerCircle = DensityUtil.dp2px(context, DEFAULT_INNER_CIRCLE_WIDTH_IN_DP);
        mDividerLength = DensityUtil.dp2px(context, DEFAULT_INNER_CIRCLE_WIDTH_IN_DP + 5);

        mCenterPoint = new Point();

        mOuterCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterCirclePaint.setColor(Color.parseColor("#D5D1D1"));
        mOuterCirclePaint.setStyle(Paint.Style.STROKE);
        mOuterCirclePaint.setStrokeWidth(mStrokeWidthOuterCircle);

        mOuterReachedCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterReachedCirclePaint.setShader(new SweepGradient(0, 0, new int[]{0x00088AA1, 0xFF088AA1}, null));
        mOuterReachedCirclePaint.setStyle(Paint.Style.STROKE);
        mOuterReachedCirclePaint.setStrokeWidth(mStrokeWidthOuterCircle);

        mInnerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerCirclePaint.setColor(Color.parseColor("#E2E2E2"));
        mInnerCirclePaint.setStyle(Paint.Style.STROKE);
        mInnerCirclePaint.setStrokeWidth(mStrokeWidthInnerCircle);

        mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDividerPaint.setColor(Color.parseColor("#D5D1D1"));//Color.parseColor("#747171")
        mDividerPaint.setStyle(Paint.Style.STROKE);
        mDividerPaint.setStrokeWidth(3);

        mDripBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDripBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_drip);
        mDripWidth = mDripBitmap.getWidth();
        mDripHeight = mDripBitmap.getHeight();
        mDripMatrix = new Matrix();
        mOuterPadding = mDripHeight / 2 - mStrokeWidthOuterCircle / 2;
        mCircleInterval = mOuterPadding;

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    }

    private void initAttribute(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PMDailyRingChart);
        mEmptyAngle = array.getFloat(R.styleable.PMDailyRingChart_EmptyAngle, DEFAULT_EMPTY_ANGLE);
        if (mEmptyAngle > 180) {
            mEmptyAngle = 180;
        }
        mSweepAngle = 360 - mEmptyAngle;
        mDripCurrentAngle = 0;
        mDividerIntervalAngle = (360 - mEmptyAngle) / mDividerValueMax * 2000;
        array.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        //translate base point
        tranAndRotateCanvas(canvas);

        //draw outer circle
        drawOuterCircle(canvas);

        //draw inner circle
        drawInnerCircle(canvas);

        //draw divider
        drawDividers(canvas);

        //draw drip indicator
        drawDripIndicator(canvas);

        canvas.restore();

        drawCenterText(canvas);

    }

    /**
     * 420
     *
     * @param canvas
     */
    private void drawCenterText(Canvas canvas) {

        int centerX = mCenterPoint.x;
        int centerY = mCenterPoint.y;
        float innerCircleRadius = Math.abs(mInnerCircleRect.width()) / 2 - mStrokeWidthInnerCircle / 2;

        mTextPaint.setColor(Color.parseColor("#212121"));
        mTextPaint.setTextSize(DensityUtil.sp2px(getContext(), 18));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.getTextBounds("今日步数", 0, 1, mTempRectBounds);
        canvas.drawText("今日步数", centerX, centerY - innerCircleRadius / 2, mTextPaint);

        mTextPaint.setColor(Color.parseColor("#388FE5"));
        mTextPaint.setTextSize(DensityUtil.sp2px(getContext(), 32));
        canvas.drawText(String.valueOf(mCurrentDripIndicatorValue), centerX, centerY, mTextPaint);

        mTextPaint.setColor(Color.parseColor("#AFB0B0"));
        mTextPaint.setTextSize(DensityUtil.sp2px(getContext(), 12));
        mTextPaint.getTextBounds("目标: 10000", 0, 1, mTempRectBounds);
        canvas.drawText("目标: 10000", centerX, centerY + 2 * innerCircleRadius / 3 - 2 * mTempRectBounds.height(), mTextPaint);

        mTextPaint.setColor(Color.parseColor("#212121"));
        mTextPaint.setTextSize(DensityUtil.sp2px(getContext(), 14));
        canvas.drawText("等级: " + getVitality(mStepCounts), centerX, centerY + 2 * innerCircleRadius / 3, mTextPaint);
    }

    private String getVitality(int stepNumber) {
        String vitality;
        if (stepNumber <= 3000) {
            vitality = "不活跃";
        } else if (stepNumber <= 7000) {
            vitality = "轻度活跃";
        } else if (stepNumber <= 10000) {
            vitality = "活跃";
        } else {
            vitality = "超活跃";
        }
        return vitality;
    }

    private void tranAndRotateCanvas(Canvas canvas) {
        canvas.translate(mCenterPoint.x, mCenterPoint.y);
        canvas.rotate(90 + mEmptyAngle / 2);
    }

    private void drawDripIndicator(Canvas canvas) {
        mDripMatrix.reset();
        mDripMatrix.setRotate(90, mDripWidth / 2, mDripHeight / 2);
        mDripMatrix.postTranslate(mOuterCircleRect.width() / 2 - mDripWidth / 2, -mDripHeight / 2);
        mDripMatrix.postRotate(mDripCurrentAngle);

        canvas.drawArc(mOuterCircleRect, mStartAngle, mDripCurrentAngle, false, mOuterReachedCirclePaint);//start from 0, notice this
        canvas.drawBitmap(mDripBitmap, mDripMatrix, mDripBitmapPaint);
    }

    private void drawDividers(Canvas canvas) {
        for (int i = 0; i < DEFAULT_DIVIDER_COUNTS; i++) {
            /**
             * 画刻度
             */
            float radius = Math.abs(mInnerCircleRect.width()) / 2 + mStrokeWidthInnerCircle / 2;
            float currentDividerAngle = i * this.mDividerIntervalAngle;
            if (currentDividerAngle > mSweepAngle) {
                currentDividerAngle = mSweepAngle;
            }
            double currentDividerRadians = Math.toRadians(currentDividerAngle);
            float startX = (float) (radius * Math.cos(currentDividerRadians));
            float startY = (float) (radius * Math.sin(currentDividerRadians));
            float stopX = (float) ((radius - mDividerLength) * Math.cos(currentDividerRadians));
            float stopY = (float) ((radius - mDividerLength) * Math.sin(currentDividerRadians));
            canvas.drawLine(startX, startY, stopX, stopY, mDividerPaint);

            canvas.save();

            /**
             * 画刻度对应的数值
             */
            float textCenterX = (float) ((radius - mDividerLength - 30) * Math.cos(currentDividerRadians));
            float textCenterY = (float) ((radius - mDividerLength - 30) * Math.sin(currentDividerRadians));
            canvas.rotate(90 + currentDividerAngle, textCenterX, textCenterY);
            //draw divider value
            mTextPaint.setColor(Color.parseColor("#212121"));
            mTextPaint.setTextSize(DensityUtil.dp2px(getContext(), 12));
            mTextPaint.setTextAlign(Paint.Align.CENTER);

            int steps = mDividerIntervalStepCounts * i;
            canvas.drawText(String.valueOf(steps), textCenterX, textCenterY, mTextPaint);

            canvas.restore();
        }
    }

    private void drawInnerCircle(Canvas canvas) {
        if (null == mInnerCircleRect || mInnerCircleRect.isEmpty()) {
            float halfInnerStrokeWidth = mStrokeWidthInnerCircle / 2;
            float outerInterval = mStrokeWidthOuterCircle + mCircleInterval;
            mInnerCircleRect = new RectF(-mViewRadius + halfInnerStrokeWidth + outerInterval + mOuterPadding, -mViewRadius + halfInnerStrokeWidth + outerInterval + mOuterPadding, mViewRadius - halfInnerStrokeWidth - outerInterval - mOuterPadding, mViewRadius - halfInnerStrokeWidth - outerInterval - mOuterPadding);
        }
        canvas.drawArc(mInnerCircleRect, mStartAngle, mSweepAngle, false, mInnerCirclePaint);
    }

    private void drawOuterCircle(Canvas canvas) {
        if (null == mOuterCircleRect || mOuterCircleRect.isEmpty()) {
            float halfOuterStrokeWidth = mStrokeWidthOuterCircle / 2;
            mOuterCircleRect = new RectF(-mViewRadius + halfOuterStrokeWidth + mOuterPadding, -mViewRadius + halfOuterStrokeWidth + mOuterPadding, mViewRadius - halfOuterStrokeWidth - mOuterPadding, mViewRadius - halfOuterStrokeWidth - mOuterPadding);
        }
        canvas.drawArc(mOuterCircleRect, mStartAngle, mSweepAngle, false, mOuterCirclePaint);
    }

    private int mViewRadius;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = (int) (widthSize * (1 + Math.cos(Math.toRadians(mEmptyAngle / 2))) / 2);
            setMeasuredDimension(widthSize, heightSize);
        }
        int tmpW = widthSize - getPaddingLeft() - getPaddingRight();
        int tmpH = heightSize - getPaddingTop() - getPaddingBottom();
        mViewRadius = Math.min(tmpW / 2, tmpH / 2);
        mCenterPoint.set(getPaddingLeft() + tmpW / 2, getPaddingTop() + tmpH / 2);
    }

    private ValueAnimator mDripAnimator;

    private void setTargetAngle(@FloatRange(from = 0f, to = 360f) final float angle) {
        mDripCurrentAngle = 0;

        if (mDripAnimator != null && mDripAnimator.isRunning()) {
            mDripAnimator.cancel();
        }
        mDripAnimator = ValueAnimator.ofFloat(0f, angle);
        mDripAnimator.setDuration(1600);
        mDripAnimator.setInterpolator(new OvershootInterpolator(0.5f));
        mDripAnimator.start();
        mDripAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDripCurrentAngle = (float) animation.getAnimatedValue();
                mCurrentDripIndicatorValue = (int) (mDripCurrentAngle * mStepPerAngle);
                if (null != mOnUpdateListener) {
                    mOnUpdateListener.onChange(mCurrentDripIndicatorValue, 1.0f * mCurrentDripIndicatorValue / mDividerValueMax);
                }
                postInvalidate();
            }
        });
    }

    private float mAnglePerStep;
    private float mStepPerAngle;

    /**
     * set the step counts and start the animation
     *
     * @param stepCounts
     */
    public void startWithStepCounts(@IntRange(from = 0) int stepCounts) {
        if (stepCounts >= mDividerValueMax) {
            int d = stepCounts / mDividerValueMax;
            int remainder = stepCounts % mDividerValueMax;
            if (remainder >= mDividerValueAppend) {
                mDividerValueMax = (d + 1) * mDividerValueMax;
            } else {
                mDividerValueMax = d * mDividerValueMax + mDividerValueAppend;
            }
        }
        mStepPerAngle = mDividerValueMax / mSweepAngle;
        mAnglePerStep = mSweepAngle / mDividerValueMax;
        this.mDividerIntervalAngle = mDividerValueMax / (DEFAULT_DIVIDER_COUNTS - 1) * mAnglePerStep;
        this.mDividerIntervalStepCounts = mDividerValueMax / (DEFAULT_DIVIDER_COUNTS - 1);
        this.mStepCounts = stepCounts;
        setTargetAngle(mAnglePerStep * stepCounts);
    }
}