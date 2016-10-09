package com.wonders.xlab.pedometer.test.ui.daily;

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
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by hua on 16/9/22.
 */
@RunWith(JUnit4.class)
public class TestPMDailyPresenter {

    private final int DATE_TYPE_DAY = PMStepLocalDataSource.DataType.DAY;

    private PMDailyPresenter mDailyPresenter;

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
    public void testGetNonDatas_CallViewToDisplay() {

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                BaseContract.Model.Callback<List<PMStepEntity>> callback = invocation.getArgument(3);

                callback.onSuccess(null);
                return 200;
            }
        }).when(mModel).getDataList(anyLong(), anyLong(), eq(DATE_TYPE_DAY), ArgumentMatchers.<BaseContract.Model.Callback<List<PMStepEntity>>>any());

        mDailyPresenter.getDatas(0L, 0L);

        verify(mView).showDailyData(anyInt(), anyInt(), anyInt(), ArgumentMatchers.<List<PMStepEntity>>isNull());
    }

    @Test
    public void testGetNonDatasWithCaptor_CallViewToDisplay() {
        //调用
        mDailyPresenter.getDatas(0L, 0L);
        //验证
        verify(mModel).getDataList(anyLong(), anyLong(), eq(DATE_TYPE_DAY), mCallback.capture());
        //触发callback
        mCallback.getValue().onSuccess(null);
        //验证callback触发后回调View显示
        verify(mView).showDailyData(anyInt(), anyInt(), anyInt(), ArgumentMatchers.<List<PMStepEntity>>isNull());
    }

    @Test
    public void testGetEmptyDatas_CallViewToDisplay() {
        //调用
        mDailyPresenter.getDatas(0L, 0L);
        //验证
        verify(mModel).getDataList(anyLong(), anyLong(), eq(DATE_TYPE_DAY), mCallback.capture());
        //触发callback
        mCallback.getValue().onSuccess(new ArrayList<PMStepEntity>());
        //验证callback触发后回调View显示
        verify(mView).showDailyData(anyInt(), anyInt(), anyInt(), ArgumentMatchers.<List<PMStepEntity>>any());
    }

    @Test
    public void testGetDatas_CallViewToDisplay() {
        List<PMStepEntity> entityList = new ArrayList<>();
        entityList.add(new PMStepEntity(System.currentTimeMillis()));

        //调用
        mDailyPresenter.getDatas(0L, 0L);
        //验证
        verify(mModel).getDataList(anyLong(), anyLong(), eq(DATE_TYPE_DAY), mCallback.capture());
        //触发callback
        mCallback.getValue().onSuccess(entityList);
        //验证callback触发后回调View显示
        verify(mView).showDailyData(anyInt(), anyInt(), anyInt(), eq(entityList));
    }

    @Test
    public void testGetDatasFail_ShowToast() {
        String errorMsg = "get data failed";

        mDailyPresenter.getDatas(0L, 0L);
        verify(mModel).getDataList(anyLong(), anyLong(), eq(DATE_TYPE_DAY), mCallback.capture());
        mCallback.getValue().onFail(new DefaultException(errorMsg));
        verify(mView).showToastMessage(anyString());
    }
}
