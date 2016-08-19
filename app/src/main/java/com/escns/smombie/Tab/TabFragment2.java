package com.escns.smombie.Tab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.escns.smombie.R;
import com.github.mikephil.charting.charts.HorizontalBarChart;
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

public class TabFragment2 extends Fragment {

    ImageView buttonOne;
    ImageView buttonTwo;

    RelativeLayout layout1;
    RelativeLayout layout2;

    HorizontalBarChart chart1;
    HorizontalBarChart chart2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab2, container, false);

        layout1 = (RelativeLayout) rootView.findViewById(R.id.tab2_charLayout1);
        layout2 = (RelativeLayout) rootView.findViewById(R.id.tab2_charLayout2);

        chart1 = (HorizontalBarChart) rootView.findViewById(R.id.tab2_chart1);
        chart2 = (HorizontalBarChart) rootView.findViewById(R.id.tab2_chart2);

        buttonOne = (ImageView) rootView.findViewById(R.id.tab2_button1);
        buttonTwo = (ImageView) rootView.findViewById(R.id.tab2_button2);

        chartOne();
        chartTwo();

        buttonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.INVISIBLE);
                buttonOne.setImageResource(R.drawable.bg_round_blue_gender);
                buttonTwo.setImageResource(R.drawable.bg_round_gray_age);
            }
        });

        buttonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.setVisibility(View.INVISIBLE);
                layout2.setVisibility(View.VISIBLE);
                buttonOne.setImageResource(R.drawable.bg_round_gray_gender);
                buttonTwo.setImageResource(R.drawable.bg_round_blue_age);
            }
        });

        buttonOne.callOnClick();

        return rootView;
    }

    /**
     * 남자평균을 기준으로 하는 HorizontalBarChart
     */
    public void chartOne() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 3.00f));
        entries.add(new BarEntry(1, 0.00f));
        entries.add(new BarEntry(2, 6.00f));

        BarDataSet set = new BarDataSet(entries, "이동거리");

        BarData data = new BarData(set);
        data.setBarWidth(1.0f); // set custom bar width
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
     * 남자평균을 기준으로 하는 HorizontalBarChart
     */
    public void chartTwo() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 5.00f));
        entries.add(new BarEntry(1, 0.00f));
        entries.add(new BarEntry(2, 16.00f));

        BarDataSet set = new BarDataSet(entries, "이동거리");

        BarData data = new BarData(set);
        data.setBarWidth(1.0f); // set custom bar width
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
     * 남자평균을 기준으로 하는 HorizontalBarChart의 가로축
     */
    public void initAxisOne() {

        XAxis xAxis = chart1.getXAxis();
        xAxis.setEnabled(false);

        YAxis leftYAxis = chart1.getAxisLeft();
        leftYAxis.setEnabled(false);

        YAxis rightYAxis = chart1.getAxisRight();
        rightYAxis.setEnabled(false);
    }

    /**
     * 남자평균을 기준으로 하는 HorizontalBarChart의 가로축
     */
    public void initAxisTwo() {

        XAxis xAxis = chart2.getXAxis();
        xAxis.setEnabled(false);

        YAxis leftYAxis = chart2.getAxisLeft();
        leftYAxis.setEnabled(false);

        YAxis rightYAxis = chart2.getAxisRight();
        rightYAxis.setEnabled(false);
    }
}
