package com.wonders.xlab.pedometer.widget;

/**
 * Created by hua on 16/9/14.
 */

public class PMMonthLineAreaBean {
    private int dayOfMonth;
    private int value;

    public PMMonthLineAreaBean(int dayOfMonth, int value) {
        this.dayOfMonth = dayOfMonth;
        this.value = value;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public int getValue() {
        return value;
    }
}
