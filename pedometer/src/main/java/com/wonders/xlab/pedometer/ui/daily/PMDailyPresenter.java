package com.wonders.xlab.pedometer.ui.daily;

import com.wonders.xlab.pedometer.base.BasePresenter;

public class PMDailyPresenter extends BasePresenter implements PMDailyContract.Presenter {
    private PMDailyContract.View mView;
    private PMDailyContract.Model mModel;

    public PMDailyPresenter(PMDailyContract.View view, PMDailyContract.Model model) {
        mView = view;
        mModel = model;
    }
}
