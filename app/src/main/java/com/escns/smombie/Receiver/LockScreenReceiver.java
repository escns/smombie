package com.escns.smombie.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.escns.smombie.View.LockViewPager;
import com.escns.smombie.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Random;

import static android.util.Log.i;

/**
 * Created by Administrator on 2016-08-04.
 */

public class LockScreenReceiver extends BroadcastReceiver {

    private Context mContext;

    private WindowManager.LayoutParams mParams;     // 최상단에 그려질 뷰의 파라미터
    private WindowManager mWindowManager;           // 최상단에 뷰를 그릴 WindowManager
    private View mLockScreenView;                   // 최상단 뷰
    private static boolean isLock;                         // lock의 상태

    private LockViewPager mViewPager;               // LockScreen 내부의 ViewPager 객체
    private PagerAdapter mPagerAdapter;             // ViewPager와 그 내부 뷰를 연결할 Adapter

    int windowWidth;                                // 전체 윈도우 너비
    int windowHeight;                               // 전체 윈도우 높이

    private static boolean isWalking;

    private PieChart mChart;

    //RequestQueue queue;

    // BroadcastReceiver에 메시지가 왔을 때
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Tag", "LockReceiver - onReceive"+intent.getAction());

        mContext = context;

        String action = intent.getAction();

        // 어떤 메시지인지 확인
        if(action.equals(intent.ACTION_SCREEN_OFF)) {
            if(isWalking) {
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

                mWindowManager.addView(mLockScreenView, mParams);
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
                        WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN                    // 풀 스크린으로 설정
                );
                mParams.screenOrientation=12;                                                   // 반드시 세로

                //mParams.verticalMargin = 30.0f;

                //mParams.flags |= WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

                windowHeight = mWindowManager.getDefaultDisplay().getHeight();
                windowWidth = mWindowManager.getDefaultDisplay().getWidth();

                mWindowManager.addView(mLockScreenView, mParams);
            }
        } else if(action.equals("com.escns.smombie.LOCK_SCREEN_OFF")) {
            isWalking=false;
            if(mWindowManager!=null && isLock) {
                mWindowManager.removeView(mLockScreenView);
                mWindowManager = null;
                isLock=false;
            }
        }
    }

    public void addWindowView() {
        mWindowManager.addView(mLockScreenView, mParams);
    }

    public void initTest() {
        final ImageView button_lock = (ImageView) mLockScreenView.findViewById(R.id.button_lock);
        final ImageView button_unlock = (ImageView) mLockScreenView.findViewById(R.id.button_unlock);
        final int offsetLeft = ((RelativeLayout.LayoutParams)button_lock.getLayoutParams()).leftMargin;
        final int offsetRight = ((RelativeLayout.LayoutParams)button_unlock.getLayoutParams()).leftMargin;

        button_lock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) button_lock.getLayoutParams();
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, mContext.getResources().getDisplayMetrics());
                        layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, mContext.getResources().getDisplayMetrics());
                        button_lock.setLayoutParams(layoutParams);
                        button_unlock.setImageResource(R.drawable.key_unlock);
                        break;
                    case MotionEvent.ACTION_MOVE:

                        int x_cord = (int)event.getRawX();
                        int btnWidth = button_lock.getWidth()/2;

                        if(x_cord>windowWidth - button_unlock.getWidth() - offsetRight) {
                            mWindowManager.removeView(mLockScreenView);
                            isLock=false;
                            break;
                        }
                        if(x_cord - btnWidth - offsetLeft < 0) {x_cord=offsetLeft+btnWidth;}

                        // 왼쪽 margin 값을 터치 위치로 변경함으로써 마치 움직이는 것처럼 보인다
                        layoutParams.leftMargin = x_cord - btnWidth;

                        // 변경된 파라미터를 적용
                        button_lock.setLayoutParams(layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        if(isLock) {
                            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, mContext.getResources().getDisplayMetrics());
                            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, mContext.getResources().getDisplayMetrics());
                            layoutParams.leftMargin = offsetLeft;
                            button_lock.setLayoutParams(layoutParams);
                            button_unlock.setImageResource(R.drawable.key_lock);
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        mChart = (PieChart) mLockScreenView.findViewById(R.id.pieChart);
        mChart.setUsePercentValues(true);
        mChart.setDescription("");
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        //mChart.setCenterTextTypeface(mTfLight);
        mChart.setCenterText(generateCenterSpannableText());

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
        //mChart.setOnChartValueSelectedListener(this);

        setData(4, 100);

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
        for (int i = 0; i < count ; i++) {
            entries.add(new PieEntry((float) ((Math.random() * mult) + mult / 5), mParties[i % mParties.length]));
        }

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


    /**
     * ViewPager와 내부 뷰를 연결할 Adapter
     * 여기선 Activity 위에 ViewPager가 있는 것이 아니기 때문에 일반적인 FragmentStatePagerAdapter를 쓸 수 없었다.
     */
    private class myPagerAdapter extends PagerAdapter {

        Context mContext;

        public myPagerAdapter(Context mCotext) {
            this.mContext = mCotext;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, int position) {

            // 포지션에 따라 다른 뷰를 그린다
            if(position==0) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                final ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.lockscreen_view1, container, false);

                /*
                LinearLayout clockLayout = (LinearLayout) layout.findViewById(R.id.clockLayout);

                List<GraphData> list = new ArrayList<>();
                list.add(new GraphData("1번", R.color.blue, 0, 40));
                list.add(new GraphData("2번", R.color.dark_emerald, 0, 70));

                GraphDataVO graphDataVO = new GraphDataVO(list, 0, 0, 0, clockLayout.getHeight());
                RelativeLayout graphView = (RelativeLayout) layout.findViewById(R.id.pieChart);
                graphView.addView(new GraphView(mContext, graphDataVO));


                TextView dateView = (TextView) layout.findViewById(R.id.dateView);
                Date now = new Date();
                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
                dateView.setText(dateFormat.format(now));

                //TextView digitalClock = (TextView) layout.findViewById(R.id.clockView);
                //digitalClock.setText(new SimpleDateFormat("a h:mm").format(now));
                */

                final ImageView button_lock = (ImageView) layout.findViewById(R.id.button_lock);
                final ImageView button_unlock = (ImageView) layout.findViewById(R.id.button_unlock);
                final int offsetLeft = windowWidth/8*3;
                i("tag", "offsetLeft " + String.valueOf(offsetLeft));

                button_lock.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) button_lock.getLayoutParams();
                        switch(event.getAction())
                        {
                            case MotionEvent.ACTION_DOWN:
                                layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, mContext.getResources().getDisplayMetrics());
                                layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, mContext.getResources().getDisplayMetrics());
                                button_lock.setLayoutParams(layoutParams);
                                button_unlock.setImageResource(R.drawable.key_unlock);
                                break;
                            case MotionEvent.ACTION_MOVE:
                                mViewPager.setTouchFlag(false);

                                int x_cord = (int)event.getRawX() - offsetLeft;
                                int btnWidth = button_lock.getWidth()/2;

                                if(x_cord+offsetLeft>windowWidth - btnWidth - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, mContext.getResources().getDisplayMetrics())) {
                                    mWindowManager.removeView(mLockScreenView);
                                    isLock=false;
                                    break;
                                }
                                if(x_cord - btnWidth < 0){x_cord=btnWidth;}

                                // 왼쪽 margin 값을 터치 위치로 변경함으로써 마치 움직이는 것처럼 보인다
                                layoutParams.leftMargin = x_cord - btnWidth;

                                // 변경된 파라미터를 적용
                                button_lock.setLayoutParams(layoutParams);
                                break;
                            case MotionEvent.ACTION_UP:
                                mViewPager.setTouchFlag(true);
                                if(isLock) {
                                    layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, mContext.getResources().getDisplayMetrics());
                                    layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, mContext.getResources().getDisplayMetrics());
                                    layoutParams.leftMargin = 0;
                                    button_lock.setLayoutParams(layoutParams);
                                    button_unlock.setImageResource(R.drawable.key_lock);
                                }
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });

                container.addView(layout);

                return layout;

            } else if(position ==1){
                LayoutInflater inflater = LayoutInflater.from(mContext);
                final ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.lockscreen_view2, container, false);

                ImageView backGroundView = (ImageView) layout.findViewById(R.id.lockView1Background);
                final ProgressBar progressBar = (ProgressBar) layout.findViewById(R.id.room_loading);
                progressBar.setVisibility(View.VISIBLE);

                String url = "https://raw.githubusercontent.com/HaJaeKwon/GnBangExam/master/app/src/main/res/drawable/";
                Random random = new Random();
                int t = random.nextInt(2);
                String subUrl;
                if(t==0) subUrl = "background1.jpg";
                else if(t==1) subUrl = "background2.jpg";
                else subUrl = "background3.jpg";
/*
                Picasso.with(mContext)
                        .load(url+subUrl)
                        .into(backGroundView, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }
                            @Override
                            public void onError() {
                            }
                        });
*/
                Glide.with(mContext)
                        .load(url+subUrl)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(backGroundView);

                container.addView(layout);
                return layout;
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }


}
