package com.wonders.xlab.pedometer.test.daily;

import com.wonders.xlab.pedometer.base.BaseContract;
import com.wonders.xlab.pedometer.base.DefaultException;
import com.wonders.xlab.pedometer.data.PMStepContract;
import com.wonders.xlab.pedometer.data.PMStepEntity;
import com.wonders.xlab.pedometer.localdata.PMStepLocalDataSource;
import com.wonders.xlab.pedometer.ui.daily.PMDailyContract;
import com.wonders.xlab.pedometer.ui.daily.PMDailyPresenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by hua on 16/9/22.
 */
@RunWith(JUnit4.class)
public class TestPMDailyPresenter {

    PMDailyPresenter mDailyPresenter;

    @Mock
    PMDailyContract.View mView;

    @Mock
    PMStepContract.Model mModel;

    @Captor
    ArgumentCaptor<BaseContract.Model.Callback<List<PMStepEntity>>> mCallback;
    @Before
    public void setup() {
        initMocks(this);
        mDailyPresenter = new PMDailyPresenter(mView, mModel);
    }

    @Test
    public void testGetDatasSuccess() {
        int[] stepsPerRecord = new int[]{200, 300, 123};
        int sumStepsOfToday = 0;

        final List<PMStepEntity> entityList = new ArrayList<>();

        for (int stepsOfPerRecord : stepsPerRecord) {
            sumStepsOfToday += stepsOfPerRecord;

            PMStepEntity entity = new PMStepEntity(System.currentTimeMillis());
            entityList.add(entity);
        }

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                BaseContract.Model.Callback<List<PMStepEntity>> callback = invocation.getArgument(3);

                callback.onSuccess(entityList);
                return 200;
            }
        }).when(mModel).getDataList(anyLong(), anyLong(), eq(PMStepLocalDataSource.DataType.DAY), any(BaseContract.Model.Callback.class));

        mDailyPresenter.getDatas(0, 0);

        verify(mView).showDailyData(eq(sumStepsOfToday), eq(0), eq(0), eq(entityList));
    }

    @Test
    public void testGetDatasSuccessWithCaptor() {
        int[] stepsPerRecord = new int[]{200, 300, 123};
        int sumStepsOfToday = 0;
        final List<PMStepEntity> entityList = new ArrayList<>();
        for (int stepsOfPerRecord : stepsPerRecord) {
            sumStepsOfToday += stepsOfPerRecord;

            PMStepEntity entity = new PMStepEntity(System.currentTimeMillis());
            entityList.add(entity);
        }

        //调用
        mDailyPresenter.getDatas(0, 0);
        //验证
        verify(mModel).getDataList(anyLong(), anyLong(),eq(PMStepLocalDataSource.DataType.DAY),mCallback.capture());
        //触发callback
        mCallback.getValue().onSuccess(entityList);
        //验证callback触发后回调View显示
        verify(mView).showDailyData(eq(sumStepsOfToday), eq(0), eq(0), eq(entityList));
    }

    @Test
    public void testGetDatasFail() {
        String errorMsg = "get data failed";

        mDailyPresenter.getDatas(0,0);
        verify(mModel).getDataList(anyLong(),anyLong(),eq(PMStepLocalDataSource.DataType.DAY),mCallback.capture());
        mCallback.getValue().onFail(new DefaultException(errorMsg));
        verify(mView).showToastMessage(eq(errorMsg));
    }
}
