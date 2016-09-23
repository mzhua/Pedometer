package com.wonders.xlab.pedometer.test.localdata;

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
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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

    @Before
    public void setup() {
        mLocalDataSource = PMStepLocalDataSource.get(InstrumentationRegistry.getContext());
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
        assertThat(reason, pmStepEntities.get(0).getStepCounts(), is(2));
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
        List<PMStepEntity> mStepEntityList = new ArrayList<>();

        long currentDataTimeInMill = System.currentTimeMillis();
        long preOutOfRangeDataTimeInMill = currentDataTimeInMill - PMStepLocalDataSource.INTERVAL_IN_MILL - 1;

        PMStepEntity earlyStepData = new PMStepEntity(preOutOfRangeDataTimeInMill);
        PMStepEntity lateStepData = new PMStepEntity(currentDataTimeInMill);

        mStepEntityList.add(earlyStepData);
        mStepEntityList.add(lateStepData);

        mLocalDataSource.insertOrReplaceWithBatchData(mStepEntityList);
        List<PMStepEntity> pmStepEntities = mLocalDataSource.queryAll();
        assertNotNull("the query result is null", pmStepEntities);
        int actualSize = pmStepEntities.size();
        assertEquals("insert batch datas failed, the result size expect 2 but actual is " + actualSize, 2, actualSize);
    }

    @Test
    public void testInsertOrReplaceWithBatchDataWithMergeAbleDatas() {
        List<PMStepEntity> mStepEntityList = new ArrayList<>();

        long currentDataTimeInMill = System.currentTimeMillis();
        long preOutOfRangeDataTimeInMill = currentDataTimeInMill - PMStepLocalDataSource.INTERVAL_IN_MILL;

        PMStepEntity earlyStepData = new PMStepEntity(preOutOfRangeDataTimeInMill);
        PMStepEntity lateStepData = new PMStepEntity(currentDataTimeInMill);

        mStepEntityList.add(earlyStepData);
        mStepEntityList.add(lateStepData);

        mLocalDataSource.insertOrReplaceWithBatchData(mStepEntityList);
        List<PMStepEntity> pmStepEntities = mLocalDataSource.queryAll();
        assertNotNull("the query result is null", pmStepEntities);
        int actualSize = pmStepEntities.size();
        assertEquals("insert batch datas failed, the result size expect 1 but actual is " + actualSize, 1, actualSize);
        assertEquals("insert batch datas wrong, the step counts should be 2 but actual is " + pmStepEntities.get(0).getStepCounts(), 2, pmStepEntities.get(0).getStepCounts());
    }

    @Test
    public void testInsertOrReplaceWithBatchDataWithNullResource() {
        mLocalDataSource.insertOrReplaceWithBatchData(null);
        List<PMStepEntity> pmStepEntities = mLocalDataSource.queryAll();
        assertNull("the query result should be null", pmStepEntities);
    }

    @Test
    public void testQueryAllBetweenTimesOfDay() {
        List<PMStepEntity> mStepEntityList = new ArrayList<>();

        long timeInMill = System.currentTimeMillis();
        long currentDataTimeInMill = DateUtil.getEndTimeOfDayInMill(timeInMill);
        long preOutOfRangeDataTimeInMill = currentDataTimeInMill - PMStepLocalDataSource.INTERVAL_IN_MILL - 1;

        PMStepEntity earlyStepData = new PMStepEntity(preOutOfRangeDataTimeInMill);
        PMStepEntity lateStepData = new PMStepEntity(currentDataTimeInMill);

        mStepEntityList.add(earlyStepData);
        mStepEntityList.add(lateStepData);

        mLocalDataSource.insertOrReplaceWithBatchData(mStepEntityList);

        List<PMStepEntity> entityList = mLocalDataSource.queryAllBetweenTimes(DateUtil.getBeginTimeOfDayInMill(timeInMill), DateUtil.getEndTimeOfDayInMill(timeInMill), PMStepLocalDataSource.DataType.DAY);
        assertNotNull(entityList);
        assertEquals(entityList.size(), 2);

    }

}
