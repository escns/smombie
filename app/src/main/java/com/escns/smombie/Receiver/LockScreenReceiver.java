package com.escns.smombie.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.escns.smombie.R;
import com.escns.smombie.Utils.RandomAd;
import com.escns.smombie.View.LoopViewPager2;

/**
 * Created by Administrator on 2016-08-04.
 */

public class LockScreenReceiver extends BroadcastReceiver {

    private static final int VIEWPAGER_COUNT = 5;

    private Context mContext;

    private WindowManager.LayoutParams mParams;     // 최상단에 그려질 뷰의 파라미터
    private WindowManager mWindowManager;           // 최상단에 뷰를 그릴 WindowManager
    private View mLockScreenView;                   // 최상단 뷰
    private LoopViewPager2 mLoopViewPager2;           // Viewpager

    int windowWidth;                                // 전체 윈도우 너비
    int windowHeight;                               // 전체 윈도우 높이

    private static boolean isLock;                  // lock의 상태
    private static boolean isWalking;
    private static boolean isRinging;

    /**
     * BroadcastReceiver에 메시지가 왔을 때 분류
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Tag", "LockReceiver - onReceive "+intent.getAction());

        mContext = context;

        String action = intent.getAction();

        // 어떤 메시지인지 확인
        if(action.equals(intent.ACTION_SCREEN_OFF)) {
            if(isWalking && !isRinging) {

                drawLockScreen(context);

            }
        } else if(action.equals("com.escns.smombie.CALL_STATE_RINGING")) {
            isRinging = true;
            if(mWindowManager!=null && isLock) {
                mWindowManager.removeView(mLockScreenView);
                mWindowManager = null;
                isLock=false;
            }

        } else if(action.equals("com.escns.smombie.CALL_STATE_OFFHOOK")) {
            isRinging = true;
            if(mWindowManager!=null && isLock) {
                mWindowManager.removeView(mLockScreenView);
                mWindowManager = null;
                isLock=false;
            }

        } else if(action.equals("com.escns.smombie.CALL_STATE_IDLE")) {
            isRinging = false;

        } else if(action.equals("com.escns.smombie.LOCK_SCREEN_ON")) {

            if(isLock) Log.i("tag", "isLock = true");
            else Log.i("tag", "isLock = false");
            if(isWalking) Log.i("tag", "isWalking = true");
            else Log.i("tag", "isWalking = false");

            if(!isLock && !isRinging) {
                isWalking=true;
                drawLockScreen(context);
            }
        } else if(action.equals("com.escns.smombie.LOCK_SCREEN_OFF")) {
            isWalking=false;
            if(mWindowManager!=null) {
                mWindowManager.removeView(mLockScreenView);
                mWindowManager = null;
                isLock=false;
            }
        }
    }

    /**
     * LockScreen 을 그린다.
     * @param context
     */
    private void drawLockScreen(Context context) {

        // lock이 활성화 된 상태라면 지우고 다시
        if(mWindowManager!=null && isLock) {
            mWindowManager.removeView(mLockScreenView);
            mWindowManager = null;
        }

        isLock = true;

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);                      // WindowManager 객체 생성
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);   // Layout을 가져오기 위해 LayoutInflater 객체 생성
        mLockScreenView = (View) inflater.inflate(R.layout.lockscreen, null);                                   // LayoutInflater를 이용하여 R.layout.lockscreen을 불러온다

        mLoopViewPager2 = (LoopViewPager2) mLockScreenView.findViewById(R.id.lockscreen_viewpager);
        RandomAd randomAd = new RandomAd();
        mLoopViewPager2.setSliders_url(randomAd.getRandomAdUrl(VIEWPAGER_COUNT));

        mParams = new WindowManager.LayoutParams(                                       // View의 파라미터 결정
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,                           // 최상단 뷰로 설정
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN                    // 풀 스크린으로 설정
        );
        mParams.screenOrientation=12;                                                   // 반드시 세로

        windowHeight = mWindowManager.getDefaultDisplay().getHeight();
        windowWidth = mWindowManager.getDefaultDisplay().getWidth();

        init();

        mWindowManager.addView(mLockScreenView, mParams);
    }

    /**
     * LockScreen 내부의 View들의 초기설정을 해준다.
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
                            mWindowManager.removeView(mLockScreenView);
                            mWindowManager = null;
                            isLock=false;



                            break;
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
}
