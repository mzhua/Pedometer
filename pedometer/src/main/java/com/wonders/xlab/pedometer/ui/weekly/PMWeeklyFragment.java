package com.wonders.xlab.pedometer.ui.weekly;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wonders.xlab.pedometer.R;
import com.wonders.xlab.pedometer.base.MVPFragment;

public class PMWeeklyFragment extends MVPFragment<PMWeeklyPresenter> implements PMWeeklyContract.View {
    private PMWeeklyPresenter mPresenter;

    @Override
    public PMWeeklyPresenter getPresenter() {
        if (null == mPresenter) {
            mPresenter = new PMWeeklyPresenter(this, new PMWeeklyModel());
        }
        return mPresenter;
    }

    public PMWeeklyFragment() {
        // Required empty public constructor
    }

    public static PMWeeklyFragment newInstance() {
        PMWeeklyFragment fragment = new PMWeeklyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pm_weekly_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
