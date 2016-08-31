package com.escns.smombie.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.escns.smombie.Interface.ApiService;
import com.escns.smombie.Manager.DBManager;
import com.escns.smombie.R;
import com.escns.smombie.Service.PedometerCheckService;
import com.escns.smombie.Utils.Global;
import com.escns.smombie.Utils.RandomAd;
import com.escns.smombie.View.LoopViewPager2;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2016-08-04.
 */

public class LockScreenReceiver extends BroadcastReceiver {

    private static final int VIEWPAGER_COUNT = 5;

    private DBManager mDbManger;
    private Retrofit mRetrofit;
    private ApiService mApiService;
    private SharedPreferences pref;

    private Context mContext;

    private WindowManager.LayoutParams mParams;     // 최상단에 그려질 뷰의 파라미터
    private static WindowManager mWindowManager;           // 최상단에 뷰를 그릴 WindowManager
    private static View mLockScreenView;                   // 최상단 뷰
    private LoopViewPager2 mLoopViewPager2;           // Viewpager
    private static View disableStatusBarView;

    int windowWidth;                                // 전체 윈도우 너비
    int windowHeight;                               // 전체 윈도우 높이

    private static boolean isLock;                  // lock의 상태
    private static boolean isRinging;

    private boolean isInit;

    Global global = Global.getInstance();

    /**
     * BroadcastReceiver에 메시지가 왔을 때 분류
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Tag", "LockReceiver - onReceive "+intent.getAction());

        mContext = context;

        if(!isInit) initUtils();

        String action = intent.getAction();

        // 어떤 메시지인지 확인
        if(action.equals(intent.ACTION_SCREEN_OFF)) {

            global.setIsScreen(false);

            drawLockScreen(context);

        } else if(action.equals("com.escns.smombie.CALL_STATE_RINGING")) {
            isRinging = true;
            if(mWindowManager!=null && isLock) {
                removeLockScreen();
                isLock=false;
            }

        } else if(action.equals("com.escns.smombie.CALL_STATE_OFFHOOK")) {
            isRinging = true;
            if(mWindowManager!=null && isLock) {
                removeLockScreen();
                isLock=false;
            }

        } else if(action.equals("com.escns.smombie.CALL_STATE_IDLE")) {
            isRinging = false;

        } else if(action.equals("com.escns.smombie.RESTART_SERVICE")) {
            Log.i("tag", "ACTION.RESTART.PedometerCheckService");

            Intent i = new Intent(context, PedometerCheckService.class);
            context.startService(i);
        } else if(action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.i("tag", "ACTION_BOOT_COMPLETED");

            Intent i = new Intent(context, PedometerCheckService.class);
            context.startService(i);
        }

        if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo.DetailedState state = info.getDetailedState();

            if (state == NetworkInfo.DetailedState.CONNECTED) {
                global.setIsNetworking(true);
                Toast.makeText(context, "인터넷이 연결되었습니다.", Toast.LENGTH_LONG).show();

            } else if (state == NetworkInfo.DetailedState.DISCONNECTED) {
                global.setIsNetworking(false);
                Toast.makeText(context, "인터넷 연결이 끊어졌습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void removeLockScreen() {
        if(mLockScreenView != null) {
            mWindowManager.removeView(mLockScreenView);
        }
        if(disableStatusBarView != null) {
            mWindowManager.removeView(disableStatusBarView);
        }
        mWindowManager = null;

        /*
        Intent i = new Intent();
        i.setAction("com.escns.smombie.REMOVE_BLANK_ACTIVITY");
        mContext.sendBroadcast(i);
        */
    }

    /**
     * mWindowManager를 이용하여 TYPE_TOAST 타입의 LockScreen 을 그린다.
     * @param context
     */
    private void drawLockScreen(Context context) {

        // lock이 활성화 된 상태라면 지우고 다시
        if(mWindowManager!=null && isLock) {
            if(mLockScreenView != null) {
                mWindowManager.removeView(mLockScreenView);
            }
            mWindowManager = null;
        }

        isLock = true;

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);                      // WindowManager 객체 생성
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);   // Layout을 가져오기 위해 LayoutInflater 객체 생성
        mLockScreenView = (View) inflater.inflate(R.layout.lockscreen, null);                                   // LayoutInflater를 이용하여 R.layout.lockscreen을 불러온다

        mLoopViewPager2 = (LoopViewPager2) mLockScreenView.findViewById(R.id.lockscreen_viewpager);
        RandomAd randomAd = new RandomAd();
        mLoopViewPager2.setSliders_url(randomAd.getRandomAdUrl(VIEWPAGER_COUNT));
        mLoopViewPager2.setWaterMarkGone();

        mParams = new WindowManager.LayoutParams(                                       // View의 파라미터 결정
                WindowManager.LayoutParams.TYPE_TOAST,                           // 최상단 뷰로 설정
                WindowManager.LayoutParams.FLAG_FULLSCREEN                    // 풀 스크린으로 설정
        );
        mParams.screenOrientation=12;                                                   // 반드시 세로

        windowHeight = mWindowManager.getDefaultDisplay().getHeight();
        windowWidth = mWindowManager.getDefaultDisplay().getWidth();

        init();

        mWindowManager.addView(mLockScreenView, mParams);

        disableStatusBarView = new View(context);

        WindowManager.LayoutParams handleParams = new WindowManager.LayoutParams(
        WindowManager.LayoutParams.TYPE_TOAST,
                // this is to keep button presses going to the background window
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        // this is to enable the notification to recieve touch events
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        // Draws over status bar
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);

        handleParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        handleParams.height = getStatusBarHeight();

        Log.i("tag", ""+getStatusBarHeight());

        handleParams.gravity = Gravity.TOP;
        mWindowManager.addView(disableStatusBarView, handleParams);

        /*
        Intent intent = new Intent(mContext, BlankActivity.class);
        PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        try {
            pi.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
        */
    }

    /**
     * LockScreen 내부의 View들의, 특히 버튼 초기설정을 해준다.
     */
    public void init() {

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
                        mLoopViewPager2.setTouchFlag(false);
                        //layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, mContext.getResources().getDisplayMetrics());
                        button_lock.setImageResource(R.drawable.btn_key_tap);
                        button_unlock.setImageResource(R.drawable.btn_unlock);
                        break;
                    case MotionEvent.ACTION_MOVE:

                        int x_cord = (int)event.getRawX();
                        int btnWidth = button_lock.getWidth();

                        if(x_cord>windowWidth - btnWidth- offsetRight) {
                            if(mWindowManager != null) {
                                removeLockScreen();
                                isLock=false;

                                queryFail();

                                break;
                            }
                        }
                        if(x_cord - btnWidth - offsetLeft < 0) {x_cord=offsetLeft+btnWidth;}

                        // 왼쪽 margin 값을 터치 위치로 변경함으로써 마치 움직이는 것처럼 보인다
                        layoutParams.leftMargin = x_cord - btnWidth - offsetLeft;

                        // 변경된 파라미터를 적용
                        button_lock.setLayoutParams(layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        mLoopViewPager2.setTouchFlag(true);
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
    }

    /**
     * Receiver에서 사용할 객체들의 초기화를 해준다.
     */
    public void initUtils() {
        isInit = true;

        mDbManger = new DBManager(mContext);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        mRetrofit = new Retrofit.Builder().baseUrl(mApiService.API_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
        mApiService = mRetrofit.create(ApiService.class);

        pref = mContext.getSharedPreferences(mContext.getResources().getString(R.string.app_name), MODE_PRIVATE);
    }

    /**
     * LockScreen 잠금해제 시 포인트적립에 실패했다는 메시지를 Service로 보낸다.
     */
    public void queryFail() {

        global.setIsScreen(true);
        global.setIsWalking(0);
    }

    /**
     * 상단의 상태바 클릭을 막기 위해서 보이지 않는 상태를 그릴 때 필요한 상태바의 높이를 계산
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}