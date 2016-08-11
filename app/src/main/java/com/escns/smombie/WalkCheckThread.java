package com.escns.smombie;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Administrator on 2016-08-02.
 */

public class WalkCheckThread extends Service {

    private long lastTime; // 만보기: 이전 시간
    private float speed; // 만보기: 가속도
    private float lastX; // 만보기: 마지막 x축 위치
    private float lastY; // 만보기: 마지막 y축 위치
    private float lastZ; // 만보기: 마지막 z축 위치
    private float x, y, z; // 만보기: 현재 x,y,z축 위치

    private static final int SHAKE_THRESHOLD = 800; // 만보기: 흔듦을 감지하는 민감도
    private static final int DATA_X = SensorManager.DATA_X; // 만보기: 센서에서 x축 데이터
    private static final int DATA_Y = SensorManager.DATA_Y; // 만보기: 센서에서 y축 데이터
    private static final int DATA_Z = SensorManager.DATA_Z; // 만보기: 센서에서 z축 데이터

    private SensorManager sensorManager; // 만보기: 센서를 쓰기위한 관리클래스
    private Sensor accelerormeterSensor; // 만보기: 센서

    double lastTimer; // 만보기: 이전 시간
    double curTimer; // 만보기: 현재 시간
    float lastSpeed; // 만보기: 이전 가속도
    boolean isWalking = false; // 만보기: 제자리이면 false / 걷는상태이면 true

    DBManager dbManager; // DB 선언
    int StepCount = 0; // 걸음 수

    // 다른 프로세스들도 Service에 접근이 가능하게 해주는 Binder를 리턴해주기 위한 Binder 생성
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        WalkCheckThread getService() {
            return WalkCheckThread.this;
        }
    }

    /**
     * 생성자
     */
    public WalkCheckThread() {
        Log.d("tag", "WalkCheckThread");
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

                    //curTimer = System.currentTimeMillis();

                    // 1초전 스피드와 현재 스피드를 비교하여 제자리/걷기를 구분
                    //if( ((curTimer - lastTimer) / 1000) > 1 ) {
                    //    if(lastSpeed < SHAKE_THRESHOLD && speed < SHAKE_THRESHOLD) { // 흔들리지 않음
                    //        isWalking = false;
                    //    }
                    //    else { // 흔들림
                    //        isWalking = true;
                    //    }
                    //    lastSpeed = speed;
                    //    lastTimer = curTimer;
                    //}

                    // 흔듦 이벤트발생!!
                    if(speed > SHAKE_THRESHOLD) {
                        // 가속도센서를 이용해 걸음수를 측정
                        Log.d("tag", "onSensorChanged SHAKE !!");
                        StepCount = dbManager.printData(); // DB에서 걸음 수 불러오기
                        StepCount++; // 걸음 수 증가
                        dbManager.inputQuery("UPDATE RECORD_LIST SET number=" + StepCount + " WHERE number=" + (StepCount - 1)); // DB에 걸음수 업데이트
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

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("tag", "WalkCheckThread onCreate");

        lastTimer = System.currentTimeMillis();

        lastSpeed = 0;

        // 만보기: 가속도센서 초기화
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // 만보기: 가속도센서 감지 시작 - 만보기
        if (accelerormeterSensor != null)
            sensorManager.registerListener(sensorEventListener, accelerormeterSensor, SensorManager.SENSOR_DELAY_GAME);

        // DB 객체화
        dbManager = new DBManager(this, "RECORD_LIST", "(number INTEGER)"); // DB 객체화

        // 잠금화면 실행
        startService(new Intent(WalkCheckThread.this, LockScreenService.class));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("tag", "WalkCheckThread onStartCommand");

        //new Thread(mRun).start();
        Log.d("tag", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("tag", "WalkCheckThread onDestroy");

        // 만보기: 가속도센서 감지 끝
        if (sensorManager != null)
            sensorManager.unregisterListener(sensorEventListener);
        //if(isWalking) {
        //    isWalking=false;
        //    // Service가 끝나면 자금화면 종료
            stopService(new Intent(WalkCheckThread.this, LockScreenService.class));
        //}
        super.onDestroy();
    }

    // Binder 리턴
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}

