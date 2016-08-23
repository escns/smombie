package com.escns.smombie.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.escns.smombie.DAO.User;
import com.escns.smombie.Manager.DBManager;
import com.escns.smombie.R;
import com.escns.smombie.Setting.Conf;
import com.escns.smombie.View.ClockView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.escns.smombie.MainActivity.UPDATE_PROFILE_IMAGE;

/**
 * Created by Administrator on 2016-08-04.
 */

public class LockScreenReceiver extends BroadcastReceiver {

    private static final int UPDATE_TEMPERATURE = 1;
    private static final String APPID = "b76dfb8d442f6f47dd42d7b0ce572408";
    private String baseURL = "http://api.openweathermap.org/data/2.5/weather?";

    private Context mContext;

    private Conf conf;

    private WindowManager.LayoutParams mParams;     // 최상단에 그려질 뷰의 파라미터
    private WindowManager mWindowManager;           // 최상단에 뷰를 그릴 WindowManager
    private View mLockScreenView;                   // 최상단 뷰

    private ImageView mTemperatureIcon;
    private TextView mTemperatureText;
    private boolean isTemperatureLoaded;
    private String mTemperature;

    int windowWidth;                                // 전체 윈도우 너비
    int windowHeight;                               // 전체 윈도우 높이
    double lat = 37;
    double lon = 126;

    private static boolean isLock;                  // lock의 상태
    private static boolean isWalking;

    private PieChart mChart;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if(msg.what==UPDATE_TEMPERATURE) {
                mTemperatureText.setText(""+(int)(Double.parseDouble(mTemperature)));
                mTemperatureIcon.setImageResource(R.drawable.icon_weather);
                isTemperatureLoaded=true;
            }
        }
    };


    /**
     * BroadcastReceiver에 메시지가 왔을 때
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Tag", "LockReceiver - onReceive "+intent.getAction());

        mContext = context;
        conf = Conf.getInstance();

        String action = intent.getAction();

        // 어떤 메시지인지 확인
        if(action.equals(intent.ACTION_SCREEN_OFF)) {
            if(isWalking) {

                drawLockScreen(context);

            }
        } else if(action.equals("com.escns.smombie.CALL_STATE_RINGING")) {
            if(mWindowManager!=null && isLock) {
                mWindowManager.removeView(mLockScreenView);
                mWindowManager = null;
                isLock=false;
            }
        } else if(action.equals("com.escns.smombie.CALL_STATE_OFFHOOK")) {

        } else if(action.equals("com.escns.smombie.CALL_STATE_IDLE")) {

        } else if(action.equals("com.escns.smombie.LOCK_SCREEN_ON")) {

            if(isLock) Log.i("tag", "isLock = true");
            else Log.i("tag", "isLock = false");
            if(isWalking) Log.i("tag", "isWalking = true");
            else Log.i("tag", "isWalking = false");

            if(!isLock) {
                isWalking=true;
                drawLockScreen(context);
            }
        } else if(action.equals("com.escns.smombie.LOCK_SCREEN_OFF")) {
            isWalking=false;
            if(mWindowManager!=null) {
                mWindowManager.removeView(mLockScreenView);
                mWindowManager = null;
                isLock=false;
            }
        }
    }

    private void drawLockScreen(Context context) {

        // lock이 활성화 된 상태라면 지우고 다시
        if(mWindowManager!=null && isLock) {
            mWindowManager.removeView(mLockScreenView);
            mWindowManager = null;
        }

        isLock = true;

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);                      // WindowManager 객체 생성
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);   // Layout을 가져오기 위해 LayoutInflater 객체 생성
        mLockScreenView = (View) inflater.inflate(R.layout.lockscreen_test, null);                                   // LayoutInflater를 이용하여 R.layout.lockscreen을 불러온다

        initTest();

            /*
            mViewPager = (LockViewPager) mLockScreenView.findViewById(R.id.viewPager);      // R.layout.lockscreen 내부의 ViewPager 객체 생성
            mPagerAdapter = new myPagerAdapter(context);                                    // adapter 생성
            mViewPager.setAdapter(mPagerAdapter);
            mViewPager.setCurrentItem(0);
            //mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());            // 옵션으로 ViewPager에 페이지 체인지 애니메이션을 줄 수 있다
            */

        mParams = new WindowManager.LayoutParams(                                       // View의 파라미터 결정
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,                           // 최상단 뷰로 설정
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN                     // 풀 스크린으로 설정
        );
        mParams.screenOrientation=12;                                                   // 반드시 세로

        //mParams.verticalMargin = 30.0f;

        //mParams.flags |= WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

        windowHeight = mWindowManager.getDefaultDisplay().getHeight();
        windowWidth = mWindowManager.getDefaultDisplay().getWidth();

        mTemperatureIcon = (ImageView)mLockScreenView.findViewById(R.id.temperature_icon);
        mTemperatureText = (TextView)mLockScreenView.findViewById(R.id.temperature_text);

        Thread thread =  new Thread(new Runnable() {
            @Override
            public void run() {
                while(!isTemperatureLoaded) {
                    try{

                        String line  = getStringFromUrl(baseURL+"lat="+lat+"&lon="+lon+"&APPID="+APPID);

                        JSONObject object = new JSONObject(line);

                        Thread.sleep(100);

                        object = object.getJSONObject("main");
                        mTemperature = object.getString("temp");

                        Message message = handler.obtainMessage();
                        message.what = UPDATE_PROFILE_IMAGE;
                        handler.sendMessage(message);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        mWindowManager.addView(mLockScreenView, mParams);
    }

    public void initTest() {

        final ImageView button_lock = (ImageView) mLockScreenView.findViewById(R.id.button_lock);
        final ImageView button_unlock = (ImageView) mLockScreenView.findViewById(R.id.button_unlock);
        final int offsetLeft = ((RelativeLayout.LayoutParams)button_lock.getLayoutParams()).leftMargin;
        //final int offsetRight = ((RelativeLayout.LayoutParams)button_unlock.getLayoutParams()).leftMargin;
        //final int offsetLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, mContext.getResources().getDisplayMetrics());
        final int offsetRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, mContext.getResources().getDisplayMetrics());

        button_lock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) button_lock.getLayoutParams();
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        //layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, mContext.getResources().getDisplayMetrics());
                        button_lock.setImageResource(R.drawable.btn_key_tap);
                        button_unlock.setImageResource(R.drawable.btn_unlock);
                        break;
                    case MotionEvent.ACTION_MOVE:

                        int x_cord = (int)event.getRawX();
                        int btnWidth = button_lock.getWidth();

                        if(x_cord>windowWidth - btnWidth- offsetRight) {
                            mWindowManager.removeView(mLockScreenView);
                            isLock=false;
                            break;
                        }
                        if(x_cord - btnWidth - offsetLeft < 0) {x_cord=offsetLeft+btnWidth;}

                        // 왼쪽 margin 값을 터치 위치로 변경함으로써 마치 움직이는 것처럼 보인다
                        layoutParams.leftMargin = x_cord - btnWidth - offsetLeft;

                        // 변경된 파라미터를 적용
                        button_lock.setLayoutParams(layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        if(isLock) {
                            layoutParams.leftMargin = offsetLeft;
                            button_lock.setLayoutParams(layoutParams);
                            button_lock.setImageResource(R.drawable.btn_key);
                            button_unlock.setImageResource(R.drawable.btn_lock);
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        ClockView clockView = new ClockView((TextView)mLockScreenView.findViewById(R.id.clock_ampm), (TextView)mLockScreenView.findViewById(R.id.clock_time), (TextView)mLockScreenView.findViewById(R.id.clock_date));

        drawChart();

    }

    private void drawChart() {
        mChart = (PieChart) mLockScreenView.findViewById(R.id.pie_chart);
        mChart.setUsePercentValues(true);
        mChart.setDescription("");
        //mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        //mChart.setCenterTextTypeface(mTfLight);
        //mChart.setCenterText(generateCenterSpannableText());
        //mChart.setCenterText("75%");
        //mChart.setCenterTextSize(25f);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        //mChart.setTransparentCircleColor(Color.WHITE);
        //mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(70f);
        //mChart.setTransparentCircleRadius(61f);

        TextView centerText = (TextView) mLockScreenView.findViewById(R.id.pie_chart_center);
        centerText.setText("75%");

        /*
        mChart.setDrawCenterText(true);
        mChart.setCenterTextSize(40f);
        mChart.setCenterTextColor(R.color.third_color);
        */

        //mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        //mChart.setRotationEnabled(true);
        //mChart.setHighlightPerTapEnabled(true);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
        //mChart.setOnChartValueSelectedListener(this);

        setData(2, 100);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);

        // entry label styling
        mChart.setEntryLabelColor(Color.WHITE);
        //mChart.setEntryLabelTypeface(mTfRegular);
        mChart.setEntryLabelTextSize(12f);
    }

    private void setData(int count, float range) {

        String[] mParties = new String[] {
                "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
                "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
                "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
                "Party Y", "Party Z"
        };

        float mult = range;

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        User user = new DBManager(mContext).getUser(conf.mPrimaryKey);
        //entries.add(new PieEntry((float) (50), "진행률"));
        entries.add(new PieEntry((float) (user.getmPoint()), "진행률"));
        entries.add(new PieEntry((float) (user.getmGoal()), "목표치"));

        PieDataSet dataSet = new PieDataSet(entries, "Election Results");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        //data.setValueTypeface(mTfLight);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;

    }


    public  String getStringFromUrl(String url) throws UnsupportedOperationException, UnsupportedEncodingException {

        // 입력 스트림을 euc-kr를 사용해서 읽은 후  이를 사용해서
        // 라인단위로 데이터를 읽을수 있는 BufferReader를 생성한다
        BufferedReader br = new BufferedReader(new InputStreamReader(getInputStreamFromUrl(url),"UTF-8"));   //(new InputStreamReader(url,"UTF-8"));

        //읽은 데이터를 저장할 StringBuffer를 생성한다
        StringBuffer sb = new StringBuffer();

        try{
            //라인 단위로 읽은 데이터를 임시 저장할 자열 변수
            String line = null;

            //라인 단위로 데이터를 읽어서 StringBuffer에 저장한다
            while ((line = br.readLine()) != null){
                sb.append(line);
                Log.e("TTL",line);
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return  sb.toString();

    }

    //url 커넥션
    public  static InputStream getInputStreamFromUrl(String url){
        InputStream contentStream = null;

        try{
            URL reauestURL  = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) reauestURL.openConnection();
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                contentStream = conn.getInputStream();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return  contentStream;
    }

}
