package com.escns.smombie.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.escns.smombie.Manager.DBManager;
import com.escns.smombie.Manager.GPSManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016-08-02.
 */

public class WalkCheckService extends Service {

    private DBManager mDbManager;

    private GPSManager mGpsManager;

    private TimerTask myTimer;
    private Handler handler;
    private boolean isWalking = false;

    private final IBinder mBinder = new LocalBinder();

    /**
     * 다른 프로세스들도 Service에 접근이 가능하게 해주는 Binder를 리턴해주기 위한 Binder 생성
     */
    public class LocalBinder extends Binder {
        WalkCheckService getService() {
            return WalkCheckService.this;
        }
    }

    /**
     * Binder 리턴
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * WalkCheckService 생성자
     */
    public WalkCheckService() {
        Log.d("tag", "WalkCheckService");
    }

    /**
     *  GPSManager를 이용하여 이동을 확인
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("tag", "WalkCheckService onCreate");

        // DB 객체화
        mDbManager = new DBManager(this); // DB 객체화

        // GPS 객체화
        mGpsManager = new GPSManager(getApplicationContext());

        myTimer = new TimerTask() {
            @Override
            public void run() {
                Log.d("tag", "5초마다 한번씩 동작!!!");
                handler.sendMessage(handler.obtainMessage());
            }
        };
        Timer timer = new Timer();
        timer.schedule(myTimer,3000,3000); // App 시작 5초 이후에 5초마다 실행

        // handler 안에 들어갈 코드
        handler = new Handler() {
            public void handleMessage(Message msg) {

                int state = mGpsManager.getData();

                if (state == 2) {                                                       // 걸을 때 잠금화면 실행
                    isWalking = true;
                    Intent intent = new Intent("com.escns.smombie.LOCK_SCREEN_ON");
                    sendBroadcast(intent);
                } else {                                                                // 제자리일 때, GPS 작동 안할 때 잠금화면 종료
                    isWalking = false;
                    Intent intent = new Intent("com.escns.smombie.LOCK_SCREEN_OFF");
                    sendBroadcast(intent);
                }
            }

        };
    }

    /**
     * Service 종료 될때 사용하던 변수들, broadcast 해제
     */
    @Override
    public void onDestroy() {
        myTimer.cancel();

        isWalking = false;
        Intent intent = new Intent("com.escns.smombie.LOCK_SCREEN_OFF");
        sendBroadcast(intent);

        super.onDestroy();
    }
}

