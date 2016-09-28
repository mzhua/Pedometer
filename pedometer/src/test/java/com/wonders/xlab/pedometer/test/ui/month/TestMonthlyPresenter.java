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
    public void testGetNonDatas_CallViewToDisplay() {

        mMonthlyPresenter.getDatas(mStartTimeInMill, mEndTimeInMill);
        verify(mModel).getDataList(eq(mStartTimeInMill), eq(mEndTimeInMill), eq(PMStepLocalDataSource.DataType.MONTH), mCallback.capture());
        mCallback.getValue().onSuccess(null);
        verify(mView).showMonthlyData(anyInt(), anyInt(), ArgumentMatchers.<List<PMMonthLineAreaBean>>isNull());
    }

    @Test
    public void testGetEmptyDatas_CallViewToDisplay() {

        mMonthlyPresenter.getDatas(mStartTimeInMill, mEndTimeInMill);
        verify(mModel).getDataList(eq(mStartTimeInMill), eq(mEndTimeInMill), eq(PMStepLocalDataSource.DataType.MONTH), mCallback.capture());
        mCallback.getValue().onSuccess(new ArrayList<PMStepEntity>());
        verify(mView).showMonthlyData(anyInt(), anyInt(), ArgumentMatchers.<List<PMMonthLineAreaBean>>isNull());
    }

    @Test
    public void testGetDatasFailed_ShowToast() {
        mMonthlyPresenter.getDatas(mStartTimeInMill, mEndTimeInMill);
        verify(mModel).getDataList(eq(mStartTimeInMill), eq(mEndTimeInMill), eq(PMStepLocalDataSource.DataType.MONTH), mCallback.capture());
        String error = "error";
        mCallback.getValue().onFail(new DefaultException(error));
        verify(mView).showToastMessage(eq(error));
    }
}
