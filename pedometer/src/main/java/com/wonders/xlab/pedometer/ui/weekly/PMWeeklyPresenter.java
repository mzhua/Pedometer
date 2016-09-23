package com.wonders.xlab.pedometer.ui.weekly;

import android.support.annotation.NonNull;

import com.wonders.xlab.pedometer.base.BaseContract;
import com.wonders.xlab.pedometer.base.BasePresenter;
import com.wonders.xlab.pedometer.base.DefaultException;
import com.wonders.xlab.pedometer.data.PMStepContract;
import com.wonders.xlab.pedometer.data.PMStepEntity;
import com.wonders.xlab.pedometer.localdata.PMStepLocalDataSource;
import com.wonders.xlab.pedometer.widget.PMWeeklyBarChartBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PMWeeklyPresenter extends BasePresenter implements PMStepContract.Presenter {
    private PMWeeklyContract.View mView;
    private PMStepContract.Model mModel;

    public PMWeeklyPresenter(PMWeeklyContract.View view, PMStepContract.Model model) {
        mView = view;
        mModel = model;
    }

    @Override
    public void getDatas(long startTimeInMill, final long endTimeInMill, @PMStepLocalDataSource.DataType int dataType) {
        mModel.getDataList(startTimeInMill, endTimeInMill, dataType, new BaseContract.Model.Callback<List<PMStepEntity>>() {
            @Override
            public void onSuccess(List<PMStepEntity> pmStepCountEntities) {
                List<PMWeeklyBarChartBean> dataList = null;
                int avgStep = 0;
                int sumStep = 0;
                if (pmStepCountEntities != null && pmStepCountEntities.size() > 0) {
                    dataList = new ArrayList<>();
                    Calendar calendar = Calendar.getInstance();

                    //just in case, take the first seven records
                    for (int i = 0; i < Math.min(pmStepCountEntities.size(), 6); i++) {
                        PMStepEntity entity = pmStepCountEntities.get(i);
                        int counts = entity.getStepCounts();
                        sumStep += counts;
                        calendar.setTimeInMillis(entity.getUpdateTimeInMill());
                        PMWeeklyBarChartBean bean = new PMWeeklyBarChartBean();
                        bean.setValue(counts);
                        bean.setDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
                        dataList.add(bean);
                    }
                    avgStep = sumStep / pmStepCountEntities.size();
                }
                mView.showWeeklyData(avgStep, sumStep, dataList);
            }

            @Override
            public void onFail(@NonNull DefaultException e) {

            }
        });
    }
}
