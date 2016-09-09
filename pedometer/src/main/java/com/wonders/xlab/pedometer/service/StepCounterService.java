package com.wonders.xlab.pedometer.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Created by hua on 16/9/9.
 */

public class StepCounterService extends Service {
    public static Boolean FLAG = false;// 服务运行标志

    private SensorManager mSensorManager;// 传感器服务
    private StepDetector detector;// 传感器监听对象

    private PowerManager mPowerManager;// 电源管理服务
    private PowerManager.WakeLock mWakeLock;// 屏幕灯

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private SharedPreferences preferences;
    SharedPreferences.Editor edit;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        edit = preferences.edit();

        FLAG = true;// 标记为服务正在运行

        // 创建监听器类，实例化监听对象
        detector = new StepDetector();
        detector.addStepListener(new StepListener() {
            @Override
            public void onStep() {
                edit.putInt("steps", preferences.getInt("steps", 0) + 1).apply();
            }

            @Override
            public void passValue() {

            }
        });

        // 获取传感器的服务，初始化传感器
        mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        // 注册传感器，注册监听器
        mSensorManager.registerListener(detector,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        // 电源管理服务
        mPowerManager = (PowerManager) this
                .getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "S");
        mWakeLock.acquire();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        FLAG = false;// 服务停止
        if (detector != null) {
            mSensorManager.unregisterListener(detector);
        }

        if (mWakeLock != null) {
            mWakeLock.release();
        }
    }
}
