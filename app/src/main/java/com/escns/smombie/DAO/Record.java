package com.escns.smombie.DAO;

/**
 * Created by Administrator on 2016-08-17.
 */

public class Record {

    private int mIdInt;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mDist;
    private int mStepCnt;

    public Record(int mIdInt, int mYear, int mMonth, int mDay, int mHour, int mDist, int mStepCnt) {
        this.mIdInt = mIdInt;
        this.mYear = mYear;
        this.mMonth = mMonth;
        this.mDay = mDay;
        this.mHour = mHour;
        this.mDist = mDist;
        this.mStepCnt = mStepCnt;
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

    public int getmStepCnt() {
        return mStepCnt;
    }

    public void setmStepCnt(int mStepCnt) {
        this.mStepCnt = mStepCnt;
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
                ", mStepCnt=" + mStepCnt +
                '}';
    }
}
