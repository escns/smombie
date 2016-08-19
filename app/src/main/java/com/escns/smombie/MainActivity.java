package com.escns.smombie;

import android.app.Activity;
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
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.escns.smombie.Adapter.ItemMainAdpater;
import com.escns.smombie.DAO.User;
import com.escns.smombie.Interface.ApiService;
import com.escns.smombie.Item.ItemMain;
import com.escns.smombie.Manager.DBManager;
import com.escns.smombie.Service.LockScreenService;
import com.escns.smombie.Setting.Conf;
import com.escns.smombie.View.CustomImageView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    public final static int UPDATE_PROFILE_IMAGE = 1;

    public final static int DEFAULT_GOAL = 1000;

    public static Activity mExitAct;

    DrawerLayout drawerLayout; // 메인화면에서의 화면
    NavigationView navigationView; // Side 메뉴바

    private SharedPreferences pref;         // 화면 꺼짐 및 이동 시 switch가 초기화되기 때문에 파일에 따로 저장하기 위한 객체
    private DBManager mDbManager;            // DB 선언

    private Conf conf;

    private Bitmap mProfileImage;
    private boolean isProfileImageLoaded;

    private View HeaderLayout;

    private double mbackPressedTime = 0; // 연속으로 두번 누르면 종료 시 사용하는 변수

    //private WalkCheckService mService;
    private boolean mBound = false; // WalkCheckService Service가 제대로 동작하면 true 아니면 false

    private Retrofit mRetrofit;
    private ApiService mApiService;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if(msg.what==UPDATE_PROFILE_IMAGE) {
                ((CustomImageView) findViewById(R.id.profile_view)).setImageBitmap(mProfileImage);
                ((CustomImageView)HeaderLayout.findViewById(R.id.header_profile)).setImageBitmap(mProfileImage);
                ((TextView)findViewById(R.id.user_email)).setText(conf.mFbEmail);
                ((TextView)HeaderLayout.findViewById(R.id.header_name)).setText(conf.mFbName);
                ((TextView)HeaderLayout.findViewById(R.id.header_email)).setText(conf.mFbEmail);
                isProfileImageLoaded=true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDrawer(); // 툴바 구현
        init();
    }

    @Override
    protected void onResume() {
        User user = mDbManager.getUser(conf.mFbId);
        int Point, Goal, Reword;
        if(user==null) {
            mDbManager.insertUser(new User(conf.mFbId, conf.mFbName, conf.mFbEmail, conf.mFbGender, conf.mFbAge, 0, DEFAULT_GOAL, 0, 0, 0));
            Point = 0;
            Goal = DEFAULT_GOAL;
            Reword = 0;
        } else {
            Point = user.getmPoint();
            Goal = user.getmGoal();
            Reword = user.getmReword();
        }

        ((TextView) findViewById(R.id.section1_text)).setText(""+Point);
        ((TextView) findViewById(R.id.section2_text)).setText(""+Goal);
        ((TextView) findViewById(R.id.section3_text)).setText(""+Reword);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        double curTime = System.currentTimeMillis();
        double intervalTime = curTime - mbackPressedTime;

        if(intervalTime <= 2000) {
            super.onBackPressed();
        }
        else {
            mbackPressedTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "종료하실려면 한번 더 눌러주십시오", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *  Initialize toolbar and navigation drawer
     */
    public void initDrawer() {

        // Navigation Drawer에 필요한 Component들 생성
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        // 기존의 ActionBar를 대체하기 위한 toolbar 설정
        setSupportActionBar(toolbar);

        // Navigation Drawer의 좌측 상단의 Arrow 애니메이션을 위한 DrawerToggle 설정
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, R.string.hello, R.string.hello) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        // 생성한 DrawerToggle을 drawerLayout에 장착
        drawerLayout.setDrawerListener(drawerToggle);

        // Arrow 애니메이션을 위한 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerToggle.syncState();

        // Drawer 내부의 메뉴들을 성정
        navigationView.inflateMenu((R.menu.navigation_item));
        HeaderLayout = navigationView.getHeaderView(0);

        // 추가한 코드...아래
        // 사이드메뉴에 있는 item들을 클릭 시 동작
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                Log.d("tag", "Click item in side Menu");

                int id = item.getItemId();

                Intent intent;
                switch(id) {
                    case R.id.drawer_Home: // 홈
                        drawerLayout.closeDrawer(navigationView);
                        return true;

                    case R.id.drawer_menu1 : // 히스토리
                        intent = new Intent(getApplicationContext(), HistoryActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawer(navigationView);
                        return true;

                    case R.id.drawer_menu2 : // 설정
                        drawerLayout.closeDrawer(navigationView);
                        return true;

                    case R.id.drawer_menu3 : // 내정보
                        drawerLayout.closeDrawer(navigationView);
                        return true;

                    case R.id.drawer_menu4 : // 로그아웃
                        com.facebook.login.LoginManager.getInstance().logOut();
                        intent = new Intent(getApplicationContext(), StartActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                }

                return false;
            }
        });

    }

    /**
     *  Initialize layout
     */
    public void init() {

        mExitAct = MainActivity.this; // 다른 Activity에서 MainActivity를 종료하기 위함

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        mDbManager = new DBManager(this);

        conf = Conf.getInstance();

        // LoginActivity로부터 페이스북 프로필정보 받아오기
        conf.mFbId = getIntent().getStringExtra("id");
        conf.mFbName = getIntent().getStringExtra("name");
        conf.mFbEmail = getIntent().getStringExtra("email");
        conf.mFbGender = getIntent().getStringExtra("gender");
        conf.mFbAge = getIntent().getIntExtra("age", 0);

        Thread thread =  new Thread(new Runnable() {
            @Override
            public void run() {
                while(!isProfileImageLoaded) {
                    try{
                        URL url = new URL("https://graph.facebook.com/" + conf.mFbId + "/picture?type=large"); // URL 주소를 이용해서 URL 객체 생성
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();              //  아래 코드는 웹에서 이미지를 가져온 뒤
                        conn.setDoInput(true);
                        conn.connect();
                        mProfileImage = BitmapFactory.decodeStream(conn.getInputStream());               //  이미지 뷰에 지정할 Bitmap을 생성하는 과정

                        Thread.sleep(100);

                        Log.i("tag", "get FB profile image");
                        Message message = handler.obtainMessage();
                        message.what = UPDATE_PROFILE_IMAGE;
                        handler.sendMessage(message);                                   //profileImage.setImageBitmap(bit); // 페이스북 사진 입력
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        SwitchCompat swc = (SwitchCompat) findViewById(R.id.switch_lock);
        swc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {

                    pref.edit().putBoolean("switch", true).commit();

                    Intent intent = new Intent("com.escns.smombie.service").setPackage("com.escns.smombie");
                    bindService(intent, mConnection, Context.BIND_AUTO_CREATE); // 만보기 동작
                    startService(new Intent(MainActivity.this, LockScreenService.class));
                } else {
                    pref.edit().putBoolean("switch", false).commit();

                    unbindService(mConnection);
                    stopService(new Intent(MainActivity.this, LockScreenService.class));
                }
            }
        });

        mRetrofit = new Retrofit.Builder().baseUrl(mApiService.API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        mApiService = mRetrofit.create(ApiService.class);

        /*
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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.id_stickynavlayout_innerscrollview);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
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
