package com.escns.smombie.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.escns.smombie.DAO.Record;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hyo99 on 2016-08-24.
 */

public class PedometerCheckService extends Service {

    private Context mContext;
    private SharedPreferences pref;         // 화면 꺼짐 및 이동 시 switch가 초기화되기 때문에 파일에 따로 저장하기 위한 객체

    private long lastTime; // 만보기: 이전 시간
    private float speed; // 만보기: 가속도
    private float lastX; // 만보기: 마지막 x축 위치
    private float lastY; // 만보기: 마지막 y축 위치
    private float lastZ; // 만보기: 마지막 z축 위치
    private float x, y, z; // 만보기: 현재 x,y,z축 위치

    private static final int SHAKE_THRESHOLD = 200; // 만보기: 흔듦을 감지하는 민감도
    private static final int DATA_X = SensorManager.DATA_X; // 만보기: 센서에서 x축 데이터
    private static final int DATA_Y = SensorManager.DATA_Y; // 만보기: 센서에서 y축 데이터
    private static final int DATA_Z = SensorManager.DATA_Z; // 만보기: 센서에서 z축 데이터

    private SensorManager sensorManager; // 만보기: 센서를 쓰기위한 관리클래스
    private Sensor accelerormeterSensor; // 만보기: 센서

    Calendar c;
    private int mIdInt;
    private int mYear;
    private int mMonth;
    private int mDate;
    private int mHour;
    private int mDist;
    Record record;

    boolean isWalkingNow = false; // 만보기: 제자리이면 false / 걷는상태이면 true

    private TimerTask myTimer;
    private Handler handler;
    boolean isWalkingPast = false;


    // 다른 프로세스들도 Service에 접근이 가능하게 해주는 Binder를 리턴해주기 위한 Binder 생성
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        PedometerCheckService getService() {
            return PedometerCheckService.this;
        }
    }

    /**
     * 생성자
     */
    public PedometerCheckService() {
        Log.d("tag", "Pedometer On");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("tag", "WalkCheckThread onCreate");

        mContext = getApplicationContext();
        pref = mContext.getSharedPreferences("pref", mContext.MODE_PRIVATE);
        c = Calendar.getInstance();
        record = new Record(0,0,0,0,0,0);

        // 만보기: 가속도센서 초기화
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // 만보기: 가속도센서 감지 시작 - 만보기
        if (accelerormeterSensor != null)
            sensorManager.registerListener(sensorEventListener, accelerormeterSensor, SensorManager.SENSOR_DELAY_GAME);

        myTimer = new TimerTask() {
            @Override
            public void run() {
                Log.d("tag", "3초마다 한번씩 동작!!!");
                handler.sendMessage(handler.obtainMessage());
            }
        };
        Timer timer = new Timer();
        timer.schedule(myTimer,1000,1000); // App 시작 3초 이후에 3초마다 실행

        handler = new Handler() {
            public void handleMessage(Message msg) {
                if(isWalkingNow) {

                    mIdInt = pref.getInt("USER_ID_INT", 0);
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH)+1;
                    mDate = c.get(Calendar.DATE);
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mDist = 0;

                    Intent intent = new Intent("com.escns.smombie.LOCK_SCREEN_ON");
                    sendBroadcast(intent);
                    isWalkingPast = true;
                }
                else {
                    if(!isWalkingPast) {


                        record.setmIdInt(mIdInt);
                        record.setmYear(mYear);
                        record.setmMonth(mMonth);
                        record.setmDay(mDate);
                        record.setmHour(mHour);
                        record.setmDist(mDist);

                        Intent intent = new Intent("com.escns.smombie.LOCK_SCREEN_OFF");
                        sendBroadcast(intent);
                    }
                    isWalkingPast = false;
                }
            }

        };

    }

    @Override
    public void onDestroy() {
        Log.d("tag", "WalkCheckThread onDestroy");

        // 만보기: 가속도센서 감지 끝
        if (sensorManager != null)
            sensorManager.unregisterListener(sensorEventListener);

        myTimer.cancel();

        isWalkingNow = false;
        Intent intent = new Intent("com.escns.smombie.LOCK_SCREEN_OFF");
        sendBroadcast(intent);

        super.onDestroy();
    }

    /**
     * 가속도센서에서 값을 받아와 걸음수를 체크하는 이벤트리스너
     */
    private SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                long currentTime = System.currentTimeMillis();
                long gabOfTime = (currentTime - lastTime);
                if (gabOfTime > 100) {
                    lastTime = currentTime;
                    x = event.values[SensorManager.DATA_X];
                    y = event.values[SensorManager.DATA_Y];
                    z = event.values[SensorManager.DATA_Z];

                    speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;

                    // 흔듦 이벤트발생!!
                    if(speed > SHAKE_THRESHOLD) {
                        // 가속도센서를 이용해 걸음수를 측정
                        Log.d("tag", "onSensorChanged SHAKE !!");

                        mDist++;

                        isWalkingNow = true;
                    }
                    else {
                        isWalkingNow = false;
                    }

                    lastX = event.values[DATA_X];
                    lastY = event.values[DATA_Y];
                    lastZ = event.values[DATA_Z];
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public boolean isWalking() {
        return isWalkingNow;
    }

    // Binder 리턴
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

}
