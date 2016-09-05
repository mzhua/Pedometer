package com.wonders.xlab.pedometer.ui.home;

import android.os.Bundle;

import com.wonders.xlab.pedometer.R;
import com.wonders.xlab.pedometer.base.MVPActivity;

public class HomeActivity extends MVPActivity<HomeContract.Presenter> implements HomeContract.View {

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
    }
}
