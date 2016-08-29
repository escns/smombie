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
import android.widget.Button;
import android.widget.TextView;

import com.escns.smombie.DAO.User;
import com.escns.smombie.MainActivity;
import com.escns.smombie.Manager.DBManager;
import com.escns.smombie.R;
import com.escns.smombie.Service.LockScreenService;
import com.escns.smombie.Service.PedometerCheckService;

/**
 * Created by hyo99 on 2016-08-23.
 */

/**
 * 내정보 화면
 */
public class InfoFragment extends Fragment {

    Context mContext;

    private SharedPreferences pref;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_info, container, false);

        init(); // 초기화

        Button testOn = (Button)rootView.findViewById(R.id.button_test);
        testOn.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                Log.d("tag", "실행ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ");
                pref.edit().putBoolean("switch", true).commit();

                mContext.startService(new Intent((mContext), PedometerCheckService.class));
                mContext.startService(new Intent((mContext), LockScreenService.class));
            }
        });

        Button testOff = (Button)rootView.findViewById(R.id.button_test2);
        testOff.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                Log.d("tag", "종료ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ");
                pref.edit().putBoolean("switch", false).commit();

                mContext.stopService(new Intent(mContext, PedometerCheckService.class));
                mContext.stopService(new Intent(mContext, LockScreenService.class));
            }
        });

        return rootView;
    }

    /**
     * 초기화 함수
     */
    public void init() {

        mContext = getActivity().getApplicationContext();
        pref = mContext.getSharedPreferences(getResources().getString(R.string.app_name), mContext.MODE_PRIVATE);

        // 내정보 출력
        ((TextView) rootView.findViewById(R.id.info_name)).setText(pref.getString("NAME",""));
        ((TextView) rootView.findViewById(R.id.info_gender)).setText(pref.getString("GENDER",""));
        ((TextView) rootView.findViewById(R.id.info_age)).setText(pref.getInt("AGE",20)+"세");
        ((TextView) rootView.findViewById(R.id.info_email)).setText(pref.getString("EMAIL",""));

    }
}
