package com.escns.smombie.ScreenFragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.escns.smombie.R;
import com.escns.smombie.Tab.TabPagerAdapter;

/**
 * Created by hyo99 on 2016-08-16.
 */

public class HistoryFragment extends Fragment {

    TabLayout tabLayout;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_history, container, false);

        initTab(); // 탭메뉴 구현

        return rootView;
    }

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
     * 탭 메뉴 구현
     */
    public void initTab() {
        
        tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("날짜별"));//setIcon(android.R.drawable.ic_dialog_email));
        tabLayout.addTab(tabLayout.newTab().setText("관계별"));//setIcon(android.R.drawable.ic_dialog_dialer));
        tabLayout.addTab(tabLayout.newTab().setText("성과별"));//setIcon(android.R.drawable.ic_dialog_map));
        tabLayout.setTabTextColors(getResources().getColor(R.color.tab_menu), getResources().getColor(R.color.tab_menuSelect));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#fd6839"));
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

