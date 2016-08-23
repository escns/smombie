package com.escns.smombie.DAO;

/**
 * Created by hyo99 on 2016-08-18.
 */

public class User {

    private int mIdInt;
    private String mIdStr;
    private String mName;
    private String mEmail;
    private String mGender;
    private int mAge;
    private int mPoint;
    private int mGoal;
    private int mReword;
    private int mSuccessCnt;
    private int mFailCnt;
    private int mAvgDist;


    public User(int mIdInt, String mIdStr, String mName, String mEmail, String mGender, int mAge, int mPoint, int mGoal, int mReword, int mSuccessCnt, int mFailCnt, int mAvgDist) {
        this.mIdInt = mIdInt;
        this.mIdStr = mIdStr;
        this.mName = mName;
        this.mEmail = mEmail;
        this.mGender = mGender;
        this.mAge = mAge;
        this.mPoint = mPoint;
        this.mGoal = mGoal;
        this.mReword = mReword;
        this.mSuccessCnt = mSuccessCnt;
        this.mFailCnt = mFailCnt;
        this.mAvgDist = mAvgDist;
    }

    public int getmIdInt() {
        return mIdInt;
    }

    public void setmIdInt(int mIdInt) {
        this.mIdInt = mIdInt;
    }

    public String getmIdStr() {
        return mIdStr;
    }

    public void setmIdStr(String mIdStr) {
        this.mIdStr = mIdStr;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmGender() {
        return mGender;
    }

    public void setmGender(String mGender) {
        this.mGender = mGender;
    }

    public int getmAge() {
        return mAge;
    }

    public void setmAge(int mAge) {
        this.mAge = mAge;
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

    public int getmFailCnt() {
        return mFailCnt;
    }

    public void setmFailCnt(int mFailCnt) {
        this.mFailCnt = mFailCnt;
    }

    public int getmAvgDist() {
        return mAvgDist;
    }

    public void setmAvgDist(int mAvgDist) {
        this.mAvgDist = mAvgDist;
    }

    @Override
    public String toString() {
        return "User{" +
                "mIdInt='" + mIdInt + '\'' +
                ", mIdStr='" + mIdStr + '\'' +
                ", mName='" + mName + '\'' +
                ", mEmail='" + mEmail + '\'' +
                ", mGender='" + mGender + '\'' +
                ", mAge=" + mAge +
                ", mPoint=" + mPoint +
                ", mGoal=" + mGoal +
                ", mReword=" + mReword +
                ", mSuccessCnt=" + mSuccessCnt +
                ", mFailCnt=" + mFailCnt +
                ", mAvgDist=" + mAvgDist +
                '}';
    }
}
