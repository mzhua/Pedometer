package com.wonders.xlab.pedometer.widget;

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
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.wonders.xlab.pedometer.R;

/**
 * Created by hua on 16/9/3.
 */

public class WalkChart extends View {
    private final int DEFAULT_DRIP_HALF_HEIGHT = 20;
    private final int DEFAULT_EMPTY_ANGLE = 45;

    private final float STROKE_WIDTH_OUTER_CIRCLE = 8;
    private final float STROKE_WIDTH_INNER_CIRCLE = 36;

    private float mOuterPadding = DEFAULT_DRIP_HALF_HEIGHT;

    private float mEmptyAngle = DEFAULT_EMPTY_ANGLE;
    private float mCircleInterval = DEFAULT_DRIP_HALF_HEIGHT;

    /**
     *
     */
    private float mStartAngle;
    private float mSweepAngle;

    private Paint mOuterCirclePaint;
    private RectF mOuterCircleRect;

    private Paint mInnerCirclePaint;
    private RectF mInnerCircleRect;

    private Bitmap mDripBitmap;
    private Paint mDripBitmapPaint;
    private int mDripWidth;
    private int mDripHeight;
    private float mDripCurrentAngle;
    private RectF mDripRect;
    private Matrix mDripMatrix;

    private Paint mDashPaint;

    private Point mCenterPoint;

    private int mCurrentValue;
    private int mMinValue;
    private int mMaxValue;

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

    private void init(Context context, AttributeSet attrs) {
        initAttribute(context, attrs);

        mCenterPoint = new Point();

        mOuterCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterCirclePaint.setColor(Color.parseColor("#D5D1D1"));
        mOuterCirclePaint.setStyle(Paint.Style.STROKE);
        mOuterCirclePaint.setStrokeWidth(STROKE_WIDTH_OUTER_CIRCLE);

        mInnerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerCirclePaint.setColor(Color.parseColor("#D5D1D1"));
        mInnerCirclePaint.setStyle(Paint.Style.STROKE);
        mInnerCirclePaint.setStrokeWidth(STROKE_WIDTH_INNER_CIRCLE);

        mDashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDashPaint.setColor(Color.parseColor("#747171"));
        mDashPaint.setStyle(Paint.Style.STROKE);
        mDashPaint.setStrokeWidth(1);

        mDripBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDripBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_drip);
        mDripWidth = mDripBitmap.getWidth();
        mDripHeight = mDripBitmap.getHeight();
        mDripRect = new RectF(0, 0, mDripWidth, mDripHeight);
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
        mStartAngle = -180 - (90 - mEmptyAngle / 2);
        mSweepAngle = 360 - mEmptyAngle;
        mDripCurrentAngle = 0;
        array.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //translate base point
        canvas.translate(mCenterPoint.x, mCenterPoint.y);

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

        //draw drip indicator
        canvas.save();
        canvas.rotate(90 + mEmptyAngle / 2);
        drawXYAxis(canvas);

        mDripMatrix.reset();
        mDripMatrix.setRotate(90, mDripWidth / 2, mDripHeight / 2);
        mDripMatrix.postTranslate(mOuterCircleRect.width() / 2 - mDripWidth / 2, -mDripHeight / 2);
        mDripMatrix.postRotate(mDripCurrentAngle);
        canvas.drawBitmap(mDripBitmap, mDripMatrix, mDripBitmapPaint);
        canvas.restore();
    }

    private void drawXYAxis(Canvas canvas) {
//        canvas.drawLine(-mCenterPoint.x, 0, mCenterPoint.x, 0, mDashPaint);
//        canvas.drawLine(0, -mCenterPoint.y, 0, mCenterPoint.y, mDashPaint);
//        canvas.drawPoint(0, mCenterPoint.y, mInnerCirclePaint);
//        canvas.drawPoint(mCenterPoint.x, 0, mInnerCirclePaint);
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

    private boolean mIsAnimating = false;
    public void setTargetAngle(final int angle) {
        if (mIsAnimating) {
            return;
        }
        mDripCurrentAngle = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mIsAnimating = true;
                boolean rotate = true;
                while (rotate) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        rotate = false;
                        e.printStackTrace();
                    }
                    mDripCurrentAngle += 1;
                    if (mDripCurrentAngle >= angle) {
                        mDripCurrentAngle = angle;
                        rotate = false;
                    }
                    float[] rect = new float[9];
                    mDripMatrix.getValues(rect);
                    Log.d("WalkChart", "mDripMatrix:" + mDripMatrix.toString());
                    Log.d("WalkChart", rect[2] + ":" + rect[5]);
                    postInvalidate();
                }
                mIsAnimating = false;
            }
        }).start();
    }
}
