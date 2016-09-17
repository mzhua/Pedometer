package com.wonders.xlab.pedometer.ui.daily;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wonders.xlab.pedometer.R;
import com.wonders.xlab.pedometer.base.MVPFragment;
import com.wonders.xlab.pedometer.data.PMStepCountEntity;
import com.wonders.xlab.pedometer.data.PMStepCountModel;
import com.wonders.xlab.pedometer.db.PMStepCount;
import com.wonders.xlab.pedometer.widget.PMDailyBarChart;
import com.wonders.xlab.pedometer.widget.PMDailyRingChart;

import java.util.Calendar;
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
            mPresenter = new PMDailyPresenter(this, new PMStepCountModel(PMStepCount.getInstance(getActivity())));
        }
        return mPresenter;
    }

    @Override
    public void refreshView(long startTimeInMill, long endTimeInMill) {
        if (hasViewCreated()) {
            getPresenter().getDatas(getStartTimeInMillOfDay(System.currentTimeMillis()),getEndTimeInMillOfDay(System.currentTimeMillis()), PMStepCount.DataType.DAY);
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
        mBarChart = (PMDailyBarChart) view.findViewById(R.id.barChart);
        mRingChart = (PMDailyRingChart) view.findViewById(R.id.walkChart);

        getPresenter().getDatas(getStartTimeInMillOfDay(System.currentTimeMillis()),getEndTimeInMillOfDay(System.currentTimeMillis()), PMStepCount.DataType.DAY);
    }

    private long getStartTimeInMillOfDay(long anyTimeOfDayInMill){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(anyTimeOfDayInMill);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTimeInMillis();
    }

    private long getEndTimeInMillOfDay(long anyTimeOfDayInMill){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(anyTimeOfDayInMill);
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        calendar.set(Calendar.MILLISECOND,999);
        return calendar.getTimeInMillis();
    }

    @Override
    public void showDailyData(int totalStepCounts, int calorie, int distanceInKm, List<PMStepCountEntity> entityList) {
        mRingChart.startWithStepCounts(totalStepCounts);
        mBarChart.setDataBeanList(entityList);
    }
}
