package com.wonders.xlab.pedometer.ui.weekly;

import com.wonders.xlab.pedometer.base.BasePresenter;

public class PMWeeklyPresenter extends BasePresenter implements PMWeeklyContract.Presenter {
    private PMWeeklyContract.View mView;
    private PMWeeklyContract.Model mModel;

    public PMWeeklyPresenter(PMWeeklyContract.View view, PMWeeklyContract.Model model) {
        mView = view;
        mModel = model;
    }
}
