package com.wonders.xlab.pedometer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.wonders.xlab.pedometer.data.PMStepCountEntity;
import com.wonders.xlab.pedometer.db.PMContract.StepCountEntry;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.wonders.xlab.pedometer.db.PMStepCount.DataType.DAY;
import static com.wonders.xlab.pedometer.db.PMStepCount.DataType.MONTH;
import static com.wonders.xlab.pedometer.db.PMStepCount.DataType.WEEK;


/**
 * Created by hua on 16/9/12.
 */

public class PMStepCount {
    private final int INTERVAL_MINUTES = 1;//1m
    /**
     * 保存记录时,在这个时间差范围内的记录合并为一条
     */
    private final long INTERVAL_IN_MILL = INTERVAL_MINUTES * 60 * 1000;

    private PMDbHelper mDbHelper;

    private static Calendar mCalendar;

    private static PMStepCount instance = null;

    private String[] mProjection = {
            StepCountEntry.COLUMN_NAME_STEPS,
            StepCountEntry.COLUMN_NAME_CREATE_TIME_IN_MILL,
            StepCountEntry.COLUMN_NAME_UPDATE_TIME_IN_MILL
    };

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DAY, WEEK, MONTH})
    public @interface DataType {
        int DAY = 0;
        int WEEK = 1;
        int MONTH = 2;
    }

    private PMStepCount(Context context) {
        mDbHelper = new PMDbHelper(context);
        mCalendar = Calendar.getInstance();
    }

    public static PMStepCount getInstance(Context context) {
        synchronized (PMStepCount.class) {
            if (instance == null) {
                instance = new PMStepCount(context);
            }
        }
        return instance;
    }

    /**
     * 如果该条记录前{@link #INTERVAL_IN_MILL}毫秒内存在记录,则合并为一条记录,步数在那一条的基础上加一,否则新开一条记录
     *
     * @param entity
     * @return the row ID of the newly inserted row OR <code>-1</code> when insert failed
     */
    public synchronized long insertOrIncrease(@NonNull PMStepCountEntity entity) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = convertEntityToContentValues(queryByUpdateTimeInMillWithin20Min(db, entity));
        long l = db.insertWithOnConflict(StepCountEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return l;
    }

    @NonNull
    private ContentValues convertEntityToContentValues(@NonNull PMStepCountEntity entity) {
        mCalendar.setTimeInMillis(entity.getUpdateTimeInMill());

        ContentValues values = new ContentValues();
        values.put(StepCountEntry.COLUMN_NAME_CREATE_TIME_IN_MILL, entity.getCreateTimeInMill());
        values.put(StepCountEntry.COLUMN_NAME_UPDATE_TIME_IN_MILL, entity.getUpdateTimeInMill());
        values.put(StepCountEntry.COLUMN_NAME_STEPS, entity.getStepCounts());
        values.put(StepCountEntry.COLUMN_NAME_YEAR, mCalendar.get(Calendar.YEAR));
        values.put(StepCountEntry.COLUMN_NAME_MONTH, mCalendar.get(Calendar.MONTH) + 1);
        values.put(StepCountEntry.COLUMN_NAME_DAY, mCalendar.get(Calendar.DAY_OF_MONTH));
        return values;
    }

    /**
     * 根据updateTimeInMill查询前{@link #INTERVAL_IN_MILL}毫秒内的数据
     * 如果存在,则step相加,并且更新updateTime,否则返回原来的记录,并且更新createTime为updateTime
     *
     * @param stepCountEntity
     */
    private PMStepCountEntity queryByUpdateTimeInMillWithin20Min(SQLiteDatabase db, @NonNull PMStepCountEntity stepCountEntity) {


        String selection = StepCountEntry.COLUMN_NAME_UPDATE_TIME_IN_MILL + " >= " + (stepCountEntity.getUpdateTimeInMill() - INTERVAL_IN_MILL) + " and " +
                StepCountEntry.COLUMN_NAME_UPDATE_TIME_IN_MILL + " <= " + stepCountEntity.getUpdateTimeInMill();

        Cursor cursor = db.query(StepCountEntry.TABLE_NAME, mProjection, selection, null, null, null, StepCountEntry.COLUMN_NAME_UPDATE_TIME_IN_MILL + " DESC");
        if (cursor.moveToFirst()) {
            //get the latest one record
            stepCountEntity.setStepCounts(stepCountEntity.getStepCounts() + cursor.getInt(cursor.getColumnIndexOrThrow(StepCountEntry.COLUMN_NAME_STEPS)));
            stepCountEntity.setUpdateTimeInMill(stepCountEntity.getUpdateTimeInMill());
            stepCountEntity.setCreateTimeInMill(cursor.getLong(cursor.getColumnIndexOrThrow(StepCountEntry.COLUMN_NAME_CREATE_TIME_IN_MILL)));
        } else {
            //or set the new record's create time to update time
            stepCountEntity.setCreateTimeInMill(stepCountEntity.getUpdateTimeInMill());
        }

        cursor.close();
        return stepCountEntity;
    }

    @Nullable
    public List<PMStepCountEntity> queryAllBetweenTimes(long startTimeInMill, long endTimeInMill, @DataType int dataType) throws IllegalArgumentException {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String selection = StepCountEntry.COLUMN_NAME_UPDATE_TIME_IN_MILL + " between ? and ?";
        String[] selectionArgs = new String[]{String.valueOf(startTimeInMill), String.valueOf(endTimeInMill)};
        String groupBy = null;
        switch (dataType) {
            case DataType.DAY:
                groupBy = null;
                break;
            case DataType.WEEK:
                groupBy = StepCountEntry.COLUMN_NAME_DAY;
                break;
            case DataType.MONTH:
                groupBy = StepCountEntry.COLUMN_NAME_DAY;
                break;
        }
        Cursor cursor = db.query(StepCountEntry.TABLE_NAME, mProjection, null, null, groupBy, null, StepCountEntry.COLUMN_NAME_UPDATE_TIME_IN_MILL + " DESC");

        List<PMStepCountEntity> entityList = null;
        if (cursor.moveToFirst()) {
            entityList = new ArrayList<>();
            do {
                PMStepCountEntity entity = new PMStepCountEntity(cursor.getLong(cursor.getColumnIndexOrThrow(StepCountEntry.COLUMN_NAME_CREATE_TIME_IN_MILL)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(StepCountEntry.COLUMN_NAME_STEPS)));
                entityList.add(entity);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return entityList;
    }
}
