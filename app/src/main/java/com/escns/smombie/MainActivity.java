package com.escns.smombie;

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
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.escns.smombie.Adapter.ItemMainAdpater;
import com.escns.smombie.DAO.Point;
import com.escns.smombie.DAO.User;
import com.escns.smombie.Interface.ApiService;
import com.escns.smombie.Item.ItemMain;
import com.escns.smombie.Manager.DBManager;
import com.escns.smombie.Service.LockScreenService;
import com.escns.smombie.View.CustomImageView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    public final static int UPDATE_PROFILE_IMAGE = 1;
    public final static int UPDATE_SECTION = 2;

    private SharedPreferences pref;         // 화면 꺼짐 및 이동 시 switch가 초기화되기 때문에 파일에 따로 저장하기 위한 객체
    private DBManager mDBManager;            // DB 선언

    private String mFbId;
    private String mFbName;
    private String mFbEmail;
    private Bitmap mProfileImage;
    private boolean isProfileImageLoaded;

    //private WalkCheckService mService;
    private boolean mBound = false; // WalkCheckService Service가 제대로 동작하면 true 아니면 false

    private Retrofit mRetrofit;
    private ApiService mApiService;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if(msg.what==UPDATE_PROFILE_IMAGE) {
                ((CustomImageView) findViewById(R.id.profile_view)).setImageBitmap(mProfileImage);
                isProfileImageLoaded=true;
            } else if(msg.what == UPDATE_SECTION) {
                User user = mDBManager.getUser("hajaekwon");
                ((TextView) findViewById(R.id.section1_text)).setText(user.getmPoint());
                ((TextView) findViewById(R.id.section2_text)).setText(user.getmGoal());
                ((TextView) findViewById(R.id.section3_text)).setText(user.getmReword());
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

    /**
     *  Initialize toolbar and navigation drawer
     */
    public void initDrawer() {

        // Navigation Drawer에 필요한 Component들 생성
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

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
        View HeaderLayout = navigationView.getHeaderView(0);

        ImageView profileImageView = (ImageView) findViewById(R.id.profile_view);
        //profileImageView.setClipToOutline(true);

        /*
        // navigation_headr에 있는 사진과 정보
        headerName = (TextView) HeaderLayout.findViewById(R.id.header_name);
        headerPhoto = (ImageView) HeaderLayout.findViewById(R.id.header_photo);

        // 추가한 코드...아래

        // 사이드메뉴에 있는 item들을 클릭 시 동작
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                switch(id) {
                    case R.id.drawer_menu1 : // 히스토리
                        return true;
                    case R.id.drawer_menu2 : // 설정
                        return true;
                    case R.id.drawer_menu3 : // 내정보
                        return true;
                    case R.id.drawer_menu4 : // 로그아웃
                        com.facebook.login.LoginManager.getInstance().logOut();
                        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                        startActivity(intent);
                        finish();

                        return true;
                }
                return false;

            }
        });
         */

    }

    /**
     *  Initialize layout
     */
    public void init() {

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        mDBManager = new DBManager(this);

        mFbId = getIntent().getStringExtra("id");
        mFbName = getIntent().getStringExtra("name");
        mFbEmail = getIntent().getStringExtra("email");

        ((TextView) findViewById(R.id.profile_name)).setText(mFbName);
        ((TextView) findViewById(R.id.profile_email)).setText(mFbEmail);

        Thread thread =  new Thread(new Runnable() {
            @Override
            public void run() {
                while(!isProfileImageLoaded) {
                    try{
                        URL url = new URL("https://graph.facebook.com/" + mFbId + "/picture?type=large"); // URL 주소를 이용해서 URL 객체 생성
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

        final TextView section1Text = (TextView) findViewById(R.id.section1_text);
        final TextView section2Text = (TextView) findViewById(R.id.section2_text);
        final TextView section3Text = (TextView) findViewById(R.id.section3_text);

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
