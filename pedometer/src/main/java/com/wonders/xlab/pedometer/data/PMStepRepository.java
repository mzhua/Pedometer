package com.wonders.xlab.pedometer.data;

import com.wonders.xlab.pedometer.base.BaseModel;
import com.wonders.xlab.pedometer.localdata.PMStepLocalDataSource;

import java.util.List;

/**
 * Created by hua on 16/9/12.
 */

public class PMStepRepository extends BaseModel implements PMStepContract.Model {
    private PMStepLocalDataSource mStepCount;

    public PMStepRepository(PMStepLocalDataSource localDataSource) {
        mStepCount = localDataSource;
    }

    @Override
    public void getDataList(long startTimeInMill, long endTimeInMill, @PMStepLocalDataSource.DataType int dataType, Callback<List<PMStepEntity>> callback) {
        callback.onSuccess(mStepCount.queryAllBetweenTimes(startTimeInMill, endTimeInMill, dataType));
    }
}
