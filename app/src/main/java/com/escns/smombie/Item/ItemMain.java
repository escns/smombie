package com.escns.smombie.Item;

/**
 * Created by Administrator on 2016-08-16.
 */

public class ItemMain {

    private boolean isHeader;
    private String mTitle;
    private int mIcon;

    private int mImage;


    public ItemMain(boolean isHeader, String mTitle, int mIcon) {
        this.isHeader = isHeader;
        this.mTitle = mTitle;
        this.mIcon = mIcon;
    }

    public ItemMain(boolean isHeader, int mImage) {
        this.isHeader = isHeader;
        this.mImage = mImage;
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

    public int getmImage() {
        return mImage;
    }

    public void setmImage(int mImage) {
        this.mImage = mImage;
    }
}
