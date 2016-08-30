package com.escns.smombie.ScreenFragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.escns.smombie.R;
import com.escns.smombie.Tab.TabPagerAdapter;
import com.escns.smombie.Utils.RandomAd;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by hyo99 on 2016-08-16.
 */

/**
 * 히스토리 화면
 */
public class HistoryFragment extends Fragment {

    TabLayout tabLayout; // 탭메뉴가 있을 레이아웃

    View rootView;

    /**
     * onCreate
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_history, container, false);

        initTab(); // 탭메뉴 구현

        return rootView;
    }

    /**
     * onResume 화면이 그려지면 호출
     */
    @Override
    public void onResume() {
        super.onResume();

        RandomAd randomAd = new RandomAd();
        ImageView historyAdView = (ImageView) rootView.findViewById(R.id.history_adview);
        final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.ad_loading);
        progressBar.setVisibility(View.VISIBLE);

        Picasso.with(getContext())
                .load(randomAd.getRandomAdUrl(1).get(0))
                .fit()
                .into(historyAdView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    /**
     * 화면이 다시 출력되었을 때 탭메뉴 재구성
     */
    @Override
    public void onStart() {

        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("날짜별"));//setIcon(android.R.drawable.ic_dialog_email));
        tabLayout.addTab(tabLayout.newTab().setText("관계별"));//setIcon(android.R.drawable.ic_dialog_dialer));
        tabLayout.addTab(tabLayout.newTab().setText("성과별"));//setIcon(android.R.drawable.ic_dialog_map));
        tabLayout.setTabTextColors(getResources().getColor(R.color.tab_menu), getResources().getColor(R.color.tab_menuSelect));
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.tab_lineSelect));
        tabLayout.setSelectedTabIndicatorHeight(8);

        super.onStart();
    }


    /**
     * 탭 메뉴 구현 - 클릭, 스와이프 기능
     */
    public void initTab() {
        
        tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("날짜별"));//setIcon(android.R.drawable.ic_dialog_email));
        tabLayout.addTab(tabLayout.newTab().setText("관계별"));//setIcon(android.R.drawable.ic_dialog_dialer));
        tabLayout.addTab(tabLayout.newTab().setText("성과별"));//setIcon(android.R.drawable.ic_dialog_map));
        tabLayout.setTabTextColors(getResources().getColor(R.color.tab_menu), getResources().getColor(R.color.tab_menuSelect));
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.tab_lineSelect));//Color.parseColor("#fd6839"));
        tabLayout.setSelectedTabIndicatorHeight(8);


        final ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.tab_view);
        final PagerAdapter adapter = new TabPagerAdapter(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}

