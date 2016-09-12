package com.wonders.xlab.pedometer.ui.month;

import android.support.annotation.NonNull;

import com.wonders.xlab.pedometer.base.BaseContract;
import com.wonders.xlab.pedometer.base.BasePresenter;
import com.wonders.xlab.pedometer.base.DefaultException;
import com.wonders.xlab.pedometer.data.PMStepCountContract;
import com.wonders.xlab.pedometer.data.PMStepCountEntity;
import com.wonders.xlab.pedometer.db.PMStepCount;

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

            }

            @Override
            public void onFail(@NonNull DefaultException e) {

            }
        });
    }
}
