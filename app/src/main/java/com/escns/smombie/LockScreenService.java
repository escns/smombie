package com.escns.smombie;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by Administrator on 2016-08-04.
 */

public class LockScreenService extends Service {

    LockScreenReceiver mReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("Tag", "LockScreenService - onCreate");

        try {
            // BroadCastReceiver의 필터 설정
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction("com.escns.smombie.CALL_STATE_RINGING");
            filter.addAction("com.escns.smombie.CALL_STATE_OFFHOOK");
            filter.addAction("com.escns.smombie.CALL_STATE_IDLE");
            mReceiver = new LockScreenReceiver();
            registerReceiver(mReceiver, filter);

            PhoneStateListener phoneStateListener = new PhoneStateListener();
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            telephonyManager.listen(phoneStateListener, android.telephony.PhoneStateListener.LISTEN_CALL_STATE);

        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d("Tag", "LockScreenService - onDestroy");
        Intent intent = new Intent("com.escns.smombie.LOCK_SCREEN_OFF");
        sendBroadcast(intent);
        unregisterReceiver(mReceiver);

        super.onDestroy();
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
}
