package com.wonders.xlab.pedometer.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.wonders.xlab.pedometer.R;

import java.util.Calendar;

/**
 * Created by hua on 16/9/8.
 */

public class CalendarPopupWindow extends RelativePopupWindow {

    private MaterialCalendarView mCalendarView;
    private MaterialCalendarView.StateBuilder mStateBuilder;


    private OnDateSelectedListener mOnDateSelectedListener;

    public CalendarPopupWindow(Context context) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.pm_calendar_popup, null, false);
        mCalendarView = (MaterialCalendarView) contentView.findViewById(R.id.calendarView);
        mCalendarView.setDateSelected(Calendar.getInstance(), true);
        mStateBuilder = mCalendarView.state().edit();
        mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if (null != mOnDateSelectedListener) {
                    mOnDateSelectedListener.onDateSelected(widget, date, selected);
                }
            }
        });
        setContentView(contentView);
    }

    @Override
    public void setContentView(View contentView) {
        if (contentView != null) {
            super.setContentView(contentView);
            contentView.setFocusable(true);
            setTouchable(true);
            contentView.setFocusableInTouchMode(true);
            setFocusable(true);
            setBackgroundDrawable(new ColorDrawable(0x00000000));
            setOutsideTouchable(true);
            setAnimationStyle(R.style.PopupAnimation);
            contentView.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            dismiss();
                            return true;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }
    }

    public void setupCalendarView(TitleFormatter titleFormatter, CalendarMode calendarMode) {
        mCalendarView.setTitleFormatter(titleFormatter);
        mStateBuilder.setCalendarDisplayMode(calendarMode).commit();
    }

    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        mOnDateSelectedListener = onDateSelectedListener;
    }
}
