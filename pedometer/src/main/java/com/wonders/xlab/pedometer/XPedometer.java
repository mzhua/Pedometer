package com.wonders.xlab.pedometer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.wonders.xlab.pedometer.data.PMStepEntity;
import com.wonders.xlab.pedometer.localdata.PMStepLocalDataSource;
import com.wonders.xlab.pedometer.ui.PMHomeActivity;

import java.util.List;

/**
 * Created by hua on 16/9/19.
 */

public class XPedometer {
    private static XPedometer instance = null;

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

    public void updateLocalRecords(Context context, List<PMStepEntity> entityList) {
        PMStepLocalDataSource.get(context).insertOrReplaceWithBatchData(entityList);
    }

    public List<PMStepEntity> getAllLocalRecords(Context context) {
        return PMStepLocalDataSource.get(context).queryAll();
    }
}
