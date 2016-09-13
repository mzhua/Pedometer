package com.wonders.xlab.pedometer.ui.weekly;

import android.support.annotation.NonNull;

import com.wonders.xlab.pedometer.base.BaseContract;
import com.wonders.xlab.pedometer.base.BasePresenter;
import com.wonders.xlab.pedometer.base.DefaultException;
import com.wonders.xlab.pedometer.data.PMStepCountContract;
import com.wonders.xlab.pedometer.data.PMStepCountEntity;
import com.wonders.xlab.pedometer.db.PMStepCount;

import java.util.ArrayList;
import java.util.List;

public class PMWeeklyPresenter extends BasePresenter implements PMStepCountContract.Presenter {
    private PMWeeklyContract.View mView;
    private PMStepCountContract.Model mModel;

    public PMWeeklyPresenter(PMWeeklyContract.View view, PMStepCountContract.Model model) {
        mView = view;
        mModel = model;
    }

    @Override
    public void getDatas(long startTimeInMill, final long endTimeInMill, @PMStepCount.DataType int dataType) {
        mModel.getDataList(startTimeInMill, endTimeInMill, dataType, new BaseContract.Model.Callback<List<PMStepCountEntity>>() {
            @Override
            public void onSuccess(List<PMStepCountEntity> pmStepCountEntities) {
                List<Integer> dataList = null;
                int avgStep = 0;
                int sumStep = 0;
                if (pmStepCountEntities != null && pmStepCountEntities.size() > 0) {
                    dataList = new ArrayList<>();
                    //just in case, take the first seven records
                    for (int i = 0; i < Math.min(pmStepCountEntities.size(), 6); i++) {
                        PMStepCountEntity entity = pmStepCountEntities.get(i);
                        int counts = entity.getStepCounts();
                        sumStep += counts;
                        dataList.add(counts);
                    }
                    avgStep = sumStep / pmStepCountEntities.size();
                }
                mView.showDailyData(avgStep, sumStep, dataList);
            }

            @Override
            public void onFail(@NonNull DefaultException e) {

            }
        });
    }
}
