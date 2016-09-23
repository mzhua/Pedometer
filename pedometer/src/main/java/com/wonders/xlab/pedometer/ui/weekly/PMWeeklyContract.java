package com.wonders.xlab.pedometer.ui.weekly;

import com.wonders.xlab.pedometer.base.BaseContract;
import com.wonders.xlab.pedometer.widget.PMWeeklyBarChartBean;

import java.util.List;

/**
 * Created by hua on 16/8/26.
 */

public interface PMWeeklyContract {
    interface View extends BaseContract.View {
        void showWeeklyData(int avgStepCounts, int sumStepCounts, List<PMWeeklyBarChartBean> dataList);
    }
}
