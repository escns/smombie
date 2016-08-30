package com.escns.smombie.DAO;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016-08-17.
 */

/**
 * Record의 정보들을 관리하는 class
 */
public class Record {

    @SerializedName("USER_ID_INT")
    private int mIdInt; // 유저 고유 번호
    @SerializedName("YEAR")
    private int mYear;  //  연도
    @SerializedName("MONTH")
    private int mMonth; // 달
    @SerializedName("DAY")
    private int mDay;   // 일
    @SerializedName("HOUR")
    private int mHour;  // 시간
    @SerializedName("DIST")
    private int mDist;  // 걸음 수

    /**
     * 생성자
     * @param mIdInt 유저 고유 번호
     * @param mYear 연도
     * @param mMonth 달
     * @param mDay 일
     * @param mHour 시간
     * @param mDist 걸음 수
     */
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
