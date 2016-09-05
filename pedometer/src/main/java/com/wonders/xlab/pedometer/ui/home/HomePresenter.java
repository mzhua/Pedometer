package com.wonders.xlab.pedometer.ui.home;


import com.wonders.xlab.pedometer.base.BasePresenter;

public class HomePresenter extends BasePresenter implements HomeContract.Presenter {
    private HomeContract.View mView;
    private HomeContract.Model mModel;

    public HomePresenter(HomeContract.View view, HomeContract.Model model) {
        mView = view;
        mModel = model;
    }
}
