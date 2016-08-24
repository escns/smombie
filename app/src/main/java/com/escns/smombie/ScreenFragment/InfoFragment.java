package com.escns.smombie.ScreenFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.escns.smombie.DAO.User;
import com.escns.smombie.Manager.DBManager;
import com.escns.smombie.R;

/**
 * Created by hyo99 on 2016-08-23.
 */

public class InfoFragment extends Fragment {

    Context mContext;

    private DBManager mDbManger;

    private SharedPreferences pref;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_info, container, false);

        init();

        return rootView;
    }

    public void init() {

        User user;

        pref = mContext.getSharedPreferences("@string/app_name", mContext.MODE_PRIVATE);

        ((TextView) rootView.findViewById(R.id.info_name)).setText("홍길동");
        ((TextView) rootView.findViewById(R.id.info_gender)).setText("남성");
        ((TextView) rootView.findViewById(R.id.info_age)).setText("25"+"세");
        ((TextView) rootView.findViewById(R.id.info_email)).setText("hyo99075@naver.com");

        //((TextView) rootView.findViewById(R.id.info_name)).setText(pref.getString("NAME",""));
        //((TextView) rootView.findViewById(R.id.info_gender)).setText(pref.getString("GENDER",""));
        //((TextView) rootView.findViewById(R.id.info_age)).setText(pref.getString("AGE","")+"세");
        //((TextView) rootView.findViewById(R.id.info_email)).setText(pref.getString("EMAIL",""));

    }
}
