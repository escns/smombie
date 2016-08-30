package com.escns.smombie.Tab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    private Context mContext; // MainActiviy의 context를 받아올 객체
    private DBManager mDbManager; // Local DB에 접근하기 위한 객체
    private List<Record> list; // DB에서 Recod를 가져오기 위한 List

    int mYear, mMonth, mDate, mTime; // 현재 연도, 월, 일, 시간

    ImageView buttonOne, buttonTwo, buttonThree; // 버튼 : 일/주/달

    RelativeLayout layout1, layout2, layout3; // 레이아웃 : 날짜별/관계별/성과별

    BarChart chart1, chart2, chart3; // 그래프 : 일/주/달

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab1, container, false);

        init(); // 초기화

        // 일(버튼)을 눌렀을 때 일(그래프)를 출력
        buttonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.GONE);
                buttonOne.setImageResource(R.drawable.bg_round_blue_day);
                buttonTwo.setImageResource(R.drawable.bg_round_gray_week);
                buttonThree.setImageResource(R.drawable.bg_round_gray_month);
                chart1.animateY(1200);
            }
        });

        // 주(버튼)을 눌렀을 때 주(그래프)를 출력
        buttonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                layout3.setVisibility(View.GONE);
                buttonOne.setImageResource(R.drawable.bg_round_gray_day);
                buttonTwo.setImageResource(R.drawable.bg_round_blue_week);
                buttonThree.setImageResource(R.drawable.bg_round_gray_month);
                chart2.animateY(1200);
            }
        });

        // 달(버튼)을 눌렀을 때 달(그래프)를 출력
        buttonThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.VISIBLE);
                buttonOne.setImageResource(R.drawable.bg_round_gray_day);
                buttonTwo.setImageResource(R.drawable.bg_round_gray_week);
                buttonThree.setImageResource(R.drawable.bg_round_blue_month);
                chart3.animateY(1200);
            }
        });

        buttonOne.callOnClick(); // 일(버튼) 클릭

        return rootView;
    }

    /**
     * 초기화 함수
     */
    public void init() {

        mContext = getActivity().getApplicationContext(); // MainActivity의 context를 받아옴
        mDbManager = new DBManager(mContext); // DB 생성

        list = null;
        list = new ArrayList<>();
        list = mDbManager.getRecord(); // Local DB에서 record list를 받아옴

        Calendar c = Calendar.getInstance();    // 날짜 객체
        mYear = c.get(Calendar.YEAR);           // 연도
        mMonth = c.get(Calendar.MONTH) + 1;     // 달
        mDate = c.get(Calendar.DATE);           // 일
        mTime = c.get(Calendar.HOUR_OF_DAY);    // 시간

        buttonOne = (ImageView) rootView.findViewById(R.id.tab1_button1);
        buttonTwo = (ImageView) rootView.findViewById(R.id.tab1_button2);
        buttonThree = (ImageView) rootView.findViewById(R.id.tab1_button3);

        layout1 = (RelativeLayout) rootView.findViewById(R.id.tab1_charLayout1);
        layout2 = (RelativeLayout) rootView.findViewById(R.id.tab1_charLayout2);
        layout3 = (RelativeLayout) rootView.findViewById(R.id.tab1_charLayout3);

        chart1 = (BarChart) rootView.findViewById(R.id.tab1_chart1);
        chart2 = (BarChart) rootView.findViewById(R.id.tab1_chart2);
        chart3 = (BarChart) rootView.findViewById(R.id.tab1_chart3);

        // 그래프를 터치하는 기능 비활성화
        chart1.setTouchEnabled(false);
        chart2.setTouchEnabled(false);
        chart3.setTouchEnabled(false);

        // 데이터가 없으면 그래프를 출력하지 않는다
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
        entries.add(new BarEntry(0, getDataChart1(0)));
        entries.add(new BarEntry(1, getDataChart1(6)));
        entries.add(new BarEntry(2, getDataChart1(12)));
        entries.add(new BarEntry(3, getDataChart1(18)));

        // 값 세팅
        BarDataSet set = new BarDataSet(entries, "이동거리");

        // 세팅한 값을 바에 입력
        BarData data = new BarData(set);
        data.setBarWidth(0.5f);             // 바 굵기
        data.setValueTextSize(13f);         // 바의 텍스트값 크기
        data.setValueFormatter(new MyValueFormatter()); // 값을 float형에서 int형으로 변환

        initAxisOne(); // 그래프의 x축, y축 설정

        chart1.setData(data); // 그래프에 데이트 입력
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

        // X축의 value 값들 설정
        final String[] xProperties = new String[]{"0:00~6:00", "6:00~12:00", "12:00~18:00", "18:00~24:00"};
        AxisValueFormatter formatter = new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xProperties[(int) value];
            }

            // we don't draw numbers, so no decimal digits needed
            @Override
            public int getDecimalDigits() {
                return 0;
            }
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
        entries.add(new BarEntry(0, getDataChart2(mDate - 6)));
        entries.add(new BarEntry(1, getDataChart2(mDate - 5)));
        entries.add(new BarEntry(2, getDataChart2(mDate - 4)));
        entries.add(new BarEntry(3, getDataChart2(mDate - 3)));
        entries.add(new BarEntry(4, getDataChart2(mDate - 2)));
        entries.add(new BarEntry(5, getDataChart2(mDate - 1)));
        entries.add(new BarEntry(6, getDataChart2(mDate)));

        // 값 세팅
        BarDataSet set = new BarDataSet(entries, "이동거리");

        // 세팅한 값을 바에 입력
        BarData data = new BarData(set);
        data.setBarWidth(0.5f);             // 바 굵기
        data.setValueTextSize(10f);         // 바의 텍스트값 크기
        data.setValueFormatter(new MyValueFormatter()); // 값을 float형에서 int형으로 변환

        initAxisTwo(); // 그래프의 x축, y축 설정

        chart2.setData(data); // 그래프에 데이트 입력
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

        // X축의 value 값들 설정
        final String[] xProperties = new String[]{
                getCalendarDate(mDate - 6)[1] + "/" + getCalendarDate(mDate - 6)[2],
                getCalendarDate(mDate - 5)[1] + "/" + getCalendarDate(mDate - 5)[2],
                getCalendarDate(mDate - 4)[1] + "/" + getCalendarDate(mDate - 4)[2],
                getCalendarDate(mDate - 3)[1] + "/" + getCalendarDate(mDate - 3)[2],
                getCalendarDate(mDate - 2)[1] + "/" + getCalendarDate(mDate - 2)[2],
                getCalendarDate(mDate - 1)[1] + "/" + getCalendarDate(mDate - 1)[2],
                getCalendarDate(mDate)[1] + "/" + getCalendarDate(mDate)[2]};
        AxisValueFormatter formatter = new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xProperties[(int) value];
            }

            // we don't draw numbers, so no decimal digits needed
            @Override
            public int getDecimalDigits() {
                return 0;
            }
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
        entries.add(new BarEntry(0, getDataChart3(mMonth - 2)));
        entries.add(new BarEntry(1, getDataChart3(mMonth - 1)));
        entries.add(new BarEntry(2, getDataChart3(mMonth)));

        // 값 세팅
        BarDataSet set = new BarDataSet(entries, "이동거리");

        // 세팅한 값을 바에 입력
        BarData data = new BarData(set);
        data.setBarWidth(0.5f);             // 바 굵기
        data.setValueTextSize(13f);         // 바의 텍스트값 크기
        data.setValueFormatter(new MyValueFormatter()); // 값을 float형에서 int형으로 변환

        initAxisThree(); // 그래프의 x축, y축 설정

        chart3.setData(data); // 그래프에 데이트 입력
        chart3.setFitBars(true); // make the x-axis fit exactly all bars
        chart3.setTouchEnabled(false); // 차트의 막대 선택 여부
        chart3.setScaleEnabled(false); // 차트의 x,y축 확대 여부
        chart3.setDescription(""); // 차트 우측하단의 설명문구
        chart3.invalidate(); // refresh
    }

    /**
     * 달을 기준으로 하는 BarChart의 가로축
     */
    public void initAxisThree() {

        // X축의 value 값들 설정
        final String[] xProperties = new String[]{
                getCalendarMonth(mMonth - 2)[1] + "월",
                getCalendarMonth(mMonth - 1)[1] + "월",
                getCalendarMonth(mMonth)[1] + "월"};
        AxisValueFormatter formatter = new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xProperties[(int) value];
            }

            // we don't draw numbers, so no decimal digits needed
            @Override
            public int getDecimalDigits() {
                return 0;
            }
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

    /**
     * 일 그래프의 데이터를 출력
     * @param time 현재 시간
     * @return 시간에 맞는 결과값 반환
     */
    public int getDataChart1(int time) {
        int result = 0;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getmYear() == mYear &&
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
                } else if (time >= 12 && time < 18) {
                    if (list.get(i).getmHour() >= 12 && list.get(i).getmHour() < 18) {
                        result += list.get(i).getmDist();
                    }
                } else {
                    if (list.get(i).getmHour() >= 18 && list.get(i).getmHour() < 24) {
                        result += list.get(i).getmDist();
                    }
                }
            }
        }

        return result;
    }

    /**
     * 주 그래프의 데이터를 출력
     * @param input 현재 일
     * @return 일수에 맞는 결과값 반환
     */
    public int getDataChart2(int input) {
        int result = 0;

        int year = getCalendarDate(input)[0];
        int month = getCalendarDate(input)[1];
        int date = getCalendarDate(input)[2];

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getmYear() == year &&
                    list.get(i).getmMonth() == month &&
                    list.get(i).getmDay() == date) {
                result += list.get(i).getmDist();
            }
        }
        return result;
    }

    /**
     * 달 그래프의 데이터를 출력
     * @param input 현재 달
     * @return 달수에 맞는 결과값 반환
     */
    public int getDataChart3(int input) {
        int result = 0;

        int year = getCalendarMonth(input)[0];
        int month = getCalendarMonth(input)[1];

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getmYear() == year &&
                    list.get(i).getmMonth() == month) {
                result += list.get(i).getmDist();
            }
        }
        return result;
    }

    /**
     * 일수를 변경하면서 달이 바꼈을 때 예외처리
     * @param date 일
     * @return 바뀐 날짜
     */
    public int[] getCalendarDate(int date) {

        int year = mYear;
        int month = mMonth;

        // 날짜 예외처리 --> 달이 바뀌는 경우
        if (date <= 0) {
            month--;
            if (month == 0) {
                year--;
                month = 12;
            } else {
                if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                    date = 31 + date;
                } else if (month == 4 || month == 6 || month == 7 || month == 11) {
                    date = 30 + date;
                    ;
                } else {
                    if (year % 4 == 0) {
                        date = 29 + date;
                        ;
                    } else {
                        date = 28 + date;
                        ;
                    }
                }
            }
        }

        int result[] = {year, month, date};

        return result;
    }

    /**
     * 달수를 변경하면서 연도가 바꼈을 때 예외처리
     * @param month 일
     * @return 바뀐 날짜
     */
    public int[] getCalendarMonth(int month) {

        int year = mYear;

        // 날짜 예외처리 --> 연도가 바뀌는 경우
        if (month <= 0) {
            year--;
            if (month == 0) {
                month = 12;
            } else {
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
