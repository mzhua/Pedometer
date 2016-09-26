package com.wonders.xlab.pedometer.test.util;

import com.wonders.xlab.pedometer.util.StringUtil;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by hua on 16/9/26.
 */

public class TestStringUtil {

    @Test
    public void testSourceLengthGreaterThenMinLength() {
        String sourceStr = "sourceStr";
        assertEquals(sourceStr, StringUtil.autoPrefixStr(sourceStr, 'a', sourceStr.length() - 1));
    }

    @Test
    public void testSourceLength1LessThenMinLength() {
        String sourceStr = "sourceStr";
        char prefixChar = 'a';
        int minLength = sourceStr.length() + 1;
        assertEquals(String.format("%s%s", prefixChar, sourceStr), StringUtil.autoPrefixStr(sourceStr, prefixChar, minLength));
    }

    @Test
    public void testSourceLength2LessThenMinLength() {
        String sourceStr = "sourceStr";
        char prefixChar = 'a';
        int minLength = sourceStr.length() + 2;
        assertEquals(String.format("%s%s%s", prefixChar, prefixChar, sourceStr), StringUtil.autoPrefixStr(sourceStr, prefixChar, minLength));
    }
}
