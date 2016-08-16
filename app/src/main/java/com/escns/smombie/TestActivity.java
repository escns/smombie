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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.escns.smombie.View.CustomImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class TestActivity extends AppCompatActivity {

    private SharedPreferences pref;         // 화면 꺼짐 및 이동 시 switch가 초기화되기 때문에 파일에 따로 저장하기 위한 객체
    private DBManager dbManager;            // DB 선언

    private String fbId;
    private String fbName;
    private String fbEmail;

    private CustomImageView profileImage;
    private Bitmap bit;

    private WalkCheckThread mService;
    private boolean mBound = false; // WalkCheckThread Service가 제대로 동작하면 true 아니면 false

    boolean isProfileImageLoaded;

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Log.i("tag", "handleMessage");
            profileImage.setImageBitmap(bit);
            isProfileImageLoaded=true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

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
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(TestActivity.this, drawerLayout, toolbar, R.string.hello, R.string.hello) {
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
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.id_stickynavlayout_innerscrollview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CustomAdapter());

        fbId = getIntent().getStringExtra("id");
        fbName = getIntent().getStringExtra("name");
        fbEmail = getIntent().getStringExtra("email");

        profileImage = (CustomImageView) findViewById(R.id.profile_view);
        //Glide.with(this).load("https://graph.facebook.com/" + fbId + "/picture?type=large").into(profileImage);

        Thread thread =  new Thread(new Runnable() {
            @Override
            public void run() {
                while(!isProfileImageLoaded) {
                    try{
                        URL url = new URL("https://graph.facebook.com/" + fbId + "/picture?type=large"); // URL 주소를 이용해서 URL 객체 생성

                        //  아래 코드는 웹에서 이미지를 가져온 뒤
                        //  이미지 뷰에 지정할 Bitmap을 생성하는 과정
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        bit = BitmapFactory.decodeStream(is);

                        Thread.sleep(100);

                        Log.i("tag", "get FB profile image");
                        //profileImage.setImageBitmap(bit); // 페이스북 사진 입력
                        handler.sendMessage(handler.obtainMessage());
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        TextView profileName = (TextView) findViewById(R.id.profile_name);
        profileName.setText(fbName);
        TextView profileEmail = (TextView) findViewById(R.id.profile_email);
        profileEmail.setText(fbEmail);


        pref = getSharedPreferences("pref", MODE_PRIVATE);

        // Lock on 스위치
        SwitchCompat swc = (SwitchCompat) findViewById(R.id.switchLock);
        swc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //isChecked = pref.getBoolean("switch", true);

                if(isChecked) {
                    // 화면이 꺼지고 켜질 때 Lock의 값이 초기화 되기 때문에
                    // SharedPreferences을 사용하여 값을 파일에 저장시켜둔다
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("switch", true);
                    editor.commit();

                    Intent intent = new Intent("com.escns.smombie.service");
                    intent.setPackage("com.escns.smombie");
                    bindService(intent, mConnection, Context.BIND_AUTO_CREATE); // 만보기 동작
                } else {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("switch", false);
                    editor.commit();

                    unbindService(mConnection);
                }
            }
        });
    }

    // ThreadService와 MainActivity를 연결 시켜줄 ServiceConnection
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        // 리턴되는 Binder를 다시 Service로 꺼내서 ThreadSerivce내부의 함수 사용이 가능하다.
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            WalkCheckThread.LocalBinder binder = (WalkCheckThread.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            Log.d("tag", "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            Log.d("tag", "onServiceDisconnected");
        }
    };


    /**
     * 메인 화면 하단의 ScrollView과 Item들을 연결해주는 Adapter. 추후 아이템 Layout에 따라 수정 필요
     */
    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private List<String> mDatas = new ArrayList<String>();

        public CustomAdapter() {
            for (int i = 0; i < 50; i++)
            {
                mDatas.add("Test" + " -> " + i);
                Log.i("tag", "CustomAdapter " + mDatas.get(i));
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Log.i("tag", "onBindViewHolder" + mDatas.get(position));
            holder.tvNature.setText(mDatas.get(position));
        }


        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tvNature;

            public ViewHolder(View itemView) {
                super(itemView);
                tvNature = (TextView) itemView.findViewById(R.id.id_info);
            }
        }
    }
}
