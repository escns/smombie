package com.escns.smombie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.escns.smombie.Interface.ApiService;
import com.escns.smombie.Manager.DBManager;
import com.escns.smombie.ScreenFragment.HistoryFragment;
import com.escns.smombie.ScreenFragment.InfoFragment;
import com.escns.smombie.ScreenFragment.MainFragment;
import com.escns.smombie.ScreenFragment.SettingFragment;
import com.escns.smombie.Setting.Conf;
import com.escns.smombie.View.CustomImageView;

import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity {

    public final static int UPDATE_PROFILE_IMAGE = 1;
    public final static int DEFAULT_GOAL = 1000;

    DrawerLayout drawerLayout;              // 메인화면에서의 화면
    NavigationView navigationView;          // Side 메뉴바

    private SharedPreferences pref;         // 화면 꺼짐 및 이동 시 switch가 초기화되기 때문에 파일에 따로 저장하기 위한 객체
    private DBManager mDbManager;           // DB 선언

    private Conf conf;

    private int mMenuState = 1;             // 1:홈   2:히스토리   3:설정   4:내정보   5:로그아웃

    MainFragment mMainFragment;
    HistoryFragment mHistoryFragment;
    SettingFragment mSettingFragment;
    InfoFragment mInfoFragment;

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
                // 사이드 메뉴 header
                ((CustomImageView)HeaderLayout.findViewById(R.id.header_profile)).setImageBitmap(conf.mFbProfileImage);
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

        mMainFragment = new MainFragment();
        mHistoryFragment = new HistoryFragment();
        mSettingFragment = new SettingFragment();
        mInfoFragment = new InfoFragment();

        initDrawer(); // 툴바 구현
        init();
    }

    @Override
    public void onBackPressed() {

        if(mMenuState == 1) {
            drawerLayout.closeDrawer(navigationView);

            double curTime = System.currentTimeMillis();
            double intervalTime = curTime - mbackPressedTime;

            if (intervalTime <= 1000) {
                super.onBackPressed();
            } else {
                mbackPressedTime = System.currentTimeMillis();
                Toast.makeText(getApplicationContext(), "종료하실려면 한번 더 눌러주십시오", Toast.LENGTH_SHORT).show();
            }
        }
        else if(mMenuState == 2) {
            mMenuState = 1;
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mMainFragment).commit();
            drawerLayout.closeDrawer(navigationView);
        }
        else if(mMenuState == 3) {
            mMenuState = 1;
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mMainFragment).commit();
            drawerLayout.closeDrawer(navigationView);
        }
        else if(mMenuState == 4) {
            mMenuState = 1;
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mMainFragment).commit();
            drawerLayout.closeDrawer(navigationView);
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
                item.setCheckable(false);

                Intent intent;
                switch(id) {
                    case R.id.drawer_Home: // 홈

                        if(mMenuState != 1) {
                            mMenuState = 1;
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mMainFragment).commit();
                        }
                        drawerLayout.closeDrawer(navigationView);

                        return true;

                    case R.id.drawer_menu1 : // 히스토리

                        if(mMenuState != 2) {
                            mMenuState = 2;
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mHistoryFragment).commit();
                        }
                        drawerLayout.closeDrawer(navigationView);
                        return true;

                    case R.id.drawer_menu2 : // 설정

                        if(mMenuState != 3) {
                            mMenuState = 3;
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mSettingFragment).commit();
                        }
                        drawerLayout.closeDrawer(navigationView);
                        return true;

                    case R.id.drawer_menu3 : // 내정보

                        if(mMenuState != 4) {
                            mMenuState = 4;
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mInfoFragment).commit();
                        }
                        drawerLayout.closeDrawer(navigationView);
                        return true;

                    case R.id.drawer_menu4 : // 로그아웃
                        mMenuState = 5;
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

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        mDbManager = new DBManager(this);

        conf = Conf.getInstance();

        Thread thread =  new Thread(new Runnable() {
            @Override
            public void run() {
                while(!isProfileImageLoaded) {
                    try{
                        URL url = new URL("https://graph.facebook.com/" + conf.mFbId + "/picture?type=large"); // URL 주소를 이용해서 URL 객체 생성
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();              //  아래 코드는 웹에서 이미지를 가져온 뒤
                        conn.setDoInput(true);
                        conn.connect();
                        conf.mFbProfileImage = BitmapFactory.decodeStream(conn.getInputStream());               //  이미지 뷰에 지정할 Bitmap을 생성하는 과정

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

        // 홈 화면 실행
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mMainFragment).commit();


    }
}
