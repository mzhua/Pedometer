package com.wonders.xlab.pedometer.data;

import com.wonders.xlab.pedometer.base.BaseModel;
import com.wonders.xlab.pedometer.db.PMStepCount;

import java.util.List;

/**
 * Created by hua on 16/9/12.
 */

public class PMStepRepository extends BaseModel implements PMStepCountContract.Model {
    private PMStepCount mStepCount;

    public PMStepRepository(PMStepCount stepCount) {
        mStepCount = stepCount;
    }

    @Override
    public void getDataList(long startTimeInMill, long endTimeInMill, @PMStepCount.DataType int dataType, Callback<List<PMStepCountEntity>> callback) {
        List<PMStepCountEntity> entityList = mStepCount.queryAllBetweenTimes(startTimeInMill, endTimeInMill, dataType);
        callback.onSuccess(entityList);
    }
}
