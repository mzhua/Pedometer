package com.wonders.xlab.pedometer.ui.month;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wonders.xlab.pedometer.R;
import com.wonders.xlab.pedometer.base.MVPFragment;
import com.wonders.xlab.pedometer.data.PMStepCountModel;
import com.wonders.xlab.pedometer.db.PMStepCount;
import com.wonders.xlab.pedometer.util.DateUtil;
import com.wonders.xlab.pedometer.widget.PMMonthLineAreaBean;
import com.wonders.xlab.pedometer.widget.PMMonthLineAreaChart;

import java.util.Calendar;
import java.util.List;

public class PMMonthlyFragment extends MVPFragment<PMMonthlyPresenter> implements PMMonthlyContract.View {
    private PMMonthLineAreaChart mAreaChart;
    private TextView mTvAvgSteps;
    private TextView mTvSumSteps;
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
            mPresenter = new PMMonthlyPresenter(this, new PMStepCountModel(PMStepCount.getInstance(getActivity())));
        }
        return mPresenter;
    }

    @Override
    public void refreshView(long startTimeInMill, long endTimeInMill) {
        if (hasViewCreated()) {
            getPresenter().getDatas(startTimeInMill, endTimeInMill, PMStepCount.DataType.MONTH);
        }
    }

    @Override
    protected boolean hasViewCreated() {
        return null != mAreaChart;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pm_month_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAreaChart = (PMMonthLineAreaChart) view.findViewById(R.id.lineAreaChart);
        mTvAvgSteps = (TextView) view.findViewById(R.id.tvAvgSteps);
        mTvSumSteps = (TextView) view.findViewById(R.id.tvSumSteps);
        long timeInMill = System.currentTimeMillis();
        getPresenter().getDatas(DateUtil.getBeginTimeOfMonthInMill(timeInMill), DateUtil.getEndTimeOfMonthInMill(timeInMill), PMStepCount.DataType.MONTH);
    }

    @Override
    public void showMonthlyData(int avgStepCounts, int sumStepCounts, List<PMMonthLineAreaBean> dataList) {
        mTvAvgSteps.setText(String.valueOf(avgStepCounts));
        mTvSumSteps.setText(String.valueOf(sumStepCounts));
        mAreaChart.setDataBean(dataList, Calendar.getInstance().getTimeInMillis());
    }
}
