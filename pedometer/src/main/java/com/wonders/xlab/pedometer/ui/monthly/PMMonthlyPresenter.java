package com.wonders.xlab.pedometer.ui.monthly;

import com.wonders.xlab.pedometer.base.BasePresenter;

public class PMMonthlyPresenter extends BasePresenter implements PMMonthlyContract.Presenter {
    private PMMonthlyContract.View mView;
    private PMMonthlyContract.Model mModel;

    public PMMonthlyPresenter(PMMonthlyContract.View view, PMMonthlyContract.Model model) {
        mView = view;
        mModel = model;
    }
}
