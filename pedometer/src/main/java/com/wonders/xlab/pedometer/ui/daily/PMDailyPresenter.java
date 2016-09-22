package com.wonders.xlab.pedometer.ui.daily;

import android.support.annotation.NonNull;

import com.wonders.xlab.pedometer.base.BaseContract;
import com.wonders.xlab.pedometer.base.BasePresenter;
import com.wonders.xlab.pedometer.base.DefaultException;
import com.wonders.xlab.pedometer.data.PMStepCountContract;
import com.wonders.xlab.pedometer.data.PMStepCountEntity;
import com.wonders.xlab.pedometer.db.PMStepCount;

import java.util.Calendar;
import java.util.List;

public class PMDailyPresenter extends BasePresenter implements PMDailyContract.Presenter {
    private PMDailyContract.View mView;
    private PMStepCountContract.Model mModel;

    public PMDailyPresenter(PMDailyContract.View view, PMStepCountContract.Model model) {
        mView = view;
        mModel = model;
    }

    @Override
    public void getDatas(long startTimeInMill, final long endTimeInMill) {
        mModel.getDataList(startTimeInMill, endTimeInMill, PMStepCount.DataType.DAY, new BaseContract.Model.Callback<List<PMStepCountEntity>>() {
            @Override
            public void onSuccess(List<PMStepCountEntity> pmStepCountEntities) {
                int totalStepCounts = 0;
                int calorie = 0;
                int distanceInKm = 0;

                if (pmStepCountEntities != null) {
                    for (PMStepCountEntity entity : pmStepCountEntities) {
                        totalStepCounts += entity.getStepCounts();
                    }
                }

                mView.showDailyData(totalStepCounts, calorie, distanceInKm, pmStepCountEntities);
            }

            @Override
            public void onFail(@NonNull DefaultException e) {
                mView.showToastMessage(e.getMessage());
            }
        });
    }
}
