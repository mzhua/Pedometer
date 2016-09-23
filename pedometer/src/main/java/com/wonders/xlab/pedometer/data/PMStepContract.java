package com.wonders.xlab.pedometer.data;

import com.wonders.xlab.pedometer.base.BaseContract;
import com.wonders.xlab.pedometer.localdata.PMStepLocalDataSource;

import java.util.List;

/**
 * Created by hua on 16/9/12.
 */

public interface PMStepContract {

    interface Presenter extends BaseContract.Presenter {
        void getDatas(long startTimeInMill, long endTimeInMill, @PMStepLocalDataSource.DataType int dataType);
    }

    interface Model extends BaseContract.Model {
        void getDataList(long startTimeInMill, long endTimeInMill, @PMStepLocalDataSource.DataType int dataType, Callback<List<PMStepEntity>> callback);
    }
}
