package com.wonders.xlab.pedometer.ui.weekly;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wonders.xlab.pedometer.R;
import com.wonders.xlab.pedometer.base.MVPFragment;
import com.wonders.xlab.pedometer.data.PMStepRepository;
import com.wonders.xlab.pedometer.localdata.PMStepLocalDataSource;
import com.wonders.xlab.pedometer.util.DateUtil;
import com.wonders.xlab.pedometer.widget.PMWeeklyBarChart;
import com.wonders.xlab.pedometer.widget.PMWeeklyBarChartBean;

import java.util.List;

public class PMWeeklyFragment extends MVPFragment<PMWeeklyPresenter> implements PMWeeklyContract.View {
    private PMWeeklyBarChart mBarChart;
    private TextView mTvAvgSteps;
    private TextView mTvSumSteps;
    private PMWeeklyPresenter mPresenter;

    @Override
    public PMWeeklyPresenter getPresenter() {
        if (null == mPresenter) {
            mPresenter = new PMWeeklyPresenter(this, new PMStepRepository(PMStepLocalDataSource.get(getActivity())));
        }
        return mPresenter;
    }

    @Override
    public void refreshView(long startTimeInMill, long endTimeInMill) {
        if (hasViewCreated()) {
            getPresenter().getDatas(startTimeInMill, endTimeInMill);
        }
    }

    @Override
    protected boolean hasViewCreated() {
        return null != mBarChart;
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
        mBarChart = (PMWeeklyBarChart) view.findViewById(R.id.barChart);
        mTvAvgSteps = (TextView) view.findViewById(R.id.tvAvgSteps);
        mTvSumSteps = (TextView) view.findViewById(R.id.tvSumSteps);
        long timeMillis = System.currentTimeMillis();
        getPresenter().getDatas(DateUtil.getBeginTimeOfWeekInMill(timeMillis),DateUtil.getEndTimeOfWeekInMill(timeMillis));
    }

    @Override
    public void showWeeklyData(int avgStepCounts, int sumStepCounts, List<PMWeeklyBarChartBean> dataList) {
        mTvAvgSteps.setText(String.valueOf(avgStepCounts));
        mTvSumSteps.setText(String.valueOf(sumStepCounts));
        mBarChart.setDataBeanList(dataList);
    }
}
