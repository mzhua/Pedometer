package com.wonders.xlab.pedometer.widget;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;

/**
 * Created by hua on 16/9/13.
 */

public class PMWeeklyBarChartBean {
    private int dayOfWeek;
    private int value;

    public PMWeeklyBarChartBean() {
    }

    public PMWeeklyBarChartBean(int dayOfWeek, int value) {
        this.dayOfWeek = dayOfWeek;
        this.value = value;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY})
    public @interface DayOfWeek {
    }

    @DayOfWeek
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(@DayOfWeek int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
