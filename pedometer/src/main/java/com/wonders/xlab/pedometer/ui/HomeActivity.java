package com.wonders.xlab.pedometer.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.wonders.xlab.pedometer.R;
import com.wonders.xlab.pedometer.base.BaseActivity;
import com.wonders.xlab.pedometer.ui.daily.PMDailyFragment;
import com.wonders.xlab.pedometer.ui.month.PMMonthlyFragment;
import com.wonders.xlab.pedometer.ui.weekly.PMWeeklyFragment;
import com.wonders.xlab.pedometer.widget.XToolBarLayout;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeActivity extends BaseActivity {

    private XToolBarLayout mToolBarLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mToolBarLayout = (XToolBarLayout) findViewById(R.id.xtbl);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        SimpleDateFormat format = new SimpleDateFormat("MM月dd日");
        mToolBarLayout.setTitle(format.format(new Date()));
        mToolBarLayout.setShowTitleSpinner(true);

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
    }
}
