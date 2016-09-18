package com.wonders.xlab.pedometer;

import org.junit.Test;

import java.util.Calendar;

import static android.R.attr.firstDayOfWeek;
import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
        System.out.print("day:" + calendar.get(Calendar.DAY_OF_MONTH));

    }
}