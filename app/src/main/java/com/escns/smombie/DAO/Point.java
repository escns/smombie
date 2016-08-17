package com.escns.smombie.DAO;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016-08-16.
 */

public class Point {

    @SerializedName("point")
    private int point;

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
