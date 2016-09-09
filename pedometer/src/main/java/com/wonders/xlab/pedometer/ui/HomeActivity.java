package com.wonders.xlab.pedometer.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    private SimpleDateFormat mMonthDayFormat;

    class DayFormat implements TitleFormatter {

        @Override
        public CharSequence format(CalendarDay day) {
            return (day.getMonth() + 1) + "月" + day.getDay() + "日";
        }
    }

    private View mDailyTitleView;
    private View mWeekTitleView;
    private View mMonthTitleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        SensorManager manager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        Toast.makeText(this, "manager.getSensorList(Sensor.TYPE_GYROSCOPE):" + manager.getSensorList(Sensor.TYPE_STEP_DETECTOR).size(), Toast.LENGTH_SHORT).show();

        mMonthDayFormat = new SimpleDateFormat("MM月dd日");

        mToolBarLayout = (XToolBarLayout) findViewById(R.id.xtbl);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        setupActionBar(mToolBarLayout.getToolbar());

        mToolBarLayout.setOnNavigationClickListener(new XToolBarLayout.OnNavigationClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
        initTitleView();
        initCalendarPopupWindow();
        initViewPager();

    }

    private void initViewPager() {
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
                        mToolBarLayout.setTitleView(mDailyTitleView, XToolBarLayout.TitleGravity.GRAVITY_TITLE_CENTER);
                        break;
                    case 1:
                        mToolBarLayout.setTitleView(mWeekTitleView, XToolBarLayout.TitleGravity.GRAVITY_TITLE_CENTER);
                        break;
                    case 2:
                        mToolBarLayout.setTitleView(mMonthTitleView, XToolBarLayout.TitleGravity.GRAVITY_TITLE_CENTER);
                        break;
                }
            }
        });
    }

    private void initTitleView() {
        mDailyTitleView = LayoutInflater.from(this).inflate(R.layout.pm_title_view_daily, null, false);
        mWeekTitleView = LayoutInflater.from(this).inflate(R.layout.pm_title_view_week, null, false);
        mMonthTitleView = LayoutInflater.from(this).inflate(R.layout.pm_title_view_month, null, false);

        ((TextView)mDailyTitleView.findViewById(R.id.tvDailyTitle)).setText(mMonthDayFormat.format(new Date()));
        mToolBarLayout.setTitleView(mDailyTitleView, XToolBarLayout.TitleGravity.GRAVITY_TITLE_CENTER);

        mDailyTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarPopupWindow.showOnAnchor(mToolBarLayout, RelativePopupWindow.VerticalPosition.ALIGN_TOP, RelativePopupWindow.HorizontalPosition.LEFT);
            }
        });
    }

    private void initCalendarPopupWindow() {
        mCalendarPopupWindow = new CalendarPopupWindow(this);
        mCalendarPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mCalendarPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mCalendarPopupWindow.setupCalendarView(mDayFormat, CalendarMode.MONTHS);
        mCalendarPopupWindow.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                mCalendarPopupWindow.dismiss();
                ((TextView)mDailyTitleView.findViewById(R.id.tvDailyTitle)).setText(mMonthDayFormat.format(date.getDate()));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!mCalendarPopupWindow.isShowing()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pm_menu_share,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_share) {

        }
        return super.onOptionsItemSelected(item);
    }
}
