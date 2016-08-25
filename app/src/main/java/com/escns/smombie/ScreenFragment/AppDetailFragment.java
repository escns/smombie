package com.escns.smombie.ScreenFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.escns.smombie.R;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2016-08-25.
 */

public class AppDetailFragment extends Fragment {

    private static AppDetailFragment mAppDetailFragment;

    private FrameLayout viewpager;
    private LinearLayout stickyView;
    private TextView placeTv, maskTv;
    private int notifyBarHeight = 0;
    private StickyScrollCallBack scrollListener;
    private View tabLayout;

    private DetailFragment1 detailFragment1;

    public static int STICKY_HEIGHT1; // height1是代表从顶部到tab的距离
    public static int STICKY_HEIGHT2; // height2是代表从顶部到viewpager的距离

    boolean first = false;

    public static AppDetailFragment getInstance() {
        if(mAppDetailFragment==null) {
            mAppDetailFragment=new AppDetailFragment();
        }
        return mAppDetailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater
                .inflate(R.layout.fragment_main3, container, false);
        initView(rootView);
        initBarHeight();

        return rootView;
    }

    private void initView(View view) {
        viewpager = (FrameLayout) view.findViewById(R.id.viewpager);
        maskTv = (TextView) view.findViewById(R.id.mask_tv);
        placeTv = (TextView) view.findViewById(R.id.place_tv);
        tabLayout = (View) view.findViewById(R.id.tabs);
        stickyView = (LinearLayout) view.findViewById(R.id.sticky_view);
        stickyView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        placeTv.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

        STICKY_HEIGHT2 = stickyView.getMeasuredHeight();
        STICKY_HEIGHT1 = stickyView.getChildAt(0).getMeasuredHeight();

        Log.i("LeiTest", "height1=" + STICKY_HEIGHT1 + " height2="
                + STICKY_HEIGHT2);

        scrollListener = new StickyScrollCallBack() {

            @Override
            public void onScrollChanged(int scrollY) {
                processStickyTranslateY(scrollY);

            }
        };

        detailFragment1 = new DetailFragment1();
        detailFragment1.setScrollCallBack(scrollListener);

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.viewpager, detailFragment1).commit();



    }

    private void initBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = getActivity().getResources()
                    .getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        notifyBarHeight = statusBarHeight;
    }

    private int downSelect = 0;
    private int lastProcessStickyTranslateY = 0;
    private int navBottomPos, locTvTopPosX, locTvTopPosY, titleHeight;

    @SuppressLint("NewApi")
    private void processStickyTranslateY(int translateY) {
        //if(translateY==-161) return;
        if (translateY == Integer.MIN_VALUE
                || translateY == lastProcessStickyTranslateY) {
            return;
        }
        Log.i("tag", "translateY  : " + translateY);
        lastProcessStickyTranslateY = translateY;

        stickyView.setTranslationY(translateY);
    }

    public interface StickyScrollCallBack {
        public void onScrollChanged(int scrollY);
    }

}
