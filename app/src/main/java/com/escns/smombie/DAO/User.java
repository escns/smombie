package com.escns.smombie.DAO;

/**
 * Created by hyo99 on 2016-08-18.
 */

public class User {

    private String mId;
    private String mName;
    private String mEmail;
    private String mGender;
    private int mAge;
    private int mPoint;
    private int mMissonSuccess;
    private int mMissonFail;

    public User(String mId, String mName, String mEmail, String mGender, int mAge, int mPoint, int mMissonSuccess, int mMissonFail) {
        this.mId = mId;
        this.mName = mName;
        this.mEmail = mEmail;
        this.mGender = mGender;
        this.mAge = mAge;
        this.mPoint = mPoint;
        this.mMissonSuccess = mMissonSuccess;
        this.mMissonFail = mMissonFail;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
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

    public int getmMissonSuccess() {
        return mMissonSuccess;
    }

    public void setmMissonSuccess(int mMissonSuccess) {
        this.mMissonSuccess = mMissonSuccess;
    }

    public int getmMissonFail() {
        return mMissonFail;
    }

    public void setmMissonFail(int mMissonFail) {
        this.mMissonFail = mMissonFail;
    }

    @Override
    public String toString() {
        return "Record {" +
                "mId='" + mId + '\'' +
                ", mName=" + mName +
                ", mEmail=" + mEmail +
                ", mGender=" + mGender +
                ", mAge=" + mAge +
                ", mPoint=" + mPoint +
                ", mMissonSuccess=" + mMissonSuccess +
                ", mMissonFail=" + mMissonFail +
                '}';
    }

}
