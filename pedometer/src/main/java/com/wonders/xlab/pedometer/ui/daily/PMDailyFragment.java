package com.wonders.xlab.pedometer.ui.daily;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wonders.xlab.pedometer.R;
import com.wonders.xlab.pedometer.base.MVPFragment;
import com.wonders.xlab.pedometer.data.PMStepEntity;
import com.wonders.xlab.pedometer.data.PMStepRepository;
import com.wonders.xlab.pedometer.localdata.PMStepLocalDataSource;
import com.wonders.xlab.pedometer.util.DateUtil;
import com.wonders.xlab.pedometer.widget.PMDailyBarChart;
import com.wonders.xlab.pedometer.widget.PMDailyRingChart;

import java.util.List;

public class PMDailyFragment extends MVPFragment<PMDailyPresenter> implements PMDailyContract.View {
    private PMDailyRingChart mRingChart;
    private PMDailyBarChart mBarChart;

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
            mPresenter = new PMDailyPresenter(this, new PMStepRepository(PMStepLocalDataSource.get(getActivity())));
        }
        return mPresenter;
    }

    @Override
    public void refreshView(long startTimeInMill, long endTimeInMill) {
        if (hasViewCreated()) {
            getPresenter().getDatas(startTimeInMill,endTimeInMill);
        }
    }

    @Override
    protected boolean hasViewCreated() {
        return mRingChart != null && mBarChart != null;
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
        mBarChart = (PMDailyBarChart) view.findViewById(R.id.barChartDaily);
        mRingChart = (PMDailyRingChart) view.findViewById(R.id.walkChart);
        long timeMillis = System.currentTimeMillis();
        getPresenter().getDatas(DateUtil.getBeginTimeOfDayInMill(timeMillis),DateUtil.getEndTimeOfDayInMill(timeMillis));
    }

    @Override
    public void showDailyData(int totalStepCounts, int calorie, int distanceInKm, List<PMStepEntity> entityList) {
        mRingChart.startWithStepCounts(totalStepCounts);
        mBarChart.setDataBeanList(entityList);
    }
}
