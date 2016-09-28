package com.wonders.xlab.pedometer.test.ui.month;

import com.wonders.xlab.pedometer.base.BaseContract;
import com.wonders.xlab.pedometer.base.DefaultException;
import com.wonders.xlab.pedometer.data.PMStepContract;
import com.wonders.xlab.pedometer.data.PMStepEntity;
import com.wonders.xlab.pedometer.localdata.PMStepLocalDataSource;
import com.wonders.xlab.pedometer.ui.month.PMMonthlyContract;
import com.wonders.xlab.pedometer.ui.month.PMMonthlyPresenter;
import com.wonders.xlab.pedometer.util.DateUtil;
import com.wonders.xlab.pedometer.widget.PMMonthLineAreaBean;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by hua on 16/9/27.
 */

public class TestMonthlyPresenter {
    private PMMonthlyPresenter mMonthlyPresenter;

    @Mock
    public PMMonthlyContract.View mView;

    @Mock
    public PMStepContract.Model mModel;

    @Captor
    public ArgumentCaptor<BaseContract.Model.Callback<List<PMStepEntity>>> mCallback;

    private long mStartTimeInMill;
    private long mEndTimeInMill;

    @Before
    public void setup() {
        initMocks(this);
        mMonthlyPresenter = new PMMonthlyPresenter(mView, mModel);

        long currentTimeMillis = System.currentTimeMillis();
        mStartTimeInMill = DateUtil.getBeginTimeOfMonthInMill(currentTimeMillis);
        mEndTimeInMill = DateUtil.getEndTimeOfMonthInMill(currentTimeMillis);
    }

    @Test
    public void testGetDatasSuccess() {
        int[] stepsPerRecord = new int[]{200, 300, 123};
        int sumStepsOfToday = 0;

        final List<PMStepEntity> entityList = new ArrayList<>();

        for (int stepsOfPerRecord : stepsPerRecord) {
            sumStepsOfToday += stepsOfPerRecord;

            PMStepEntity entity = new PMStepEntity(System.currentTimeMillis(), stepsOfPerRecord);
            entityList.add(entity);
        }
        int avgStepsOfToday = sumStepsOfToday / 3;

        mMonthlyPresenter.getDatas(mStartTimeInMill, mEndTimeInMill);
        verify(mModel).getDataList(eq(mStartTimeInMill), eq(mEndTimeInMill), eq(PMStepLocalDataSource.DataType.MONTH), mCallback.capture());
        mCallback.getValue().onSuccess(entityList);
        verify(mView).showMonthlyData(eq(avgStepsOfToday), eq(sumStepsOfToday), ArgumentMatchers.<PMMonthLineAreaBean>anyList());
        verify(mView, times(0)).showToastMessage(anyString());
    }

    @Test
    public void testGetDatasSuccessReturnNull() {

        mMonthlyPresenter.getDatas(mStartTimeInMill, mEndTimeInMill);
        verify(mModel).getDataList(eq(mStartTimeInMill), eq(mEndTimeInMill), eq(PMStepLocalDataSource.DataType.MONTH), mCallback.capture());
        mCallback.getValue().onFail(new DefaultException("error"));
        verify(mView).showToastMessage(eq("error"));
        verify(mView, times(0)).showMonthlyData(anyInt(), anyInt(), ArgumentMatchers.<PMMonthLineAreaBean>anyList());
    }

    @Test
    public void testGetDatasFailed() {
        mMonthlyPresenter.getDatas(mStartTimeInMill, mEndTimeInMill);
        verify(mModel).getDataList(eq(mStartTimeInMill), eq(mEndTimeInMill), eq(PMStepLocalDataSource.DataType.MONTH), mCallback.capture());
        mCallback.getValue().onFail(new DefaultException("error"));
        verify(mView).showToastMessage(eq("error"));
        verify(mView, times(0)).showMonthlyData(anyInt(), anyInt(), ArgumentMatchers.<PMMonthLineAreaBean>anyList());
    }
}
