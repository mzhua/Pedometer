package com.wonders.xlab.pedometer.util;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

/**
 * Created by hua on 16/9/7.
 */

public class StringUtil {
    @NonNull
    public static String autoPrefixStr(@NonNull String timeStr,@NonNull String prefix, @IntRange(from = 1) int minLength) {

        if (timeStr.length() < minLength) {
            for (int i = 0; i < minLength - timeStr.length(); i++) {
                timeStr = prefix + timeStr;
            }
        }
        return timeStr;
    }
}
