package com.wonders.xlab.pedometer.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hua on 16/9/14.
 */

public class DateUtil {
    private static SimpleDateFormat mDayFormat = new SimpleDateFormat("MM月dd日", Locale.CHINA);
    private static SimpleDateFormat mMonthFormat = new SimpleDateFormat("yyyy年MM月", Locale.CHINA);
    private static Date date = new Date();

    public static String getDayFormatString(long timeInMill) {
        date.setTime(timeInMill);
        return mDayFormat.format(date);
    }

    public static String getMonthFormatString(long timeInMill) {
        date.setTime(timeInMill);
        return mMonthFormat.format(date);
    }

    public static long getBeginTimeOfDayInMill(Calendar calendar) {
        setCalendarToBeginOfDay(calendar);
        return calendar.getTimeInMillis();
    }

    private static void setCalendarToBeginOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static long getEndTimeOfDayInMill(Calendar calendar) {
        setCalendarToEndOfDay(calendar);
        return calendar.getTimeInMillis();
    }

    private static void setCalendarToEndOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }

    private static Calendar mCalendar = Calendar.getInstance();

    public static long getBeginTimeOfWeekInMill(long timeInMill) {
        mCalendar.setTimeInMillis(timeInMill);
        mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        mCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        setCalendarToBeginOfDay(mCalendar);
        return mCalendar.getTimeInMillis();
    }

    public static long getEndTimeOfWeekInMill(long timeInMill) {
        mCalendar.setTimeInMillis(timeInMill);
        mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        mCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        setCalendarToEndOfDay(mCalendar);
        return mCalendar.getTimeInMillis();
    }

    public static long getBeginTimeOfMonthInMill(long timeInMill) {
        mCalendar.setTimeInMillis(timeInMill);
        mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.getMinimum(Calendar.DAY_OF_MONTH));
        return mCalendar.getTimeInMillis();
    }

    public static long getEndTimeOfMonthInMill(long timeInMill) {
        mCalendar.setTimeInMillis(timeInMill);
        mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.getMaximum(Calendar.DAY_OF_MONTH));
        return mCalendar.getTimeInMillis();
    }
}
