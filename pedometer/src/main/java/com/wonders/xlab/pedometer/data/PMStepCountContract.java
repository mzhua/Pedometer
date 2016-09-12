package com.wonders.xlab.pedometer.data;

import com.wonders.xlab.pedometer.base.BaseContract;
import com.wonders.xlab.pedometer.db.PMStepCount;

import java.util.List;

/**
 * Created by hua on 16/9/12.
 */

public interface PMStepCountContract {

    interface Presenter extends BaseContract.Presenter {
        void getDatas(long startTimeInMill, long endTimeInMill, @PMStepCount.DataType int dataType);
    }

    interface Model extends BaseContract.Model {
        void getDataList(long startTimeInMill, long endTimeInMill, @PMStepCount.DataType int dataType, Callback<List<PMStepCountEntity>> callback);
    }
}
