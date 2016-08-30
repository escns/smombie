package com.escns.smombie.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.escns.smombie.DAO.Record;
import com.escns.smombie.DAO.User;
import com.escns.smombie.Interface.ApiService;
import com.escns.smombie.MainActivity;
import com.escns.smombie.Manager.DBManager;
import com.escns.smombie.R;
import com.escns.smombie.Receiver.LockScreenReceiver;
import com.escns.smombie.Utils.Global;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hyo99 on 2016-08-24.
 */

/**
 * 가속도 센서를 이용해 걷는지 안걷는지를 판단하기 위한 class
 */
public class PedometerCheckService extends Service {

    private Context mContext;
    private SharedPreferences pref;         // 화면 꺼짐 및 이동 시 switch가 초기화되기 때문에 파일에 따로 저장하기 위한 객체

    private Retrofit mRetrofit;     // 서버와 통신을 위한 Retrofit
    private ApiService mApiService; // 서버와 통신을 위한 ApiService

    private LockScreenReceiver mReceiver;

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

    private Calendar c; // 날짜를 받아오기위한 객체
    private int mIdInt; // 사용자 아이디
    private int mYear; // 현재 날짜
    private int mMonth; // 현재 달
    private int mDate; // 현재 일
    private int mHour; // 현재 시간
    private int mDist; // 걸은 거리
    private int mBeforeDist; // 전 걸은거리

    private int noSaveConut;

    private DBManager mDbManger;
    private Record record;
    List<Record> list;

    Global global = Global.getInstance();

    private TimerTask myTimer; // 주기적으로 데이터를 DB에 저장해주기 위한 시간변수
    private Handler handler; // 데이터 값을 DB에 저장하기 위한 핸들러

    private CountDownTimer mCountDownTimer;

    // Binder 리턴
    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    /**
     * onCreate
     */
    @Override
    public void onCreate() {
        unregisterRestartAlarm();
        super.onCreate();
        Log.d("tag", "WalkCheckThread onCreate");

        init();
        startSensor();

        startReceiver();
    }

    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon_logo)
                        .setContentTitle("Smombie")
                        .setContentText("도보 시 잠금화면이 실행됩니다.");

        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(mContext);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(resultIntent);

        PendingIntent pendingIntent =
                taskStackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(pendingIntent);

        startForeground(9510, builder.build());

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Receiver가 동작
     */
    private void startReceiver() {
        try {
            // BroadCastReceiver의 필터 설정
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
            mReceiver = new LockScreenReceiver();
            registerReceiver(mReceiver, filter);

            PhoneStateListener phoneStateListener = new PhoneStateListener();
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            telephonyManager.listen(phoneStateListener, android.telephony.PhoneStateListener.LISTEN_CALL_STATE);

            Log.d("tag", "stopSensor - send LOCK_SCREEN_OFF to Broadcast");
            Intent intent = new Intent("com.escns.smombie.LOCK_SCREEN_ON");
            sendBroadcast(intent);

        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * 가속도센서가 동작을 시작할 때 발생할 이벤트들
     */
    private void startSensor() {
        // 만보기: 가속도센서 감지 시작 - 만보기
        if (accelerormeterSensor != null)
            sensorManager.registerListener(sensorEventListener, accelerormeterSensor, SensorManager.SENSOR_DELAY_GAME);

        // 주기적으로 데이터를 저장하기 위함
        myTimer = new TimerTask() {
            @Override
            public void run() {
                //Log.d("tag", "startSensor - 1초마다 한번씩 동작!!!");
                handler.sendMessage(handler.obtainMessage());
            }
        };
        Timer timer = new Timer();
        timer.schedule(myTimer,3000,3000); // App 시작 1초 이후에 1초마다 실행

        // 데이터를 DB에 저장하기 위한 핸들러
        handler = new Handler() {
            public void handleMessage(Message msg) {

                // Local DB 업데이트
                record.setmDist(mDist);
                mDbManger.updateLastRecord(record);
                Log.d("tag", "로컬 DB 업데이트!!! mDist = " + record.getmDist());
                Log.d("tag", "현재 데이터 수!!! cnt = " + mDbManger.getRowCount());


                // 밀어서 잠금해제 했을 때 / 인터넷이 연결되 있을 때 / 누적된 데이터가 없을 때 --> 서버에 업데이트
                if(global.getIsWalking() == 0 && global.getIsNetworking() && noSaveConut == 0) {
                    Log.d("tag", "서버 DB 업데이트!!!");
                    insertRecordData();

                    updateUserFunc();

                    // 새로운 행에 기록하기 위한 초기화 부분
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH)+1;
                    mDate = c.get(Calendar.DATE);
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mDist = 0;
                    mBeforeDist = 0;
                    record = new Record(mIdInt,mYear,mMonth,mDate,mHour,mDist);
                    mDbManger.insertRecord(record);

                    global.setIsWalking(1);
                }
                // 밀어서 잠금해제 했을 때 / 인터넷이 연결되 있을 때 / 누적된 데이터가 없을 때 --> 서버에 업데이트
                else if (global.getIsWalking() == 0 && global.getIsNetworking() && noSaveConut > 0) {
                    Log.d("tag", "서버 DB 누적 업데이트!!!");
                    list = mDbManger.getRecord();

                    for(int i = list.size()-noSaveConut-1; i<list.size(); i++) {
                        mYear = list.get(i).getmYear();
                        mMonth = list.get(i).getmMonth();
                        mDate = list.get(i).getmDay();
                        mHour = list.get(i).getmHour();
                        mDist = list.get(i).getmDist();
                        Log.d("tag", "데이터 거리 : " + mDist);
                        try {
                            Thread.sleep(1000);
                        }
                        catch (Exception e) {

                        }
                        insertRecordData();
                    }

                    updateUserFunc();

                    // 새로운 행에 기록하기 위한 초기화 부분
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH)+1;
                    mDate = c.get(Calendar.DATE);
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mDist = 0;
                    mBeforeDist = 0;
                    record = new Record(mIdInt,mYear,mMonth,mDate,mHour,mDist);
                    mDbManger.insertRecord(record);

                    noSaveConut = 0;
                    global.setIsWalking(1);

                }
                // 밀어서 잠금해제 했을 때 / 인터넷이 연결 안되 있을 때 --> 서버에 업데이트 못함, 데이터 누적
                else if(global.getIsWalking() == 0 && !global.getIsNetworking()) {
                    Log.d("tag", "서버 DB 업데이터 못함!!!");
                    noSaveConut ++;

                    updateUserFunc();

                    // 새로운 행에 기록하기 위한 초기화 부분
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH)+1;
                    mDate = c.get(Calendar.DATE);
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mDist = 0;
                    mBeforeDist = 0;
                    record = new Record(mIdInt,mYear,mMonth,mDate,mHour,mDist);
                    mDbManger.insertRecord(record);

                    global.setIsWalking(1);
                }



            }
        };
    }

    /**
     * 초기화
     */
    private void init() {
        // 객체 할당
        mContext = getApplicationContext();
        pref = mContext.getSharedPreferences(getResources().getString(R.string.app_name), mContext.MODE_PRIVATE);
        mDbManger = new DBManager(mContext);
        list = null;
        list = new ArrayList<>();
        list = mDbManger.getRecord();

        c = Calendar.getInstance();
        mIdInt = pref.getInt("USER_ID_INT", 0);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH)+1;
        mDate = c.get(Calendar.DATE);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mDist = 0;
        mBeforeDist = 0;
        noSaveConut = 0;
        record = new Record(mIdInt,mYear,mMonth,mDate,mHour,mDist);
        mDbManger.insertRecord(record);

        // 서버와의 통신
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        mRetrofit = new Retrofit.Builder().baseUrl(mApiService.API_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
        mApiService = mRetrofit.create(ApiService.class);

        // 만보기: 가속도센서 초기화
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        countDownTimer();
        mCountDownTimer.start();

    }

    public void countDownTimer(){

        mCountDownTimer = new CountDownTimer(1000*1000, 1000) {
            public void onTick(long millisUntilFinished) {

                //Log.i("PersistentService","onTick");
            }
            public void onFinish() {

                Log.i("PersistentService","onFinish");
            }
        };
    }

    public void updateUserFunc() {

        // SharedPreferences 유저데이터 업데이트
        // Point, GOAL, REWORD
        int cnt = pref.getInt("POINT", 0) + mDist;
        pref.edit().putInt("POINT", (cnt%pref.getInt("GOAL", 0))).commit();
        int reward = pref.getInt("REWORD", 0) + (cnt/pref.getInt("GOAL", 0));
        pref.edit().putInt("REWORD", reward ).commit();

        // AvgDist 하루평균이동거리 구하기
        list = mDbManger.getRecord();
        int totalDist = 0;
        int dateCnt = 0;
        int tempYear = 0;
        int tempMonth = 0;
        int tempDate = 0;
        for(int i=0; i<list.size(); i++) {
            totalDist += list.get(i).getmDist();
            if(tempYear != list.get(i).getmYear() || tempMonth != list.get(i).getmMonth() || tempDate != list.get(i).getmDay()) {
                tempYear = list.get(i). getmYear();
                tempMonth = list.get(i).getmMonth();
                tempDate = list.get(i).getmDay();
                dateCnt++;
            }
        }
        totalDist /= dateCnt;
        pref.edit().putInt("AVGDIST", totalDist).commit();

        User user = new User(
                pref.getInt("USER_ID_INT", 0),
                pref.getString("USER_ID_TEXT", ""),
                pref.getString("NAME", ""),
                pref.getString("EMAIL", ""),
                pref.getString("GENDER", ""),
                pref.getInt("AGE", 0),
                pref.getInt("POINT", 0),
                pref.getInt("GOAL", 0),
                pref.getInt("REWORD", 0),
                pref.getInt("SUCCESSCNT", 0),
                pref.getInt("FAILCNT", 0),
                pref.getInt("AVGDIST", 0)
        );
        updateUserData(user);
    }

    /**
     * 스위치가 OFF가 되면 가속도센서, 주기시간, 잠금화면이 비활성화
     */
    @Override
    public void onDestroy() {
        Log.d("tag", "WalkCheckThread onDestroy");

        // 만보기: 가속도센서 감지 끝
        if (sensorManager != null)
            sensorManager.unregisterListener(sensorEventListener);

        myTimer.cancel();

        Log.d("tag", "stopSensor - send LOCK_SCREEN_OFF to Broadcast");
        Intent intent = new Intent("com.escns.smombie.LOCK_SCREEN_OFF");
        sendBroadcast(intent);

        if(pref.getBoolean("switch", false)) {
            mCountDownTimer.cancel();
            registerRestartAlarm();
        } else {
            unregisterReceiver(mReceiver);
        }

        mDbManger.deleteLastRecord();

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

                        if(!global.getIsScreen()) {
                            mDist++;
                        }
                    }
                    else {

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

    /**
     * 서버 DB에 record를 저장하기 위함
     */
    public void insertRecordData() {
        Call<String> currentPoint = mApiService.insertRecord(mIdInt, mYear, mMonth, mDate, mHour, mDist);
        currentPoint.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i("tag", "insertRecordData onResponse");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("tag", "insertRecordData onFailure");
                Log.i("tag", t.getMessage());
            }
        });
    }

    /**
     * 파일에 유저정보를 갱신하기 위함
     */
    public void updateUserData(User user) {
        Call<String> currentPoint = mApiService.updateUser(user.getmIdInt(), user.getmName(), user.getmEmail(), user.getmGender(), user.getmAge(), user.getmPoint(), user.getmGoal(), user.getmReword(), user.getmSuccessCnt(), user.getmFailCnt(), user.getmAvgDist());
        currentPoint.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i("tag", "updateUserData onResponse");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("tag", "updateUserData onFailure");
                Log.i("tag", t.getMessage());
            }
        });
    }

    // 전화 상태를 체크하는 Listener
    class PhoneStateListener extends android.telephony.PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            Intent intent;
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    intent = new Intent("com.escns.smombie.CALL_STATE_RINGING");
                    sendBroadcast(intent);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    intent = new Intent("com.escns.smombie.CALL_STATE_OFFHOOK");
                    sendBroadcast(intent);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    intent = new Intent("com.escns.smombie.CALL_STATE_IDLE");
                    sendBroadcast(intent);
                    break;
            }
        }
    }

    private void registerRestartAlarm() {
        Log.i("tag", "registerRestartAlarm");

        Intent i = new Intent(PedometerCheckService.this, LockScreenReceiver.class);
        i.setAction("com.escns.smombie.RESTART_SERVICE");
        PendingIntent sender = PendingIntent.getBroadcast(PedometerCheckService.this, 0, i, 0);

        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 1*1000;

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 1*1000, sender);
    }

    private void unregisterRestartAlarm() {
        Log.i("tag", "unregisterRestartAlarm");

        Intent i = new Intent(PedometerCheckService.this, LockScreenReceiver.class);
        i.setAction("com.escns.smombie.RESTART_SERVICE");
        PendingIntent sender = PendingIntent.getBroadcast(PedometerCheckService.this, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        alarmManager.cancel(sender);
    }
}
