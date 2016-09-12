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
    public void getDatas(long startTimeInMill, long endTimeInMill, @PMStepCount.DataType int dataType) {
        mModel.getDataList(startTimeInMill, endTimeInMill, dataType, new BaseContract.Model.Callback<List<PMStepCountEntity>>() {
            @Override
            public void onSuccess(List<PMStepCountEntity> pmStepCountEntities) {
                List<Integer> dataList = new ArrayList<>();
            }

            @Override
            public void onFail(@NonNull DefaultException e) {

            }
        });
    }
}
