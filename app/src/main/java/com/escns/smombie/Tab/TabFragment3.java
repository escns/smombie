package com.escns.smombie.Tab;

import android.content.Context;
import android.content.SharedPreferences;
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

    Context mContext;
    private SharedPreferences pref;

    RelativeLayout layout1;

    PieChart chart1;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab3, container, false);

        init();

        return rootView;
    }

    public void init() {

        mContext = getActivity().getApplicationContext();
        pref = mContext.getSharedPreferences(getResources().getString(R.string.app_name), mContext.MODE_PRIVATE);

        layout1 = (RelativeLayout) rootView.findViewById(R.id.tab3_charLayout1);

        chart1 = (PieChart) rootView.findViewById(R.id.tab3_chart1);

        chart();
    }

    public void chart() {List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(30f, "실패"));
        entries.add(new PieEntry(70f, "성공"));

        //entries.add(new PieEntry(pref.getInt("FAILCNT",0), "실패"));
        //entries.add(new PieEntry(pref.getInt("SUCCESSCNT",0), "성공"));

        PieDataSet set = new PieDataSet(entries, "Election Results");
        set.setColors(new int[] {getResources().getColor(R.color.tab_PieChart_Success), getResources().getColor(R.color.tab_PieChart_Fail)});

        PieData data = new PieData(set);
        data.setValueTextSize(11);

        chart1.setData(data);
        chart1.setDescription("");
        chart1.setCenterText("성공률");
        chart1.setCenterTextSize(15);
        chart1.setTouchEnabled(false);
        chart1.invalidate(); // refresh

    }
}
