package com.escns.smombie.Tab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.escns.smombie.DAO.Record;
import com.escns.smombie.Manager.DBManager;
import com.escns.smombie.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by hyo99 on 2016-08-16.
 */

public class TabFragment1 extends Fragment {

    Context mContext;
    DBManager mDbManager;
    List<Record> list;

    int mYear, mMonth, mDate, mTime;

    ImageView buttonOne, buttonTwo, buttonThree;

    RelativeLayout layout1, layout2, layout3;

    BarChart chart1, chart2, chart3;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab1, container, false);

        init();

        buttonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.GONE);
                buttonOne.setImageResource(R.drawable.bg_round_blue_day);
                buttonTwo.setImageResource(R.drawable.bg_round_gray_week);
                buttonThree.setImageResource(R.drawable.bg_round_gray_month);


            }
        });

        buttonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                layout3.setVisibility(View.GONE);
                buttonOne.setImageResource(R.drawable.bg_round_gray_day);
                buttonTwo.setImageResource(R.drawable.bg_round_blue_week);
                buttonThree.setImageResource(R.drawable.bg_round_gray_month);
            }
        });

        buttonThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.VISIBLE);
                buttonOne.setImageResource(R.drawable.bg_round_gray_day);
                buttonTwo.setImageResource(R.drawable.bg_round_gray_week);
                buttonThree.setImageResource(R.drawable.bg_round_blue_month);
            }
        });

        buttonOne.callOnClick();


        return rootView;
    }

    public void init() {
        mContext = getActivity().getApplicationContext();
        mDbManager = new DBManager(mContext);

        list = mDbManager.getRecord();

        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH)+1;
        mDate = c.get(Calendar.DATE);
        mTime = c.get(Calendar.HOUR_OF_DAY);

        Log.d("tag", "Year : " + mYear);
        Log.d("tag", "Month : " + mMonth);
        Log.d("tag", "Date : " + mDate);
        Log.d("tag", "Time : " + mTime);

        layout1 = (RelativeLayout) rootView.findViewById(R.id.tab1_charLayout1);
        layout2 = (RelativeLayout) rootView.findViewById(R.id.tab1_charLayout2);
        layout3 = (RelativeLayout) rootView.findViewById(R.id.tab1_charLayout3);

        chart1 = (BarChart) rootView.findViewById(R.id.tab1_chart1);
        chart2 = (BarChart) rootView.findViewById(R.id.tab1_chart2);
        chart3 = (BarChart) rootView.findViewById(R.id.tab1_chart3);

        buttonOne = (ImageView) rootView.findViewById(R.id.tab1_button1);
        buttonTwo = (ImageView) rootView.findViewById(R.id.tab1_button2);
        buttonThree = (ImageView) rootView.findViewById(R.id.tab1_button3);

        chartOne();
        chartTwo();
        chartThree();
    }

    /**
     * 일을 기준으로 하는 BarChart
     */
    public void chartOne() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 2.0f));
        entries.add(new BarEntry(1, 1.0f));
        entries.add(new BarEntry(2, 3.0f));
        entries.add(new BarEntry(3, 5.0f));

        //entries.add(new BarEntry(0, getDate(0)));
        //entries.add(new BarEntry(1, getDate(6)));
        //entries.add(new BarEntry(2, getDate(12)));
        //entries.add(new BarEntry(3, getDate(18)));

        BarDataSet set = new BarDataSet(entries, "이동거리");

        BarData data = new BarData(set);
        data.setBarWidth(0.5f); // set custom bar width
        data.setValueTextSize(10f);

        initAxisOne();

        chart1.setData(data);
        chart1.setFitBars(true); // make the x-axis fit exactly all bars
        chart1.setTouchEnabled(false); // 차트의 막대 선택 여부
        chart1.setScaleEnabled(false); // 차트의 x,y축 확대 여부
        chart1.setDescription(""); // 차트 우측하단의 설명문구
        chart1.invalidate(); // refresh
    }

    /**
     * 일을 기준으로 하는 BarChart의 가로축
     */
    public void initAxisOne() {

        final String[] xProperties = new String[] {"0:00~6:00", "6:00~12:00", "12:00~18:00", "18:00~24:00"};
        AxisValueFormatter formatter = new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xProperties[(int) value];
            }

            // we don't draw numbers, so no decimal digits needed
            @Override
            public int getDecimalDigits() {  return 0; }
        };

        XAxis xAxis = chart1.getXAxis();
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineWidth(3f);
        xAxis.setTextSize(10f);
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1

        YAxis leftYAxis = chart1.getAxisLeft();
        leftYAxis.setDrawGridLines(false);
        leftYAxis.setDrawLabels(false);
        leftYAxis.setEnabled(false);

        YAxis rightYAxis = chart1.getAxisRight();
        rightYAxis.setEnabled(false);
    }

    /**
     * 주를 기준으로 하는 BarChart
     */
    public void chartTwo() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 20.000f));
        entries.add(new BarEntry(1, 10.000f));
        entries.add(new BarEntry(2, 40.000f));
        entries.add(new BarEntry(3, 50.000f));
        entries.add(new BarEntry(4, 40.000f));
        entries.add(new BarEntry(5, 30.000f));
        entries.add(new BarEntry(6, 50.000f));

        //entries.add(new BarEntry(0, getWeek(mDate-6)));
        //entries.add(new BarEntry(1, getWeek(mDate-5)));
        //entries.add(new BarEntry(2, getWeek(mDate-4)));
        //entries.add(new BarEntry(3, getWeek(mDate-3)));
        //entries.add(new BarEntry(4, getWeek(mDate-2)));
        //entries.add(new BarEntry(5, getWeek(mDate-1)));
        //entries.add(new BarEntry(6, getWeek(mDate)));

        BarDataSet set = new BarDataSet(entries, "이동거리");

        BarData data = new BarData(set);
        data.setBarWidth(0.5f); // set custom bar width
        data.setValueTextSize(10f);

        initAxisTwo();

        chart2.setData(data);
        chart2.setFitBars(true); // make the x-axis fit exactly all bars
        chart2.setTouchEnabled(false); // 차트의 막대 선택 여부
        chart2.setScaleEnabled(false); // 차트의 x,y축 확대 여부
        chart2.setDescription(""); // 차트 우측하단의 설명문구
        chart2.invalidate(); // refresh
    }

    /**
     * 주를 기준으로 하는 BarChart의 가로축
     */
    public void initAxisTwo() {

        final String[] xProperties = new String[] { "8/11", "8/12", "8/13", "8/14", "8/15", "8/16", "8/17" };
        AxisValueFormatter formatter = new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xProperties[(int) value];
            }

            // we don't draw numbers, so no decimal digits needed
            @Override
            public int getDecimalDigits() {  return 0; }
        };

        XAxis xAxis = chart2.getXAxis();
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineWidth(3f);
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1

        YAxis leftYAxis = chart2.getAxisLeft();
        leftYAxis.setDrawGridLines(false);
        leftYAxis.setDrawLabels(false);
        leftYAxis.setEnabled(false);

        YAxis rightYAxis = chart2.getAxisRight();
        rightYAxis.setEnabled(false);

    }

    /**
     * 달을 기준으로 하는 BarChart
     */
    public void chartThree() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 20.000f));
        entries.add(new BarEntry(1, 10.000f));
        entries.add(new BarEntry(2, 30.000f));

       //entries.add(new BarEntry(0, getMonth(mMonth-2)));
       //entries.add(new BarEntry(1, getMonth(mMonth-1)));
       //entries.add(new BarEntry(2, getMonth(mMonth)));

        BarDataSet set = new BarDataSet(entries, "이동거리");

        BarData data = new BarData(set);
        data.setBarWidth(0.5f); // set custom bar width
        data.setValueTextSize(10f);

        initAxisThree();

        chart3.setData(data);
        chart3.setFitBars(true); // make the x-axis fit exactly all bars
        chart3.setTouchEnabled(false); // 차트의 막대 선택 여부
        chart3.setScaleEnabled(false); // 차트의 x,y축 확대 여부
        chart3.setDescription(""); // 차트 우측하단의 설명문구
        chart3.invalidate(); // refresh
    }

    /**
     *달을 기준으로 하는 BarChart의 가로축
     */
    public void initAxisThree() {

        final String[] xProperties = new String[] { "6월", "7월", "8월"};
        AxisValueFormatter formatter = new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xProperties[(int) value];
            }

            // we don't draw numbers, so no decimal digits needed
            @Override
            public int getDecimalDigits() {  return 0; }
        };

        XAxis xAxis = chart3.getXAxis();
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineWidth(3f);
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1

        YAxis leftYAxis = chart3.getAxisLeft();
        leftYAxis.setDrawGridLines(false);
        leftYAxis.setDrawLabels(false);
        leftYAxis.setEnabled(false);

        YAxis rightYAxis = chart3.getAxisRight();
        rightYAxis.setEnabled(false);

    }

    public int getDate(int time) {
        int result = 0;

        for(int i=0; i<list.size(); i++) {
            if(list.get(i).getmYear() == mYear &&
                    list.get(i).getmMonth() == mMonth &&
                    list.get(i).getmDay() == mDate) {
                if (time >= 0 && time < 6) {
                    if (list.get(i).getmHour() >= 0 && list.get(i).getmHour() < 6) {
                        result += list.get(i).getmDist();
                    }
                } else if (time >= 6 && time < 12) {
                    if (list.get(i).getmHour() >= 6 && list.get(i).getmHour() < 12) {
                        result += list.get(i).getmDist();
                    }
                }
            } else if (time >= 12 && time < 18) {
                if (list.get(i).getmHour() >=12 && list.get(i).getmHour() < 18) {
                    result += list.get(i).getmDist();
                }
            } else {
                if (list.get(i).getmHour() >= 18 && list.get(i).getmHour() < 24) {
                    result += list.get(i).getmDist();
                }
            }
        }

        return result;
    }

    public int getWeek(int date) {
        int result = 0;

        int year = mYear;
        int month = mMonth;

        // 날짜 예외처리 --> 달이 바뀌는 경우
        if(date <= 0) {
            month--;
            if(month == 0) {
                year--;
                month = 12;
            }
            else {
                if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                    date = 31+date;
                } else if (month == 4 || month == 6 || month == 7 || month == 11) {
                    date = 30+date;;
                } else {
                    if (year % 4 == 0) {
                        date = 29+date;;
                    } else {
                        date = 28+date;;
                    }
                }
            }
        }

        for(int i=0; i<list.size(); i++) {
            if(list.get(i).getmYear() == year &&
                    list.get(i).getmMonth() == month &&
                    list.get(i).getmDay() == date ) {
                result += list.get(i).getmDist();
            }
        }
        return result;
    }

    public int getMonth(int month) {
        int result = 0;

        int year = mYear;

        // 날짜 예외처리 --> 달이 바뀌는 경우
        if(month <= 0) {
            year--;
            if(month == 0) {
                month = 12;
            }
            else {
                month = 11;
            }
        }

        for(int i=0; i<list.size(); i++) {
            if(list.get(i).getmYear() == year &&
                    list.get(i).getmMonth() == month) {
                result += list.get(i).getmDist();
            }
        }
        return result;
    }
}
