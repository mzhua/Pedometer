package com.wonders.xlab.pedometer.ui.daily;

import com.wonders.xlab.pedometer.base.BaseContract;
import com.wonders.xlab.pedometer.data.PMStepEntity;

import java.util.List;

/**
 * Created by hua on 16/8/26.
 */

public interface PMDailyContract {
    interface View extends BaseContract.View {
        void showDailyData(int totalStepCounts, int calorie, int distanceInKm, List<PMStepEntity> entityList);
    }

    interface Presenter extends BaseContract.Presenter {
        void getDatas(long startTimeInMill, long endTimeInMill);
    }
}
