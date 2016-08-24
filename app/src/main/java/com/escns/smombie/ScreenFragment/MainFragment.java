package com.escns.smombie.ScreenFragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.escns.smombie.Adapter.ItemMainAdpater;
import com.escns.smombie.DAO.User;
import com.escns.smombie.Interface.ApiService;
import com.escns.smombie.Item.ItemMain;
import com.escns.smombie.Manager.DBManager;
import com.escns.smombie.R;
import com.escns.smombie.Service.LockScreenService;
import com.escns.smombie.Setting.Conf;
import com.escns.smombie.View.CustomImageView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;

/**
 * Created by hyo99 on 2016-08-23.
 */

public class MainFragment extends Fragment {

    public final static int UPDATE_PROFILE_DATA = 1;

    private Context mContext;

    public final static int DEFAULT_GOAL = 1000;

    private Conf conf;

    private SharedPreferences pref;         // 화면 꺼짐 및 이동 시 switch가 초기화되기 때문에 파일에 따로 저장하기 위한 객체
    private DBManager mDbManager;            // DB 선언

    private boolean mBound = false; // WalkCheckService Service가 제대로 동작하면 true 아니면 false
    private Retrofit mRetrofit;
    private ApiService mApiService;

    private boolean isProfileDataLoaded;
    private Bitmap mFbProfileImage;
    private int section1Text;
    private int section2Text;
    private int section3Text;

    View rootView;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==UPDATE_PROFILE_DATA) {
                // 사이드 메뉴 header
                ((CustomImageView)rootView.findViewById(R.id.profile_view)).setImageBitmap(mFbProfileImage);
                ((TextView)rootView.findViewById(R.id.user_email)).setText(conf.mFbEmail);
                isProfileDataLoaded=true;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        init();



        return rootView;
    }

    @Override
    public void onResume() {

        User user = mDbManager.getUser(conf.mPrimaryKey);
        int Point, Goal, Reword;
        if(user==null) {
            Log.i("tag", "MainFragment onResume user is null");
            mDbManager.insertUser(new User(conf.mPrimaryKey, conf.mFbId, conf.mFbName, conf.mFbEmail, conf.mFbGender, conf.mFbAge, 0, DEFAULT_GOAL, 0, 0, 0, 0));
            Point = 0;
            Goal = DEFAULT_GOAL;
            Reword = 0;
        } else {
            Log.i("tag", "MainFragment onResume user is not null");
            Log.i("tag", "MainFragment onResume user is " + user.toString());
            Point = user.getmPoint();
            Goal = user.getmGoal();
            Reword = user.getmReword();
        }

        ((TextView) rootView.findViewById(R.id.section1_text)).setText(""+Point);
        ((TextView) rootView.findViewById(R.id.section2_text)).setText(""+Goal);
        ((TextView) rootView.findViewById(R.id.section3_text)).setText(""+Reword);
        if(mFbProfileImage!=null) ((CustomImageView)rootView.findViewById(R.id.profile_view)).setImageBitmap(mFbProfileImage);
        ((TextView)rootView.findViewById(R.id.user_email)).setText(conf.mFbEmail);

        super.onResume();
    }


    public void init() {

        mContext = getContext();

        pref = mContext.getSharedPreferences("pref", mContext.MODE_PRIVATE);
        mDbManager = new DBManager(mContext);

        conf = Conf.getInstance();

        SwitchCompat swc = (SwitchCompat) rootView.findViewById(R.id.switch_lock);
        //swc.setChecked(pref.getBoolean("switch",false));

        swc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    pref.edit().putBoolean("switch", true).commit();

                    Intent intent = new Intent("com.escns.smombie.service").setPackage("com.escns.smombie");
                    mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE); // 만보기 동작
                    mContext.startService(new Intent(mContext, LockScreenService.class));
                } else {
                    pref.edit().putBoolean("switch", false).commit();

                    mContext.unbindService(mConnection);
                    mContext.stopService(new Intent(mContext, LockScreenService.class));
                }
            }
        });

        Thread thread =  new Thread(new Runnable() {
            @Override
            public void run() {
                while(!isProfileDataLoaded) {
                    try{
                        URL url = new URL("https://graph.facebook.com/" + conf.mFbId + "/picture?type=large"); // URL 주소를 이용해서 URL 객체 생성
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();              //  아래 코드는 웹에서 이미지를 가져온 뒤
                        conn.setDoInput(true);
                        conn.connect();
                        mFbProfileImage = BitmapFactory.decodeStream(conn.getInputStream());               //  이미지 뷰에 지정할 Bitmap을 생성하는 과정

                        Thread.sleep(100);

                        Message message = handler.obtainMessage();
                        message.what = UPDATE_PROFILE_DATA;
                        handler.sendMessage(message);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
/*
        mRetrofit = new Retrofit.Builder().baseUrl(mApiService.API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        mApiService = mRetrofit.create(ApiService.class);


        Call<Point> currentPoint = mApiService.getCurrentPoint(1);
        currentPoint.enqueue(new Callback<Point>() {
            @Override
            public void onResponse(Call<Point> call, Response<Point> response) {
                if(response.body()!=null) {
                    section1Text.setText(""+response.body().getPoint()+"m");
                }
            }

            @Override
            public void onFailure(Call<Point> call, Throwable t) {

            }
        });

        Call<Point> goalPoint = mApiService.getGoalPoint(1);
        goalPoint.enqueue(new Callback<Point>() {
            @Override
            public void onResponse(Call<Point> call, Response<Point> response) {
                if(response.body()!=null) {
                    section2Text.setText("" + response.body().getPoint() + "m");
                }
            }

            @Override
            public void onFailure(Call<Point> call, Throwable t) {

            }
        });
        */

        final List<ItemMain> ItemMains = new ArrayList<>();
        ItemMains.add(new ItemMain(true, "이벤트", R.drawable.title_icon_event));
        ItemMains.add(new ItemMain(false, R.drawable.img_event1));
        ItemMains.add(new ItemMain(false, R.drawable.img_event2));
        ItemMains.add(new ItemMain(false, R.drawable.img_event1));

        ItemMains.add(new ItemMain(true, "제휴 서비스", R.drawable.title_icon_service));
        ItemMains.add(new ItemMain(false, R.drawable.img_event2));
        ItemMains.add(new ItemMain(false, R.drawable.img_event2));
        ItemMains.add(new ItemMain(false, R.drawable.img_event1));

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.id_stickynavlayout_innerscrollview);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(ItemMains.get(position).isHeader()) return gridLayoutManager.getSpanCount();
                return 1;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(new ItemMainAdpater(ItemMains));
    }

    // ThreadService와 MainActivity를 연결 시켜줄 ServiceConnection
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        // 리턴되는 Binder를 다시 Service로 꺼내서 ThreadSerivce내부의 함수 사용이 가능하다.
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            //WalkCheckService.LocalBinder binder = (WalkCheckService.LocalBinder) service;
            //mService = binder.getService();
            mBound = true;
            Log.d("tag", "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            Log.d("tag", "onServiceDisconnected");
        }
    };
}
