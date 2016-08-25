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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by hyo99 on 2016-08-16.
 */

public class TabFragment1 extends Fragment {

    private Context mContext;
    private DBManager mDbManager;
    private List<Record> list;

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

        //list = new ArrayList<>();
        //
        //for(int i=0; i<mDbManager.getRecord().size(); i++)

        list = mDbManager.getRecord();

        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH)+1;
        mDate = c.get(Calendar.DATE);
        mTime = c.get(Calendar.HOUR_OF_DAY);

        layout1 = (RelativeLayout) rootView.findViewById(R.id.tab1_charLayout1);
        layout2 = (RelativeLayout) rootView.findViewById(R.id.tab1_charLayout2);
        layout3 = (RelativeLayout) rootView.findViewById(R.id.tab1_charLayout3);

        chart1 = (BarChart) rootView.findViewById(R.id.tab1_chart1);
        chart2 = (BarChart) rootView.findViewById(R.id.tab1_chart2);
        chart3 = (BarChart) rootView.findViewById(R.id.tab1_chart3);

        buttonOne = (ImageView) rootView.findViewById(R.id.tab1_button1);
        buttonTwo = (ImageView) rootView.findViewById(R.id.tab1_button2);
        buttonThree = (ImageView) rootView.findViewById(R.id.tab1_button3);

        //Log.d("tag", "사이즈 : " + mDbManager.getRecord().size());
        //Log.d("tag", "사이즈 : " + list.size());
        ////for(int i=0; i< list.size(); i++) {
        //    Log.d("tag", "ID : " + list.get(0).getmIdInt());
        //    Log.d("tag", "Year : " + list.get(0).getmYear());
        //    Log.d("tag", "Month : " + list.get(0).getmMonth());
        //    Log.d("tag", "Date : " + list.get(0).getmDay());
        //    Log.d("tag", "Time : " + list.get(0).getmHour());
        //    Log.d("tag", "Dist : " + list.get(0).getmDist());
        ////}

        if(list != null) {
            chartOne();
            chartTwo();
            chartThree();
        }
    }

    /**
     * 일을 기준으로 하는 BarChart
     */
    public void chartOne() {
        List<BarEntry> entries = new ArrayList<>();
        //entries.add(new BarEntry(0, 2.0f));
        //entries.add(new BarEntry(1, 1.0f));
        //entries.add(new BarEntry(2, 3.0f));
        //entries.add(new BarEntry(3, 5.0f));

        entries.add(new BarEntry(0, getDataChart1(0)));
        entries.add(new BarEntry(1, getDataChart1(6)));
        entries.add(new BarEntry(2, getDataChart1(12)));
        entries.add(new BarEntry(3, getDataChart1(18)));

        BarDataSet set = new BarDataSet(entries, "이동거리");

        BarData data = new BarData(set);
        data.setBarWidth(0.5f); // set custom bar width
        data.setValueTextSize(13f);
        data.setValueFormatter(new MyValueFormatter());

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
        //entries.add(new BarEntry(0, 20.000f));
        //entries.add(new BarEntry(1, 10.000f));
        //entries.add(new BarEntry(2, 40.000f));
        //entries.add(new BarEntry(3, 50.000f));
        //entries.add(new BarEntry(4, 40.000f));
        //entries.add(new BarEntry(5, 30.000f));
        //entries.add(new BarEntry(6, 50.000f));

        entries.add(new BarEntry(0, getDataChart2(mDate-6)));
        entries.add(new BarEntry(1, getDataChart2(mDate-5)));
        entries.add(new BarEntry(2, getDataChart2(mDate-4)));
        entries.add(new BarEntry(3, getDataChart2(mDate-3)));
        entries.add(new BarEntry(4, getDataChart2(mDate-2)));
        entries.add(new BarEntry(5, getDataChart2(mDate-1)));
        entries.add(new BarEntry(6, getDataChart2(mDate)));

        BarDataSet set = new BarDataSet(entries, "이동거리");

        BarData data = new BarData(set);
        data.setBarWidth(0.5f); // set custom bar width
        data.setValueTextSize(10f);
        data.setValueFormatter(new MyValueFormatter());

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

        final String[] xProperties = new String[] {
                getCalendarDate(mDate-6)[1] +"/"+ getCalendarDate(mDate-6)[2],
                getCalendarDate(mDate-5)[1] +"/"+ getCalendarDate(mDate-5)[2],
                getCalendarDate(mDate-4)[1] +"/"+ getCalendarDate(mDate-4)[2],
                getCalendarDate(mDate-3)[1] +"/"+ getCalendarDate(mDate-3)[2],
                getCalendarDate(mDate-2)[1] +"/"+ getCalendarDate(mDate-2)[2],
                getCalendarDate(mDate-1)[1] +"/"+ getCalendarDate(mDate-1)[2],
                getCalendarDate(mDate)[1] +"/"+ getCalendarDate(mDate)[2] };
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
        //entries.add(new BarEntry(0, 300));
        //entries.add(new BarEntry(1, 100));
        //entries.add(new BarEntry(2, 130));

       entries.add(new BarEntry(0, getDataChart3(mMonth-2)));
       entries.add(new BarEntry(1, getDataChart3(mMonth-1)));
       entries.add(new BarEntry(2, getDataChart3(mMonth)));

        BarDataSet set = new BarDataSet(entries, "이동거리");

        BarData data = new BarData(set);
        data.setBarWidth(0.5f); // set custom bar width
        data.setValueTextSize(13f);
        data.setValueFormatter(new MyValueFormatter());

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

        final String[] xProperties = new String[] {
                getCalendarMonth(mMonth-2)[1]+"월",
                getCalendarMonth(mMonth-1)[1]+"월",
                getCalendarMonth(mMonth)[1]+"월"};
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

    public int getDataChart1(int time) {
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

    public int getDataChart2(int input) {
        int result = 0;

        int year = getCalendarDate(input)[0];
        int month = getCalendarDate(input)[1];
        int date = getCalendarDate(input)[2];

        for(int i=0; i<list.size(); i++) {
            if(list.get(i).getmYear() == year &&
                    list.get(i).getmMonth() == month &&
                    list.get(i).getmDay() == date ) {
                result += list.get(i).getmDist();
            }
        }
        return result;
    }

    public int getDataChart3(int input) {
        int result = 0;

        int year = getCalendarMonth(input)[0];
        int month = getCalendarMonth(input)[1];

        for(int i=0; i<list.size(); i++) {
            if(list.get(i).getmYear() == year &&
                    list.get(i).getmMonth() == month) {
                result += list.get(i).getmDist();
            }
        }
        return result;
    }

    public int[] getCalendarDate(int date) {

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

        int result[] = {year, month, date};

        return result;
    }

    public int[] getCalendarMonth(int month) {

        int year = mYear;

        // 날짜 예외처리 --> 연도가 바뀌는 경우
        if(month <= 0) {
            year--;
            if(month == 0) {
                month = 12;
            }
            else {
                month = 11;
            }
        }

        int result[] = {year, month};

        return result;
    }

    /**
     * float형으로 나오던 바의 값을 int형으로 변환
     */
    public class MyValueFormatter implements ValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return "" + ((int) value);
        }
    }
}
