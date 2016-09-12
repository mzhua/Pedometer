package com.wonders.xlab.pedometer.service;

import android.hardware.SensorEvent;

/**
 * Created by hua on 16/9/9.
 */

public interface StepListener {
    void onStep(SensorEvent event);
}
