package com.wonders.xlab.pedometer.util;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

/**
 * Created by hua on 16/9/7.
 */

public class StringUtil {
    @NonNull
    public static String autoPrefixStr(@NonNull String sourceStr, char prefix, @IntRange(from = 1) int minLength) {

        StringBuilder builder = new StringBuilder(sourceStr);
        if (sourceStr.length() < minLength) {
            for (int i = 0; i < minLength - sourceStr.length(); i++) {
                builder.insert(0, prefix);
            }
        }
        return builder.toString();
    }
}
