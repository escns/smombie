package com.escns.smombie.ScreenFragment;

import android.Manifest;
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
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

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

                    PermissionListener permissionlistener = new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            //Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                            swc.setImageResource(R.drawable.swc_on);
                            Log.d("tag", "잠금화면 활성화");
                            pref.edit().putBoolean("switch", true).commit();
                            mContext.startService(new Intent(mContext, PedometerCheckService.class));
                        }

                        @Override
                        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                            //Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                        }
                    };

                    new TedPermission(getActivity())
                            .setPermissionListener(permissionlistener)
                            .setDeniedMessage("해당 서비스에서 제공하는 권한 설정을 거부하셨다면\n\n[Setting] > [Permission] 에서 권한 설정을 해주시기 바랍니다.")
                            .setPermissions(Manifest.permission.READ_PHONE_STATE)
                            .check();


                }
                else {
                    swc.setImageResource(R.drawable.swc_off);
                    Log.d("tag", "잠금화면 비활성화");
                    pref.edit().putBoolean("switch", false).commit();
                    mContext.stopService(new Intent(mContext, PedometerCheckService.class));
                }

            }
        });



    }
}
