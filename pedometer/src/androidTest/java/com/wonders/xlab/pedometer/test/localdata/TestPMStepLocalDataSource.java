package com.wonders.xlab.pedometer.test.localdata;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.wonders.xlab.pedometer.data.PMStepEntity;
import com.wonders.xlab.pedometer.localdata.PMStepLocalDataSource;
import com.wonders.xlab.pedometer.util.DateUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Created by hua on 16/9/23.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestPMStepLocalDataSource {

    private PMStepLocalDataSource mLocalDataSource;

    private Calendar mCalendar;

    @Before
    public void setup() {
        mLocalDataSource = PMStepLocalDataSource.get(InstrumentationRegistry.getContext());
        mCalendar = Calendar.getInstance();
        mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
    }

    @After
    public void clearUp() {
        mLocalDataSource.deleteAll();
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mLocalDataSource);
    }

    @Test
    public void testFirstInsertData() {
        PMStepEntity entity = new PMStepEntity(System.currentTimeMillis());
        mLocalDataSource.insertOrIncrease(entity);
        List<PMStepEntity> pmStepEntities = mLocalDataSource.queryAll();
        assertThat("insert data failed, the query result is null", pmStepEntities, notNullValue());
        assertThat("insert data failed", pmStepEntities.size(), is(1));
        assertThat("insert data failed", pmStepEntities.get(0), equalTo(entity));
        assertThat("insert data failed", pmStepEntities.get(0).getStepCounts(), equalTo(1));
    }

    @Test
    public void testReplaceData() {
        PMStepEntity earlyStepData = new PMStepEntity(System.currentTimeMillis() - PMStepLocalDataSource.INTERVAL_IN_MILL);
        PMStepEntity lateStepData = new PMStepEntity(System.currentTimeMillis());
        mLocalDataSource.insertOrIncrease(earlyStepData);
        mLocalDataSource.insertOrIncrease(lateStepData);
        List<PMStepEntity> pmStepEntities = mLocalDataSource.queryAll();
        assertThat("replace data failed, the query result is null", pmStepEntities, notNullValue());
        String reason = "replace data failed, because the interval of two data time is less or equal to " + PMStepLocalDataSource.INTERVAL_IN_MILL + ", the two records should be merged";
        assertThat(reason, pmStepEntities.size(), is(1));

        int SUM_OF_TWO_INDEPENDENT_STEP = 2;
        assertThat(reason, pmStepEntities.get(0).getStepCounts(), is(SUM_OF_TWO_INDEPENDENT_STEP));
    }

    @Test
    public void testInsertNewData() {
        long currentDataTimeInMill = System.currentTimeMillis();
        long preOutOfRangeDataTimeInMill = currentDataTimeInMill - PMStepLocalDataSource.INTERVAL_IN_MILL - 1;

        PMStepEntity earlyStepData = new PMStepEntity(preOutOfRangeDataTimeInMill);
        PMStepEntity lateStepData = new PMStepEntity(currentDataTimeInMill);
        mLocalDataSource.insertOrIncrease(earlyStepData);
        mLocalDataSource.insertOrIncrease(lateStepData);
        List<PMStepEntity> pmStepEntities = mLocalDataSource.queryAll();
        assertThat("insert new data failed, the query result is null", pmStepEntities, notNullValue());
        assertThat("insert new data failed, the two records should not be merged", pmStepEntities.size(), is(2));
        assertThat("insert new data failed, the two records should not be merged", pmStepEntities.get(0).getStepCounts(), is(1));
        assertThat("insert new data failed, the two records should not be merged", pmStepEntities.get(1).getStepCounts(), is(1));
    }

    @Test
    public void testInsertOrReplaceWithBatchDataWithIndependentDatas() {
        List<PMStepEntity> sourceList = getTwoStepsOutOf20Minutes();

        mLocalDataSource.insertOrReplaceWithBatchData(sourceList);
        List<PMStepEntity> results = mLocalDataSource.queryAll();
        assertNotNull("the query result is null", results);
        int actualSize = results.size();
        assertEquals("insert batch datas failed, the result size expect 2 but actual is " + actualSize, 2, actualSize);
    }

    @Test
    public void testInsertOrReplaceWithBatchDataWithMergeAbleDatas() {
        List<PMStepEntity> sourceList = getTwoStepsWithin20Minutes();

        mLocalDataSource.insertOrReplaceWithBatchData(sourceList);
        List<PMStepEntity> results = mLocalDataSource.queryAll();
        assertNotNull("the query result is null", results);
        int actualSize = results.size();
        assertEquals("insert batch datas failed, the result size expect 1 but actual is " + actualSize, 1, actualSize);
        assertEquals("insert batch datas wrong, the step counts should be 2 but actual is " + results.get(0).getStepCounts(), 2, results.get(0).getStepCounts());
    }

    @Test
    public void testInsertOrReplaceWithBatchDataWithNullResource() {
        mLocalDataSource.insertOrReplaceWithBatchData(null);
        List<PMStepEntity> pmStepEntities = mLocalDataSource.queryAll();
        assertNull("the query result should be null", pmStepEntities);
    }

    @Test
    public void testQueryAllBetweenTimesOfDay() {
        List<PMStepEntity> sourceList = getTwoStepsOutOf20Minutes();

        mLocalDataSource.insertOrReplaceWithBatchData(sourceList);

        long timeInMill = System.currentTimeMillis();
        List<PMStepEntity> results = mLocalDataSource.queryAllBetweenTimes(DateUtil.getBeginTimeOfDayInMill(timeInMill), DateUtil.getEndTimeOfDayInMill(timeInMill), PMStepLocalDataSource.DataType.DAY);
        assertNotNull(results);

        assertEquals(results.size(), sourceList.size());
    }

    @Test
    public void testQueryAllBetweenTimesOfWeek() {
        List<PMStepEntity> sourceList = new ArrayList<>();

        mCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        PMStepEntity mondayEntity = new PMStepEntity(mCalendar.getTimeInMillis());
        sourceList.add(mondayEntity);

        mCalendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
        PMStepEntity tuesdayEntity = new PMStepEntity(mCalendar.getTimeInMillis());
        sourceList.add(tuesdayEntity);

        mLocalDataSource.insertOrReplaceWithBatchData(sourceList);

        long timeInMill = mCalendar.getTimeInMillis();
        List<PMStepEntity> results = mLocalDataSource.queryAllBetweenTimes(DateUtil.getBeginTimeOfWeekInMill(timeInMill), DateUtil.getEndTimeOfWeekInMill(timeInMill), PMStepLocalDataSource.DataType.WEEK);
        assertNotNull(results);

        assertEquals(results.size(), sourceList.size());
    }

    @Test
    public void testQueryAllBetweenTimesOfMonth() {
        List<PMStepEntity> sourceList = new ArrayList<>();

        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        PMStepEntity dayOne = new PMStepEntity(mCalendar.getTimeInMillis());
        sourceList.add(dayOne);

        mCalendar.set(Calendar.DAY_OF_MONTH, 2);
        PMStepEntity dayTwo = new PMStepEntity(mCalendar.getTimeInMillis());
        sourceList.add(dayTwo);

        mLocalDataSource.insertOrReplaceWithBatchData(sourceList);

        long timeInMill = mCalendar.getTimeInMillis();
        List<PMStepEntity> results = mLocalDataSource.queryAllBetweenTimes(DateUtil.getBeginTimeOfMonthInMill(timeInMill), DateUtil.getEndTimeOfMonthInMill(timeInMill), PMStepLocalDataSource.DataType.MONTH);
        assertNotNull(results);

        assertEquals(results.size(), sourceList.size());
    }

    @Test
    public void testQueryAllBetweenTimesOfAll() {
        List<PMStepEntity> sourceList = new ArrayList<>();

        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        PMStepEntity dayOne = new PMStepEntity(mCalendar.getTimeInMillis());
        sourceList.add(dayOne);

        mCalendar.set(Calendar.DAY_OF_MONTH, 2);
        PMStepEntity dayTwo = new PMStepEntity(mCalendar.getTimeInMillis());
        sourceList.add(dayTwo);

        mCalendar.add(Calendar.MONTH, 1);
        PMStepEntity monthNext = new PMStepEntity(mCalendar.getTimeInMillis());
        sourceList.add(monthNext);

        mLocalDataSource.insertOrReplaceWithBatchData(sourceList);

        List<PMStepEntity> results = mLocalDataSource.queryAllBetweenTimes(0, 0, PMStepLocalDataSource.DataType.ALL);
        assertNotNull(results);

        assertEquals(results.size(), sourceList.size());
    }

    @Test
    public void testQueryAll() {
        List<PMStepEntity> sourceList = new ArrayList<>();

        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        PMStepEntity dayOne = new PMStepEntity(mCalendar.getTimeInMillis());
        sourceList.add(dayOne);

        mCalendar.set(Calendar.DAY_OF_MONTH, 2);
        PMStepEntity dayTwo = new PMStepEntity(mCalendar.getTimeInMillis());
        sourceList.add(dayTwo);

        mCalendar.add(Calendar.MONTH, 1);
        PMStepEntity monthNext = new PMStepEntity(mCalendar.getTimeInMillis());
        sourceList.add(monthNext);

        mLocalDataSource.insertOrReplaceWithBatchData(sourceList);

        List<PMStepEntity> results = mLocalDataSource.queryAll();
        assertNotNull(results);

        assertEquals(results.size(), sourceList.size());
    }

    @Test
    public void testDeleteAll() {
        List<PMStepEntity> sourceList = getTwoStepsOutOf20Minutes();
        assertNotNull(sourceList);
        assertThat(sourceList.size(), is(2));
        mLocalDataSource.insertOrReplaceWithBatchData(sourceList);
        List<PMStepEntity> results = mLocalDataSource.queryAll();
        assertNotNull(results);
        assertThat(results.size(), is(2));
        mLocalDataSource.deleteAll();
        List<PMStepEntity> resultsAfterDelete = mLocalDataSource.queryAll();
        assertNull(resultsAfterDelete);
    }

    @NonNull
    private List<PMStepEntity> getTwoStepsWithin20Minutes() {
        List<PMStepEntity> sourceList = new ArrayList<>(2);

        long currentDataTimeInMill = System.currentTimeMillis();
        long preOutOfRangeDataTimeInMill = currentDataTimeInMill - PMStepLocalDataSource.INTERVAL_IN_MILL;

        PMStepEntity earlyStepData = new PMStepEntity(preOutOfRangeDataTimeInMill);
        PMStepEntity lateStepData = new PMStepEntity(currentDataTimeInMill);

        sourceList.add(earlyStepData);
        sourceList.add(lateStepData);

        assertNotNull(sourceList);
        assertThat(sourceList.size(), is(2));
        assertThat(sourceList.get(0).getCreateTimeInMill(), lessThan(sourceList.get(1).getCreateTimeInMill()));
        return sourceList;
    }

    @NonNull
    private List<PMStepEntity> getTwoStepsOutOf20Minutes() {
        List<PMStepEntity> sourceList = new ArrayList<>(2);

        long currentDataTimeInMill = DateUtil.getEndTimeOfDayInMill(System.currentTimeMillis());
        long preOutOfRangeDataTimeInMill = currentDataTimeInMill - PMStepLocalDataSource.INTERVAL_IN_MILL - 1;

        PMStepEntity earlyStepData = new PMStepEntity(preOutOfRangeDataTimeInMill);
        PMStepEntity lateStepData = new PMStepEntity(currentDataTimeInMill);

        sourceList.add(earlyStepData);
        sourceList.add(lateStepData);

        assertNotNull(sourceList);
        assertThat(sourceList.size(), is(2));
        assertThat(sourceList.get(0).getCreateTimeInMill(), lessThan(sourceList.get(1).getCreateTimeInMill()));
        return sourceList;
    }


}
