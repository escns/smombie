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

    private ServiceConnection mConnection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        init();

        return rootView;
    }

    public void init() {

        mContext = getActivity().getApplicationContext();

        SwitchCompat swc = (SwitchCompat) rootView.findViewById(R.id.switch_lock_setting);
        pref = mContext.getSharedPreferences("pref", mContext.MODE_PRIVATE);

        boolean state = pref.getBoolean("switch", true);
        if (state) {
            swc.setChecked(true);
        }
        else {
            swc.setChecked(false);
        }


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
}
