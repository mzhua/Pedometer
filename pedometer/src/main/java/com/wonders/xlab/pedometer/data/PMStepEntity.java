package com.wonders.xlab.pedometer.data;

/**
 * Created by hua on 16/9/6.
 */

public class PMStepEntity {
    private long updateTimeInMill;
    private long createTimeInMill;
    private int stepCounts = 1;

    public PMStepEntity(long updateTimeInMill) {
        this.createTimeInMill = updateTimeInMill;
        this.updateTimeInMill = updateTimeInMill;
    }

    public PMStepEntity(long updateTimeInMill,int stepCounts) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PMStepEntity entity = (PMStepEntity) o;

        if (updateTimeInMill != entity.updateTimeInMill) return false;
        if (createTimeInMill != entity.createTimeInMill) return false;
        return stepCounts == entity.stepCounts;

    }

    @Override
    public int hashCode() {
        int result = (int) (updateTimeInMill ^ (updateTimeInMill >>> 32));
        result = 31 * result + (int) (createTimeInMill ^ (createTimeInMill >>> 32));
        result = 31 * result + stepCounts;
        return result;
    }
}
