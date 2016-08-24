package com.escns.smombie.DAO;

/**
 * Created by hyo99 on 2016-08-24.
 */

public class Statistic {

    private int mAvgMale;
    private int mAvgFemale;
    private int mAvg10s;
    private int mAvg20s;
    private int mAvg30s;
    private int mAvg40s;
    private int mAvg50s;

    public Statistic(int mAvgMale, int mAvgFemale, int mAvg10s, int mAvg20s, int mAvg30s, int mAvg40s, int mAvg50s) {
        this.mAvgMale = mAvgMale;
        this.mAvgFemale = mAvgFemale;
        this.mAvg10s = mAvg10s;
        this.mAvg20s = mAvg20s;
        this.mAvg30s = mAvg30s;
        this.mAvg40s = mAvg40s;
        this.mAvg50s = mAvg50s;
    }

    public int getmAvgMale() {
        return mAvgMale;
    }

    public void setmAvgMale(int mAvgMale) {
        this.mAvgMale = mAvgMale;
    }

    public int getmAvgFemale() {
        return mAvgFemale;
    }

    public void setmAvgFemale(int mAvgFemale) {
        this.mAvgFemale = mAvgFemale;
    }

    public int getmAvg10s() {
        return mAvg10s;
    }

    public void setmAvg10s(int mAvg10s) {
        this.mAvg10s = mAvg10s;
    }

    public int getmAvg20s() {
        return mAvg20s;
    }

    public void setmAvg20s(int mAvg20s) {
        this.mAvg20s = mAvg20s;
    }

    public int getmAvg30s() {
        return mAvg30s;
    }

    public void setmAvg30s(int mAvg30s) {
        this.mAvg30s = mAvg30s;
    }

    public int getmAvg40s() {
        return mAvg40s;
    }

    public void setmAvg40s(int mAvg40s) {
        this.mAvg40s = mAvg40s;
    }

    public int getmAvg50s() {
        return mAvg50s;
    }

    public void setmAvg50s(int mAvg50s) {
        this.mAvg50s = mAvg50s;
    }

    @Override
    public String toString() {
        return "Statistic {" +
                "mAvgMale='" + mAvgMale + '\'' +
                ", mAvgFemale=" + mAvgFemale +
                ", mAvg10s=" + mAvg10s +
                ", mAvg20s=" + mAvg20s +
                ", mAvg30s=" + mAvg30s +
                ", mAvg40s=" + mAvg40s +
                ", mAvg50s=" + mAvg50s +
                '}';
    }
}
