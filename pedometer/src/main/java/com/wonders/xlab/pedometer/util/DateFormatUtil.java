package com.wonders.xlab.pedometer.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hua on 16/9/14.
 */

public class DateFormatUtil {
    private static SimpleDateFormat mFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.CHINA);

    public static void printLogDateToString(long timeInMill) {
        Log.d("DateFormatUtil", mFormat.format(new Date(timeInMill)));
    }

    public static void printLogDateToString(Date date) {
        Log.d("DateFormatUtil", mFormat.format(date));
    }
}
