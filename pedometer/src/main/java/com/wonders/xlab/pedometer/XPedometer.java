package com.wonders.xlab.pedometer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.wonders.xlab.pedometer.data.PMStepCountEntity;
import com.wonders.xlab.pedometer.db.PMStepCount;
import com.wonders.xlab.pedometer.ui.PMHomeActivity;

import java.util.List;

/**
 * Created by hua on 16/9/19.
 */

public class XPedometer {
    private static XPedometer instance = null;

    public static final int EVENT_PAGE_CREATE_HOME = 0;
    public static final int EVENT_PAGE_DESTROY_HOME = 1;
    public static final int EVENT_CLICK_MENU_SHARE = 3;

    private XPedometer() {
    }

    public static XPedometer getInstance() {
        synchronized (XPedometer.class) {
            if (instance == null) {
                instance = new XPedometer();
            }
        }
        return instance;
    }

    public void start(Activity activity) {
        activity.startActivity(getIntent(activity));
    }

    @NonNull
    private Intent getIntent(Context context) {
        return new Intent(context, PMHomeActivity.class);
    }

    public void start(android.app.Fragment fragment) {
        fragment.startActivity(getIntent(fragment.getActivity()));
    }

    public void start(android.support.v4.app.Fragment fragment) {
        fragment.startActivity(getIntent(fragment.getContext()));
    }

    public void updateLocalRecords(Context context, List<PMStepCountEntity> entityList) {
        PMStepCount.getInstance(context).insertOrReplaceWithBatchData(entityList);
    }

    public List<PMStepCountEntity> getAllLocalRecords(Context context) {
        return PMStepCount.getInstance(context).queryAll();
    }
}
