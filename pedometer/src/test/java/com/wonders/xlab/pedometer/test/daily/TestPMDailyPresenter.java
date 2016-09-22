package com.wonders.xlab.pedometer.test.daily;

import com.wonders.xlab.pedometer.base.BaseContract;
import com.wonders.xlab.pedometer.data.PMStepCountContract;
import com.wonders.xlab.pedometer.data.PMStepCountEntity;
import com.wonders.xlab.pedometer.db.PMStepCount;
import com.wonders.xlab.pedometer.ui.daily.PMDailyContract;
import com.wonders.xlab.pedometer.ui.daily.PMDailyPresenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
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
    PMStepCountContract.Model mModel;

    @Before
    public void setup() {
        initMocks(this);
        mDailyPresenter = new PMDailyPresenter(mView, mModel);
    }

    @Test
    public void testGetDatasSuccess() {
        int[] stepsPerRecord = new int[]{200, 300, 123};
        int sumStepsOfToday = 0;

        final List<PMStepCountEntity> entityList = new ArrayList<>();

        for (int stepsOfPerRecord : stepsPerRecord) {
            sumStepsOfToday += stepsOfPerRecord;

            PMStepCountEntity entity = new PMStepCountEntity(System.currentTimeMillis(), stepsOfPerRecord);
            entityList.add(entity);
        }

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                BaseContract.Model.Callback<List<PMStepCountEntity>> callback = invocation.getArgument(3);

                callback.onSuccess(entityList);
                return 200;
            }
        }).when(mModel).getDataList(anyLong(), anyLong(), eq(PMStepCount.DataType.DAY), any(BaseContract.Model.Callback.class));

        mDailyPresenter.getDatas(0, 0);

        verify(mView).showDailyData(eq(sumStepsOfToday), eq(0), eq(0), eq(entityList));
    }

}
