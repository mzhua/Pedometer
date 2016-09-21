package com.wonders.xlab.pedometer.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.wonders.xlab.pedometer.R;
import com.wonders.xlab.pedometer.XPedometer;
import com.wonders.xlab.pedometer.XPedometerEventConstant;
import com.wonders.xlab.pedometer.base.BaseActivity;
import com.wonders.xlab.pedometer.ui.daily.PMDailyFragment;
import com.wonders.xlab.pedometer.ui.month.PMMonthlyFragment;
import com.wonders.xlab.pedometer.ui.weekly.PMWeeklyFragment;
import com.wonders.xlab.pedometer.util.DateUtil;
import com.wonders.xlab.pedometer.util.FileUtil;
import com.wonders.xlab.pedometer.widget.CircleIndicator;
import com.wonders.xlab.pedometer.widget.XToolBarLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.wonders.xlab.pedometer.widget.XToolBarLayout.TitleView.Daily;
import static com.wonders.xlab.pedometer.widget.XToolBarLayout.TitleView.Monthly;
import static com.wonders.xlab.pedometer.widget.XToolBarLayout.TitleView.Weekly;

public class PMHomeActivity extends BaseActivity {

    private XToolBarLayout mToolBarLayout;
    private ViewPager mViewPager;
    private CircleIndicator mCircleIndicator;

    private StepBroadcastReceiver mStepBroadcastReceiver;
    private PMDailyFragment mDailyFragment;
    private PMWeeklyFragment mWeeklyFragment;
    private PMMonthlyFragment mMonthlyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDailyFragment = PMDailyFragment.newInstance();
        mWeeklyFragment = PMWeeklyFragment.newInstance();
        mMonthlyFragment = PMMonthlyFragment.newInstance();

        mToolBarLayout = (XToolBarLayout) findViewById(R.id.xtbl);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mCircleIndicator = (CircleIndicator) findViewById(R.id.indicator);
        mViewPager.setOffscreenPageLimit(3);

        setupActionBar(mToolBarLayout.getToolbar());
        mToolBarLayout.getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
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
                        mToolBarLayout.showTitleView(Daily);
                        break;
                    case 1:
                        mToolBarLayout.showTitleView(Weekly);
                        break;
                    case 2:
                        mToolBarLayout.showTitleView(Monthly);
                        break;
                }
            }
        });
    }

    private void initTitleView() {
        mToolBarLayout.showTitleView(Daily);
        mToolBarLayout.setDailyTitleViewListener(new XToolBarLayout.OnDailyTitleViewDateChangeListener() {
            @Override
            public void onDateChange(long startTimeInMill, long endTimeInMill) {
                mDailyFragment.refreshView(startTimeInMill, endTimeInMill);
            }
        });
        mToolBarLayout.setWeeklyTitleViewListener(new XToolBarLayout.OnTitleArrowClickListener() {

            @Override
            public void onClick(long startTimeInMill, long endTimeInMill) {
                mWeeklyFragment.refreshView(startTimeInMill, endTimeInMill);
            }
        });
        mToolBarLayout.setMonthlyTitleViewListener(new XToolBarLayout.OnTitleArrowClickListener() {

            @Override
            public void onClick(long startTimeInMill, long endTimeInMill) {
                mMonthlyFragment.refreshView(startTimeInMill, endTimeInMill);
            }
        });
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

    private void sendEventBroadcast(String event, String name) {
        Intent intent = new Intent(getPackageName() + ".pm.event");
        intent.putExtra(XPedometerEventConstant.EXTRA_KEY_EVENT, event);
        intent.putExtra(XPedometerEventConstant.EXTRA_KEY_NAME, name);
        intent.putExtra(XPedometerEventConstant.EXTRA_KEY_TIME_IN_MILL, System.currentTimeMillis());
        sendBroadcast(intent);
    }
}
