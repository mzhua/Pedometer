package com.wonders.xlab.pedometer.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wonders.xlab.pedometer.R;
import com.wonders.xlab.pedometer.util.DensityUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by hua on 16/8/26.
 */
public class XToolBarLayout extends LinearLayout {
    private Context mContext;
    private final float DIVIDER_HEIGHT_DEFAULT = 0.8f;

    private static final int GRAVITY_TITLE_MASK = 1;
    public static final int GRAVITY_TITLE_LEFT = GRAVITY_TITLE_MASK << 1;
    public static final int GRAVITY_TITLE_CENTER = GRAVITY_TITLE_MASK << 2;

    private Toolbar mToolbar;
    private TextView mTitleView;
    private View mDividerView;

    private boolean mShowDivider;
    private int mTitleGravity;
    private int mTitleColor;
    private boolean mShowTitleSpinner;
    private String mTitleText;
    private int mBackgroundColor;

    private OnTitleClickListener mOnTitleClickListener;
    private OnNavigationClickListener mOnNavigationClickListener;

    public void setOnTitleClickListener(OnTitleClickListener onTitleClickListener) {
        mOnTitleClickListener = onTitleClickListener;
    }

    public void setOnNavigationClickListener(OnNavigationClickListener onNavigationClickListener) {
        mOnNavigationClickListener = onNavigationClickListener;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({GRAVITY_TITLE_LEFT, GRAVITY_TITLE_CENTER})
    public @interface TitleGravity {
    }

    public interface OnTitleClickListener {
        void onClick();
    }

    public interface OnNavigationClickListener {
        void onClick();
    }

    public XToolBarLayout(Context context) {
        this(context, null);
    }

    public XToolBarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XToolBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOrientation(VERTICAL);
        mContext = context;

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.XToolBarLayout, defStyleAttr, 0);
        mTitleText = array.getString(R.styleable.XToolBarLayout_xtblTitleText);
        if (TextUtils.isEmpty(mTitleText)) {
            mTitleText = getResources().getString(R.string.pm_app_name);
        }
        mTitleGravity = array.getInt(R.styleable.XToolBarLayout_xtblTitleGravity, GRAVITY_TITLE_CENTER);
        mTitleColor = array.getColor(R.styleable.XToolBarLayout_xtblTitleColor, ContextCompat.getColor(context, R.color.pmTopBarTitleColor));
        mBackgroundColor = array.getColor(R.styleable.XToolBarLayout_xtblBackgroundColor, ContextCompat.getColor(context, R.color.pmTopBarBackground));
        mShowDivider = array.getBoolean(R.styleable.XToolBarLayout_xtblShowDivider, false);
        mShowTitleSpinner = array.getBoolean(R.styleable.XToolBarLayout_xtblShowTitleSpinner, false);
        array.recycle();

        mToolbar = (Toolbar) LayoutInflater.from(context).inflate(R.layout.cb_tool_bar, this, false);
        addView(mToolbar);
        setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        mToolbar.setBackgroundColor(mBackgroundColor);
        mToolbar.setTitleTextColor(mTitleColor);
        mToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnNavigationClickListener) {
                    mOnNavigationClickListener.onClick();
                }
            }
        });
        setupTitleView();

        setupDividerView();
    }

    private void setupTitleView() {
        mTitleView = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        updateTitleSpinner();
        mTitleView.setTextColor(mTitleColor);
        mTitleView.setText(mTitleText);
        Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) mTitleView.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        switch (mTitleGravity) {
            case GRAVITY_TITLE_CENTER:
                layoutParams.gravity = Gravity.CENTER;
                break;
            case GRAVITY_TITLE_LEFT:
                layoutParams.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                break;
        }

        mTitleView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnTitleClickListener != null) {
                    mOnTitleClickListener.onClick();
                }
            }
        });
    }

    private void updateTitleSpinner() {
        if (mShowTitleSpinner) {
            Drawable spinnerDrawable = getCompatDrawable(getResources().getDrawable(R.drawable.ic_arrow_drop_down_black_24dp));
            spinnerDrawable.setBounds(0, 0, spinnerDrawable.getIntrinsicWidth(), spinnerDrawable.getIntrinsicHeight());
            mTitleView.setCompoundDrawables(null, null, spinnerDrawable, null);
        } else {
            mTitleView.setCompoundDrawables(null, null, null, null);
        }
    }

    private void setupDividerView() {
        if (mShowDivider) {
            if (mDividerView == null) {
                mDividerView = new View(mContext);
                mDividerView.setId(R.id.divider);
                mDividerView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.pmDivider));
                LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(mContext, DIVIDER_HEIGHT_DEFAULT));
                mDividerView.setLayoutParams(layoutParams);
                addView(mDividerView);
            }
        } else {
            if (mDividerView != null) {
                removeView(mDividerView);
            }
        }
    }

    public void setShowTitleSpinner(boolean showTitleSpinner) {
        this.mShowTitleSpinner = showTitleSpinner;
        updateTitleSpinner();
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public void setTitleGravity(int titleGravity) {
        mTitleGravity = titleGravity;
    }

    public void setTitleColor(int titleColor) {
        mTitleColor = titleColor;
    }

    public void setTitle(@NonNull String title) {
        mTitleView.setText(title);
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
    }

    public void setNavigationIcon(Drawable drawable) {
        getToolbar().setNavigationIcon(getCompatDrawable(drawable));
    }

    /**
     * 获取与标题颜色相同的Drawable
     *
     * @param drawable
     * @return
     */
    public Drawable getCompatDrawable(@NonNull Drawable drawable) {
        Drawable stateButtonDrawable = drawable.mutate();
        stateButtonDrawable.setColorFilter(mTitleColor, PorterDuff.Mode.SRC_ATOP);
        return stateButtonDrawable;
    }

    public void hideNavigationIcon() {
        getToolbar().setNavigationIcon(null);
    }
}
