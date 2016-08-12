package com.escns.smombie;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hyo99 on 2016-08-10.
 */

public class MainActivity extends AppCompatActivity {

    private TextView stepView; // 화면에 출력되는 걸음 수

    String fbId, fbName; // 페이스북으로부터 id, 이름을 받아올 변수
    Bitmap myBitmap; // 페이스북부터 사진을 받아올 객체

    ImageView headerPhoto; // 사이드 메뉴에 사용자 이름,이메일
    TextView headerName; // 사이드 메뉴에 사용자 사진

    private SharedPreferences pref; // 화면 꺼짐 및 이동 시 switch가 초기화되기 때문에 파일에 따로 저장하기 위한 객체
    private SwitchCompat swc; // 화면에 출력되는 lock 스위치

    DBManager dbManager; // DB 선언
    int StepCount=0; // 걸음 수

    private WalkCheckThread mService;
    private boolean mBound = false; // WalkCheckThread Service가 제대로 동작하면 true 아니면 false

    /**
     * UI thread에서 mHandler로 message를 보내면 logView를 갱신시켜준다.
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            StepCount = dbManager.printData(); // DB에서 걸음수 불러오기
            stepView.setText("" + StepCount);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DB 객체화
        dbManager = new DBManager(this, "RECORD_LIST", "(number INTEGER)");
        // 테이블에 행이 하나도 없을 때 한 줄 추가
        if (dbManager.getRowCount() == 0) {
            dbManager.insertRow("(0)");
        }

        fbId = getIntent().getStringExtra("id");
        fbName = getIntent().getStringExtra("name");
        Log.d("tag", "id : " + fbId);
        Log.d("tag", "name : " + fbName);

        initDrawer(); // 툴바 구현
        init(); // Lock 화면 구현
    }

    @Override
    protected void onDestroy() {
        if(mBound) {
            //pref = getSharedPreferences("pref", MODE_PRIVATE);
            //if(!pref.getBoolean("switch", true))
            //unbindService(mConnection); // 만보기 끝
            mBound = false;
        }
        super.onDestroy();
    }

    /**
     * 툴바를 구현하는 함수
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
                    case R.id.drawer_menu1 : // 어워드
                        return true;
                    case R.id.drawer_menu2 : // 그래프
                        return true;
                    case R.id.drawer_menu3 : // 설정
                        return true;
                    case R.id.drawer_menu4 : // 내정보
                        return true;
                    case R.id.drawer_menu5 : // 로그아웃
                        com.facebook.login.LoginManager.getInstance().logOut();
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
     * Lock 화면을 구현하는 함수
     */
    public void init() {
        stepView = (TextView) findViewById(R.id.stepView);

        pref = getSharedPreferences("pref", MODE_PRIVATE);

        // Lock on 스위치
        swc = (SwitchCompat) findViewById(R.id.switchLock);
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

        StepCount = dbManager.printData(); // DB에서 걸음수 불러오기
        stepView.setText("" + StepCount);


        // UI 변경을 위한 Thread 생성 --> Because 서브쓰레드는 직접적으로 UI를 변경시킬 수 없다
        Thread thread =  new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try{
                        URL url = new URL("https://graph.facebook.com/" + fbId + "/picture?type=large"); // URL 주소를 이용해서 URL 객체 생성

                        //  아래 코드는 웹에서 이미지를 가져온 뒤
                        //  이미지 뷰에 지정할 Bitmap을 생성하는 과정
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        Bitmap bit = BitmapFactory.decodeStream(is);
                        myBitmap = getRoundedCornerBitmap(bit);

                        Thread.sleep(100);

                        headerName.setText(fbName);  // 페이스북 이름 입력
                        headerPhoto.setImageBitmap(myBitmap); // 페이스북 사진 입력

                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    if(mBound) {
                        // mHandler로 메시지를 보낸다.
                        //step = mService.getStep();
                        mHandler.sendMessage(mHandler.obtainMessage());
                    }
                }
            }
        });
        thread.start();
    }

    /**
     * 페이스북으로부터 받아온 사진을 편집해주는 함수
     * @param bitmap
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {

        // 원 모양으로 편집
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        int size = (bitmap.getWidth()/2);
        canvas.drawCircle(size, size, size, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;

        /*
        // 모서리를 라운딩으로 편집
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 50; // 테두리 곡률 설정

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
         */
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
}
