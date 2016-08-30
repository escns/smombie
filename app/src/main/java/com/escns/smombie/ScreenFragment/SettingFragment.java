package com.escns.smombie.ScreenFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.escns.smombie.R;
import com.escns.smombie.Service.PedometerCheckService;

/**
 * Created by hyo99 on 2016-08-23.
 */

/**
 * 설정 화면
 */
public class SettingFragment extends Fragment {

    private Context mContext;
    private SharedPreferences pref; // 화면 꺼짐 및 이동 시 switch가 초기화되기 때문에 파일에 따로 저장하기 위한 객체

    ImageView swc; // 잠금화면 On/Off 스위치

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        init();

        return rootView;
    }

    /**
     * 초기화 함수
     */
    public void init() {

        mContext = getActivity().getApplicationContext();
        pref = mContext.getSharedPreferences(getResources().getString(R.string.app_name), mContext.MODE_PRIVATE);

        swc = (ImageView) rootView.findViewById(R.id.checkBox);

        if ( pref.getBoolean("switch", false )) {
            Log.d("tag", "버튼상태 true");
            swc.setImageResource(R.drawable.swc_on);
        }
        else {
            Log.d("tag", "버튼상태 false");
            swc.setImageResource(R.drawable.swc_off);
        }

        swc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( !pref.getBoolean("switch", false )) {
                    swc.setImageResource(R.drawable.swc_on);
                    Log.d("tag", "잠금화면 활성화");
                    pref.edit().putBoolean("switch", true).commit();
                    mContext.startService(new Intent(mContext, PedometerCheckService.class));
                }
                else {
                    swc.setImageResource(R.drawable.swc_off);
                    Log.d("tag", "잠금화면 비활성화");
                    pref.edit().putBoolean("switch", false).commit();
                    mContext.stopService(new Intent(mContext, PedometerCheckService.class));
                }

            }
        });

        /*
        // 잠금화면 활성화/비활성화 스위치
        swc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d("tag", "잠금화면 활성화");
                    pref.edit().putBoolean("switch", true).commit();
                    mContext.startService(new Intent(mContext, PedometerCheckService.class));

                    //Intent intent = new Intent("com.escns.smombie.service").setPackage("com.escns.smombie");
                    //mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE); // 만보기 동작

                    //mContext.startService(new Intent(mContext, LockScreenService.class));
                } else {
                    Log.d("tag", "잠금화면 비활성화");
                    pref.edit().putBoolean("switch", false).commit();
                    mContext.stopService(new Intent(mContext, PedometerCheckService.class));

                    //mContext.unbindService(mConnection);
                    //mContext.stopService(new Intent(mContext, LockScreenService.class));
                }
            }
        });
       */

    }
}
