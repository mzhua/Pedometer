package com.wonders.xlab.pedometer.localdata;

import android.provider.BaseColumns;

/**
 * Created by hua on 16/9/12.
 */

final class PMContract {
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " TEXT";
    private static final String LONG_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    /**
     * 以createTime为主键,因为createTime(创建时间)不会变,方便后面数据保存
     */
    static class StepCountEntry implements BaseColumns {
        static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + StepCountEntry.TABLE_NAME + " (" +
                        StepCountEntry.COLUMN_NAME_STEPS + INT_TYPE + COMMA_SEP +
                        StepCountEntry.COLUMN_NAME_DAY + INT_TYPE + COMMA_SEP +
                        StepCountEntry.COLUMN_NAME_MONTH + INT_TYPE + COMMA_SEP +
                        StepCountEntry.COLUMN_NAME_YEAR + INT_TYPE + COMMA_SEP +
                        StepCountEntry.COLUMN_NAME_UPDATE_TIME_IN_MILL + LONG_TYPE + COMMA_SEP +
                        StepCountEntry.COLUMN_NAME_CREATE_TIME_IN_MILL + LONG_TYPE + " PRIMARY KEY" + " )";

        static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + StepCountEntry.TABLE_NAME;

        static final String TABLE_NAME = "StepCountEntry";
        static final String COLUMN_NAME_STEPS = "steps";
        static final String COLUMN_NAME_YEAR = "year";
        static final String COLUMN_NAME_MONTH = "month";
        static final String COLUMN_NAME_DAY = "day";
        static final String COLUMN_NAME_CREATE_TIME_IN_MILL = "createTime";
        static final String COLUMN_NAME_UPDATE_TIME_IN_MILL = "updateTime";
    }
}
