package com.wonders.xlab.pedometer.test.data;

import com.wonders.xlab.pedometer.base.BaseContract;
import com.wonders.xlab.pedometer.data.PMStepEntity;
import com.wonders.xlab.pedometer.data.PMStepRepository;
import com.wonders.xlab.pedometer.localdata.PMStepLocalDataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by hua on 16/9/22.
 */

public class TestPMStepRepository {

    @Mock
    PMStepLocalDataSource mLocalDataSource;

    @Mock
    BaseContract.Model.Callback<List<PMStepEntity>> mCallback;

    private PMStepRepository mStepRepository;

    @Before
    public void setup() {
        initMocks(this);
        mStepRepository = new PMStepRepository(mLocalDataSource);
    }

    @Test
    public void testGetDataList() {
        mStepRepository.getDataList(anyLong(), anyLong(), anyInt(), mCallback);
        verify(mLocalDataSource).queryAllBetweenTimes(anyLong(), anyLong(), anyInt());
        verify(mCallback).onSuccess(ArgumentMatchers.<PMStepEntity>anyList());
    }
}
