package com.wonders.xlab.pedometer.ui.monthly;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wonders.xlab.pedometer.R;
import com.wonders.xlab.pedometer.base.MVPFragment;

public class PMMonthlyFragment extends MVPFragment<PMMonthlyPresenter> implements PMMonthlyContract.View {
    private PMMonthlyPresenter mPresenter;

    public PMMonthlyFragment() {
        // Required empty public constructor
    }

    public static PMMonthlyFragment newInstance() {
        PMMonthlyFragment fragment = new PMMonthlyFragment();
        return fragment;
    }

    @Override
    public PMMonthlyPresenter getPresenter() {
        if (null == mPresenter) {
            mPresenter = new PMMonthlyPresenter(this, new PMMonthlyModel());
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
        return inflater.inflate(R.layout.pm_monthly_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
