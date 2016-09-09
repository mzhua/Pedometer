package com.wonders.xlab.pedometer.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wonders.xlab.pedometer.R;
import com.wonders.xlab.pedometer.util.DensityUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.wonders.xlab.pedometer.widget.XToolBarLayout.TitleGravity.GRAVITY_TITLE_CENTER;
import static com.wonders.xlab.pedometer.widget.XToolBarLayout.TitleGravity.GRAVITY_TITLE_LEFT;

/**
 * Created by hua on 16/8/26.
 */
public class XToolBarLayout extends LinearLayout {
    private Context mContext;
    private final float DIVIDER_HEIGHT_DEFAULT = 0.8f;

//    private static final int GRAVITY_TITLE_MASK = 1;
//    public static final int GRAVITY_TITLE_LEFT = GRAVITY_TITLE_MASK << 1;
//    public static final int GRAVITY_TITLE_CENTER = GRAVITY_TITLE_MASK << 2;

    private Toolbar mToolbar;
    private View mTitleView;
    private View mDividerView;

    private boolean mShowDivider;
    private int mTitleGravity;
    private int mTitleColor;
    private boolean mShowTitleSpinner;
    private boolean mShowNavigation = true;
    private String mTitleText;
    private int mBackgroundColor;

    private OnNavigationClickListener mOnNavigationClickListener;

    public void setOnNavigationClickListener(OnNavigationClickListener onNavigationClickListener) {
        mOnNavigationClickListener = onNavigationClickListener;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({GRAVITY_TITLE_LEFT, GRAVITY_TITLE_CENTER})
    public @interface TitleGravity {
        int GRAVITY_TITLE_LEFT = 2;
        int GRAVITY_TITLE_CENTER = 4;
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
        mShowNavigation = array.getBoolean(R.styleable.XToolBarLayout_xtblShowNavigation, false);
        array.recycle();

        mToolbar = (Toolbar) LayoutInflater.from(context).inflate(R.layout.pm_tool_bar, this, false);
        addView(mToolbar);
        if (mShowNavigation) {
            setNavigationIcon(getResources().getDrawable(R.drawable.pm_ic_arrow_back_black));
        } else {
            hideNavigationIcon();
        }
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
        setupDividerView();
    }

    public void setTitleView(@NonNull View titleView, @TitleGravity int titleViewGravity) {
        this.mTitleGravity = titleViewGravity;
        if (mTitleView != null) {
            mToolbar.removeView(mTitleView);
        }
        mTitleView = titleView;
        mToolbar.addView(titleView);

        titleView.setBackgroundResource(getThemeSelectableBackgroundId(mContext));

        Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) mTitleView.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        switch (mTitleGravity) {
            case GRAVITY_TITLE_CENTER:
                layoutParams.gravity = Gravity.CENTER;
                break;
            case GRAVITY_TITLE_LEFT:
                layoutParams.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                break;
        }
    }

    private static int getThemeSelectableBackgroundId(Context context) {
        //Get selectableItemBackgroundBorderless defined for AppCompat
        int colorAttr = context.getResources().getIdentifier(
                "selectableItemBackgroundBorderless", "attr", context.getPackageName());

        if (colorAttr == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                colorAttr = android.R.attr.selectableItemBackgroundBorderless;
            } else {
                colorAttr = android.R.attr.selectableItemBackground;
            }
        }

        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.resourceId;
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

    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
    }

    public void setNavigationIcon(Drawable drawable) {
        mShowNavigation = (drawable != null);
        getToolbar().setNavigationIcon(getCompatDrawable(drawable));
    }

    /**
     * 获取与标题颜色相同的Drawable
     *
     * @param drawable
     * @return
     */
    public Drawable getCompatDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Drawable stateButtonDrawable = drawable.mutate();
        stateButtonDrawable.setColorFilter(mTitleColor, PorterDuff.Mode.SRC_ATOP);
        return stateButtonDrawable;
    }

    public void hideNavigationIcon() {
        getToolbar().setNavigationIcon(null);
    }
}
