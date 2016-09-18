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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.wonders.xlab.pedometer.R;
import com.wonders.xlab.pedometer.util.DateUtil;
import com.wonders.xlab.pedometer.util.DensityUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;

import static com.wonders.xlab.pedometer.widget.XToolBarLayout.TitleGravity.GRAVITY_TITLE_CENTER;
import static com.wonders.xlab.pedometer.widget.XToolBarLayout.TitleGravity.GRAVITY_TITLE_LEFT;
import static com.wonders.xlab.pedometer.widget.XToolBarLayout.TitleView.Daily;
import static com.wonders.xlab.pedometer.widget.XToolBarLayout.TitleView.Monthly;
import static com.wonders.xlab.pedometer.widget.XToolBarLayout.TitleView.Weekly;

/**
 * Created by hua on 16/8/26.
 */
public class XToolBarLayout extends LinearLayout {
    private Context mContext;
    private final float DIVIDER_HEIGHT_DEFAULT = 0.8f;

    private Toolbar mToolbar;
    private View mTitleView;
    private View mDividerView;//Toolbar下方的分割线

    private boolean mShowDivider;
    private int mTitleGravity = TitleGravity.GRAVITY_TITLE_CENTER;
    private int mTitleColor;
    private boolean mShowNavigation = true;
    private String mTitleText;
    private int mBackgroundColor;

    private TextView mDailyTitleView;
    private View mWeekTitleView;
    private View mMonthTitleView;

    private long mCurrentDayTimeInMill;
    private long mCurrentWeekTimeInMill;
    private long mCurrentMonthTimeInMill;

    private OnDailyTitleViewDateChangeListener mDailyDateChangeListener;

    private CalendarPopupWindow mCalendarPopupWindow;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({Daily, Weekly, Monthly})
    public @interface TitleView {
        int Daily = 0;
        int Weekly = 1;
        int Monthly = 2;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({GRAVITY_TITLE_LEFT, GRAVITY_TITLE_CENTER})
    public @interface TitleGravity {
        int GRAVITY_TITLE_LEFT = 2;
        int GRAVITY_TITLE_CENTER = 4;
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
        initTitleView(context);
        initAttributes(context, attrs, defStyleAttr);
        initToolbar(context);
        initCalendarPopupWindow();
        setupDividerView();
    }

    /**
     * 自定义属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void initAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.XToolBarLayout, defStyleAttr, 0);
        mTitleText = array.getString(R.styleable.XToolBarLayout_xtblTitleText);
        if (TextUtils.isEmpty(mTitleText)) {
            mTitleText = getResources().getString(R.string.pm_app_name);
        }
        mTitleGravity = array.getInt(R.styleable.XToolBarLayout_xtblTitleGravity, GRAVITY_TITLE_CENTER);
        mTitleColor = array.getColor(R.styleable.XToolBarLayout_xtblTitleColor, ContextCompat.getColor(context, R.color.pmTopBarTitleColor));
        mBackgroundColor = array.getColor(R.styleable.XToolBarLayout_xtblBackgroundColor, ContextCompat.getColor(context, R.color.pmTopBarBackground));
        mShowDivider = array.getBoolean(R.styleable.XToolBarLayout_xtblShowDivider, false);
        mShowNavigation = array.getBoolean(R.styleable.XToolBarLayout_xtblShowNavigation, false);
        array.recycle();
    }

    /**
     * Toolbar
     * 负责导航图标和菜单
     *
     * @param context
     */
    private void initToolbar(Context context) {
        mToolbar = (Toolbar) LayoutInflater.from(context).inflate(R.layout.pm_tool_bar, this, false);
        addView(mToolbar);
        if (mShowNavigation) {
            setNavigationIcon(getResources().getDrawable(R.drawable.pm_ic_arrow_back_black));
        } else {
            hideNavigationIcon();
        }
        mToolbar.setBackgroundColor(mBackgroundColor);
        mToolbar.setTitleTextColor(mTitleColor);
    }

    /**
     * 月、星期、日三种类型的Title View
     *
     * @param context
     */
    private void initTitleView(Context context) {
        mDailyTitleView = (TextView) LayoutInflater.from(context).inflate(R.layout.pm_title_view_daily, null, false);
        mWeekTitleView = LayoutInflater.from(context).inflate(R.layout.pm_title_view_week, null, false);
        mMonthTitleView = LayoutInflater.from(context).inflate(R.layout.pm_title_view_month, null, false);
        mCurrentDayTimeInMill = System.currentTimeMillis();
        mCurrentWeekTimeInMill = mCurrentDayTimeInMill;
        mCurrentMonthTimeInMill = mCurrentDayTimeInMill;

        mDailyTitleView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarPopupWindow.showOnAnchor(mToolbar, RelativePopupWindow.VerticalPosition.ALIGN_TOP, RelativePopupWindow.HorizontalPosition.LEFT);
            }
        });
    }

    /**
     * 日期选择控件
     */
    private void initCalendarPopupWindow() {
        mCalendarPopupWindow = new CalendarPopupWindow(mContext);
        mCalendarPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mCalendarPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mCalendarPopupWindow.setupCalendarView(null, CalendarMode.MONTHS);
        mCalendarPopupWindow.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                mCalendarPopupWindow.dismiss();
                Calendar calendar = date.getCalendar();
                mCurrentDayTimeInMill = calendar.getTimeInMillis();
                if (null != mDailyDateChangeListener) {
                    mDailyDateChangeListener.onDateChange(DateUtil.getBeginTimeOfDayInMill(calendar), DateUtil.getEndTimeOfDayInMill(calendar));
                }
                setTitleViewText(Daily, mCurrentDayTimeInMill);
            }
        });
    }

    /**
     * 先隐藏提起选择控件
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mCalendarPopupWindow.isShowing()) {
            mCalendarPopupWindow.dismiss();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 显示标题(日/周/月)
     *
     * @param titleView
     */
    public void showTitleView(@TitleView int titleView) {

        mDailyTitleView.setVisibility(GONE);
        mWeekTitleView.setVisibility(GONE);
        mMonthTitleView.setVisibility(GONE);
        long currentTimeInMill = System.currentTimeMillis();

        switch (titleView) {
            case Daily:
                currentTimeInMill = mCurrentDayTimeInMill;
                mTitleView = mDailyTitleView;
                break;
            case Weekly:
                currentTimeInMill = mCurrentWeekTimeInMill;
                mTitleView = mWeekTitleView;
                break;
            case Monthly:
                currentTimeInMill = mCurrentMonthTimeInMill;
                mTitleView = mMonthTitleView;
                break;
        }
        setTitleViewText(titleView, currentTimeInMill);
        mTitleView.setVisibility(VISIBLE);
        if (mTitleView.getParent() == null) {
            mToolbar.addView(mTitleView);
        }
        mTitleView.setBackgroundResource(getThemeSelectableBackgroundId(mContext));

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

    private Calendar mCalendar = Calendar.getInstance();

    /**
     * 设置标题文字
     *
     * @param titleView
     * @param timeInMill
     */
    private void setTitleViewText(@TitleView int titleView, long timeInMill) {
        String titleText = "";
        switch (titleView) {
            case Daily:
                mDailyTitleView.setText(DateUtil.getDayFormatString(timeInMill));
                break;
            case Weekly:
                GetWeekTimeRange getWeekTimeRange = new GetWeekTimeRange().invoke();
                long begin = getWeekTimeRange.getBegin();
                long end = getWeekTimeRange.getEnd();
                titleText = DateUtil.getDayFormatString(begin) + "-" + DateUtil.getDayFormatString(end);
                ((TextView) mWeekTitleView.findViewById(R.id.tvWeekTitle)).setText(titleText);
                break;
            case Monthly:
                ((TextView) mMonthTitleView.findViewById(R.id.tvMonthTitle)).setText(DateUtil.getMonthFormatString(timeInMill));
                break;
        }
    }

    public interface OnTitleArrowClickListener {
        void onClick(long startTimeInMill, long endTimeInMill);
    }

    public interface OnDailyTitleViewDateChangeListener {
        void onDateChange(long startTimeInMill, long endTimeInMill);
    }

    /**
     * 日模式下监听日期选择的变化
     *
     * @param listener
     */
    public void setDailyTitleViewListener(@NonNull OnDailyTitleViewDateChangeListener listener) {
        mDailyDateChangeListener = listener;
    }

    /**
     * 周模式下监听上一周,下一周的事件
     *
     * @param callback
     */
    public void setWeeklyTitleViewListener(@NonNull final OnTitleArrowClickListener callback) {
        mWeekTitleView.findViewById(R.id.ivWeekPre).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar.setTimeInMillis(mCurrentWeekTimeInMill);
                mCalendar.add(Calendar.WEEK_OF_MONTH,-1);
                mCurrentWeekTimeInMill = mCalendar.getTimeInMillis();
                GetWeekTimeRange getWeekTimeRange = new GetWeekTimeRange().invoke();
                long begin = getWeekTimeRange.getBegin();
                long end = getWeekTimeRange.getEnd();
                setTitleViewText(Weekly,mCurrentWeekTimeInMill);
                callback.onClick(begin, end);
            }
        });
        mWeekTitleView.findViewById(R.id.ivWeekNext).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar.setTimeInMillis(mCurrentWeekTimeInMill);
                mCalendar.add(Calendar.WEEK_OF_MONTH,1);
                mCurrentWeekTimeInMill = mCalendar.getTimeInMillis();
                GetWeekTimeRange getWeekTimeRange = new GetWeekTimeRange().invoke();
                long begin = getWeekTimeRange.getBegin();
                long end = getWeekTimeRange.getEnd();
                setTitleViewText(Weekly,mCurrentWeekTimeInMill);
                callback.onClick(begin, end);
            }
        });
    }

    /**
     * 月模式下监听上一周,下一周的事件
     *
     * @param callback
     */
    public void setMonthlyTitleViewListener(final OnTitleArrowClickListener callback) {
        mMonthTitleView.findViewById(R.id.ivMonthPre).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar.setTimeInMillis(mCurrentMonthTimeInMill);
                mCalendar.add(Calendar.MONTH, -1);
                mCurrentMonthTimeInMill = mCalendar.getTimeInMillis();
                setTitleViewText(Monthly,mCurrentMonthTimeInMill);
                callback.onClick(DateUtil.getBeginTimeOfMonthInMill(mCurrentMonthTimeInMill), DateUtil.getEndTimeOfMonthInMill(mCurrentMonthTimeInMill));
            }
        });
        mMonthTitleView.findViewById(R.id.ivMonthNext).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar.setTimeInMillis(mCurrentMonthTimeInMill);
                mCalendar.add(Calendar.MONTH, 1);
                mCurrentMonthTimeInMill = mCalendar.getTimeInMillis();
                setTitleViewText(Monthly,mCurrentMonthTimeInMill);
                callback.onClick(DateUtil.getBeginTimeOfMonthInMill(mCurrentMonthTimeInMill), DateUtil.getEndTimeOfMonthInMill(mCurrentMonthTimeInMill));
            }
        });
    }

    /**
     * Material Design按下效果
     *
     * @param context
     * @return
     */
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

    /**
     * 设置分割线
     */
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

    /**
     * 隐藏导航图标
     */
    public void hideNavigationIcon() {
        getToolbar().setNavigationIcon(null);
    }

    private class GetWeekTimeRange {
        private long mBegin;
        private long mEnd;

        public long getBegin() {
            return mBegin;
        }

        public long getEnd() {
            return mEnd;
        }

        public GetWeekTimeRange invoke() {
            mBegin = DateUtil.getBeginTimeOfWeekInMill(mCurrentWeekTimeInMill);
            mEnd = DateUtil.getEndTimeOfWeekInMill(mCurrentWeekTimeInMill);
            return this;
        }
    }
}
