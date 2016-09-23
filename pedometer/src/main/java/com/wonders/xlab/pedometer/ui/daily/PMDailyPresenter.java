package com.wonders.xlab.pedometer.ui.daily;

import android.support.annotation.NonNull;

import com.wonders.xlab.pedometer.base.BaseContract;
import com.wonders.xlab.pedometer.base.BasePresenter;
import com.wonders.xlab.pedometer.base.DefaultException;
import com.wonders.xlab.pedometer.data.PMStepContract;
import com.wonders.xlab.pedometer.data.PMStepEntity;
import com.wonders.xlab.pedometer.localdata.PMStepLocalDataSource;

import java.util.List;

public class PMDailyPresenter extends BasePresenter implements PMDailyContract.Presenter {
    private PMDailyContract.View mView;
    private PMStepContract.Model mModel;

    public PMDailyPresenter(PMDailyContract.View view, PMStepContract.Model model) {
        mView = view;
        mModel = model;
    }

    @Override
    public void getDatas(long startTimeInMill, final long endTimeInMill) {
        mModel.getDataList(startTimeInMill, endTimeInMill, PMStepLocalDataSource.DataType.DAY, new BaseContract.Model.Callback<List<PMStepEntity>>() {
            @Override
            public void onSuccess(List<PMStepEntity> pmStepCountEntities) {
                int totalStepCounts = 0;
                int calorie = 0;
                int distanceInKm = 0;

                if (pmStepCountEntities != null) {
                    for (PMStepEntity entity : pmStepCountEntities) {
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
