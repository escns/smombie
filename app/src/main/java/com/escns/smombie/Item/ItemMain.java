package com.escns.smombie.Item;

/**
 * Created by Administrator on 2016-08-16.
 */

/**
 * 홈 화면에서 쓸 데이터들을 관리하는 class
 */
public class ItemMain {

    private String mTitle;
    private int mIcon;

    private String mUrl;

    public ItemMain(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getmIcon() {
        return mIcon;
    }

    public void setmIcon(int mIcon) {
        this.mIcon = mIcon;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}
