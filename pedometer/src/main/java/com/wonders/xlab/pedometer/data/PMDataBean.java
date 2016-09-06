package com.wonders.xlab.pedometer.data;

/**
 * Created by hua on 16/9/6.
 */

public class PMDataBean {
    private long timeInMill;
    private int stepCounts;

    public PMDataBean(long timeInMill, int stepCounts) {
        this.timeInMill = timeInMill;
        this.stepCounts = stepCounts;
    }

    public long getTimeInMill() {
        return timeInMill;
    }

    public void setTimeInMill(long timeInMill) {
        this.timeInMill = timeInMill;
    }

    public int getStepCounts() {
        return stepCounts;
    }

    public void setStepCounts(int stepCounts) {
        this.stepCounts = stepCounts;
    }
}
