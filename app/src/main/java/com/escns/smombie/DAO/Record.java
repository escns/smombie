package com.escns.smombie.DAO;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016-08-17.
 */

public class Record {

    @SerializedName("USER_ID_INT")
    private int mIdInt;
    @SerializedName("YEAR")
    private int mYear;
    @SerializedName("MONTH")
    private int mMonth;
    @SerializedName("DAY")
    private int mDay;
    @SerializedName("HOUR")
    private int mHour;
    @SerializedName("DIST")
    private int mDist;

    public Record(int mIdInt, int mYear, int mMonth, int mDay, int mHour, int mDist) {
        this.mIdInt = mIdInt;
        this.mYear = mYear;
        this.mMonth = mMonth;
        this.mDay = mDay;
        this.mHour = mHour;
        this.mDist = mDist;
    }

    public int getmIdInt() {
        return mIdInt;
    }

    public void setmIdInt(int mIdInt) {
        this.mIdInt = mIdInt;
    }

    public int getmYear() {
        return mYear;
    }

    public void setmYear(int mYear) {
        this.mYear = mYear;
    }

    public int getmMonth() {
        return mMonth;
    }

    public void setmMonth(int mMonth) {
        this.mMonth = mMonth;
    }

    public int getmDay() {
        return mDay;
    }

    public void setmDay(int mDay) {
        this.mDay = mDay;
    }

    public int getmHour() {
        return mHour;
    }

    public void setmHour(int mHour) {
        this.mHour = mHour;
    }

    public int getmDist() {
        return mDist;
    }

    public void setmDist(int mDist) {
        this.mDist = mDist;
    }

    @Override
    public String toString() {
        return "Record {" +
                "mIdInt='" + mIdInt + '\'' +
                ", mYear=" + mYear +
                ", mMonth=" + mMonth +
                ", mDay=" + mDay +
                ", mHour=" + mHour +
                ", mDist=" + mDist +
                '}';
    }
}
