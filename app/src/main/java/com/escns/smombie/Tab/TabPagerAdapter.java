package com.escns.smombie.Tab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by hyo99 on 2016-08-17.
 */

public class TabPagerAdapter extends FragmentPagerAdapter {

    private int tabCount;

    TabFragment1 frag1;
    TabFragment2 frag2;
    TabFragment3 frag3;

    public TabPagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.tabCount = numberOfTabs;
        frag1 = new TabFragment1();
        frag2 = new TabFragment2();
        frag3 = new TabFragment3();

    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return frag1;
            case 1:
                return frag2;
            case 2:
                return frag3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

}
