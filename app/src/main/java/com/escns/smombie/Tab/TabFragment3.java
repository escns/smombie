package com.escns.smombie.Tab;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.escns.smombie.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

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


        Log.d("tag", "Tab3_Chart Fail " + pref.getInt("FAILCNT", 999));
        Log.d("tag", "Tab3_Chart Success " + pref.getInt("SUCCESSCNT", 999));

        if(pref.getInt("FAILCNT",999) != 0 && pref.getInt("SUCCESSCNT",999) != 0) {
            chart();
        }
    }

    public void chart() {
        List<PieEntry> entries = new ArrayList<>();

        //entries.add(new PieEntry(1, "실패"));
        //entries.add(new PieEntry(3, "성공"));
        entries.add(new PieEntry(pref.getInt("FAILCNT",999), "실패"));
        entries.add(new PieEntry(pref.getInt("SUCCESSCNT",999), "성공"));

        int failCnt = pref.getInt("FAILCNT", 999);
        int successCnt = pref.getInt("SUCCESSCNT", 999);
        float percent;
        if (successCnt == 0)
            percent = 0;
        else
            percent = (float) successCnt / ((float)failCnt + (float)successCnt) * 100;

        Log.d("tag", "Tab3_Chart percent " + percent);


        PieDataSet set = new PieDataSet(entries, "Election Results");
        set.setColors(new int[]{getResources().getColor(R.color.tab_PieChart_Success), getResources().getColor(R.color.tab_PieChart_Fail)});

        PieData data = new PieData(set);
        data.setValueTextSize(15);
        data.setValueFormatter(new MyValueFormatter());

        chart1.setData(data);
        chart1.setDescription("");
        chart1.setCenterText("성공률\n" + percent + "%");
        chart1.setCenterTextSize(15);
        chart1.setTouchEnabled(false);
        chart1.invalidate(); // refresh
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
