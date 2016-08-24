package com.escns.smombie.ScreenFragment;

import android.content.Context;
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

    DBManager mDbManger;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_info, container, false);

        init();

        return rootView;
    }

    public void init() {

        mDbManger = new DBManager(mContext);

        User user;

        user = mDbManger.getUser();

        ((TextView) rootView.findViewById(R.id.info_name)).setText("홍길동");
        ((TextView) rootView.findViewById(R.id.info_gender)).setText("남성");
        ((TextView) rootView.findViewById(R.id.info_age)).setText("25"+"세");
        ((TextView) rootView.findViewById(R.id.info_email)).setText("hyo99075@naver.com");

        //((TextView) rootView.findViewById(R.id.info_name)).setText(user.getmName());
        //((TextView) rootView.findViewById(R.id.info_gender)).setText(user.getmGender());
        //((TextView) rootView.findViewById(R.id.info_age)).setText(user.getmAge()+"세");
        //((TextView) rootView.findViewById(R.id.info_email)).setText(user.getmEmail());

    }
}
