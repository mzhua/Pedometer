package com.wonders.xlab.pedometer.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.wonders.xlab.pedometer.R;
import com.wonders.xlab.pedometer.XPedometerEventConstant;
import com.wonders.xlab.pedometer.base.BaseActivity;
import com.wonders.xlab.pedometer.ui.daily.PMDailyFragment;
import com.wonders.xlab.pedometer.ui.month.PMMonthlyFragment;
import com.wonders.xlab.pedometer.ui.weekly.PMWeeklyFragment;
import com.wonders.xlab.pedometer.util.DateUtil;
import com.wonders.xlab.pedometer.util.FileUtil;
import com.wonders.xlab.pedometer.widget.CalendarPopupWindow;
import com.wonders.xlab.pedometer.widget.CircleIndicator;
import com.wonders.xlab.pedometer.widget.RelativePopupWindow;
import com.wonders.xlab.pedometer.widget.XTopBarLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import static com.wonders.xlab.pedometer.widget.XTopBarLayout.TitleView.Daily;
import static com.wonders.xlab.pedometer.widget.XTopBarLayout.TitleView.Monthly;
import static com.wonders.xlab.pedometer.widget.XTopBarLayout.TitleView.Weekly;

public class PMHomeActivity extends BaseActivity {

    private XTopBarLayout mTopBarLayout;
    private ViewPager mViewPager;
    private CircleIndicator mCircleIndicator;

    private StepBroadcastReceiver mStepBroadcastReceiver;
    private PMDailyFragment mDailyFragment;
    private PMWeeklyFragment mWeeklyFragment;
    private PMMonthlyFragment mMonthlyFragment;

    private CalendarPopupWindow mCalendarPopupWindow;

    /**
     * 先隐藏提起选择控件
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (null != mCalendarPopupWindow && mCalendarPopupWindow.isShowing()) {
            mCalendarPopupWindow.dismiss();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDailyFragment = PMDailyFragment.newInstance();
        mWeeklyFragment = PMWeeklyFragment.newInstance();
        mMonthlyFragment = PMMonthlyFragment.newInstance();

        mTopBarLayout = (XTopBarLayout) findViewById(R.id.xtbl);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mCircleIndicator = (CircleIndicator) findViewById(R.id.indicator);
        mViewPager.setOffscreenPageLimit(3);

        setupActionBar(mTopBarLayout.getToolbar());
        mTopBarLayout.getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initTitleView();
        initViewPager();

        IntentFilter intentFilter = new IntentFilter(getPackageName() + ".pm.step.broadcast");
        mStepBroadcastReceiver = new StepBroadcastReceiver();
        registerReceiver(mStepBroadcastReceiver, intentFilter);

        sendEventBroadcast(XPedometerEventConstant.EVENT_PAGE_CREATE_HOME, getResources().getString(R.string.pm_app_name));
    }

    /**
     * 接收计步Service发出的广播
     */
    class StepBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long timeMillis = System.currentTimeMillis();
            mDailyFragment.refreshView(DateUtil.getBeginTimeOfDayInMill(timeMillis), DateUtil.getEndTimeOfDayInMill(timeMillis));
            mWeeklyFragment.refreshView(DateUtil.getBeginTimeOfWeekInMill(timeMillis), DateUtil.getEndTimeOfWeekInMill(timeMillis));
            mMonthlyFragment.refreshView(DateUtil.getBeginTimeOfMonthInMill(timeMillis), DateUtil.getEndTimeOfMonthInMill(timeMillis));
        }
    }

    private void initViewPager() {
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = null;
                switch (position) {
                    case 0:
                        fragment = mDailyFragment;
                        break;
                    case 1:
                        fragment = mWeeklyFragment;
                        break;
                    case 2:
                        fragment = mMonthlyFragment;
                        break;
                }

                return fragment;
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
        mCircleIndicator.setViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        mTopBarLayout.showTitleView(Daily);
                        break;
                    case 1:
                        mTopBarLayout.showTitleView(Weekly);
                        break;
                    case 2:
                        mTopBarLayout.showTitleView(Monthly);
                        break;
                }
            }
        });
    }

    private void initTitleView() {
        mTopBarLayout.showTitleView(Daily);
        mTopBarLayout.setDailyTitleViewListener(new XTopBarLayout.OnDailyTitleViewClickListener() {
            @Override
            public void onClick() {
                showCalendarPopupWindow();
            }
        });
        mTopBarLayout.setWeeklyTitleViewListener(new XTopBarLayout.OnTitleArrowClickListener() {

            @Override
            public void onClick(long startTimeInMill, long endTimeInMill) {
                mWeeklyFragment.refreshView(startTimeInMill, endTimeInMill);
            }
        });
        mTopBarLayout.setMonthlyTitleViewListener(new XTopBarLayout.OnTitleArrowClickListener() {

            @Override
            public void onClick(long startTimeInMill, long endTimeInMill) {
                mMonthlyFragment.refreshView(startTimeInMill, endTimeInMill);
            }
        });
    }

    /**
     * 日期选择控件
     */
    private void showCalendarPopupWindow() {
        if (null == mCalendarPopupWindow) {
            mCalendarPopupWindow = new CalendarPopupWindow(this);
            mCalendarPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            mCalendarPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            mCalendarPopupWindow.setupCalendarView(null, CalendarMode.MONTHS);
            mCalendarPopupWindow.setOnDateSelectedListener(new OnDateSelectedListener() {
                @Override
                public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                    mCalendarPopupWindow.dismiss();
                    Calendar calendar = date.getCalendar();
                    long mCurrentDayTimeInMill = calendar.getTimeInMillis();
                    mDailyFragment.refreshView(DateUtil.getBeginTimeOfDayInMill(calendar), DateUtil.getEndTimeOfDayInMill(calendar));
                    mTopBarLayout.setTitleViewText(Daily, mCurrentDayTimeInMill);
                }
            });
        }
        mCalendarPopupWindow.showOnAnchor(mTopBarLayout, RelativePopupWindow.VerticalPosition.ALIGN_TOP, RelativePopupWindow.HorizontalPosition.LEFT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pm_menu_share, menu);
        return true;
    }

    /**
     * 菜单图标预处理,与toolbar颜色区分
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        setupMenuIcon(menu.findItem(R.id.menu_share));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_share) {
            shareViewPager();
            sendEventBroadcast(XPedometerEventConstant.EVENT_CLICK_MENU_SHARE, getResources().getString(R.string.menu_share));
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 截屏分享
     */
    private void shareViewPager() {
        if (mViewPager.getDrawingCache() != null) {
            mViewPager.destroyDrawingCache();
        }
        mViewPager.destroyDrawingCache();

        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setDrawingCacheEnabled(true);
                mViewPager.buildDrawingCache();

                Bitmap bm = mViewPager.getDrawingCache();
                FileOutputStream outputStream;
                try {
                    File file = FileUtil.createTempFile(PMHomeActivity.this, "share.jpg");
                    if (file != null) {
                        outputStream = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        shareImage(Uri.parse("file:///" + file.getAbsolutePath()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void shareImage(Uri uriToImage) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.pm_share_to)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mStepBroadcastReceiver) {
            unregisterReceiver(mStepBroadcastReceiver);
            mStepBroadcastReceiver = null;
        }
        sendEventBroadcast(XPedometerEventConstant.EVENT_PAGE_DESTROY_HOME, getResources().getString(R.string.pm_app_name));
    }

    /**
     * 发送事件广播供APP自行记录,处理
     *
     * @param event
     * @param name
     */
    private void sendEventBroadcast(String event, String name) {
        Intent intent = new Intent(getPackageName() + ".pm.event");
        intent.putExtra(XPedometerEventConstant.EXTRA_KEY_EVENT, event);
        intent.putExtra(XPedometerEventConstant.EXTRA_KEY_NAME, name);
        intent.putExtra(XPedometerEventConstant.EXTRA_KEY_TIME_IN_MILL, System.currentTimeMillis());
        sendBroadcast(intent);
    }
}
