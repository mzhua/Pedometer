package com.wonders.xlab.pedometer.ui.daily;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wonders.xlab.pedometer.R;
import com.wonders.xlab.pedometer.base.MVPFragment;

public class PMDailyFragment extends MVPFragment<PMDailyPresenter> implements PMDailyContract.View {
    private PMDailyPresenter mPresenter;

    public PMDailyFragment() {
        // Required empty public constructor
    }

    public static PMDailyFragment newInstance() {
        PMDailyFragment fragment = new PMDailyFragment();
        return fragment;
    }

    @Override
    public PMDailyPresenter getPresenter() {
        if (null == mPresenter) {
            mPresenter = new PMDailyPresenter(this, new PMDailyModel());
        }
        return mPresenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pm_daily_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
