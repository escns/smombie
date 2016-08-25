package com.escns.smombie.Item;

/**
 * Created by Administrator on 2016-08-16.
 */

public class ItemMain {

    private boolean isHeader;
    private String mTitle;
    private int mIcon;

    private String mUrl;


    public ItemMain(boolean isHeader, String mTitle, int mIcon) {
        this.isHeader = isHeader;
        this.mTitle = mTitle;
        this.mIcon = mIcon;
    }

    public ItemMain(boolean isHeader, String mUrl) {
        this.isHeader = isHeader;
        this.mUrl = mUrl;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
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
