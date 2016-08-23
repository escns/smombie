package com.escns.smombie.ScreenFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.escns.smombie.R;

/**
 * Created by hyo99 on 2016-08-23.
 */

public class SetFragment extends Fragment {

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_set, container, false);

        //init();

        return rootView;
    }
}
