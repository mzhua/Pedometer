package com.wonders.xlab.pedometer.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;

import com.wonders.xlab.pedometer.data.PMStepCountEntity;
import com.wonders.xlab.pedometer.db.PMStepCount;

/**
 * Created by hua on 16/9/9.
 */

public class StepCounterService extends Service {
    public static Boolean FLAG = false;// 服务运行标志

    private SensorManager mSensorManager;// 传感器服务
    private SensorEventListener mSensorEventListener;// 传感器监听对象

    private Intent mBroadcastIntent;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        FLAG = true;// 标记为服务正在运行

        // 获取传感器的服务，初始化传感器
        mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        // 注册传感器，注册监听器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            OfficialStepDetector detector = new OfficialStepDetector();
            mSensorManager.registerListener(detector,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
                    SensorManager.SENSOR_DELAY_FASTEST);

            mSensorEventListener = detector;
        } else {
            // 创建监听器类，实例化监听对象
            StepDetector detector = new StepDetector();
            detector.addStepListener(new StepListener() {
                @Override
                public void onStep(SensorEvent event) {
                    increaseStepCountByOne();
                }
            });
            mSensorEventListener = detector;

            mSensorManager.registerListener(detector,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_FASTEST);
        }

        mBroadcastIntent = new Intent(getPackageName()+".pm.step.broadcast");

    }

    class OfficialStepDetector implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            increaseStepCountByOne();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }


    /**
     * 步数加一
     */
    private void increaseStepCountByOne() {
        PMStepCount.getInstance(this).insertOrIncrease(new PMStepCountEntity(System.currentTimeMillis(), 1));
        sendBroadcast(mBroadcastIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FLAG = false;// 服务停止
        if (mSensorEventListener != null) {
            mSensorManager.unregisterListener(mSensorEventListener);
        }
    }
}
