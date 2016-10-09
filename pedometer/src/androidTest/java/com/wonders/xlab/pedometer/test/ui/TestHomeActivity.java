package com.wonders.xlab.pedometer.test.ui;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.view.ViewPager;

import com.wonders.xlab.pedometer.R;
import com.wonders.xlab.pedometer.ui.PMHomeActivity;
import com.wonders.xlab.pedometer.util.DateUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasType;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;

/**
 * Created by hua on 16/10/9.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestHomeActivity {
    @Rule
    public IntentsTestRule<PMHomeActivity> mRule = new IntentsTestRule<>(PMHomeActivity.class);

    @Test
    public void testInitialStatus() {
        onView(withId(R.id.menu_share)).check(matches(allOf(isDisplayed(), withContentDescription(R.string.menu_share))));
        onView(withId(R.id.indicator)).check(matches(isDisplayed()));
        onView(withId(R.id.viewPager)).check(matches(isDisplayed()));
        ViewPager viewPager = (ViewPager) mRule.getActivity().findViewById(R.id.viewPager);
        assertEquals("the child counts of ViewPager should be 3", 3, viewPager.getChildCount());
    }

    @Test
    public void testClickShareMenu_ShowShareChooseDialog() {
        onView(withId(R.id.menu_share)).perform(click());
        intended(allOf(hasExtra(Intent.EXTRA_TITLE, mRule.getActivity().getResources().getString(R.string.pm_share_to)),
                hasAction(Intent.ACTION_CHOOSER),
                hasExtra(is(Intent.EXTRA_INTENT),
                        allOf(hasAction(Intent.ACTION_SEND),
                                hasType("image/*")))));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            InstrumentationRegistry.getInstrumentation().getUiAutomation()
                    .performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        }
    }

    @Test
    public void testDailyPageInitialStatus() {
        onView(withId(R.id.walkChart)).check(matches(isDisplayed()));
        onView(withId(R.id.barChartDaily)).check(matches(isDisplayed()));
    }

    @Test
    public void testDailyPageClickDailyTitle_ShowCalendarWindow() {
        onView(withId(R.id.tvDailyTitle))
                .check(matches(allOf(isDisplayed(), withText(DateUtil.getDayFormatString(System.currentTimeMillis())))))
                .perform(click());
        onView(withId(R.id.tvWeekTitle)).check(doesNotExist());
        onView(withId(R.id.tvMonthTitle)).check(doesNotExist());
        onView(withId(R.id.calendarView))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testDailyPagePressBack_DismissCalendarWindow() {
        onView(withId(R.id.tvDailyTitle)).perform(click());
        pressBack();
        onView(withId(R.id.calendarView))
                .check(doesNotExist());
    }

    @Test
    public void testWeeklyPageInitialStatus() {
        onView(withId(R.id.viewPager)).perform(swipeLeft());
        long timeInMill = System.currentTimeMillis();
        long beginOfWeek = DateUtil.getBeginTimeOfWeekInMill(timeInMill);
        long endOfWeek = DateUtil.getEndTimeOfWeekInMill(timeInMill);
        String titleText = DateUtil.getDayFormatString(beginOfWeek) + "-" + DateUtil.getDayFormatString(endOfWeek);
        onView(withId(R.id.tvWeekTitle)).check(matches(withText(titleText)));
        onView(withId(R.id.barChartWeekly)).check(matches(isDisplayed()));
    }

    @Test
    public void testMonthlyPageInitialStatus() {
        onView(withId(R.id.viewPager)).perform(swipeLeft(), swipeLeft());
        long timeInMill = System.currentTimeMillis();
        String titleText = DateUtil.getMonthFormatString(timeInMill);
        onView(withId(R.id.tvMonthTitle)).check(matches(withText(titleText)));
        onView(withId(R.id.lineAreaChart)).check(matches(isDisplayed()));
    }
}
