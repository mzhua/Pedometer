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
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Parcelable;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.wonders.xlab.pedometer.R;

/**
 * Created by hua on 16/9/3.
 */

public class WalkChart extends View {
    private final int DEFAULT_DRIP_HALF_HEIGHT = 20;
    /**
     * 底部默认空白的角度
     */
    private final int DEFAULT_EMPTY_ANGLE = 90;
    /**
     * 刻度数量
     */
    private final int DEFAULT_DIVIDER_COUNTS = 5;
    /**
     * 外圈的宽度
     */
    private final float STROKE_WIDTH_OUTER_CIRCLE = 8;
    /**
     * 内圈的宽度
     */
    private final float STROKE_WIDTH_INNER_CIRCLE = 36;

    private float mOuterPadding = DEFAULT_DRIP_HALF_HEIGHT;

    private float mEmptyAngle = DEFAULT_EMPTY_ANGLE;
    private float mCircleInterval = DEFAULT_DRIP_HALF_HEIGHT;

    /**
     *
     */
    private float mStartAngle = 0;
    private float mSweepAngle;

    private Paint mOuterCirclePaint;
    private Paint mOuterReachedCirclePaint;
    private RectF mOuterCircleRect;

    private Paint mInnerCirclePaint;
    private RectF mInnerCircleRect;

    private Bitmap mDripBitmap;
    private Paint mDripBitmapPaint;
    private int mDripWidth;
    private int mDripHeight;
    private float mDripCurrentAngle;
    private Matrix mDripMatrix;

    private Paint mDividerPaint;
    private float mDividerLength = STROKE_WIDTH_OUTER_CIRCLE + 6;
    private float mDividerValuePadding = 5000;
    private float mDividerValueMin = 11000;
    private float mStepNumber = 8900;
    private float mDividerIntervalAngle;

    private Point mCenterPoint;

    public WalkChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public WalkChart(Context context) {
        super(context);
        init(context, null);
    }

    public WalkChart(Context context, AttributeSet attrs) {
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

    private void init(Context context, AttributeSet attrs) {
        initAttribute(context, attrs);

        mCenterPoint = new Point();

        mOuterCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterCirclePaint.setColor(Color.parseColor("#D5D1D1"));
        mOuterCirclePaint.setStyle(Paint.Style.STROKE);
        mOuterCirclePaint.setStrokeWidth(STROKE_WIDTH_OUTER_CIRCLE);

        mOuterReachedCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterReachedCirclePaint.setShader(new SweepGradient(0, 0, new int[]{0x00088AA1, 0xFF088AA1}, null));
        mOuterReachedCirclePaint.setStyle(Paint.Style.STROKE);
        mOuterReachedCirclePaint.setStrokeWidth(STROKE_WIDTH_OUTER_CIRCLE);

        mInnerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerCirclePaint.setColor(Color.parseColor("#D5D1D1"));
        mInnerCirclePaint.setStyle(Paint.Style.STROKE);
        mInnerCirclePaint.setStrokeWidth(STROKE_WIDTH_INNER_CIRCLE);

        mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDividerPaint.setColor(Color.RED);//Color.parseColor("#747171")
        mDividerPaint.setStyle(Paint.Style.STROKE);
        mDividerPaint.setStrokeWidth(2);

        mDripBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDripBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_drip);
        mDripWidth = mDripBitmap.getWidth();
        mDripHeight = mDripBitmap.getHeight();
        mDripMatrix = new Matrix();
        mOuterPadding = mDripHeight / 2 - STROKE_WIDTH_OUTER_CIRCLE / 2;
        mCircleInterval = mOuterPadding;
    }

    private void initAttribute(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WalkChart);
        mEmptyAngle = array.getFloat(R.styleable.WalkChart_EmptyAngle, DEFAULT_EMPTY_ANGLE);
        if (mEmptyAngle > 180) {
            mEmptyAngle = 180;
        }
        mSweepAngle = 360 - mEmptyAngle;
        mDripCurrentAngle = 0;
        array.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //translate base point
        canvas.translate(mCenterPoint.x, mCenterPoint.y);
        canvas.rotate(90 + mEmptyAngle / 2);

        //draw outer circle
        if (null == mOuterCircleRect || mOuterCircleRect.isEmpty()) {
            float halfOuterStrokeWidth = STROKE_WIDTH_OUTER_CIRCLE / 2;
            mOuterCircleRect = new RectF(-mCenterPoint.x + halfOuterStrokeWidth + mOuterPadding, -mCenterPoint.y + halfOuterStrokeWidth + mOuterPadding, mCenterPoint.x - halfOuterStrokeWidth - mOuterPadding, mCenterPoint.y - halfOuterStrokeWidth - mOuterPadding);
        }
        canvas.drawArc(mOuterCircleRect, mStartAngle, mSweepAngle, false, mOuterCirclePaint);

        //draw inner circle
        if (null == mInnerCircleRect || mInnerCircleRect.isEmpty()) {
            float halfInnerStrokeWidth = STROKE_WIDTH_INNER_CIRCLE / 2;
            float outerInterval = STROKE_WIDTH_OUTER_CIRCLE + mCircleInterval;
            mInnerCircleRect = new RectF(-mCenterPoint.x + halfInnerStrokeWidth + outerInterval + mOuterPadding, -mCenterPoint.y + halfInnerStrokeWidth + outerInterval + mOuterPadding, mCenterPoint.x - halfInnerStrokeWidth - outerInterval - mOuterPadding, mCenterPoint.y - halfInnerStrokeWidth - outerInterval - mOuterPadding);
        }
        canvas.drawArc(mInnerCircleRect, mStartAngle, mSweepAngle, false, mInnerCirclePaint);

        //draw divide
        for (int i = 0; i < DEFAULT_DIVIDER_COUNTS; i++) {
            float radius = mOuterCircleRect.width() / 2;
            canvas.drawLine((float) (radius * Math.cos(Math.toRadians(i * this.mDividerIntervalAngle))), (float) (radius * Math.sin(Math.toRadians(i * this.mDividerIntervalAngle))), (float) ((radius - mDividerLength) * Math.cos(Math.toRadians(i * this.mDividerIntervalAngle))), (float) ((radius - mDividerLength) * Math.cos(Math.toRadians(i * this.mDividerIntervalAngle))), mDividerPaint);
        }

        //draw drip indicator
        mDripMatrix.reset();
        mDripMatrix.setRotate(90, mDripWidth / 2, mDripHeight / 2);
        mDripMatrix.postTranslate(mOuterCircleRect.width() / 2 - mDripWidth / 2, -mDripHeight / 2);
        mDripMatrix.postRotate(mDripCurrentAngle);

        canvas.drawArc(mOuterCircleRect, mStartAngle, mDripCurrentAngle, false, mOuterReachedCirclePaint);//start from 0, notice this
        canvas.drawBitmap(mDripBitmap, mDripMatrix, mDripBitmapPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int radius = width / 2;
        int height = (int) ((Math.cos(Math.toRadians(mEmptyAngle / 2)) + 1) * radius);

        mCenterPoint.set(radius, radius);
        setMeasuredDimension(width, height);
    }

    private ValueAnimator mDripAnimator;

    private void setTargetAngle(@FloatRange(from = 0f, to = 360f) final float angle) {
        if (mDripAnimator != null && mDripAnimator.isRunning()) {
            mDripAnimator.cancel();
        }
        mDripCurrentAngle = 0;

        mDripAnimator = ValueAnimator.ofFloat(0f, angle);
        mDripAnimator.setDuration(2000);
        mDripAnimator.setInterpolator(new OvershootInterpolator());
        mDripAnimator.start();
        mDripAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDripCurrentAngle = (float) animation.getAnimatedValue();
                float[] rect = new float[9];
                mDripMatrix.getValues(rect);
                Log.d("WalkChart", "mDripMatrix:" + mDripMatrix.toString());
                Log.d("WalkChart", rect[2] + ":" + rect[5]);
                postInvalidate();
            }
        });
    }

    public void setStepNumber(@IntRange(from = DEFAULT_DIVIDER_COUNTS) int stepNumber) {
        this.mDividerIntervalAngle = stepNumber / DEFAULT_DIVIDER_COUNTS * (360.0f / stepNumber);
        this.mStepNumber = stepNumber;
        setTargetAngle(170);
    }
}