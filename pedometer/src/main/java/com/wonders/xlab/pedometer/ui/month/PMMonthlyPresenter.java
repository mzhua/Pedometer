package com.wonders.xlab.pedometer.ui.month;

import android.support.annotation.NonNull;

import com.wonders.xlab.pedometer.base.BaseContract;
import com.wonders.xlab.pedometer.base.BasePresenter;
import com.wonders.xlab.pedometer.base.DefaultException;
import com.wonders.xlab.pedometer.data.PMStepCountContract;
import com.wonders.xlab.pedometer.data.PMStepCountEntity;
import com.wonders.xlab.pedometer.db.PMStepCount;
import com.wonders.xlab.pedometer.widget.PMMonthLineAreaBean;
import com.wonders.xlab.pedometer.widget.PMMonthLineAreaChart;
import com.wonders.xlab.pedometer.widget.PMWeeklyBarChartBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PMMonthlyPresenter extends BasePresenter implements PMStepCountContract.Presenter {
    private PMMonthlyContract.View mView;
    private PMStepCountContract.Model mModel;

    public PMMonthlyPresenter(PMMonthlyContract.View view, PMStepCountContract.Model model) {
        mView = view;
        mModel = model;
    }

    @Override
    public void getDatas(long startTimeInMill, long endTimeInMill, @PMStepCount.DataType int dataType) {
        mModel.getDataList(startTimeInMill, endTimeInMill, dataType, new BaseContract.Model.Callback<List<PMStepCountEntity>>() {
            @Override
            public void onSuccess(List<PMStepCountEntity> pmStepCountEntities) {
                List<PMMonthLineAreaBean> dataList = null;
                int avgStep = 0;
                int sumStep = 0;
                if (pmStepCountEntities != null && pmStepCountEntities.size() > 0) {
                    dataList = new ArrayList<>();
                    Calendar calendar = Calendar.getInstance();

                    //just in case, take the first seven records
                    for (int i = 0; i < Math.min(pmStepCountEntities.size(), 6); i++) {
                        PMStepCountEntity entity = pmStepCountEntities.get(i);
                        int counts = entity.getStepCounts();
                        sumStep += counts;
                        calendar.setTimeInMillis(entity.getUpdateTimeInMill());
                        PMMonthLineAreaBean bean = new PMMonthLineAreaBean(calendar.get(Calendar.DAY_OF_MONTH),counts);
                        dataList.add(bean);
                    }
                    avgStep = sumStep / pmStepCountEntities.size();
                }
                mView.showMonthlyData(avgStep, sumStep, dataList);
            }

            @Override
            public void onFail(@NonNull DefaultException e) {

            }
        });
    }
}
