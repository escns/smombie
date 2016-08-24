package com.escns.smombie.ScreenFragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.escns.smombie.R;
import com.escns.smombie.Service.LockScreenService;

/**
 * Created by hyo99 on 2016-08-23.
 */

public class SettingFragment extends Fragment {

    private Context mContext;

    private SharedPreferences pref;         // 화면 꺼짐 및 이동 시 switch가 초기화되기 때문에 파일에 따로 저장하기 위한 객체

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        init();

        return rootView;
    }

    public void init() {

        mContext = getActivity().getApplicationContext();


        pref = mContext.getSharedPreferences("pref", mContext.MODE_PRIVATE);

        SwitchCompat swc = (SwitchCompat) rootView.findViewById(R.id.switch_lock_setting);
        swc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    pref.edit().putBoolean("switch", true).commit();

                    Intent intent = new Intent("com.escns.smombie.service").setPackage("com.escns.smombie");
                    mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE); // 만보기 동작
                    mContext.startService(new Intent(mContext, LockScreenService.class));
                } else {
                    pref.edit().putBoolean("switch", false).commit();

                    mContext.unbindService(mConnection);
                    mContext.stopService(new Intent(mContext, LockScreenService.class));
                }
            }
        });

    }

    // ThreadService와 MainActivity를 연결 시켜줄 ServiceConnection
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        // 리턴되는 Binder를 다시 Service로 꺼내서 ThreadSerivce내부의 함수 사용이 가능하다.
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            //WalkCheckService.LocalBinder binder = (WalkCheckService.LocalBinder) service;
            //mService = binder.getService();
            //mBound = true;
            Log.d("tag", "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            //mBound = false;
            Log.d("tag", "onServiceDisconnected");
        }
    };
}
