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
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

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
    private final int DEFAULT_DIVIDER_COUNTS = 6;
    /**
     * 外圈的宽度
     */
    private final float STROKE_WIDTH_OUTER_CIRCLE = 8;
    /**
     * 内圈的宽度
     */
    private final float STROKE_WIDTH_INNER_CIRCLE = 40;

    private float mOuterPadding = DEFAULT_DRIP_HALF_HEIGHT;

    private float mEmptyAngle = DEFAULT_EMPTY_ANGLE;
    private float mCircleInterval = DEFAULT_DRIP_HALF_HEIGHT;

    private float mSweepAngle;
    private Paint mOuterCirclePaint;
    private float mStartAngle = 0;

    /**
     *外圈
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
    private float mDividerLength = STROKE_WIDTH_INNER_CIRCLE + 6;//长度
    private int mDividerValueAppend = 5000;
    private int mDividerValueMax = 10000;
    private float mDividerIntervalAngle;//每等分的角度
    private int mDividerIntervalStepCounts = mDividerValueMax / (DEFAULT_DIVIDER_COUNTS - 1);//每等分对应的步数

    private int mCurrentDripIndicatorValue;//当前指示位置的值

    private int mStepCounts = 0;

    private TextPaint mTextPaint;
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
        mInnerCirclePaint.setColor(Color.parseColor("#E2E2E2"));
        mInnerCirclePaint.setStyle(Paint.Style.STROKE);
        mInnerCirclePaint.setStrokeWidth(STROKE_WIDTH_INNER_CIRCLE);

        mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDividerPaint.setColor(Color.parseColor("#D5D1D1"));//Color.parseColor("#747171")
        mDividerPaint.setStyle(Paint.Style.STROKE);
        mDividerPaint.setStrokeWidth(3);

        mDripBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDripBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_drip);
        mDripWidth = mDripBitmap.getWidth();
        mDripHeight = mDripBitmap.getHeight();
        mDripMatrix = new Matrix();
        mOuterPadding = mDripHeight / 2 - STROKE_WIDTH_OUTER_CIRCLE / 2;
        mCircleInterval = mOuterPadding;

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    }

    private void initAttribute(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WalkChart);
        mEmptyAngle = array.getFloat(R.styleable.WalkChart_EmptyAngle, DEFAULT_EMPTY_ANGLE);
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

        drawScalesText(canvas);

        drawCenterText(canvas);

    }

    private void drawCenterText(Canvas canvas) {
        canvas.restore();

        int centerX = getWidth() / 2;//显示区域的半宽
        float innerCircleInnerRadius = centerX - mInnerCircleRect.width() / 2 - STROKE_WIDTH_INNER_CIRCLE / 2;//内部圆内边界圆的半径

//        canvas.drawLine(centerX, 0, centerX, getHeight(), mDividerPaint);
//        canvas.drawLine(0, centerX, getWidth(), centerX, mDividerPaint);

        mTextPaint.setColor(Color.parseColor("#212121"));
        mTextPaint.setTextSize(56);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        float centerY = innerCircleInnerRadius + 250;
        canvas.drawText("今日步数", centerX, centerY, mTextPaint);

        mTextPaint.setColor(Color.parseColor("#388FE5"));
        mTextPaint.setTextSize(100);
        canvas.drawText(mCurrentDripIndicatorValue + " 步", centerX, centerY + 150, mTextPaint);

        mTextPaint.setColor(Color.parseColor("#AFB0B0"));
        mTextPaint.setTextSize(36);
        canvas.drawText("目标: 10000", centerX, centerY + 300, mTextPaint);

        mTextPaint.setColor(Color.parseColor("#212121"));
        mTextPaint.setTextSize(48);
        canvas.drawText("等级: " + getVitality(mStepCounts), centerX, centerY + 400, mTextPaint);
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

    private void drawScalesText(Canvas canvas) {

    }

    private void tranAndRotateCanvas(Canvas canvas) {
        canvas.save();
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
            float radius = mInnerCircleRect.width() / 2 + STROKE_WIDTH_INNER_CIRCLE / 2;
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

            float textCenterX = (float) ((radius - mDividerLength - 30) * Math.cos(currentDividerRadians));
            float textCenterY = (float) ((radius - mDividerLength - 30) * Math.sin(currentDividerRadians));
            canvas.rotate(90 + currentDividerAngle, textCenterX, textCenterY);
            //draw divider value
            mTextPaint.setColor(Color.parseColor("#212121"));
            mTextPaint.setTextSize(28);
            mTextPaint.setTextAlign(Paint.Align.CENTER);

            int steps = mDividerIntervalStepCounts * i;
            canvas.drawText(String.valueOf(steps), textCenterX, textCenterY, mTextPaint);
            canvas.restore();
        }
    }

    private void drawInnerCircle(Canvas canvas) {
        if (null == mInnerCircleRect || mInnerCircleRect.isEmpty()) {
            float halfInnerStrokeWidth = STROKE_WIDTH_INNER_CIRCLE / 2;
            float outerInterval = STROKE_WIDTH_OUTER_CIRCLE + mCircleInterval;
            mInnerCircleRect = new RectF(-mCenterPoint.x + halfInnerStrokeWidth + outerInterval + mOuterPadding, -mCenterPoint.y + halfInnerStrokeWidth + outerInterval + mOuterPadding, mCenterPoint.x - halfInnerStrokeWidth - outerInterval - mOuterPadding, mCenterPoint.y - halfInnerStrokeWidth - outerInterval - mOuterPadding);
        }
        canvas.drawArc(mInnerCircleRect, mStartAngle, mSweepAngle, false, mInnerCirclePaint);
    }

    private void drawOuterCircle(Canvas canvas) {
        if (null == mOuterCircleRect || mOuterCircleRect.isEmpty()) {
            float halfOuterStrokeWidth = STROKE_WIDTH_OUTER_CIRCLE / 2;
            mOuterCircleRect = new RectF(-mCenterPoint.x + halfOuterStrokeWidth + mOuterPadding, -mCenterPoint.y + halfOuterStrokeWidth + mOuterPadding, mCenterPoint.x - halfOuterStrokeWidth - mOuterPadding, mCenterPoint.y - halfOuterStrokeWidth - mOuterPadding);
        }
        canvas.drawArc(mOuterCircleRect, mStartAngle, mSweepAngle, false, mOuterCirclePaint);
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
        mDripAnimator.setInterpolator(new DecelerateInterpolator());
        mDripAnimator.start();
        mDripAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDripCurrentAngle = (float) animation.getAnimatedValue();
                mCurrentDripIndicatorValue = (int) (mDripCurrentAngle * mStepPerAngle);
//                float[] rect = new float[9];
//                mDripMatrix.getValues(rect);
//                Log.d("WalkChart", "mDripMatrix:" + mDripMatrix.toString());
//                Log.d("WalkChart", rect[2] + ":" + rect[5]);
                postInvalidate();
            }
        });
    }

    private float mAnglePerStep;
    private float mStepPerAngle;

    public void setStepCounts(@IntRange(from = DEFAULT_DIVIDER_COUNTS) int stepCounts) {
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