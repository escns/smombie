package com.escns.smombie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.escns.smombie.ScreenFragment.HistoryFragment;
import com.escns.smombie.ScreenFragment.InfoFragment;
import com.escns.smombie.ScreenFragment.MainFragment;
import com.escns.smombie.ScreenFragment.SettingFragment;
import com.escns.smombie.Service.LockScreenService;
import com.escns.smombie.Service.PedometerCheckService;
import com.escns.smombie.View.CustomImageView;

import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    public final static int UPDATE_PROFILE_IMAGE = 1;
    public final static int DEFAULT_GOAL = 1000;

    private DrawerLayout drawerLayout;              // 메인화면에서의 화면
    private NavigationView navigationView;          // Side 메뉴바
    private View HeaderLayout;

    private SharedPreferences pref;                 // 화면 꺼짐 및 이동 시 switch가 초기화되기 때문에 파일에 따로 저장하기 위한 객체

    Fragment mMainFragment;
    Fragment mHistoryFragment;
    Fragment mSettingFragment;
    Fragment mInfoFragment;

    private int mMenuState = 1;                     // 1:홈   2:히스토리   3:설정   4:내정보   5:로그아웃
    private boolean isProfileImageLoaded;
    private double mbackPressedTime = 0;            // 연속으로 두번 누르면 종료 시 사용하는 변수
    private Bitmap mFbProfileImage;

    /**
     * Profile Image 다운 성공 시 호출하여 profile update를 수행한다.
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==UPDATE_PROFILE_IMAGE) {
                // 사이드 메뉴 header
                ((CustomImageView)HeaderLayout.findViewById(R.id.header_profile)).setImageBitmap(mFbProfileImage);
                ((TextView)HeaderLayout.findViewById(R.id.header_name)).setText(pref.getString("NAME", "사용자 이름"));
                ((TextView)HeaderLayout.findViewById(R.id.header_email)).setText(pref.getString("EMAIL", "사용자 이메일"));
                Log.i("tag", "EMAIL " + pref.getString("EMAIL", "사용자 이메일"));
                Log.i("tag", "GENDER " + pref.getString("GENDER", "dddd"));
                isProfileImageLoaded=true;
            }
        }
    };

    /**
     * view들의 설정을 해주는 함수들을 호출
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDrawer(); // 툴바 구현
        init();
    }

    /**
     * back key 2번에 종료되게 key event를 가로챈다.
     */
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
        else {
            mMenuState = 1;
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mMainFragment).commit();
            drawerLayout.closeDrawer(navigationView);
        }

    }

    /**
     *  Navigation Drawer와 Toolbar 설정을 해준다.
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

                int id = item.getItemId();
                item.setCheckable(false);

                switch(id) {
                    case R.id.drawer_Home: // 홈
                        moveFragment(1, mMainFragment);
                        return true;

                    case R.id.drawer_menu1 : // 히스토리
                        moveFragment(2, mHistoryFragment);
                        return true;

                    case R.id.drawer_menu2 : // 설정
                        moveFragment(3, mSettingFragment);
                        return true;

                    case R.id.drawer_menu3 : // 내정보
                        moveFragment(4, mInfoFragment);
                        return true;

                    case R.id.drawer_menu4 : // 로그아웃
                        mMenuState = 5;

                        if( pref.getBoolean("switch",false) ) {
                            pref.edit().putBoolean("switch", false).commit();
                            (MainActivity.this).stopService(new Intent((MainActivity.this), PedometerCheckService.class));
                            (MainActivity.this).stopService(new Intent((MainActivity.this), LockScreenService.class));
                        }

                        com.facebook.login.LoginManager.getInstance().logOut();
                        startActivity(new Intent(getApplicationContext(), StartActivity.class));
                        finish();
                        return true;
                }

                return false;
            }
        });

    }

    /**
     * 파라미터로 받은 fragment로 이동
     * @param i
     * @param fragment
     */
    private void moveFragment(int i, Fragment fragment) {
        if(mMenuState != i) {
            mMenuState = i;
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
        }
        drawerLayout.closeDrawer(navigationView);
    }

    /**
     *  View들의 초기설정을 해준다.
     */
    public void init() {

        pref = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);

        //if( pref.getBoolean("switch",false) ) {
        //    pref.edit().putBoolean("switch", true).commit();
        //
        //    Intent intent = new Intent("com.escns.smombie.service").setPackage("com.escns.smombie");
        //    (MainActivity.this).bindService(intent, mConnection, Context.BIND_AUTO_CREATE); // 만보기 동작
        //    (MainActivity.this).startService(new Intent((MainActivity.this), LockScreenService.class));
        //}

        mMainFragment = new MainFragment();
        mHistoryFragment = new HistoryFragment();


        mSettingFragment = new SettingFragment();
        mInfoFragment = new InfoFragment();

        Thread thread =  new Thread(new Runnable() {
            @Override
            public void run() {
                while(!isProfileImageLoaded) {
                    try{
                        URL url = new URL("https://graph.facebook.com/" + pref.getString("USER_ID_TEXT", "1111") + "/picture?type=large"); // URL 주소를 이용해서 URL 객체 생성
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();              //  아래 코드는 웹에서 이미지를 가져온 뒤
                        conn.setDoInput(true);
                        conn.connect();
                        mFbProfileImage = BitmapFactory.decodeStream(conn.getInputStream());               //  이미지 뷰에 지정할 Bitmap을 생성하는 과정

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
