package com.wonders.xlab.pedometer.data;

/**
 * Created by hua on 16/9/6.
 */

public class PMStepCountEntity {
    private long updateTimeInMill;
    private long createTimeInMill;
    private int stepCounts;

    public PMStepCountEntity(long updateTimeInMill, int stepCounts) {
        this.createTimeInMill = updateTimeInMill;
        this.updateTimeInMill = updateTimeInMill;
        this.stepCounts = stepCounts;
    }

    public long getUpdateTimeInMill() {
        return updateTimeInMill;
    }

    public void setUpdateTimeInMill(long updateTimeInMill) {
        this.updateTimeInMill = updateTimeInMill;
    }

    public int getStepCounts() {
        return stepCounts;
    }

    public void setStepCounts(int stepCounts) {
        this.stepCounts = stepCounts;
    }

    public long getCreateTimeInMill() {
        return createTimeInMill;
    }

    public void setCreateTimeInMill(long createTimeInMill) {
        this.createTimeInMill = createTimeInMill;
    }
}
