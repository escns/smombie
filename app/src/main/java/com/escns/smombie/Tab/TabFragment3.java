package com.escns.smombie.Tab;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.escns.smombie.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hyo99 on 2016-08-16.
 */

public class TabFragment3 extends Fragment {

    RelativeLayout layout1;

    PieChart chart1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab3, container, false);

        layout1 = (RelativeLayout) rootView.findViewById(R.id.tab3_charLayout1);

        chart1 = (PieChart) rootView.findViewById(R.id.tab3_chart1);

        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(30f, "실패"));
        entries.add(new PieEntry(70f, "성공"));

        PieDataSet set = new PieDataSet(entries, "Election Results");
        set.setColors(new int[] {Color.rgb(220,100,100), Color.rgb(200,200,200)});

        PieData data = new PieData(set);
        data.setValueTextSize(11);

        chart1.setData(data);
        chart1.setDescription("");
        chart1.setCenterText("성공률");
        chart1.setCenterTextSize(15);
        chart1.setTouchEnabled(false);
        chart1.invalidate(); // refresh

        return rootView;
    }
}
