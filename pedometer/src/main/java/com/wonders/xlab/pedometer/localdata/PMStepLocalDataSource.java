package com.wonders.xlab.pedometer.localdata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.wonders.xlab.pedometer.data.PMStepEntity;
import com.wonders.xlab.pedometer.localdata.PMContract.StepCountEntry;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.wonders.xlab.pedometer.localdata.PMStepLocalDataSource.DataType.ALL;
import static com.wonders.xlab.pedometer.localdata.PMStepLocalDataSource.DataType.DAY;
import static com.wonders.xlab.pedometer.localdata.PMStepLocalDataSource.DataType.MONTH;
import static com.wonders.xlab.pedometer.localdata.PMStepLocalDataSource.DataType.WEEK;


/**
 * Created by hua on 16/9/12.
 * 本地sqlite数据源
 */

public class PMStepLocalDataSource {
    private final static int INTERVAL_MINUTES = 20;
    /**
     * 保存记录时,在这个时间差范围内的记录合并为一条
     */
    public final static long INTERVAL_IN_MILL = INTERVAL_MINUTES * 60 * 1000;

    private PMDbHelper mDbHelper;

    private static Calendar mCalendar;

    private static PMStepLocalDataSource instance = null;

    private String[] mProjectionDay = {
            StepCountEntry.COLUMN_NAME_STEPS,
            StepCountEntry.COLUMN_NAME_CREATE_TIME_IN_MILL,
            StepCountEntry.COLUMN_NAME_UPDATE_TIME_IN_MILL
    };

    private String[] mProjectionWeekAndMonth = {
            "sum(" + StepCountEntry.COLUMN_NAME_STEPS + ") " + StepCountEntry.COLUMN_NAME_STEPS,
            "max(" + StepCountEntry.COLUMN_NAME_UPDATE_TIME_IN_MILL + ") " + StepCountEntry.COLUMN_NAME_UPDATE_TIME_IN_MILL
    };

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DAY, WEEK, MONTH, ALL})
    public @interface DataType {
        int DAY = 0;
        int WEEK = 1;
        int MONTH = 2;
        int ALL = 3;
    }

    private PMStepLocalDataSource(Context context) {
        mDbHelper = new PMDbHelper(context);
        mCalendar = Calendar.getInstance();
    }

    public static PMStepLocalDataSource get(Context context) {
        synchronized (PMStepLocalDataSource.class) {
            if (instance == null) {
                instance = new PMStepLocalDataSource(context);
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
    public long insertOrIncrease(@NonNull PMStepEntity entity) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = convertEntityToContentValues(queryByUpdateTimeInMillWithin20Min(db, entity));
        long l = db.insertWithOnConflict(StepCountEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return l;
    }

    public void insertOrReplaceWithBatchData(List<PMStepEntity> entityList) {
        if (entityList == null || entityList.size() == 0) {
            return;
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        for (PMStepEntity entity : entityList) {
            db.insertWithOnConflict(StepCountEntry.TABLE_NAME, null, convertEntityToContentValues(queryByUpdateTimeInMillWithin20Min(db, entity)), SQLiteDatabase.CONFLICT_REPLACE);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    private ContentValues convertEntityToContentValues(@NonNull PMStepEntity entity) {
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
    private PMStepEntity queryByUpdateTimeInMillWithin20Min(SQLiteDatabase db, @NonNull PMStepEntity stepCountEntity) {
        String selection = StepCountEntry.COLUMN_NAME_UPDATE_TIME_IN_MILL + " between ? and ?";
        String[] selectionArgs = new String[]{String.valueOf(stepCountEntity.getUpdateTimeInMill() - INTERVAL_IN_MILL), String.valueOf(stepCountEntity.getUpdateTimeInMill())};

        Cursor cursor = db.query(StepCountEntry.TABLE_NAME, mProjectionDay, selection, selectionArgs, null, null, StepCountEntry.COLUMN_NAME_UPDATE_TIME_IN_MILL + " DESC");
        if (cursor.moveToFirst()) {
            //get the latest one record
            stepCountEntity.setStepCounts(stepCountEntity.getStepCounts() + cursor.getInt(cursor.getColumnIndexOrThrow(StepCountEntry.COLUMN_NAME_STEPS)));
            stepCountEntity.setCreateTimeInMill(cursor.getLong(cursor.getColumnIndexOrThrow(StepCountEntry.COLUMN_NAME_CREATE_TIME_IN_MILL)));
        } else {
            //or set the new record's create time to update time
            stepCountEntity.setCreateTimeInMill(stepCountEntity.getUpdateTimeInMill());
        }

        cursor.close();
        return stepCountEntity;
    }

    /**
     * 查询所有数据
     *
     * @return
     */
    public List<PMStepEntity> queryAll() {
        return queryAllBetweenTimes(0, 0, ALL);
    }

    /**
     * 根据{@link DataType}查询一段时间内的数据
     *
     * @param startTimeInMill
     * @param endTimeInMill
     * @param dataType
     * @return
     */
    @Nullable
    public List<PMStepEntity> queryAllBetweenTimes(long startTimeInMill, long endTimeInMill, @DataType int dataType) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String selection = StepCountEntry.COLUMN_NAME_UPDATE_TIME_IN_MILL + " between ? and ?";
        String[] selectionArgs = new String[]{String.valueOf(startTimeInMill), String.valueOf(endTimeInMill)};
        String groupBy = null;
        String[] projection = mProjectionDay;
        switch (dataType) {
            case DAY:
                projection = mProjectionDay;
                groupBy = null;
                break;
            case WEEK:
                projection = mProjectionWeekAndMonth;
                groupBy = StepCountEntry.COLUMN_NAME_DAY;
                break;
            case MONTH:
                projection = mProjectionWeekAndMonth;
                groupBy = StepCountEntry.COLUMN_NAME_DAY;
                break;
            case ALL:
                selection = null;
                selectionArgs = null;
                projection = mProjectionDay;
                groupBy = null;
                break;
        }
        Cursor cursor = db.query(StepCountEntry.TABLE_NAME, projection, selection, selectionArgs, groupBy, null, StepCountEntry.COLUMN_NAME_UPDATE_TIME_IN_MILL + " ASC");

        List<PMStepEntity> entityList = null;
        if (cursor.moveToFirst()) {
            entityList = new ArrayList<>();
            do {
                PMStepEntity entity = new PMStepEntity(cursor.getLong(cursor.getColumnIndexOrThrow(StepCountEntry.COLUMN_NAME_UPDATE_TIME_IN_MILL)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(StepCountEntry.COLUMN_NAME_STEPS))
                );
                entityList.add(entity);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return entityList;
    }

    @VisibleForTesting
    public void deleteAll() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(StepCountEntry.TABLE_NAME, null, null);
        db.close();
    }
}
