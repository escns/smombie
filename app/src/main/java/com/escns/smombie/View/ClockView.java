package com.escns.smombie.View;

import android.os.Handler;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016-08-19.
 */

public class ClockView {
    private int hours;
    private int minutes;
    private int seconds;

    private TextView textViewAmpm;
    private TextView textViewTime;
    private TextView textViewDate;

    private Timer clockTimer;
    private final TimerTask clockTask = new TimerTask() {
        @Override
        public void run() {
            mHandler.post(mUpdateResults);
        }
    };

    final Handler mHandler = new Handler();
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            update();
        }
    };

    public ClockView(TextView textViewAmpm, TextView textViewTime, TextView textViewDate) {
        this.textViewAmpm = textViewAmpm;
        this.textViewTime = textViewTime;
        this.textViewDate = textViewDate;

        init();
    }


    private void update() {
        seconds++;

        if (seconds >= 60) {
            seconds = 0;
            if (minutes < 59) {
                minutes++;
            } else if (hours < 23) {
                minutes = 0;
                hours++;
            } else {
                minutes = 0;
                hours = 0;
            }
        }
        if(hours>=12) {
            textViewAmpm.setText("PM");
        } else {
            textViewAmpm.setText("AM");
        }
        textViewTime.setText(String.format("%02d:%02d", hours, minutes));
        Calendar calendar = Calendar.getInstance();
        textViewDate.setText(String.format("%1$tb %1$td일 %1$tA", calendar));
        //String.format("%1$tA %1$tb %1$td %1$tY at %1$tI:%1$tM %1$Tp", calendar)
    }

    private void init() {
        clockTimer = new Timer();

        Calendar mCalendar = Calendar.getInstance();
        hours = mCalendar.get(Calendar.HOUR_OF_DAY);
        minutes = mCalendar.get(Calendar.MINUTE);
        seconds = mCalendar.get(Calendar.SECOND);

        clockTimer.scheduleAtFixedRate(clockTask, 0, 1000);
    }
}
