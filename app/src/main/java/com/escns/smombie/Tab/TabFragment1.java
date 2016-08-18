package com.escns.smombie.Tab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
import java.util.List;


/**
 * Created by hyo99 on 2016-08-16.
 */

public class TabFragment1 extends Fragment {

    ImageView buttonOne;
    ImageView buttonTwo;
    ImageView buttonThree;

    RelativeLayout layout1;
    RelativeLayout layout2;
    RelativeLayout layout3;

    BarChart chart1;
    BarChart chart2;
    BarChart chart3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab1, container, false);

        layout1 = (RelativeLayout) rootView.findViewById(R.id.tab1_charLayout1);
        layout2 = (RelativeLayout) rootView.findViewById(R.id.tab1_charLayout2);
        layout3 = (RelativeLayout) rootView.findViewById(R.id.tab1_charLayout3);

        chart1 = (BarChart) rootView.findViewById(R.id.tab1_chart1);
        chart2 = (BarChart) rootView.findViewById(R.id.tab1_chart2);
        chart3 = (BarChart) rootView.findViewById(R.id.tab1_chart3);

        chartOne();
        chartTwo();
        chartThree();

        buttonOne = (ImageView) rootView.findViewById(R.id.tab1_button1);
        buttonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.INVISIBLE);
                layout3.setVisibility(View.INVISIBLE);
            }
        });

        buttonTwo = (ImageView) rootView.findViewById(R.id.tab1_button2);
        buttonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.setVisibility(View.INVISIBLE);
                layout2.setVisibility(View.VISIBLE);
                layout3.setVisibility(View.INVISIBLE);
            }
        });

        buttonThree = (ImageView) rootView.findViewById(R.id.tab1_button3);
        buttonThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.setVisibility(View.INVISIBLE);
                layout2.setVisibility(View.INVISIBLE);
                layout3.setVisibility(View.VISIBLE);
            }
        });

        buttonOne.callOnClick();


        return rootView;
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
     * 달을 기준으로 하는 BarChart
     */
    public void chartThree() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 20.000f));
        entries.add(new BarEntry(1, 10.000f));
        entries.add(new BarEntry(2, 30.000f));

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

}
