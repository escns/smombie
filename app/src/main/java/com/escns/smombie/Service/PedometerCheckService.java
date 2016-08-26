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
import android.widget.RelativeLayout;

import com.escns.smombie.DAO.Record;
import com.escns.smombie.DAO.User;
import com.escns.smombie.Interface.ApiService;
import com.escns.smombie.MainActivity;
import com.escns.smombie.Manager.DBManager;
import com.escns.smombie.R;
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

public class PedometerCheckService extends Service {

    private Context mContext;
    private SharedPreferences pref;         // 화면 꺼짐 및 이동 시 switch가 초기화되기 때문에 파일에 따로 저장하기 위한 객체

    private Retrofit mRetrofit;
    private ApiService mApiService;

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

    private Calendar c;
    private int mIdInt;
    private int mYear;
    private int mMonth;
    private int mDate;
    private int mHour;
    private int mDist;
    private DBManager mDbManger;
    private Record record;
    List<Record> list;

    boolean isWalkingNow = false; // 만보기: 제자리이면 false / 걷는상태이면 true

    private TimerTask myTimer;
    private Handler handler;
    private boolean isWalkingPast = false;
    private boolean isSave = false;


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
        pref = mContext.getSharedPreferences(getResources().getString(R.string.app_name), mContext.MODE_PRIVATE);
        c = Calendar.getInstance();
        mDbManger = new DBManager(mContext);
        record = new Record(0,0,0,0,0,0);
        list = null;
        list = new ArrayList<>();
        list = mDbManger.getRecord();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        mRetrofit = new Retrofit.Builder().baseUrl(mApiService.API_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
        mApiService = mRetrofit.create(ApiService.class);

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
                    isSave = true;

                    Intent intent = new Intent("com.escns.smombie.LOCK_SCREEN_ON");
                    sendBroadcast(intent);
                    isWalkingPast = true;
                }
                else {
                    if(!isWalkingPast) {

                        if(isSave) {
                            record.setmIdInt(mIdInt);
                            record.setmYear(mYear);
                            record.setmMonth(mMonth);
                            record.setmDay(mDate);
                            record.setmHour(mHour);
                            record.setmDist(mDist);
                            mDbManger.insertRecord(record);
                            insertRecordData();

                            Log.d("tag", "값 한번 보자 mIdInt = " + record.getmIdInt());
                            Log.d("tag", "값 한번 보자 mYear = " + record.getmYear());
                            Log.d("tag", "값 한번 보자 mMonth = " + record.getmMonth());
                            Log.d("tag", "값 한번 보자 mDate = " + record.getmDay());
                            Log.d("tag", "값 한번 보자 mHour = " + record.getmHour());
                            Log.d("tag", "값 한번 보자 mDist = " + record.getmDist());

                            int cnt = pref.getInt("POINT", 0);

                            if(cnt >= pref.getInt("GOAL", 1000))  {
                                cnt -= pref.getInt("GOAL", 1000);
                                pref.edit().putInt("POINT", cnt+mDist).commit();
                                cnt = pref.getInt("REWORD", 1000);
                                pref.edit().putInt("REWORD", cnt+1).commit();
                            }
                            else {
                                pref.edit().putInt("POINT", cnt+mDist).commit();
                            }

                            

                            cnt = pref.getInt("SUCCESSCNT", 0);
                            pref.edit().putInt("SUCCESSCNT", cnt+1).commit();

                            int totalDist = 0;
                            for(int i=0; i<list.size(); i++) {
                                totalDist += list.get(i).getmDist();
                            }
                            totalDist /= list.size();
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


                            isSave = false;
                        }
                        else {
                            isSave = false;
                        }

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

    public void updateUserData(User user) {
        Call<String> currentPoint = mApiService.updateUser(user.getmIdInt(), user.getmName(), user.getmEmail(), user.getmGender(), user.getmAge(), user.getmGoal(), user.getmPoint(), user.getmReword(), user.getmSuccessCnt(), user.getmFailCnt(), user.getmAvgDist());
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
}
