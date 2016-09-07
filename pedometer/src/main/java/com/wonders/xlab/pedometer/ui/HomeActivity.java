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

public class HomeActivity extends BaseActivity {

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);


        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = null;
                switch (position) {
                    case 2:
                        fragment = new PMDailyFragment();
                        break;
                    case 1:
                        fragment = new PMWeeklyFragment();
                        break;
                    case 0:
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
