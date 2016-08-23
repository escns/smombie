package com.escns.smombie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.escns.smombie.Setting.Conf;
import com.escns.smombie.Tab.TabPagerAdapter;
import com.escns.smombie.View.CustomImageView;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hyo99 on 2016-08-16.
 */

public class HistoryActivity extends Fragment {

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_history, container, false);

        initTab(); // 탭메뉴 구현

        return rootView;
    }


    /**
     * 탭 메뉴 구현
     */
    public void initTab() {
        
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("날짜별"));//setIcon(android.R.drawable.ic_dialog_email));
        tabLayout.addTab(tabLayout.newTab().setText("관계별"));//setIcon(android.R.drawable.ic_dialog_dialer));
        tabLayout.addTab(tabLayout.newTab().setText("성과별"));//setIcon(android.R.drawable.ic_dialog_map));
        tabLayout.setTabTextColors(getResources().getColor(R.color.tab_menu), getResources().getColor(R.color.tab_menuSelect));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#fd6839"));
        tabLayout.setSelectedTabIndicatorHeight(8);

        final ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.tab_view);
        final PagerAdapter adapter = new TabPagerAdapter(getFragmentManager(), tabLayout.getTabCount());
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

