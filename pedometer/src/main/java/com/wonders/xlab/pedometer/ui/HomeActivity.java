package com.wonders.xlab.pedometer.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.wonders.xlab.pedometer.R;
import com.wonders.xlab.pedometer.base.BaseActivity;
import com.wonders.xlab.pedometer.ui.daily.PMDailyFragment;
import com.wonders.xlab.pedometer.ui.month.PMMonthlyFragment;
import com.wonders.xlab.pedometer.ui.weekly.PMWeeklyFragment;
import com.wonders.xlab.pedometer.widget.CalendarPopupWindow;
import com.wonders.xlab.pedometer.widget.RelativePopupWindow;
import com.wonders.xlab.pedometer.widget.XToolBarLayout;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeActivity extends BaseActivity {

    private XToolBarLayout mToolBarLayout;
    private ViewPager mViewPager;
    private CalendarPopupWindow mCalendarPopupWindow;
    private DayFormat mDayFormat = new DayFormat();
    private MonthFormat mMonthFormat = new MonthFormat();


    class DayFormat implements TitleFormatter {

        @Override
        public CharSequence format(CalendarDay day) {
            return (day.getMonth() + 1) + "月" + day.getDay() + "日";
        }
    }

    class MonthFormat implements TitleFormatter {

        @Override
        public CharSequence format(CalendarDay day) {
            return day.getYear() + "年" + (day.getMonth() + 1) + "月";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final SimpleDateFormat format = new SimpleDateFormat("MM月dd日");

        mToolBarLayout = (XToolBarLayout) findViewById(R.id.xtbl);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        mCalendarPopupWindow = new CalendarPopupWindow(this);
        mCalendarPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mCalendarPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        mCalendarPopupWindow.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                mToolBarLayout.setTitle(format.format(date.getDate()));
                mCalendarPopupWindow.dismiss();
            }
        });

        mToolBarLayout.setTitle(format.format(new Date()));
        mToolBarLayout.setShowTitleSpinner(true);
        mToolBarLayout.setOnTitleClickListener(new XToolBarLayout.OnTitleClickListener() {
            @Override
            public void onClick() {
                mCalendarPopupWindow.showOnAnchor(mToolBarLayout, RelativePopupWindow.VerticalPosition.ALIGN_TOP, RelativePopupWindow.HorizontalPosition.LEFT);
            }
        });

        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = null;
                switch (position) {
                    case 0:
                        fragment = new PMDailyFragment();
                        break;
                    case 1:
                        fragment = new PMWeeklyFragment();
                        break;
                    case 2:
                        fragment = new PMMonthlyFragment();
                        break;
                }

                return fragment;
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        mCalendarPopupWindow.setupCalendarView(mDayFormat, CalendarMode.MONTHS);
                        break;
                    case 1:
                        mCalendarPopupWindow.setupCalendarView(mDayFormat, CalendarMode.WEEKS);
                        break;
                    case 2:
                        mCalendarPopupWindow.setupCalendarView(mMonthFormat, CalendarMode.MONTHS);
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mCalendarPopupWindow.isShowing()) {
            mCalendarPopupWindow.dismiss();
        } else {
            super.onBackPressed();
        }
    }
}
