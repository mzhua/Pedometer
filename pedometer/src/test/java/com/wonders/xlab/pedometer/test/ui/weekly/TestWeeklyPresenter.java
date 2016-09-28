package com.wonders.xlab.pedometer.test.ui.weekly;

import com.wonders.xlab.pedometer.base.BaseContract;
import com.wonders.xlab.pedometer.base.DefaultException;
import com.wonders.xlab.pedometer.data.PMStepContract;
import com.wonders.xlab.pedometer.data.PMStepEntity;
import com.wonders.xlab.pedometer.ui.weekly.PMWeeklyContract;
import com.wonders.xlab.pedometer.ui.weekly.PMWeeklyPresenter;
import com.wonders.xlab.pedometer.util.DateUtil;
import com.wonders.xlab.pedometer.widget.PMWeeklyBarChartBean;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static com.wonders.xlab.pedometer.localdata.PMStepLocalDataSource.DataType.MONTH;
import static com.wonders.xlab.pedometer.localdata.PMStepLocalDataSource.DataType.WEEK;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;

/**
 * Created by hua on 16/9/28.
 */

public class TestWeeklyPresenter {
    private PMWeeklyPresenter mPresenter;

    @Mock
    public PMWeeklyContract.View mView;

    @Mock
    public PMStepContract.Model mModel;

    @Captor
    public ArgumentCaptor<BaseContract.Model.Callback<List<PMStepEntity>>> mArgumentCaptor;

    private long mStartTimeInMill;
    private long mEndTimeInMill;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mPresenter = new PMWeeklyPresenter(mView, mModel);

        long currentTimeMillis = System.currentTimeMillis();
        mStartTimeInMill = DateUtil.getBeginTimeOfMonthInMill(currentTimeMillis);
        mEndTimeInMill = DateUtil.getEndTimeOfMonthInMill(currentTimeMillis);
    }

    @Test
    public void testGetDatasSuccess() {
        mPresenter.getDatas(mStartTimeInMill, mEndTimeInMill);
        verify(mModel).getDataList(eq(mStartTimeInMill), eq(mEndTimeInMill),eq(WEEK), mArgumentCaptor.capture());
        mArgumentCaptor.getValue().onSuccess(null);
        verify(mView).showWeeklyData(anyInt(),anyInt(),isNull(List.class));
    }

    @Test
    public void testGetDatasFailed() {
        mPresenter.getDatas(mStartTimeInMill, mEndTimeInMill);
        verify(mModel).getDataList(eq(mStartTimeInMill), eq(mEndTimeInMill),eq(WEEK), mArgumentCaptor.capture());
        mArgumentCaptor.getValue().onFail(new DefaultException("error"));
        verify(mView).showToastMessage(eq("error"));
    }
}
