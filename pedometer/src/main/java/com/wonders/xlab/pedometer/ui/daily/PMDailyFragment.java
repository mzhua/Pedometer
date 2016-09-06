package com.wonders.xlab.pedometer.ui.daily;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wonders.xlab.pedometer.R;
import com.wonders.xlab.pedometer.base.MVPFragment;
import com.wonders.xlab.pedometer.widget.DataBean;
import com.wonders.xlab.pedometer.widget.WalkMinutesBarChart;

import java.util.ArrayList;
import java.util.Random;

public class PMDailyFragment extends MVPFragment<PMDailyPresenter> implements PMDailyContract.View {
    private WalkMinutesBarChart mBarChart;

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
        mBarChart = (WalkMinutesBarChart) view.findViewById(R.id.barChart);

        ArrayList<DataBean> mStepDataBeanList = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 20; i++) {
            random.setSeed(System.currentTimeMillis() + i);
            int value = random.nextInt(30000);
            mStepDataBeanList.add(new DataBean(System.currentTimeMillis() + i * 1000 * 60 * 20, value));//隔20分钟
        }

        mBarChart.setDataBeanList(mStepDataBeanList);
    }
}
