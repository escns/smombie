package com.escns.smombie;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.escns.smombie.Tab.TabPagerAdapter;
import com.escns.smombie.View.CustomImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hyo99 on 2016-08-16.
 */

public class HistoryActivity extends AppCompatActivity {

    DrawerLayout drawerLayout; // 메인화면에서의 화면
    NavigationView navigationView; // Side 메뉴바

    private String mFbId;                // 페이스북 ID
    private String mFbName;              // 페이스북 이름
    private String mFbEmail;             // 페이스북 이메일
    private Bitmap mProfileImage;                 // 페이스북으로부터 사진을 받아올 객체


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initDrawer(); // 툴바 및 사이드메뉴 구현
        init(); // 초기화
        initTab(); // 탭메뉴 구현
    }

    /**
     * 툴바 및 사이드메뉴를 구현하는 함수
     */
    public void initDrawer() {
        // Navigation Drawer에 필요한 Component들 생성
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        // 기존의 ActionBar를 대체하기 위한 toolbar 설정
        setSupportActionBar(toolbar);

        // Navigation Drawer의 좌측 상단의 Arrow 애니메이션을 위한 DrawerToggle 설정
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(HistoryActivity.this, drawerLayout, toolbar, R.string.hello, R.string.hello) {
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

        mFbId = getIntent().getStringExtra("id");
        mFbName = getIntent().getStringExtra("name");
        mFbEmail = getIntent().getStringExtra("email");

        // 추가한 코드...아래

        // 사이드메뉴에 있는 item들을 클릭 시 동작
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                switch (id) {
                    case R.id.drawer_Home: // 홈
                        finish();
                        return true;

                    case R.id.drawer_menu1: // 히스토리
                        drawerLayout.closeDrawer(navigationView);
                        return true;

                    case R.id.drawer_menu2: // 설정
                        drawerLayout.closeDrawer(navigationView);
                        return true;

                    case R.id.drawer_menu3: // 내정보
                        drawerLayout.closeDrawer(navigationView);
                        return true;

                    case R.id.drawer_menu4: // 로그아웃
                        com.facebook.login.LoginManager.getInstance().logOut();

                        // 로그아웃을 위해 MainActivity 종료
                        MainActivity exitAct = (MainActivity) MainActivity.mExitAct;
                        exitAct.finish();

                        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                        startActivity(intent);
                        finish();

                        return true;
                }
                return false;

            }
        });
    }

    /**
     * 초기화
     */
    public void init() {

        // 사이드 메뉴에서 프로필 사진,이름,이메일을 입력
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        URL url = new URL("https://graph.facebook.com/" + mFbId + "/picture?type=large"); // URL 주소를 이용해서 URL 객체 생성
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();              //  아래 코드는 웹에서 이미지를 가져온 뒤
                        conn.setDoInput(true);
                        conn.connect();
                        mProfileImage = BitmapFactory.decodeStream(conn.getInputStream());               //  이미지 뷰에 지정할 Bitmap을 생성하는 과정

                        Thread.sleep(100);

                        ((CustomImageView) findViewById(R.id.header_photo)).setImageBitmap(mProfileImage);
                        ((TextView) findViewById(R.id.header_name)).setText(mFbName);
                        ((TextView) findViewById(R.id.header_email)).setText(mFbEmail);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    /**
     * 탭 메뉴 구현
     */
    public void initTab() {

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("날짜별"));//setIcon(android.R.drawable.ic_dialog_email));
        tabLayout.addTab(tabLayout.newTab().setText("관계별"));//setIcon(android.R.drawable.ic_dialog_dialer));
        tabLayout.addTab(tabLayout.newTab().setText("성과별"));//setIcon(android.R.drawable.ic_dialog_map));
        tabLayout.setTabTextColors(Color.rgb(200,200,200)/*getResources().getColor(R.color.tab_menu)*/, getResources().getColor(R.color.tab_menuSelect));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#fd6839"));
        //tabLayout.setDrawingCacheBackgroundColor(Color.parseColor("#fd6839"));

        final ViewPager viewPager = (ViewPager) findViewById(R.id.tab_view);
        final PagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}

