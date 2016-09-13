package com.wonders.xlab.pedometer.widget;

/**
 * Created by hua on 16/9/13.
 */

public class PMWeeklyBarChartBean {
    private long timeInMill;
    private int stepCounts;

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
