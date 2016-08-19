package com.escns.smombie.DAO;

/**
 * Created by Administrator on 2016-08-18.
 */

public class User {
    private String mId;
    private int mPoint;
    private int mGoal;
    private int mReword;
    private int mSuccessCnt;
    private int mTotal;


    public User(String mId, int mPoint, int mGoal, int mReword, int mSuccessCnt, int mTotal) {
        this.mId = mId;
        this.mPoint = mPoint;
        this.mGoal = mGoal;
        this.mReword = mReword;
        this.mSuccessCnt = mSuccessCnt;
        this.mTotal = mTotal;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public int getmPoint() {
        return mPoint;
    }

    public void setmPoint(int mPoint) {
        this.mPoint = mPoint;
    }

    public int getmGoal() {
        return mGoal;
    }

    public void setmGoal(int mGoal) {
        this.mGoal = mGoal;
    }

    public int getmReword() {
        return mReword;
    }

    public void setmReword(int mReword) {
        this.mReword = mReword;
    }

    public int getmSuccessCnt() {
        return mSuccessCnt;
    }

    public void setmSuccessCnt(int mSuccessCnt) {
        this.mSuccessCnt = mSuccessCnt;
    }

    public int getmTotal() {
        return mTotal;
    }

    public void setmTotal(int mTotal) {
        this.mTotal = mTotal;
    }
}
