package com.wonders.xlab.pedometer.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.wonders.xlab.pedometer.R;

/**
 * Created by hua on 16/9/3.
 */

public class WalkChart extends View {
    private final float mOuterPadding = 20;
    private final float STROKE_WIDTH_OUTER_CIRCLE = 8;
    private final float STROKE_WIDTH_INNER_CIRCLE = 36;
    private final int DEFAULT_EMPTY_ANGLE = 45;

    private float mEmptyAngle = DEFAULT_EMPTY_ANGLE;
    private float mCircleInterval = 20;

    private Paint mOuterCirclePaint;
    private RectF mOuterCircleRect;

    private Paint mInnerCirclePaint;
    private RectF mInnerCircleRect;

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
    }

    private void initAttribute(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WalkChart);
        mEmptyAngle = array.getFloat(R.styleable.WalkChart_EmptyAngle, DEFAULT_EMPTY_ANGLE);
        if (mEmptyAngle > 180) {
            mEmptyAngle = 180;
        }
        array.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float startAngle = -180 - (90 - mEmptyAngle / 2);
        float sweepAngle = 360 - mEmptyAngle;

        canvas.translate(mCenterPoint.x, mCenterPoint.y);
        //draw outer circle
        if (null == mOuterCircleRect || mOuterCircleRect.isEmpty()) {
            float halfOuterStrokeWidth = STROKE_WIDTH_OUTER_CIRCLE / 2;
            mOuterCircleRect = new RectF(-mCenterPoint.x + halfOuterStrokeWidth + mOuterPadding, -mCenterPoint.y + halfOuterStrokeWidth + mOuterPadding, mCenterPoint.x - halfOuterStrokeWidth - mOuterPadding, mCenterPoint.y - halfOuterStrokeWidth - mOuterPadding);
        }
        canvas.drawArc(mOuterCircleRect, startAngle, sweepAngle, false, mOuterCirclePaint);

        //draw inner circle
        if (null == mInnerCircleRect || mInnerCircleRect.isEmpty()) {
            float halfInnerStrokeWidth = STROKE_WIDTH_INNER_CIRCLE / 2;
            float outerInterval = STROKE_WIDTH_OUTER_CIRCLE + mCircleInterval;
            mInnerCircleRect = new RectF(-mCenterPoint.x + halfInnerStrokeWidth + outerInterval +mOuterPadding, -mCenterPoint.y + halfInnerStrokeWidth + outerInterval +mOuterPadding, mCenterPoint.x - halfInnerStrokeWidth - outerInterval - mOuterPadding, mCenterPoint.y - halfInnerStrokeWidth - outerInterval - mOuterPadding);
        }
        canvas.drawArc(mInnerCircleRect, startAngle, sweepAngle, false, mInnerCirclePaint);
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
}
