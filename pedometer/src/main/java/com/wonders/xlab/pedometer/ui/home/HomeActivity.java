package com.wonders.xlab.pedometer.ui.home;

import android.os.Bundle;

import com.wonders.xlab.pedometer.R;
import com.wonders.xlab.pedometer.base.MVPActivity;
import com.wonders.xlab.pedometer.widget.WalkChart;

public class HomeActivity extends MVPActivity<HomeContract.Presenter> implements HomeContract.View {

    private WalkChart mWalkChart;
    private HomeContract.Presenter mPresenter;

    @Override
    public HomeContract.Presenter getPresenter() {
        if (null == mPresenter) {
            mPresenter = new HomePresenter(this, new HomeModel());
        }
        return mPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mWalkChart = (WalkChart) findViewById(R.id.walkChart);
        mWalkChart.startWithStepCounts(67899);
    }
}
