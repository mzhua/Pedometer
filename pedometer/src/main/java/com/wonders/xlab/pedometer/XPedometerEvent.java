package com.wonders.xlab.pedometer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * Created by hua on 16/10/12.
 */

public class XPedometerEvent {
    private static XPedometerEvent instance = null;

    private XPedometerEvent() {
    }

    public static XPedometerEvent getInstance() {
        synchronized (XPedometerEvent.class) {
            if (instance == null) {
                instance = new XPedometerEvent();
            }
        }
        return instance;
    }

    public String getActionOfEventBroadcast(@NonNull Context context) {
        return context.getPackageName() + XPedometerEventConstant.EVENT_BROADCAST_SUFFIX;
    }

    public XPedometerEventDataBean getEventDataBean(@NonNull Context context, @NonNull Intent intent) {
        if (!intent.getAction().equals(getActionOfEventBroadcast(context))) {
            throw new IllegalArgumentException("please pass the intent with action : " + getActionOfEventBroadcast(context));
        }
        XPedometerEventDataBean bean = new XPedometerEventDataBean();
        bean.setEvent(intent.getStringExtra(XPedometerEventConstant.EXTRA_KEY_EVENT));
        bean.setName(intent.getStringExtra(XPedometerEventConstant.EXTRA_KEY_NAME));
        bean.setTimeInMill(intent.getLongExtra(XPedometerEventConstant.EXTRA_KEY_TIME_IN_MILL, System.currentTimeMillis()));

        return bean;
    }

}
