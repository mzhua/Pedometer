package com.wonders.xlab.pedometer.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.wonders.xlab.pedometer.XPedometer;
import com.wonders.xlab.pedometer.XPedometerEvent;
import com.wonders.xlab.pedometer.service.StepCounterService;
import com.wonders.xlab.pedometer.widget.PMDailyRingChart;

public class MainActivity extends AppCompatActivity {
    PMDailyRingChart mPMDailyRingChart;

    private BroadcastReceiver mEventBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPMDailyRingChart = (PMDailyRingChart) findViewById(R.id.walkChart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                XPedometer.getInstance().start(MainActivity.this);
            }
        });

        startService(new Intent(this, StepCounterService.class));

        IntentFilter filter = new IntentFilter(XPedometerEvent.getInstance().getActionOfEventBroadcast(this));
        mEventBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, XPedometerEvent.getInstance().getEventDataBean(context, intent).getEvent() + ":" + XPedometerEvent.getInstance().getEventDataBean(context, intent).getName(), Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(mEventBroadcastReceiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void goToPedometer(View view) {
        Toast.makeText(this, "XPedometer.getInstance().getAllLocalRecords().size():" + XPedometer.getInstance().getAllLocalRecords(this).size(), Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mEventBroadcastReceiver);
    }
}
