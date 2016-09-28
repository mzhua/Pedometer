package com.wonders.xlab.pedometer.test.ui.daily;


import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.wonders.xlab.pedometer.R;
import com.wonders.xlab.pedometer.data.PMStepEntity;
import com.wonders.xlab.pedometer.localdata.PMStepLocalDataSource;
import com.wonders.xlab.pedometer.ui.PMHomeActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DailyFragmentTest {

    @Rule
    public ActivityTestRule<PMHomeActivity> mActivityTestRule = new ActivityTestRule<>(PMHomeActivity.class);

    PMStepLocalDataSource mLocalDataSource;

    @Before
    public void setup() {
        mLocalDataSource = PMStepLocalDataSource.get(InstrumentationRegistry.getContext());
        PMStepEntity entity = new PMStepEntity(System.currentTimeMillis(), 123);
        mLocalDataSource.insertOrIncrease(entity);
    }

    @After
    public void cleaUp() {
        mLocalDataSource.deleteAll();
    }

    @Test
    public void dailyFragmentTest() {


        ViewInteraction textViewDaily = onView(
                allOf(ViewMatchers.withId(R.id.tvDailyTitle), 
                        childAtPosition(
                                allOf(ViewMatchers.withId(R.id.toolbar),
                                        childAtPosition(
                                                ViewMatchers.withId(R.id.xtbl),
                                                0)),
                                1),
                        isDisplayed()));
        textViewDaily.check(matches(withText("09月28日")));

        ViewInteraction view = onView(
                allOf(ViewMatchers.withId(R.id.walkChart),
                        childAtPosition(
                                childAtPosition(
                                        ViewMatchers.withId(R.id.viewPager),
                                        0),
                                0),
                        isDisplayed()));
        view.check(matches(isDisplayed()));

        ViewInteraction textView2 = onView(
                allOf(withText("0"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText("0")));

        ViewInteraction textView3 = onView(
                allOf(withText("0公里"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        2),
                                1),
                        isDisplayed()));
        textView3.check(matches(withText("0公里")));

        ViewInteraction view2 = onView(
                allOf(ViewMatchers.withId(R.id.barChart),
                        childAtPosition(
                                childAtPosition(
                                        ViewMatchers.withId(R.id.viewPager),
                                        0),
                                2),
                        isDisplayed()));
        view2.check(matches(isDisplayed()));

        ViewInteraction linearLayout = onView(
                allOf(ViewMatchers.withId(R.id.indicator),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        linearLayout.check(matches(isDisplayed()));


        onView(withId(R.id.viewPager))
                .perform(swipeLeft());
        ViewInteraction textViewWeek = onView(
                withId(R.id.tvWeekTitle));
        textViewWeek.check(matches(isDisplayed()));

        onView(withId(R.id.viewPager))
                .perform(swipeLeft());
        ViewInteraction textViewMonth = onView(
                withId(R.id.tvMonthTitle));
        textViewMonth.check(matches(isDisplayed()));

        onView(withId(R.id.viewPager))
                .perform(swipeRight());
        textViewWeek.check(matches(isDisplayed()));
        onView(withId(R.id.viewPager))
                .perform(swipeRight());
        textViewDaily.check(matches(isDisplayed()));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
