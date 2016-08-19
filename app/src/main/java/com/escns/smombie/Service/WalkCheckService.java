package com.escns.smombie.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.escns.smombie.DAO.Record;
import com.escns.smombie.DAO.User;
import com.escns.smombie.MainActivity;
import com.escns.smombie.Manager.DBManager;
import com.escns.smombie.Manager.GPSManager;

import java.util.Calendar;
import java.util.List;
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
    private Handler mHandlerMain;

    private double mStartLon = 0.0;
    private double mStartLat = 0.0;
    private double mLastLon = 0.0;
    private double mLastLat = 0.0;

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
    public WalkCheckService(Handler handlerMain) {
        Log.d("tag", "WalkCheckService");
        mHandlerMain = handlerMain;
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
                    if(!isWalking) {
                        mStartLon = mGpsManager.getmCurLon();
                        mStartLat = mGpsManager.getmCurLat();
                        isWalking = true;
                    }
                    Intent intent = new Intent("com.escns.smombie.LOCK_SCREEN_ON");
                    sendBroadcast(intent);
                } else {                                                                // 제자리일 때, GPS 작동 안할 때 잠금화면 종료

                    Intent intent = new Intent("com.escns.smombie.LOCK_SCREEN_OFF");
                    sendBroadcast(intent);

                    if(isWalking) {
                        mLastLon = mGpsManager.getmCurLon();
                        mLastLat = mGpsManager.getmCurLat();
                        isWalking = false;

                        double distance = mGpsManager.calculateDistance(mStartLon, mStartLat, mLastLon, mLastLat);

                        Calendar calendar = Calendar.getInstance();
                        Record record = new Record("hajaekwon", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR), (int)distance, 0);
                        mDbManager.insertRecord(record);

                        mDbManager.updateUser(new User("hajaekwon", (int)distance, 1000, 1, 100, 20000));

                        Message message = mHandlerMain.obtainMessage();
                        message.what = MainActivity.UPDATE_SECTION;
                        mHandlerMain.sendMessage(message);

                        List<Record> list = mDbManager.getRecord("hajaekwon");
                        for(Record s : list) {
                            Toast.makeText(getApplicationContext(), s.toString(), Toast.LENGTH_SHORT).show();
                            Log.i("tag", s.toString());
                            Toast.makeText(getBaseContext(), s.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
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

