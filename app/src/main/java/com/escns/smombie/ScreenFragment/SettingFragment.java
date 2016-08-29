package com.escns.smombie.ScreenFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.escns.smombie.R;
import com.escns.smombie.Service.LockScreenService;
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

    RelativeLayout layoutSwitch;
    ToggleButton swc;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        init();

        return rootView;
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    /**
     * 초기화 함수
     */
    public void init() {

        mContext = getActivity().getApplicationContext();
        pref = mContext.getSharedPreferences(getResources().getString(R.string.app_name), mContext.MODE_PRIVATE);

        layoutSwitch = (RelativeLayout) rootView.findViewById(R.id.layout_swtich);
        swc = (ToggleButton) rootView.findViewById(R.id.toggleButton);

        //layoutSwitch.setVisibility(View.INVISIBLE);
        //if ( pref.getBoolean("switch", true )) {
        //    Log.d("tag", "버튼상태 True");
        //    swc.setChecked(true);
        //}
        //else {
        //    Log.d("tag", "버튼상태 flase");
        //    swc.setChecked(false);
        //}
        //layoutSwitch.setVisibility(View.VISIBLE);

        // 잠금화면 활성화/비활성화 스위치
        swc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    pref.edit().putBoolean("switch", true).commit();

                    /*
                    Intent intent = new Intent("com.escns.smombie.service").setPackage("com.escns.smombie");
                    mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE); // 만보기 동작
                    */
                    mContext.startService(new Intent(mContext, PedometerCheckService.class));
                    mContext.startService(new Intent(mContext, LockScreenService.class));
                } else {
                    pref.edit().putBoolean("switch", false).commit();

                    //mContext.unbindService(mConnection);
                    mContext.stopService(new Intent(mContext, PedometerCheckService.class));
                    mContext.stopService(new Intent(mContext, LockScreenService.class));

                }
            }
        });

    }
}
