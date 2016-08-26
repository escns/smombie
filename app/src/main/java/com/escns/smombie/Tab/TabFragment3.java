package com.escns.smombie.Tab;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    Context mContext; // MainActiviy의 context를 받아올 객체
    private SharedPreferences pref; // 파일에 있는 정보를 불러오기위한 객체

    RelativeLayout layout1; // 레이아웃 : 성공률

    PieChart chart1; // 그래프 : 성공률

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab3, container, false);

        init(); // 초기화

        return rootView;
    }

    /**
     * 초기화 함수
     */
    public void init() {

        mContext = getActivity().getApplicationContext(); // MainActivity의 context를 받아옴

        // 'smombie'란 파일을 읽음
        pref = mContext.getSharedPreferences(getResources().getString(R.string.app_name), mContext.MODE_PRIVATE);

        layout1 = (RelativeLayout) rootView.findViewById(R.id.tab3_charLayout1);

        chart1 = (PieChart) rootView.findViewById(R.id.tab3_chart1);
        chart1.setTouchEnabled(false);

        // 데이터가 없으면 그래프를 출력하지 않는다
        if(pref.getInt("FAILCNT",999) != 0 || pref.getInt("SUCCESSCNT",999) != 0) {
            chart();
        }
    }

    /**
     * 성공률을 나타내는 PieChart
     */
    public void chart() {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(pref.getInt("FAILCNT",999), "실패"));
        entries.add(new PieEntry(pref.getInt("SUCCESSCNT",999), "성공"));

        int failCnt = pref.getInt("FAILCNT", 999);
        int successCnt = pref.getInt("SUCCESSCNT", 999);
        float percent;
        if (successCnt == 0)
            percent = 0;
        else
            percent = (float) successCnt / ((float)failCnt + (float)successCnt) * 100;

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
