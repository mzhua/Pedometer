package com.wonders.xlab.pedometer.ui.month;

import com.wonders.xlab.pedometer.base.BaseContract;
import com.wonders.xlab.pedometer.widget.PMMonthLineAreaBean;
import com.wonders.xlab.pedometer.widget.PMWeeklyBarChartBean;

import java.util.List;

/**
 * Created by hua on 16/8/26.
 */

public interface PMMonthlyContract {
    interface View extends BaseContract.View {
        void showMonthlyData(int avgStepCounts, int sumStepCounts, List<PMMonthLineAreaBean> dataList);
    }
}
