package com.wonders.xlab.pedometer.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.wonders.xlab.pedometer.XPedometer;
import com.wonders.xlab.pedometer.XPedometerEvent;
import com.wonders.xlab.pedometer.data.PMStepEntity;
import com.wonders.xlab.pedometer.service.StepCounterService;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BroadcastReceiver mEventBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        registerEventReceiver();
        startService(new Intent(this, StepCounterService.class));
    }

    /**
     *
     */
    private void registerEventReceiver() {
        IntentFilter filter = new IntentFilter(XPedometerEvent.getInstance().getActionOfEventBroadcast(this));
        mEventBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, XPedometerEvent.getInstance().getEventDataBean(context, intent).getEvent() + ":" + XPedometerEvent.getInstance().getEventDataBean(context, intent).getName(), Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(mEventBroadcastReceiver, filter);
    }

    public void backupPedometer(View view) {
        List<PMStepEntity> allLocalRecords = XPedometer.getInstance().getAllLocalRecords(this);
        int size = 0;
        if (null != allLocalRecords) {
            size = allLocalRecords.size();
        }
        Toast.makeText(this, "Records size : " + size, Toast.LENGTH_SHORT).show();
    }

    public void goToPedometer(View view) {
        XPedometer.getInstance().start(MainActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mEventBroadcastReceiver);
    }
}
